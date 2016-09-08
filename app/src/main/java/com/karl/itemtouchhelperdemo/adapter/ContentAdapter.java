package com.karl.itemtouchhelperdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.karl.itemtouchhelperdemo.R;

import java.util.List;

/**
 * Created by Karl on 2016/9/8.
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private Context context;
    private List<String> mList;
    private LayoutInflater inflater;

    public ContentAdapter(Context context, List<String> mList) {
        this.context = context;
        this.mList = mList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**
         * 这里inflater.inflate()方法一定要写三个参数的！不然不能实现item子项宽度match_parent！
         */
        View view = inflater.inflate(R.layout.item,parent,false);
        ContentViewHolder holder = new ContentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        holder.content.setText(mList.get(position));
        holder.content.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        holder.content.setGravity(Gravity.CENTER);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder{
        public  TextView content;
        public ContentViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.txt_item);
        }
    }
}
