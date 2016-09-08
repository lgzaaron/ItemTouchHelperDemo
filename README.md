# ItemTouchHelperDemo
## 前言
需求：

1、ScrollView嵌套下RecyclerView的GridView布局实现拖拽功能

2、禁用RecyclerView的LinearLayout竖直布局滚动效果，实现ScrollView的整体滚动

效果图：

![RecyclerViewInScrollView.gif](http://upload-images.jianshu.io/upload_images/2288127-0535496cc9f590be.gif?imageMogr2/auto-orient/strip)



正式开始之前先简单说一下：

关于拖拽效果网上实现的方案特别多，但是大都是在常用根布局下实现的，一旦碰到根布局是ScrollView，就会发生冲突，这时候的解决方法往往是
进行事件分发，但是这对新手来说显然理解无能。而且就算解决了这个，往往还要解决ScrollView布局下[列表不能完全显示问题](http://coderfan.com/2016/09/03/ListViewInScrollView/)，以及完全显示了但是[列表子项item宽高都变成了自适应](http://coderfan.com/2016/09/07/list-item-in-scroll-not-match-parent/)，自己设置的match_parent或者指定宽高不生效问题，这两种当然也有解决方案，点击蓝色部分就可以查看了。

就在卡功能卡到不要不要的时候，朋友给我提供了一种很好很强大的解决方案：RecyclerView的ItemTouchHelper类

> ItemTouchHelper是一个强大的工具，它处理好了关于在RecyclerView上添加拖动排序与滑动删除的所有事情。它是[RecyclerView.ItemDecoration](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ItemDecoration.html)的子类，也就是说它可以轻易的添加到几乎所有的LayoutManager和Adapter中。它还可以和现有的item动画一起工作，提供受类型限制的拖放动画等等，

下面来看一下具体的实现过程，因为demo比较简单，大家也可以直接去看源码：[Gtihub-ItemTouchHelperDemo](https://github.com/fanKarl/ItemTouchHelperDemo)

## 实现过程
1、我们需要用到一个很重要的类[ItemTouchHelper.Callback](https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.Callback.html)，它能帮我们监听ItemTouchHelper的事件
先看一下源码：
```
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter adapter;

    public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 返回true，开启长按拖拽
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 返回true，开启swipe事件
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * getMovementFlags、onMove、onSwiped是必须要实现的三个方法
     *
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        /**
         * ItemTouchHelper支持事件方向判断，但是必须重写当前getMovementFlags来指定支持的方向
         * 这里我同时设置了dragFlag为上下左右四个方向，swipeFlag的左右方向
         * 最后通过makeMovementFlags（dragFlag，swipe）创建方向的Flag，
         * 因为我们目前只需要实现拖拽，所以我并未创建swipe的flag
         */
        int dragFlag = ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        int swipe = ItemTouchHelper.START|ItemTouchHelper.END;
        return makeMovementFlags(dragFlag,0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        /**
         * 回调
         */
        adapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        /**
         * 回调
         */
        adapter.onItemDismiss(viewHolder.getAdapterPosition());

    }
}

```
这里面有三个很关键的方法需要重写：
  
> getMovementFlags(RecyclerView, ViewHolder)
> onMove(RecyclerView, ViewHolder, ViewHolder)
> onSwiped(ViewHolder, int)

另外需要两个辅助方法

>isLongPressDragEnabled()
>isItemViewSwipeEnabled()

isLongPressDragEnabled()方法返回true开启拖拽效果，isItemViewSwipeEnabled()方法返回true开启滑动效果；

当然也可以通过starDragListener来实现，这里不在多说，想了解的可以在参考文献里找到。

2、这里我们需要定义一个接口
```
public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition,int toPosition);
    void onItemDismiss(int position);
}
```

3、然后就可以自定义adapter了

自定义DragAdapter需要实现：

```
extends RecyclerView.Adapter<DragAdapter.DragViewHolder> implements ItemTouchHelperAdapter 
```
根据提示创建需要重写的方法，这里摘出来与ItemTouchHelperAdapter有关的方法如下：
```
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
```
两个方法一个是进行数据排序管理，一个进行移除item管理

4、最后在MainActivity里引用就可以了

```
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
```
这里粘贴核心代码，我用了两个RecyclerView，一个是drag实现拖拽效果，通过ItemTouchHelper的对象绑定Callback和drag列表一个是content，为了让ScrollView整体滚动起来，在这种情况下进行拖拽测试，同时解决了ScrollView嵌套下RecyclerView线性布局下子项不能指定宽高的问题（文章开头有链接）。为了方便区分这两个RecyclerView我使用了不同的自定义Adapter。

以上就实现了需求的效果。通过ItemTouchHelper还能实现滑动删除等功能，我暂时没有加上，想了解的小伙伴可以看参考文章，里面有详细描述。


## 参考文献
[RecyclerView的拖动和滑动 第一部分：基本的ItemTouchHelper示例](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0630/3123.html)
[Android-RecyclerView拖拽排序和删除](http://www.jianshu.com/p/fd67184f1aa2/comments/4070281#comment-4070281)
