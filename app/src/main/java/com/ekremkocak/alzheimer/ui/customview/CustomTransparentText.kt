package com.ekremkocak.alzheimer.ui.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TransparentTextTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var mMaskBitmap: Bitmap? = null
    private var mMaskCanvas: Canvas? = null
    private val mPaint = Paint()
    private var mBackground: Drawable? = null
    private var mBackgroundBitmap: Bitmap? = null
    private var mBackgroundCanvas: Canvas? = null

    init {
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        super.setTextColor(Color.BLACK)
        super.setBackground(ColorDrawable(Color.TRANSPARENT))
    }

    override fun setBackground(bg: Drawable?) {
        if (mBackground === bg) {
            return
        }

        mBackground = bg
        val w = width
        val h = height
        if (mBackground != null && w != 0 && h != 0) {
            mBackground!!.setBounds(0, 0, w, h)
        }
    }

    override fun setBackgroundColor(color: Int) {
        setBackground(ColorDrawable(color))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0 || h == 0) {
            freeBitmaps()
            return
        }

        createBitmaps(w, h)
        if (mBackground != null) {
            mBackground!!.setBounds(0, 0, w, h)
        }
    }

    private fun createBitmaps(w: Int, h: Int) {
        mBackgroundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mBackgroundCanvas = Canvas(mBackgroundBitmap!!)
        mMaskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        mMaskCanvas = Canvas(mMaskBitmap!!)
    }

    private fun freeBitmaps() {
        mBackgroundBitmap = null
        mBackgroundCanvas = null
        mMaskBitmap = null
        mMaskCanvas = null
    }

    override fun onDraw(canvas: Canvas) {
        if (isNothingToDraw()) {
            return
        }
        drawMask()
        drawBackground()
        canvas.drawBitmap(mBackgroundBitmap!!, 0f, 0f, null)
    }

    private fun isNothingToDraw(): Boolean {
        return mBackground == null || width == 0 || height == 0
    }

    @SuppressLint("WrongCall")
    private fun drawMask() {
        clear(mMaskCanvas!!)
        super.onDraw(mMaskCanvas!!)
    }

    private fun drawBackground() {
        clear(mBackgroundCanvas!!)
        mBackground!!.draw(mBackgroundCanvas!!)
        mBackgroundCanvas!!.drawBitmap(mMaskBitmap!!, 0f, 0f, mPaint)
    }

    private fun clear(canvas: Canvas) {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR)
    }
}