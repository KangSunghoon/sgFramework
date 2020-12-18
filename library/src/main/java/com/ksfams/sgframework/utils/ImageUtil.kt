package com.ksfams.sgframework.utils

import android.Manifest
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Base64
import android.view.View
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.ksfams.sgframework.models.enums.ImageOrientation
import com.ksfams.sgframework.models.enums.ResizeMode
import com.ksfams.sgframework.modules.reference.ApplicationReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

/**
 *
 * Image Bitmap 관련 유틸
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

private const val MAX_SIZE = 1024

/**
 * String filePath로 부터
 * bitmap 파일을 리턴
 *
 * @receiver String
 * @return Bitmap?
 */
fun String.getBitmap(): Bitmap? = if (this.isEmpty()) null else BitmapFactory.decodeFile(this)

/**
 * get Bitmap from InputStream
 *
 * @receiver InputStream
 * @return Bitmap object
 */
fun InputStream.getBitmap(): Bitmap? = use {
    BitmapFactory.decodeStream(it)
}

/**
 * Drawable을 Bitmap으로 변환
 *
 * @receiver Drawable
 * @return Bitmap
 */
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap
    }

    var width = intrinsicWidth
    width = if (width > 0) width else 1
    var height = intrinsicHeight
    height = if (height > 0) height else 1

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)

    return bitmap
}

/**
 * Bitmap to drawable.
 *
 * @receiver Drawable
 * @return drawable
 */
@RequiresApi(Build.VERSION_CODES.DONUT)
fun Bitmap.toDrawable(): Drawable {
    return BitmapDrawable(ApplicationReference.getApp().resources, this)
}

/**
 * View to bitmap.
 *
 * @receiver View
 * @return bitmap
 */
fun View.toBitmap(): Bitmap {
    val ret = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(ret)
    val bgDrawable = background
    if (bgDrawable != null) {
        bgDrawable.draw(canvas)
    } else {
        canvas.drawColor(Color.WHITE)
    }
    draw(canvas)
    return ret
}

/**
 * Return bitmap.
 *
 * @receiver File
 * @param maxWidth  The maximum width.
 * @param maxHeight The maximum height.
 * @return bitmap
 */
fun File.getBitmap(maxWidth: Int? = null, maxHeight: Int? = null): Bitmap {
    return if (maxWidth == null && maxHeight == null) {
        BitmapFactory.decodeFile(absolutePath)
    } else {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(absolutePath, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth!!, maxHeight!!)
        options.inJustDecodeBounds = false
        BitmapFactory.decodeFile(absolutePath, options)
    }
}

/**
 * Return bitmap.
 *
 * @receiver ByteArray
 * @param offset    The offset.
 * @param maxWidth  The maximum width.
 * @param maxHeight The maximum height.
 * @return bitmap
 */
fun ByteArray.getBitmap(offset: Int,
                        maxWidth: Int? = null,
                        maxHeight: Int? = null): Bitmap? {
    if (isEmpty()) return null

    return if (maxWidth == null && maxHeight == null) {
        BitmapFactory.decodeByteArray(this, offset, size)
    } else {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(this, offset, size, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth!!, maxHeight!!)
        options.inJustDecodeBounds = false
        BitmapFactory.decodeByteArray(this, offset, size, options)
    }
}

/**
 * Return bitmap.
 *
 * @receiver Context
 * @param resId     The resource id.
 * @param maxWidth  The maximum width.
 * @param maxHeight The maximum height.
 * @return bitmap
 */
fun Context.getBitmap(@DrawableRes resId: Int,
                      maxWidth: Int? = null,
                      maxHeight: Int? = null): Bitmap? {
    return if (maxWidth == null && maxHeight == null) {
        val drawable: Drawable? = ContextCompat.getDrawable(this, resId)
        drawable?.let {
            val canvas = Canvas()
            val bitmap = Bitmap.createBitmap(
                    it.intrinsicWidth,
                    it.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
            )
            canvas.setBitmap(bitmap)
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            it.draw(canvas)
            bitmap
        }
        null
    } else {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, resId, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth!!, maxHeight!!)
        options.inJustDecodeBounds = false
        BitmapFactory.decodeResource(resources, resId, options)
    }
}

/**
 * Return bitmap from file descriptor
 *
 * @receiver FileDescriptor
 * @param maxWidth  The maximum width.
 * @param maxHeight The maximum height.
 * @return bitmap
 */
