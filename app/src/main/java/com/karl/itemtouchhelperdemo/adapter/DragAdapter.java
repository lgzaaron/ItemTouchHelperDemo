package com.karl.itemtouchhelperdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karl.itemtouchhelperdemo.R;
import com.karl.itemtouchhelperdemo.helper.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by Karl on 2016/9/8.
 */
public class DragAdapter extends RecyclerView.Adapter<DragAdapter.DragViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private List<String> mList;
    private LayoutInflater inflater;

    public DragAdapter(Context context, List<String> mList) {
        this.context = context;
        this.mList = mList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public DragViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item,null);
        DragViewHolder  holder = new DragViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DragViewHolder holder, int position) {
        holder.textView.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        /**
         * 拖拽后，切换位置，数据排序
         */
        Collections.swap(mList,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);

        return true;

    }

    @Override
    public void onItemDismiss(int position) {
        /**
         * 移除之前的数据
         */
        mList.remove(position);
        notifyItemRemoved(position);

    }

    public static class DragViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public DragViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.txt_item);
        }
    }
}
