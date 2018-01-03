package com.github.bysky.textimage;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static int CHOOSE_PHOTO = 1001;
    private RecordDatabaseOpenHelper databaseOpenHelper;
    private SQLiteDatabase database;
    private TextView textViewShowFile, textViewHint;
    private RecyclerView recycler;
    private Button buttonCommit;
    private ImageView image;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //读取数据库
        databaseOpenHelper = new RecordDatabaseOpenHelper(this,"TextImage.db",null,1);
        database = databaseOpenHelper.getWritableDatabase();
        textViewShowFile = findViewById(R.id.text_view_show_file);
        textViewHint = findViewById(R.id.text_view_hint);
        buttonCommit = findViewById(R.id.button_commit);
        image = findViewById(R.id.image_preview);
        //
        textViewShowFile.setOnClickListener(this);
        buttonCommit.setOnClickListener(this);
        //recyclerview
        recycler = findViewById(R.id.recycler_record);
        ArrayList<Record> list = getRecordList(database.query("RECORD"
                ,new String[]{"FILE_PATH","FILE_NAME"},null,null,null,null,null));
        RecordAdapter.OnItemClickListener listener = new RecordAdapter.OnItemClickListener() {
            @Override
            public void onClick(RecordAdapter.RecordHolder holder) {
                //TODO:调用浏览器
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( holder.getTxtPath()));
                startActivity(intent);
            }
        };
        RecordAdapter adapter = new RecordAdapter(this, list, listener);
        GridLayoutManager glm = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(glm);
        recycler.setAdapter(adapter);
        //
        if(!list.isEmpty())
            textViewHint.setVisibility(View.GONE);
        //申请权限
        requestPermission();
    }

    private ArrayList<Record> getRecordList(Cursor src){
        ArrayList<Record> list = new ArrayList<Record>();
        if(!src.moveToFirst())
            return list;
        do{
            list.add(new Record( src.getString(src.getColumnIndex("FILE_PATH")),
                    src.getString( src.getColumnIndex("FILE_NAME"))) );
        }while(src.moveToNext());
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_show_file:
                if (requestPermission()) {
                    //有权限了
                    getImage();
                }
                break;
            case R.id.button_commit:
                //处理
                textViewHint.setVisibility(View.GONE);
                processImage();
                Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show();
                //输出
                break;
        }
    }

    protected void processImage() {
        if(imageFile==null || !imageFile.exists())
            return;
        int w,h;
        int[] data;
        //得到图像
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
        w = bitmap.getWidth();  h = bitmap.getHeight();
        //可能图片过大
        data = new int[w*h];
        bitmap.getPixels(data,0,w,0,0,w,h);
        output(data,image.getWidth(),image.getHeight());
    }

    protected void output(int[] image,int w,int h){
        BufferedWriter bw;
        String path,imgPath;
        imgPath = imageFile.getPath();
        path = imgPath.substring(0,imgPath.lastIndexOf('.'))+".txt";
        if(image == null)
            return;
        //开始处理图像
        File file = new File(path);
        try{
//			if(file.exists()){
//				System.out.print("该文件已存在,是否继续？");
//			}
            file.createNewFile();
            bw = new BufferedWriter( new FileWriter(file));
            char[] text = new char[10000];
            int i,x,y,len,cf,temp;
            //虽然我也不知道为什么返回的数组长度有时!=w*h
            cf = image.length/w/h;
            if(cf!=1)
                Toast.makeText(this,"Not 1 :"+cf, Toast.LENGTH_SHORT).show();
            for(i=y=x=0,len=w*h; i<len; x+=cf){
//            Gray = (R*30 + G*59 + B*11 + 50) / 100
//                [ .'^-"~+*/coxm#$NM%@]
//                temp = ( (image[x]&0xFF)*30 +  (image[x+1]&0xFF)*59 + (image[x+2]&0xFF)*11 + 50 ) /100;  //RGB
                temp = ( ((image[x]>>16)&0xFF)*30 +  ((image[x]>>8)&0xFF)*59 + (image[x]&0xFF)*11 + 50 ) /100;  //RGB
                if(temp<=12) 	text[y++]='#';
                else if(temp<=25) text[y++]='@';
                else if(temp<=38) text[y++]='%';
                else if(temp<=51) text[y++]='M';
                else if(temp<=63) text[y++]='N';
                else if(temp<=76) text[y++]='$';
                else if(temp<=89) text[y++]='m';
                else if(temp<=102) text[y++]='x';
                else if(temp<=114) text[y++]='o';
                else if(temp<=127) text[y++]='c';
                else if(temp<=140) text[y++]='/';
                else if(temp<=153) text[y++]='*';
                else if(temp<=165) text[y++]='+';
                else if(temp<=178) text[y++]='~';
                else if(temp<=191) text[y++]='"';
                else if(temp<=204) text[y++]='-';
                else if(temp<=216) text[y++]='^';
                else if(temp<=229) text[y++]='\47';
                else if(temp<=242) text[y++]='.';
                else text[y++]=' ';
                i++;
                if(i!=0 && i%w==0){
                    Log.e("=====:","WIDTH="+w+" I="+i+" I%W="+(i%w));
                    text[y++]='\n';
                    bw.write(text,0,y);
                    y=0;
                }
                if(y >= 9990){
                    //满了
                    bw.write(text,0,y);
                    y=0;
                }
            }
            bw.write(text,0,y);
            bw.close();
            writeRecord(imgPath);
            //添加记录
            ((RecordAdapter)recycler.getAdapter()).addItem(imgPath
                    , imgPath.substring(imgPath.lastIndexOf('/'+1, imgPath.lastIndexOf('.'))));
        }catch(IOException ioe){
            System.out.println(ioe);
        }
    }

    protected void getImage() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    handleImage(data);
                }
                break;
        }
    }

    protected void writeRecord(String path){
        ContentValues values = new ContentValues();
        values.clear();
        values.put("FILE_PATH", path);
        values.put("FILE_NAME", path.substring(path.lastIndexOf('/')+1, path.lastIndexOf('.')));
        database.insert("RECORD", null, values);
    }

    protected void handleImage(Intent data) {
        String path = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //document类型Uri
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                path = getImagePath(contentUri, null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            path = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            path = uri.getPath();
        }
        //获得了路径，为所欲为
        if(path!=null){
            imageFile = new File(path);
            image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getPath()));
            textViewShowFile.setText(imageFile.getPath()
                    .substring(imageFile.getPath().lastIndexOf('/')+1, imageFile.getPath().length()));
        }
    }

    protected String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    protected boolean requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9528);
        } else
            return true;
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 9528:
                Toast.makeText(this, "无权读写图像文件！", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
