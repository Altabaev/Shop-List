package ru.altabaev.iliya.shoplist.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import ru.altabaev.iliya.shoplist.interfaces.ItemTouchHelperAdapter


class SimpleItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    private var currentView: View? = null

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
        if (viewHolder != null && viewHolder is ItemsRecyclerAdapter.ItemViewHolder) {
            currentView = viewHolder.itemView
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_SWIPE -> {currentView?.alpha = 0.8.toFloat()}
                ItemTouchHelper.ACTION_STATE_DRAG -> {currentView?.alpha = 0.9.toFloat()}
            }
        } else {
            currentView?.alpha = 1.toFloat()
            currentView = null
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder,
                        target: ViewHolder): Boolean {
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }


}