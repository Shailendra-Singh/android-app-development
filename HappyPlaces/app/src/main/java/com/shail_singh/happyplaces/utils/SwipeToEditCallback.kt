package com.shail_singh.happyplaces.utils

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

//private val backgroundColor = Color.parseColor(com.google.android.material.R.attr.colorSecondary.toString())
// https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6

abstract class SwipeToEditCallback(private var context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val editIcon =
        ContextCompat.getDrawable(context, com.shail_singh.happyplaces.R.drawable.ic_edit)
    private val intrinsicWidth = editIcon!!.intrinsicWidth
    private val intrinsicHeight = editIcon!!.intrinsicHeight
    private val background = ColorDrawable()
    private var backgroundColor = Color.parseColor("#FFFFFF")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }


    override fun getMovementFlags(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
    ): Int {
        /**
         * To disable "swipe" for specific item return 0 here.
         * For example:
         * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
         * if (viewHolder?.adapterPosition == 0) return 0
         */
        if (viewHolder.adapterPosition == 10) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        // get colorSecondary for background color
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        backgroundColor = typedValue.data

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.left + dX,
                itemView.top.toFloat(),
                itemView.left.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the color Secondary edit background
        background.color = backgroundColor

        background.setBounds(
            itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom
        )

        background.draw(c)

        val movingX = (dX - itemView.left) / 2
        // Calculate position of edit icon
        val staticY = (itemHeight - intrinsicHeight)
        val editIconTop = itemView.top + staticY / 2
        val editIconLeft = movingX.toInt() - intrinsicWidth / 2
        val editIconRight = movingX.toInt() + intrinsicWidth / 2
        val editIconBottom = itemView.bottom - staticY / 2

        // Draw the edit icon
        editIcon!!.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
        editIcon.draw(c)

        super.onChildDraw(
            c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
        )
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}