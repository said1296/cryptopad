package com.prin.notes.viewhandlers
import android.app.Activity
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.prin.notes.R

class RecyclerViewSwiperCallback(private val activity: Activity, val callback: (Int) -> Unit) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("NOT NEEDED")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.LEFT -> callback(viewHolder.adapterPosition)
         //   ItemTouchHelper.LEFT -> filesViewModel.eventDeleteNote(viewHolder.adapterPosition)
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.8f
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
        super.onChildDraw(c, recyclerView, viewHolder, dX / 2, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView

        val path = Path()
        val radiusBorder = 15f
        val radiusBorderPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            radiusBorder,
            activity.resources.displayMetrics
        )
        val point1 = Pair(itemView.right.toFloat() - radiusBorderPx, itemView.top.toFloat())
        val point2 = Pair(itemView.right + dX / 2 - radiusBorderPx, itemView.top.toFloat())
        val point3 = Pair(itemView.right + dX / 2, itemView.top.toFloat() + radiusBorderPx)
        val point4 = Pair(itemView.right + dX / 2, itemView.bottom.toFloat() - radiusBorderPx)
        val point5 = Pair(itemView.right + dX / 2 - radiusBorderPx, itemView.bottom.toFloat())
        val point6 = Pair(itemView.right.toFloat() - radiusBorderPx, itemView.bottom.toFloat())
        val point7 = Pair(itemView.right.toFloat(), itemView.bottom - radiusBorderPx)
        val point8 = Pair(itemView.right.toFloat(), itemView.top + radiusBorderPx)

        path.moveTo(point1.first, point1.second)
        path.lineTo(point2.first, point2.second)
        path.arcTo(
            RectF(
                point2.first - radiusBorderPx,
                point2.second,
                point3.first,
                point3.second + radiusBorderPx
            ), 270f, 90f
        )
        path.lineTo(point4.first, point4.second)
        path.arcTo(
            RectF(
                point4.first - 2 * radiusBorderPx,
                point4.second - radiusBorderPx,
                point5.first + radiusBorderPx,
                point5.second
            ), 0f, 90f
        )
        path.lineTo(point6.first, point6.second)
        path.arcTo(
            RectF(
                point6.first - radiusBorderPx,
                point7.second - radiusBorderPx,
                point7.first,
                point7.second + radiusBorderPx
            ), 90f, -90f
        )
        path.lineTo(point8.first, point8.second)
        path.arcTo(
            RectF(
                point1.first - radiusBorderPx,
                point1.second,
                point8.first,
                point8.second + radiusBorderPx
            ), 0f, -90f
        )

        val paint = Paint()
        paint.color = ContextCompat.getColor(activity, R.color.error)

        val iconSizeDp = 30f
        val iconSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            iconSizeDp,
            activity.resources.displayMetrics
        )
        val deleteIcon: Drawable? =
            ContextCompat.getDrawable(activity, R.drawable.ic_delete_swipe)
        val deleteIconBitmap = Bitmap.createBitmap(
            iconSizePx.toInt(),
            iconSizePx.toInt(),
            Bitmap.Config.ARGB_8888
        )
        val iconDeleteCanvas = Canvas(deleteIconBitmap)
        deleteIcon?.setBounds(0, 0, iconDeleteCanvas.width, iconDeleteCanvas.height)
        deleteIcon?.draw(iconDeleteCanvas)

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            c.drawPath(path, paint)
            paint.color = Color.WHITE
        }
        if (dX < (iconDeleteCanvas.width * -3)) {
            c.drawBitmap(
                deleteIconBitmap,
                itemView.right - iconSizePx - iconDeleteCanvas.width,
                itemView.top.toFloat() + (itemView.bottom - itemView.top - iconDeleteCanvas.height) / 2,
                paint
            )
        }
        if (dX == 0.toFloat()) {
            c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }
    }
}