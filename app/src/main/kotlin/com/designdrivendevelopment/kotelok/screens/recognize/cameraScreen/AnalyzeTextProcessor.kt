package com.designdrivendevelopment.kotelok.screens.recognize.cameraScreen

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class AnalyzeTextProcessor(
    private val onRecognitionEnd: (Text) -> Unit,
    private val onRecognitionFailed: (() -> Unit)? = null,
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            recognizer.process(inputImage)
                .addOnSuccessListener { recognizedText -> onRecognitionEnd(recognizedText) }
                .addOnFailureListener { onRecognitionFailed?.invoke() }
                .addOnCompleteListener { image.close() }
        }
    }
}
