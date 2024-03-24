package com.bignerdranch.android.criminalintent

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.roundToInt
// The key parameter in this code is sampleSize.
// This determines how big the “sample” should be for each pixel
// – a sample size of 1 has one final horizontal pixel for
// each horizontal pixel in the original file, and a sample
//size of 2 has one horizontal pixel for every two horizontal pixels in the
//original file. So when sampleSize is 2, the pixel count in the image is
//one-quarter of the pixel count in the original.

// при размере образца 1 на каждый горизонтальный пиксель исходного файла приходится
// один конечный горизонтальный пиксель,
// а при размере образца 2 - один горизонтальный пиксель на каждые
// два горизонтальных пикселя исходного файла.
// Таким образом, когда sampleSize равен 2,
// количество пикселей в изображении составляет четверть
// от количества пикселей в оригинале

fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
    // Read in the dimensions of the image on disk
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    // Figure out how much to scale down by
    val sampleSize = if (srcHeight <= destHeight && srcWidth <= destWidth) {
        1
    } else {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth

        minOf(heightScale, widthScale).roundToInt()
    }

    // Read in and create final bitmap
    return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
        inSampleSize = sampleSize
    })
}

