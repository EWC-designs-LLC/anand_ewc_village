package com.golfcart.ui.favourite.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView


class ItemMoveCallback(private val mAdapter: ItemTouchHelperContract) : ItemTouchHelper.Callback() {

    companion object{
        var mDraggable = false
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {

    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
//        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN // Enable up and down movement
        val dragFlags = if (mDraggable) UP or DOWN or START or END else 0
        val swipeFlags = 0 // Disable swipe gestures
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is DragDropAdapter.MyViewHolder) {
                val myViewHolder: DragDropAdapter.MyViewHolder? =
                    viewHolder as DragDropAdapter.MyViewHolder?
                mAdapter.onRowSelected(myViewHolder)
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is DragDropAdapter.MyViewHolder) {
            val myViewHolder: DragDropAdapter.MyViewHolder =
                viewHolder as DragDropAdapter.MyViewHolder
            mAdapter.onRowClear(myViewHolder)
        }
    }



    interface ItemTouchHelperContract {
        fun onRowMoved(fromPosition: Int, toPosition: Int)
        fun onRowSelected(myViewHolder: DragDropAdapter.MyViewHolder?)
        fun onRowClear(myViewHolder: DragDropAdapter.MyViewHolder?)
    }
}