fun FileDescriptor.getBitmap(maxWidth: Int? = null, maxHeight: Int? = null): Bitmap {
    return if (maxWidth == null && maxHeight == null) {
        BitmapFactory.decodeFileDescriptor(this)
    } else {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFileDescriptor(this, null, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth!!, maxHeight!!)
        options.inJustDecodeBounds = false
        BitmapFactory.decodeFileDescriptor(this, null, options)
    }
}

/**
 * Return the scaled bitmap.
 *
 * @receiver Bitmap
 * @param newWidth  The new width.
 * @param newHeight The new height.
 * @param recycle   True to recycle the source of bitmap, false otherwise.
 * @return the scaled bitmap
 */
fun Bitmap.scale(newWidth: Int, newHeight: Int, recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val ret = Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the scaled bitmap
 *
 * @receiver Bitmap
 * @param scaleWidth  The scale of width.
 * @param scaleHeight The scale of height.
 * @param recycle     True to recycle the source of bitmap, false otherwise.
 * @return the scaled bitmap
 */
fun Bitmap.scale(scaleWidth: Float, scaleHeight: Float, recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val matrix = Matrix()
    matrix.setScale(scaleWidth, scaleHeight)
    val ret = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the clipped bitmap.
 *
 * @receiver Bitmap
 * @param x       The x coordinate of the first pixel.
 * @param y       The y coordinate of the first pixel.
 * @param width   The width.
 * @param height  The height.
 * @param recycle True to recycle the source of bitmap, false otherwise.
 * @return the clipped bitmap
 */
fun Bitmap.clip(x: Int, y: Int, width: Int, height: Int, recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val ret = Bitmap.createBitmap(this, x, y, width, height)
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the rotated bitmap.
 *
 * @receiver Bitmap
 * @param degrees The number of degrees.
 * @param px      The x coordinate of the pivot point.
 * @param py      The y coordinate of the pivot point.
 * @param recycle True to recycle the source of bitmap, false otherwise.
 * @return the rotated bitmap
 */
fun Bitmap.rotate(degrees: Int, px: Float, py: Float, recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    if (degrees == 0) return this
    val matrix = Matrix()
    matrix.setRotate(degrees.toFloat(), px, py)
    val ret = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the rotated degree.
 *
 * @param filePath The path of file.
 * @return the rotated degree
 */
@RequiresApi(Build.VERSION_CODES.ECLAIR)
fun getRotateDegree(filePath: String): Int {
    return try {
        val exifInterface = ExifInterface(filePath)
        val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    } catch (e: IOException) {
        LogUtil.e(e)
        -1
    }
}

/**
 * Return the round bitmap.
 *
 * @receiver Bitmap
 * @param recycle     True to recycle the source of bitmap, false otherwise.
 * @param borderSize  The size of border.
 * @param borderColor The color of border.
 * @return the round bitmap
 */
fun Bitmap.toRound(borderSize: Int = 0,
                   @ColorInt borderColor: Int = 0,
                   recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val width = width
    val height = height
    val size = width.coerceAtMost(height)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val ret = Bitmap.createBitmap(width, height, config)
    val center = size / 2f
    val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
    rectF.inset((width - size) / 2f, (height - size) / 2f)
    val matrix = Matrix()
    matrix.setTranslate(rectF.left, rectF.top)
    matrix.preScale(size.toFloat() / width, size.toFloat() / height)
    val shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    shader.setLocalMatrix(matrix)
    paint.shader = shader
    val canvas = Canvas(ret)
    canvas.drawRoundRect(rectF, center, center, paint)
    if (borderSize > 0) {
        paint.shader = null
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize.toFloat()
        val radius = center - borderSize / 2f
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
    }
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the round corner bitmap.
 *
 * @receiver Bitmap
 * @param radius      The radius of corner.
 * @param borderSize  The size of border.
 * @param borderColor The color of border.
 * @param recycle     True to recycle the source of bitmap, false otherwise.
 * @return the round corner bitmap
 */
fun Bitmap.toRoundCorner(radius: Float,
                         borderSize: Int = 0,
                         @ColorInt borderColor: Int = 0,
                         recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val width = width
    val height = height
    val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
    val ret = Bitmap.createBitmap(width, height, config)
    val shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    paint.shader = shader
    val canvas = Canvas(ret)
    val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
    val halfBorderSize = borderSize / 2f
    rectF.inset(halfBorderSize, halfBorderSize)
    canvas.drawRoundRect(rectF, radius, radius, paint)
    if (borderSize > 0) {
        paint.shader = null
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize.toFloat()
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawRoundRect(rectF, radius, radius, paint)
    }
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the bitmap with border.
 *
 * @receiver Bitmap
 * @param borderSize   The size of border.
 * @param color        The color of border.
 * @param isCircle     True to draw circle, false to draw corner.
 * @param cornerRadius The radius of corner.
 * @param recycle      True to recycle the source of bitmap, false otherwise.
 * @return the bitmap with border
 */
fun Bitmap.addBorder(@IntRange(from = 1) borderSize: Int,
                     @ColorInt color: Int,
                     isCircle: Boolean,
                     cornerRadius: Float = 0f,
                     recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val ret = if (recycle) this else copy(config, true)
    val width = ret.width
    val height = ret.height
    val canvas = Canvas(ret)
    val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = color
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = borderSize.toFloat()
    if (isCircle) {
        val radius = Math.min(width, height) / 2f - borderSize / 2f
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
    } else {
        val halfBorderSize = borderSize shr 1
        val rectF = RectF(
                halfBorderSize.toFloat(), halfBorderSize.toFloat(),
                (width - halfBorderSize).toFloat(), (height - halfBorderSize).toFloat()
        )
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
    }
    return ret
}

/**
 * Return the bitmap with reflection.
 *
 * @receiver Bitmap
 * @param reflectionHeight The height of reflection.
 * @param recycle          True to recycle the source of bitmap, false otherwise.
 * @return the bitmap with reflection
 */
fun Bitmap.addReflection(reflectionHeight: Int, recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val REFLECTION_GAP = 0
    val srcWidth = width
    val srcHeight = height
    val matrix = Matrix()
    matrix.preScale(1f, -1f)
    val reflectionBitmap = Bitmap.createBitmap(
            this, 0, srcHeight - reflectionHeight,
            srcWidth, reflectionHeight, matrix, false
    )
    val ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, config)
    val canvas = Canvas(ret)
    canvas.drawBitmap(this, 0f, 0f, null)
    canvas.drawBitmap(reflectionBitmap, 0f, srcHeight + REFLECTION_GAP.toFloat(), null)
    val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
    val shader = LinearGradient(
            0f, srcHeight.toFloat(),
            0f, ret.height + REFLECTION_GAP.toFloat(),
            0x70FFFFFF,
            0x00FFFFFF,
            Shader.TileMode.MIRROR
    )
    paint.shader = shader
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    canvas.drawRect(
            0f,
            srcHeight + REFLECTION_GAP.toFloat(),
            srcWidth.toFloat(),
            ret.height.toFloat(),
            paint
    )
    if (!reflectionBitmap.isRecycled) reflectionBitmap.recycle()
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the bitmap with text watermarking.
 *
 * @receiver Bitmap
 * @param content  The content of text.
 * @param textSize The size of text.
 * @param color    The color of text.
 * @param x        The x coordinate of the first pixel.
 * @param y        The y coordinate of the first pixel.
 * @param recycle  True to recycle the source of bitmap, false otherwise.
 * @return the bitmap with text watermarking
 */
fun Bitmap.addTextWatermark(content: String, textSize: Float,
                            @ColorInt color: Int,
                            x: Float, y: Float,
                            recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val ret = copy(config, true)
    val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
    val canvas = Canvas(ret)
    paint.color = color
    paint.textSize = textSize
    val bounds = Rect()
    paint.getTextBounds(content, 0, content.length, bounds)
    canvas.drawText(content, x, y + textSize, paint)
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the bitmap with image watermarking.
 *
 * @receiver Bitmap
 * @param watermark The image watermarking.
 * @param x         The x coordinate of the first pixel.
 * @param y         The y coordinate of the first pixel.
 * @param alpha     The alpha of watermark.
 * @param recycle   True to recycle the source of bitmap, false otherwise.
 * @return the bitmap with image watermarking
 */
fun Bitmap.addImageWatermark(watermark: Bitmap,
                             x: Int, y: Int, alpha: Int,
                             recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val ret = copy(config, true)
    if (!watermark.isEmpty()) {
        val paint =
                Paint(Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas(ret)
        paint.alpha = alpha
        canvas.drawBitmap(watermark, x.toFloat(), y.toFloat(), paint)
    }
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the alpha bitmap.
 *
 * @receiver Bitmap
 * @param recycle True to recycle the source of bitmap, false otherwise.
 * @return the alpha bitmap
 */
fun Bitmap.toAlpha(recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val ret = extractAlpha()
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the gray bitmap.
 *
 * @receiver Bitmap
 * @param recycle True to recycle the source of bitmap, false otherwise.
 * @return the gray bitmap
 */
fun Bitmap.toGray(recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val ret = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(ret)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
    paint.colorFilter = colorMatrixColorFilter
    canvas.drawBitmap(this, 0f, 0f, paint)
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the blur bitmap fast.
 *
 * zoom out, blur, zoom in
 *
 * @receiver Bitmap
 * @param scale   The scale(0...1).
 * @param radius  The radius(0...25).
 * @param recycle True to recycle the source of bitmap, false otherwise.
 * @return the blur bitmap
 */
fun Bitmap.fastBlur(@FloatRange(from = 0.0, to = 1.0, fromInclusive = false) scale: Float,
                    @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float,
                    recycle: Boolean = false): Bitmap? {
    if (isEmpty()) return null
    val width = width
    val height = height
    val matrix = Matrix()
    matrix.setScale(scale, scale)
    var scaleBitmap =
            Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    val paint =
            Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    val canvas = Canvas()
    val filter = PorterDuffColorFilter(
            Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP
    )
    paint.colorFilter = filter
    canvas.scale(scale, scale)
    canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)
    scaleBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        scaleBitmap.renderScriptBlur(radius, recycle)
    } else {
        scaleBitmap.stackBlur(radius.toInt(), recycle)
    }
    if (scale == 1f) {
        if (recycle && !isRecycled) recycle()
        return scaleBitmap
    }
    val ret = Bitmap.createScaledBitmap(scaleBitmap, width, height, true)
    if (!scaleBitmap.isRecycled) scaleBitmap.recycle()
    if (recycle && !isRecycled) recycle()
    return ret
}

/**
 * Return the blur bitmap using render script.
 *
 * @receiver Bitmap
 * @param radius  The radius(0...25).
 * @param recycle True to recycle the source of bitmap, false otherwise.
 * @return the blur bitmap
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun Bitmap.renderScriptBlur(@FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float,
                            recycle: Boolean = false): Bitmap {
    var rs: RenderScript? = null
    val ret = if (recycle) this else copy(config, true)
    try {
        rs = RenderScript.create(ApplicationReference.getApp())
        rs.messageHandler = RenderScript.RSMessageHandler()
        val input = Allocation.createFromBitmap(
                rs,
                ret,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
        )
        val output = Allocation.createTyped(rs, input.type)
        val blurScript =
                ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        blurScript.setInput(input)
        blurScript.setRadius(radius)
        blurScript.forEach(output)
        output.copyTo(ret)
    } finally {
        rs?.destroy()
    }
    return ret
}

/**
 * Return the blur bitmap using stack.
 *
 * @receiver Bitmap
 * @param radius  The radius(0...25).
 * @param recycle True to recycle the source of bitmap, false otherwise.
 * @return the blur bitmap
 */
fun Bitmap.stackBlur(radius: Int,
                     recycle: Boolean = false): Bitmap {
    var radiusVar = radius
    val ret = if (recycle) this else copy(config, true)
    if (radiusVar < 1) {
        radiusVar = 1
    }
    val w = ret.width
    val h = ret.height
    val pix = IntArray(w * h)
    ret.getPixels(pix, 0, w, 0, 0, w, h)
    val wm = w - 1
    val hm = h - 1
    val wh = w * h
    val div = radiusVar + radiusVar + 1
    val r = IntArray(wh)
    val g = IntArray(wh)
    val b = IntArray(wh)
    var rsum: Int
    var gsum: Int
    var bsum: Int
    var x: Int
    var y: Int
    var i: Int
    var p: Int
    var yp: Int
    var yi: Int
    var yw: Int
    val vmin = IntArray(Math.max(w, h))
    var divsum = div + 1 shr 1
    divsum *= divsum
    val dv = IntArray(256 * divsum)
    i = 0
    while (i < 256 * divsum) {
        dv[i] = i / divsum
        i++
    }
    yi = 0
    yw = yi
    val stack = Array(div) { IntArray(3) }
    var stackpointer: Int
    var stackstart: Int
    var sir: IntArray
    var rbs: Int
    val r1 = radiusVar + 1
    var routsum: Int
    var goutsum: Int
    var boutsum: Int
    var rinsum: Int
    var ginsum: Int
    var binsum: Int
    y = 0
    while (y < h) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        i = -radiusVar
        while (i <= radiusVar) {
            p = pix[yi + Math.min(wm, Math.max(i, 0))]
            sir = stack[i + radiusVar]
            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff
            rbs = r1 - Math.abs(i)
            rsum += sir[0] * rbs
            gsum += sir[1] * rbs
            bsum += sir[2] * rbs
            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }
            i++
        }
        stackpointer = radiusVar
        x = 0
        while (x < w) {
            r[yi] = dv[rsum]
            g[yi] = dv[gsum]
            b[yi] = dv[bsum]
            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum
            stackstart = stackpointer - radiusVar + div
            sir = stack[stackstart % div]
            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]
            if (y == 0) {
                vmin[x] = Math.min(x + radiusVar + 1, wm)
            }
            p = pix[yw + vmin[x]]
            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff
            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]
            rsum += rinsum
            gsum += ginsum
            bsum += binsum
            stackpointer = (stackpointer + 1) % div
            sir = stack[stackpointer % div]
            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]
            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]
            yi++
            x++
        }
        yw += w
        y++
    }
    x = 0
    while (x < w) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        yp = -radiusVar * w
        i = -radiusVar
        while (i <= radiusVar) {
            yi = Math.max(0, yp) + x
            sir = stack[i + radiusVar]
            sir[0] = r[yi]
            sir[1] = g[yi]
            sir[2] = b[yi]
            rbs = r1 - Math.abs(i)
            rsum += r[yi] * rbs
            gsum += g[yi] * rbs
            bsum += b[yi] * rbs
            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }
            if (i < hm) {
                yp += w
            }
            i++
        }
        yi = x
        stackpointer = radiusVar
        y = 0
        while (y < h) {
            // Preserve alpha channel: ( 0xff000000 & pix[yi] )
            pix[yi] =
                    -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum
            stackstart = stackpointer - radiusVar + div
            sir = stack[stackstart % div]
            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]
            if (x == 0) {
                vmin[y] = Math.min(y + r1, hm) * w
            }
            p = x + vmin[y]
            sir[0] = r[p]
            sir[1] = g[p]
            sir[2] = b[p]
            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]
            rsum += rinsum
            gsum += ginsum
            bsum += binsum
            stackpointer = (stackpointer + 1) % div
            sir = stack[stackpointer]
            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]
            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]
            yi += w
            y++
        }
        x++
    }
    ret.setPixels(pix, 0, w, 0, 0, w, h)
    return ret
}

