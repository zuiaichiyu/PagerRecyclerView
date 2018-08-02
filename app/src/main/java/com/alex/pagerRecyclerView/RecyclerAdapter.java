package com.alex.pagerRecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {


    private List<String> list = new ArrayList<>();
    private int pageWidth = 0;

    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    private RelativeLayout.LayoutParams mItemParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT);

    public void setList(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (i == 0) {
            mItemParams.setMargins(50, 0, 0, 0);
        } else if (i == list.size() - 1) {
            mItemParams.setMargins(0, 0, 50, 0);
        } else {
            mItemParams.setMargins(0, 0, 0, 0);
        }

        myViewHolder.itemView.setLayoutParams(mItemParams);
        myViewHolder.itemView.getLayoutParams().width = pageWidth;
        myViewHolder.name.setText(list.get(i));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
        }
    }
}
