package com.golfcart.ui.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewGenericAdapter<T, VM : ViewDataBinding?>(
    var items: ArrayList<T>,
    private val layoutId: Int,
    private val bindingInterface: RecyclerCallback<VM, T>
) : RecyclerView.Adapter<RecyclerViewGenericAdapter<T, VM>.RecyclerViewHolder>() {


    inner class RecyclerViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        var binding: VM? = DataBindingUtil.bind(view!!)
        fun bindData(model: T, pos: Int) {
            binding?.executePendingBindings()
            bindingInterface.bindData(binding!!, model, pos, itemView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return RecyclerViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.tag = position
        holder.bindData(item, position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    //    public int getItemViewType(int position) {
    //        return position;
    //    }
    fun upDateAdapter() {
        notifyDataSetChanged()
    }

    fun updateAdapterList(items: ArrayList<T>) {
        this.items = items
        notifyDataSetChanged()
    }


}