/**
 * bitmap 이미지를
 * 파일로 저장.
 *
 * @receiver Bitmap
 * @param filePath 저장할 파일
 * @param format 이미지 포맷
 * @param quality 이미지 quality
 * @param recycle Bitmap recycle 여부
 * @return File Object
 */
@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
fun Bitmap.save(filePath: String,
                format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
                quality: Int = 100, recycle: Boolean = true): File? {
    filePath.toFile()?.let {
        save(it, format, quality, recycle)
    }
    return null
}

/**
 * bitmap 이미지를
 * 파일로 저장.
 *
 * @receiver Bitmap
 * @param file    The file.
 * @param format  The format of the image.
 * @param quality 이미지 quality
 * @param recycle True to recycle the source of bitmap, false otherwise.
 * @return File Object
 */
@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
fun Bitmap.save(file: File,
                format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
                quality: Int = 100, recycle: Boolean = true): File? {
    if (isEmpty()) return null

    file.delete()
    if (!file.createOrExistsFile()) {
        return null
    }

    file.outputStream().use {
        compress(format, quality, it)
        if (recycle && !isRecycled) recycle()
    }
    return file
}

/**
 * bitmap 이미지를
 * 파일로 생성
 *
 * @receiver Context
 * @param bitmap Bitmap
 * @param dateFormat 생성할 파일명
 * @param format 이미지 포맷
 * @param quality 이미지 quality
 * @return File Object
 */
