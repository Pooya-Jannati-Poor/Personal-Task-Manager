package ir.pooyadev.presentation.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TaskLastItemMarginDecoration (
    private val margin: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position == itemCount - 1) {
            val lastItemMarginPx = (26 * parent.context.resources.displayMetrics.density).toInt()
            outRect.bottom = lastItemMarginPx
        } else {
            outRect.bottom = margin
        }
    }
}