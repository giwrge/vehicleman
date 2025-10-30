package com.vehicleman.ui.screens.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.github.g0dkar.qrcode.QRCode

/**
 * A Composable function that takes a string and displays it as a QR code.
 *
 * @param content The string to be encoded in the QR code.
 * @param modifier The modifier to be applied to the QR code image.
 */
@Composable
fun VehicleQrCode(content: String, modifier: Modifier = Modifier) {
    // Render the QR code as a byte array
    val rawBytes = QRCode(content).render()

    // Decode the byte array into a Bitmap
    val qrBitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size)

    // Display the bitmap if it was decoded successfully
    qrBitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "QR Code for '$content'",
            modifier = modifier
        )
    }
}