@JvmOverloads
@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
fun Context.saveBitmapToFile(bitmap: Bitmap,
                             dateFormat: String? = null,
                             format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
                             quality: Int = 100): File? {
    val file = createImageFile(dateFormat, format)
    return bitmap.save(file, format, quality)
}


/**
 * 내장 메모리에
 * 이미지 파일을 생성
 *
 * @receiver Context
 * @param dateFormat 생성할 파일명
 * @param format 이미지 포맷
 * @return 생성된 파일
 */
@JvmOverloads
fun Context.createImageFile(dateFormat: String? = null,
                            format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): File {
    val extension = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        when(format) {
            Bitmap.CompressFormat.JPEG -> ".jpg"
            Bitmap.CompressFormat.PNG -> ".png"
            Bitmap.CompressFormat.WEBP_LOSSLESS, Bitmap.CompressFormat.WEBP_LOSSY -> ".webp"
            else -> ".jpg"
        }
    } else {
        when(format) {
            Bitmap.CompressFormat.JPEG -> ".jpg"
            Bitmap.CompressFormat.PNG -> ".png"
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP -> ".webp"
            else -> ".jpg"
        }
    }

    val picName = (if (dateFormat == null) nowDateString() else nowDateString(dateFormat)) + extension
    val folder = this.getExternalFilesDir(null)
    folder.createOrExistsDir()
    return File(folder, picName)
}

