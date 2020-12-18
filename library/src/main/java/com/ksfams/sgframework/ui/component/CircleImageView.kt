package com.ksfams.sgframework.ui.component

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.ksfams.sgframework.R
import com.ksfams.sgframework.utils.getImageFromUri
import kotlin.math.min

/**
 *
 * Circle ImageView
 *
 * Create Date  12/18/20
 * @version 1.00    12/18/20
 * @since   1.00
 * @see
 * @author  강성훈(ssogaree@gmail.com)
 * Revision History
 * who      when            what
 * 강성훈      12/18/20     신규 개발.
 */

class CircleImageView @JvmOverloads constructor(context: Context,
                                                attrs: AttributeSet? = null,
                                                defStyle: Int = 0) : AppCompatImageView(context, attrs, defStyle) {

    private val DEFAULT_SHADOW_RADIUS = 10f

    private var borderWidth: Float = 0f
    private var borderColor: Int = Color.WHITE

    private var isShadow: Boolean = false
    private var shadowColor: Int = Color.BLACK
    private var shadowRadius: Float = DEFAULT_SHADOW_RADIUS

    private var mBitmap: Bitmap? = null

    private val paint = Paint()
    private var paintBorder = Paint()
    private val shadowPaint = Paint()

    private var bitmapShader: BitmapShader? = null
    private var borderRadius: Float = 0f

    private var drawRadius: Float = 0f
    private var bitmapWidth = 0
    private var bitmapHeight = 0


    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, 0, 0)
            try {
                borderWidth = typedArray.getDimension(R.styleable.CircleImageView_border_width, 0f)
                borderColor = typedArray.getColor(R.styleable.CircleImageView_border_color, Color.WHITE)

                isShadow = typedArray.getBoolean(R.styleable.CircleImageView_add_shadow, false)
                shadowColor = typedArray.getColor(R.styleable.CircleImageView_shadow_color, Color.BLACK)
                shadowRadius = typedArray.getFloat(R.styleable.CircleImageView_shadow_radius, DEFAULT_SHADOW_RADIUS)
            }
            finally {
                typedArray.recycle()
            }
        }
    }


    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        getDrawableToBitmap(drawable)
        setup()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        getDrawableToBitmap(drawable)
        setup()
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        mBitmap = bm
        setup()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        mBitmap = context.getImageFromUri(uri)
        setup()
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), borderRadius, paintBorder)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), drawRadius, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }


    private fun setup() {
        if (mBitmap == null || width == 0 || height == 0) {
            return
        }

        paint.isAntiAlias = true
        paintBorder.isAntiAlias = true
        paintBorder.color = borderColor
        paintBorder.style = Paint.Style.STROKE
        paintBorder.strokeWidth = borderWidth
        shadowPaint.style = Paint.Style.STROKE
        shadowPaint.strokeWidth = borderWidth
        shadowPaint.color = Color.LTGRAY

        bitmapWidth = mBitmap!!.width
        bitmapHeight = mBitmap!!.height
        bitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = bitmapShader
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        borderRadius = min((width - borderWidth) / 2, (height - borderWidth) / 2)
        drawRadius = min((width - 2 * borderWidth) / 2,(height - borderWidth * 2) / 2)

        if (isShadow) {
            borderRadius -= shadowRadius
            drawRadius -= shadowRadius
            paintBorder.setShadowLayer(shadowRadius, 0f, 3f, shadowColor)
        }

        scaleImage()
        invalidate()
    }

    private fun getDrawableToBitmap(drawable: Drawable?) {
        //return if drawable is null that means it doen't have a bitmap
        if (drawable == null) {
            return
        }
        val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)
        drawable.draw(canvas)
        mBitmap = bitmap
    }

    private fun scaleImage() {
        var dx = 0f
        var dy = 0f
        val scale: Float
        matrix.set(null)

        /* You can also get view width  by substracting both sides padding
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getHeight() - getPaddingLeft() - getPaddingRight();
        */
        if (bitmapWidth * height > bitmapHeight * width) {
            scale = height.toFloat() / bitmapHeight.toFloat()
            dx = (width - bitmapWidth * scale) * 0.5f
        } else {
            scale = width.toFloat() / bitmapWidth.toFloat()
            dy = (height - bitmapHeight * scale) * 0.5f
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate((dx + 0.5f).toInt() + borderWidth, (dy + 0.5f).toInt() + borderWidth)
        bitmapShader!!.setLocalMatrix(matrix)
    }
}