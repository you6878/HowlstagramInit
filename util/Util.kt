import android.graphics.*
import android.media.Image
import java.io.ByteArrayOutputStream

fun Image.toBitmap(quality: Int): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val uBuffer = planes[1].buffer // U
    val vBuffer = planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), quality, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun Bitmap.rotateWithReverse(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    var bitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)

    val m = Matrix()
    m.preScale(-1f, 1f)

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, false)
}
