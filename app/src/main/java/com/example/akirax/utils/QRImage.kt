package com.example.akirax.utils

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.core.graphics.toColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


//Not Being Used
@Composable
fun QrCodeImage(data: String) {
    val bitmap = generateQrCodeBitmap(data)
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "QR Code",
        modifier = Modifier
            .size(200.dp)
    )
}

val black = "#000000".toColorInt()
val white = "#FFFFFF".toColorInt() // android.graphics.Color.parseColor("#FFFFFF") -> Older method


fun generateQrCodeBitmap(data: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = createBitmap(width, height)                           //Older method -> Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap[x, y] = if (bitMatrix[x, y]) black else white      //Older method -> bitmap.setPixel(x, y, if (bitMatrix[x, y]) black else white)
        }
    }
    return bitmap
}