/**
 * 해당 파일이 이미지 인지 여부
 *
 * @receiver File
 * @return Boolean
 */
fun File.isImage(): Boolean {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    return try {
        val bitmap = BitmapFactory.decodeFile(absolutePath, options)
        val result = options.outWidth != -1 && options.outHeight != -1
        bitmap.recycle()
        result
    } catch (e: Exception) {
        false
    }
}

/**
 * Return the type of image.
 *
 * @receiver String
 * @return the type of image
 */
fun String.getImageType(): String? = toFile()?.getImageType()

/**
 * Return the type of image.
 *
 * @receiver File
 * @return the type of image
 */
fun File.getImageType(): String? {
    if (!isImage()) return ""
    return toByteArray().getImageType()
}


/**
 * 이미지 resize
 *
 * @receiver Bitmap
 * @param width desire width
 * @param height desire height
 * @param mode Resize mode
 * @param isExcludeAlpha true - exclude alpha (copy as RGB_565) false - include alpha (copy as ARGB_888)
 * @return Resize Bitmap
 */
@JvmOverloads
fun Bitmap.resize(width: Int, height: Int,
                  mode: ResizeMode = ResizeMode.AUTOMATIC,
                  isExcludeAlpha: Boolean = false): Bitmap {
    var resizeWidth = width
    var resizeHeight = height
    var resizeMode = mode

    val sourceWidth = this.width
    val sourceHeight = this.height

    if (mode == ResizeMode.AUTOMATIC) {
        resizeMode = calculateResizeMode(sourceWidth, sourceHeight)
    }

    if (resizeMode == ResizeMode.FIT_TO_WIDTH) {
        resizeHeight = calculateHeight(sourceWidth, sourceHeight, width)
    } else if (resizeMode == ResizeMode.FIT_TO_HEIGHT) {
        resizeWidth = calculateWidth(sourceWidth,sourceHeight, height)
    }

    val config = if (isExcludeAlpha) Bitmap.Config.RGB_565 else Bitmap.Config.ARGB_8888
    return Bitmap.createScaledBitmap(this, resizeWidth, resizeHeight, true).copy(config, true)
}

