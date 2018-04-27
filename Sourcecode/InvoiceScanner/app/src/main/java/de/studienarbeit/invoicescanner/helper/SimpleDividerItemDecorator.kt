package de.studienarbeit.invoicescanner.helper

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.graphics.drawable.Drawable
import de.studienarbeit.invoicescanner.R
import android.content.Context


class SimpleDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable

    init {
        //mDivider = context.resources.getDrawable(R.drawable.line_divider, context.theme)
        mDivider = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider)).getDrawable(0)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight

            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}