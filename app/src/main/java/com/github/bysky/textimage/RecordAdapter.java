package com.github.bysky.textimage;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by asus on 2017/12/29.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder> {

    class RecordHolder extends RecyclerView.ViewHolder{
        public RecordHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, int position) {

    }

    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