/**
 * 이미지 resize
 *
 * @receiver Uri
 * @param context Context
 * @return Bitmap?
 */
@RequiresApi(Build.VERSION_CODES.N)
fun Uri.resize(context: Context): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(context.contentResolver.openInputStream(this), null, options)

    var widthTemp = options.outWidth
    var heightTemp = options.outHeight
    var scale = 1

    while (true) {
        if (widthTemp / 2 < MAX_SIZE || heightTemp / 2 < MAX_SIZE)
            break
        widthTemp /= 2
        heightTemp /= 2
        scale *= 2
    }

    val resultOptions = BitmapFactory.Options().apply {
        inSampleSize = scale
    }
    val resizedBitmap = BitmapFactory.decodeStream(
            context.contentResolver.openInputStream(this),
            null,
            resultOptions
    )

    return resizedBitmap?.rotateBitmap(context.contentResolver.openInputStream(this).getOrientation())
}

/**
 * getOrientation
 *
 * @receiver InputStream
 * @return Int
 */
@RequiresApi(Build.VERSION_CODES.N)
fun InputStream?.getOrientation(): Int {
    val exifInterface: ExifInterface
    var orientation = 0
    try {
        exifInterface = ExifInterface(this!!)
        orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
        )
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return orientation
}

/**
 * Bitmap rotate
 *
 * @receiver Bitmap
 * @param orientation Int
 * @return Bitmap
 */
