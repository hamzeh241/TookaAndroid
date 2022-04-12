package ir.tdaapp.tooka.views.activities

import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import ir.tdaapp.tooka.databinding.ActivityQrBinding
import ir.tdaapp.tooka.util.TookaImageAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrActivity: AppCompatActivity() {
  private lateinit var binding: ActivityQrBinding

  private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
  private lateinit var cameraExecutor: ExecutorService
  private lateinit var analyzer: TookaImageAnalyzer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityQrBinding.inflate(layoutInflater)
    setContentView(binding.root)

    analyzer = TookaImageAnalyzer {
      Log.i("QrActivity", it)
    }
    cameraExecutor = Executors.newSingleThreadExecutor()
    cameraProviderFuture = ProcessCameraProvider.getInstance(this)

    cameraProviderFuture.addListener({
      val cameraProvider = cameraProviderFuture.get()
      bindPreview(cameraProvider)
    }, ContextCompat.getMainExecutor(this))
  }

  private fun bindPreview(cameraProvider: ProcessCameraProvider) {
    val preview: Preview = Preview.Builder()
      .build()
    val cameraSelector: CameraSelector = CameraSelector.Builder()
      .requireLensFacing(CameraSelector.LENS_FACING_BACK)
      .build()
    preview.setSurfaceProvider(binding.previewView.surfaceProvider)

    val imageAnalysis = ImageAnalysis.Builder()
      .setTargetResolution(Size(1280, 720))
      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
      .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
      .build()
    imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

    cameraProvider.bindToLifecycle(
      this as LifecycleOwner,
      cameraSelector,
      imageAnalysis,
      preview
    )
  }
}