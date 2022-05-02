package ir.tdaapp.tooka.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.DialogQrCodeScannerBinding
import ir.tdaapp.tooka.models.util.TookaImageAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class QrCodeScannerDialog(private val result: (String)->Unit): DialogFragment() {

  companion object {
    const val TAG = "QrCodeScannerDialog"
  }

  private lateinit var binding: DialogQrCodeScannerBinding

  private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
  private lateinit var cameraExecutor: ExecutorService
  private lateinit var cameraProvider: ProcessCameraProvider
  private lateinit var analyzer: TookaImageAnalyzer

  override fun onStart() {
    super.onStart()
    val dialog: Dialog? = dialog
    if (dialog != null) {
      val width = ViewGroup.LayoutParams.MATCH_PARENT
      val height = ViewGroup.LayoutParams.MATCH_PARENT
      dialog.window?.setLayout(width, height)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
  }

  override fun onDestroy() {
    super.onDestroy()
    try {
      cameraProvider.unbindAll()
      cameraExecutor.shutdown()
      cameraProviderFuture.cancel(false)
    } catch (ignored: Exception) {
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DialogQrCodeScannerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initToolbar()
    initScanner()
  }

  private fun initScanner() {
    analyzer = TookaImageAnalyzer {
      result(it)
      dismiss()
    }
    cameraExecutor = Executors.newSingleThreadExecutor()
    cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

    cameraProviderFuture.addListener({
      cameraProvider = cameraProviderFuture.get()
      bindPreview(cameraProvider)
    }, ContextCompat.getMainExecutor(requireContext()))
  }

  private fun initToolbar() {
    binding.toolbar.setNavigationOnClickListener { v -> dismiss() }
    binding.toolbar.setTitle(getString(R.string.scan_qr))
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