fun Bitmap.rotateBitmap(orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
        androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL -> return this
        androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
        androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
            matrix.setRotate(180f)
            matrix.postScale(-1f, 1f)
        }
        androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.setRotate(90f)
            matrix.postScale(-1f, 1f)
        }
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
        androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.setRotate(-90f)
            matrix.postScale(-1f, 1f)
        }
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
        else -> return this
    }
    try {
        val bmRotated = Bitmap.createBitmap(
                this,
                0,
                0,
                width,
                height,
                matrix,
                true
        )
        if (bmRotated != this)
            recycle()
        return bmRotated
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        return this
    }
}

/**
 * bitmap 이미지를
 * 비동기 방식으로
 * 파일로 저장.
 *
 * @receiver Bitmap
 * @param fileName 저장할 파일명
 * @param format 이미지 포맷
 * @param quality 이미지 quality
 * @param recycle Bitmap recycle 여부
 * @param onComplete async 완료 후, 실행
 */
@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
fun Bitmap.saveAsync(fileName: String,
                     format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
                     quality: Int = 100, recycle: Boolean = true,
                     onComplete: ((savedFileName: String) -> Unit) = {}) {
    GlobalScope.launch(Dispatchers.IO) {
        save(fileName, format, quality, recycle)

        withContext(Dispatchers.Main) {
            onComplete(fileName)
        }
    }
}


/**
 * Crop image easily.
 *
 * @receiver Bitmap
 * @param r is the Rect to crop from the Bitmap
 * @return cropped #android.graphics.Bitmap
 */
fun Bitmap.crop(r: Rect): Bitmap? =
        if (Rect(0, 0, width, height).contains(r)) {
            Bitmap.createBitmap(this, r.left, r.top, r.width(), r.height())
        } else {
            null
        }


/**
 * Converts Bitmap to Base64 Easily.
 *
 * @receiver Bitmap
 * @param compressFormat
 * @return
 */
@RequiresApi(Build.VERSION_CODES.FROYO)
fun Bitmap.toBase64(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): String {
    val result: String
    val baos = ByteArrayOutputStream()
    compress(compressFormat, 100, baos)
    baos.flush()
    baos.close()

    val bitmapBytes = baos.toByteArray()
    result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
    baos.flush()
    baos.close()

    return result
}


/**
 * resize Bitmap With a ease. Just call [resize] with the [w] and [h] and you will get new Resized Bitmap
 *
 * @receiver Bitmap
 * @param w Number
 * @param h Number
 * @param recycle Boolean
 * @return Bitmap
 */
fun Bitmap.resize(w: Number, h: Number, recycle: Boolean = true): Bitmap {
    val width = width
    val height = height
    val scaleWidth = w.toFloat() / width
    val scaleHeight = h.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    if (width > 0 && height > 0) {
        val newBitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        if (recycle && isRecycled.not() && newBitmap != this)
            recycle()
        return newBitmap
    }
    return this
}

/**
 * Converts Bitmap to ByteArray Easily.
 *
 * @receiver Bitmap
 * @param compressFormat CompressFormat
 * @return ByteArray
 */
fun Bitmap.toByteArray(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(compressFormat, 100, stream)
    return stream.toByteArray()
}

/**
 * Compress Bitmap by Sample Size
 *
 * @receiver Bitmap
 * @param maxWidth Int
 * @param maxHeight Int
 * @param recycle Boolean
 * @return Bitmap?
 */
fun Bitmap.compressBySampleSize(maxWidth: Int, maxHeight: Int,
                                recycle: Boolean = true): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val bytes = baos.toByteArray()
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
    options.inJustDecodeBounds = false
    if (recycle && !isRecycled) recycle()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
}

/**
 * Compress Bitmap by Quality
 *
 * @receiver Bitmap
 * @param quality Int
 * @param recycle Boolean
 * @return Bitmap?
 */
