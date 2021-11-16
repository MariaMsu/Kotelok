package com.designdrivendevelopment.kotelok

import android.content.res.Resources
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class MarginItemDecoration(
    private val marginVertical: Int,
    private val marginHorizontal: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = dpToPx(marginVertical).roundToInt()
            }
            left = dpToPx(marginHorizontal).roundToInt()
            right = dpToPx(marginHorizontal).roundToInt()
            bottom = dpToPx(marginVertical).roundToInt()
        }
    }
}

fun dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        Resources.getSystem().displayMetrics
    )
}
