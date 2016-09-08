package com.karl.itemtouchhelperdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.karl.itemtouchhelperdemo.R;
import com.karl.itemtouchhelperdemo.adapter.ContentAdapter;
import com.karl.itemtouchhelperdemo.adapter.DragAdapter;
import com.karl.itemtouchhelperdemo.helper.ItemTouchHelperAdapter;
import com.karl.itemtouchhelperdemo.helper.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView drag,content;
    private DragAdapter dragAdapter;
    private ContentAdapter contentAdapter;
    private List<String> dragList = new ArrayList<>();
    private List<String> contentList = new ArrayList<>();
    private ItemTouchHelper helper;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();


    }
    private void initView(){
        drag = (RecyclerView) findViewById(R.id.rv_drag_layout);
        content = (RecyclerView) findViewById(R.id.rv_content);
    }
    private void initEvent(){
        initData();
        /**
         * 实例化helper，附加到RecyclerView上
         */
        drag.setLayoutManager(new GridLayoutManager(this,4));
        drag.setAdapter(dragAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(dragAdapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(drag);

        /**
         * 下面是内容展示RecyclerView，
         * 这里主要为了使ScrollView整体滑动以及item能够在ScrollView下实现match_parent
         * 因此并未绑定拖拽事件
         */
        linearLayoutManager = new LinearLayoutManager(this){
            /**
             * 返回false禁止RecyclerView竖直滚动
             * @return
             */
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        content.setLayoutManager(linearLayoutManager);
        content.setAdapter(contentAdapter);
        /**
         * 测试经过adapter以后dragList数据有没有发生变化
         */
        for (Object list:dragList ) {
            Log.d("TAG","拖拽排序前后的顺序"+list);
        }
    }
    private void initData(){
        for (int i = 0; i < 10; i++) {
            dragList.add("可拖拽#"+i);
            contentList.add("整体滚动标签"+i);
        }
        dragAdapter = new DragAdapter(this,dragList);
        contentAdapter = new ContentAdapter(this,contentList);
    }
}