fun Bitmap.compressByQuality(quality: Int,
                             recycle: Boolean = true): Bitmap? {
    val baos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, quality, baos)
    val bytes = baos.toByteArray()
    if (recycle && !isRecycled) recycle()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}




//***********Private Methods Are below**********************
/**
 * sample size calculate
 *
 * @param options Options
 * @param maxWidth Int
 * @param maxHeight Int
 * @return Int
 */
private fun calculateInSampleSize(options: BitmapFactory.Options,
                                  maxWidth: Int,
                                  maxHeight: Int): Int {
    var height = options.outHeight
    var width = options.outWidth
    var inSampleSize = 1
    do {
        width = width shr 1
        height = height shr 1
        val bool = width >= maxWidth && height >= maxHeight
        if (bool.not())
            break
        else
            inSampleSize = inSampleSize shl 1
    } while (true)
    return inSampleSize
}

/**
 * 비율에 맞춰
 * width 값을 재 계산
 *
 * @param originalWidth
 * @param originalHeight
 * @param height
 * @return
 */
private fun calculateWidth(originalWidth: Int, originalHeight: Int, height: Int): Int =
        Math.ceil(originalWidth / (originalHeight.toDouble() / height)).toInt()

/**
 * 비율에 맞춰
 * height 값을 재 계산
 *
 * @param originalWidth
 * @param originalHeight
 * @param width
 * @return
 */
private fun calculateHeight(originalWidth: Int, originalHeight: Int, width: Int): Int =
        Math.ceil(originalHeight / (originalWidth.toDouble() / width)).toInt()

/**
 * Orientation에 맞춰
 * Resize mode 값을 계산
 *
 * @param width
 * @param height
 * @return
 */
private fun calculateResizeMode(width: Int, height: Int): ResizeMode =
        if (ImageOrientation.getOrientation(width, height) === ImageOrientation.LANDSCAPE) {
            ResizeMode.FIT_TO_WIDTH
        } else {
            ResizeMode.FIT_TO_HEIGHT
        }

/**
 * is empty
 *
 * @receiver Bitmap?
 * @return Boolean
 */
private fun Bitmap?.isEmpty(): Boolean {
    return this == null || width == 0 || height == 0
}

/**
 * get image type
 *
 * @receiver InputStream?
 * @return String?
 */
private fun InputStream?.getImageType(): String? {
    return if (this == null) {
        null
    } else {
        try {
            val bytes = ByteArray(8)
            if (this.read(bytes, 0, 8) != -1) bytes.getImageType() else null
        } catch (e: IOException) {
            LogUtil.e(e)
            null
        }
    }
}

/**
 * get image type
 *
 * @receiver ByteArray
 * @return String?
 */
private fun ByteArray.getImageType(): String? {
    return when {
        isJPEG() -> "JPEG"
        isGIF() -> "GIF"
        isPNG() -> "PNG"
        isBMP() -> "BMP"
        else -> null
    }
}

/**
 * is jpeg
 *
 * @receiver ByteArray
 * @return Boolean
 */
private fun ByteArray.isJPEG(): Boolean {
    return size >= 2 && this[0] == 0xFF.toByte() && this[1] == 0xD8.toByte()
}

/**
 * is gif
 *
 * @receiver ByteArray
 * @return Boolean
 */
private fun ByteArray.isGIF(): Boolean {
    return size >= 6
            && this[0] == 'G'.toByte()
            && this[1] == 'I'.toByte()
            && this[2] == 'F'.toByte()
            && this[3] == '8'.toByte()
            && (this[4] == '7'.toByte() || this[4] == '9'.toByte())
            && this[5] == 'a'.toByte()
}

/**
 * is png
 *
 * @receiver ByteArray
 * @return Boolean
 */
private fun ByteArray.isPNG(): Boolean {
    return (size >= 8
            && this[0] == 137.toByte()
            && this[1] == 80.toByte()
            && this[2] == 78.toByte()
            && this[3] == 71.toByte()
            && this[4] == 13.toByte()
            && this[5] == 10.toByte()
            && this[6] == 26.toByte()
            && this[7] == 10.toByte())
}

/**
 * is bmp
 *
 * @receiver ByteArray
 * @return Boolean
 */
private fun ByteArray.isBMP(): Boolean {
    return size >= 2 && this[0] == 0x42.toByte() && this[1] == 0x4d.toByte()
}