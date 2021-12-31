package com.vinade.todorooms

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.vinade.todorooms.adapter.CardAdapter

class SwipeToDeleteCallBack(var adapter: CardAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        var pos = viewHolder.adapterPosition
        adapter.deleteItem(pos)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20
        val background = ColorDrawable(adapter.getContext().getColor(R.color.indigo_dye))
        //val ic = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_delete)
        val dr = adapter.getContext().resources.getDrawable(R.drawable.ic_delete)
        val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        dr.setBounds(0,0, canvas.width, canvas.height)
        dr.draw(canvas)

        val icon = BitmapDrawable(bitmap)
        icon.setTint(Color.WHITE)

        //val icon = BitmapDrawable(adapter.getContext().resources, Bitmap.createScaledBitmap(bitmap, 50,50, true))


        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) /2
        val iconBottom = iconTop + icon.intrinsicHeight
        if(dX < 0 ){
            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            background.setBounds((itemView.right + dX - backgroundCornerOffset).toInt(), itemView.top, itemView.right, itemView.bottom)
        }else{
            background.setBounds(0,0,0,0)
        }
        background.draw(c)
        icon.draw(c)
    }

}