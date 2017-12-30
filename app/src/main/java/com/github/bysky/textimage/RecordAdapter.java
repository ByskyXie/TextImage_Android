package com.github.bysky.textimage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by asus on 2017/12/29.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder> {

    private Context context;
    private ArrayList<Record> recordList;
    private OnItemClickListener listener;

    RecordAdapter(Context context, ArrayList<Record> recordList, OnItemClickListener listener) {
        this.context = context;
        this.recordList = recordList;
        this.listener = listener;
    }

    class RecordHolder extends RecyclerView.ViewHolder{
        private View root;
        private ImageView img;
        private TextView textViewTitle;
        private RecordHolder(View itemView) {
            super(itemView);
            root = itemView;
            img = itemView.findViewById(R.id.image_record_item);
            textViewTitle = itemView.findViewById(R.id.text_view_record_item);
        }
    }

    @Override
    public void onBindViewHolder(final RecordHolder holder, int position) {
        String path = recordList.get(holder.getAdapterPosition()).getFilePath();
        holder.textViewTitle.setText(recordList.get(holder.getAdapterPosition()).getFileName());
        holder.img.setImageBitmap(BitmapFactory.decodeFile(path));
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(holder);
            }
        });
    }

    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_record,null,false);
        return new RecordHolder(view);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    protected void addItem(String filePath,String fileName){
        recordList.add(new Record(filePath,fileName));
        notifyDataSetChanged();
    }

    interface OnItemClickListener{
        void onClick(RecordHolder holder);
    }
}
