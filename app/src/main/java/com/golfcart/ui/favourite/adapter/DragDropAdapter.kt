package com.golfcart.ui.favourite.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.golfcart.R
import com.golfcart.model.favorite.FavoriteResponse
import com.golfcart.ui.favourite.adapter.ItemMoveCallback.ItemTouchHelperContract
import com.golfcart.ui.village.VillageActivity
import com.golfcart.utils.Logs.VillageLogs
import com.golfcart.utils.view_utils.hide
import com.golfcart.utils.view_utils.loadGlide
import com.golfcart.utils.view_utils.show
import java.util.*
import kotlin.collections.ArrayList

class DragDropAdapter(
    var dataList: ArrayList<FavoriteResponse.Data>?,
    val activity: VillageActivity, val listener: DragDropInterface
) : RecyclerView.Adapter<DragDropAdapter.MyViewHolder>(), ItemTouchHelperContract {

    var showDelete = false
    var isEditEnable = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            mTitle.text = dataList!![position].location
            tvSubTitle.text = dataList!![position].address

            if (isEditEnable) {
                ivLines.show()
                ivDelete.show()
                mTitle.setOnLongClickListener {
                    listener.onTitleClick(dataList!![position], position)
                    return@setOnLongClickListener false
                }
                dragItem.setOnClickListener {
                }
            } else {
                ivLines.hide()
                ivDelete.hide()
                mTitle.setOnLongClickListener  {
                    false
                }
                dragItem.setOnClickListener {
                    listener.onClick(dataList!![position], position)
                }
            }

            loadGlide(dataList!![position].image, ivLogo, activity)

            ivDelete.setOnClickListener {
                listener.onDelete(dataList!![position], position)
            }

        }
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(dataList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(dataList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        VillageLogs.printLog(">>>>row", "onRowMoved")
    }

    override fun onRowSelected(myViewHolder: MyViewHolder?) {
        myViewHolder?.rowView?.setBackgroundColor(Color.TRANSPARENT)
        VillageLogs.printLog(">>>>row", "onRowSelected")
    }

    override fun onRowClear(myViewHolder: MyViewHolder?) {
        myViewHolder?.rowView?.setBackgroundColor(Color.TRANSPARENT)
        listener.onRowClear()
        VillageLogs.printLog(">>>>row", "onRowClear")
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dragItem: LinearLayout
        val ivDelete: AppCompatImageView
        val ivLines: AppCompatImageView
        val ivLogo: AppCompatImageView
        val mTitle: TextView
        val tvSubTitle: TextView
        var rowView: View

        init {
            rowView = itemView
            dragItem = itemView.findViewById(R.id.dragItem)
            ivDelete = itemView.findViewById(R.id.ivDelete)
            ivLines = itemView.findViewById(R.id.ivLines)
            ivLogo = itemView.findViewById(R.id.ivLogo)
            mTitle = itemView.findViewById(R.id.tvHeading)
            tvSubTitle = itemView.findViewById(R.id.tvAddress)
        }
    }

    interface DragDropInterface {
        fun onClick(model: FavoriteResponse.Data?, position: Int)
        fun onTitleClick(model: FavoriteResponse.Data?, position: Int)
        fun onDelete(model: FavoriteResponse.Data?, position: Int)
        fun onRowClear()
    }

    fun isEditEnable(isEditEnable: Boolean) {
        this.isEditEnable = isEditEnable
    }

    fun updateArraList(dataList: ArrayList<FavoriteResponse.Data>?){
        this.dataList = dataList
        notifyDataSetChanged()
    }

}