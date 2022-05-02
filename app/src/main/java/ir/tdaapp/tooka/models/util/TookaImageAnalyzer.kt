package ir.tdaapp.tooka.models.util

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class TookaImageAnalyzer(private val action: (String)->Unit): ImageAnalysis.Analyzer {

  companion object {
    const val TAG = "TookaImageAnalyzer"
  }

  override fun analyze(imageProxy: ImageProxy) {
    scanBarcode(imageProxy)
  }

  @SuppressLint("UnsafeOptInUsageError")
  private fun scanBarcode(imageProxy: ImageProxy) {
    imageProxy.image?.let { image ->
      val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
      val scanner = BarcodeScanning.getClient()
      scanner.process(inputImage)
        .addOnSuccessListener {
          if (!it.isNullOrEmpty())
            if (it[0].format.equals(Barcode.FORMAT_QR_CODE))
              it[0].rawValue?.let { it1 -> action(it1) }
        }
        .addOnFailureListener {

        }
        .addOnCompleteListener {
          imageProxy.close()
        }
    }
  }
}
