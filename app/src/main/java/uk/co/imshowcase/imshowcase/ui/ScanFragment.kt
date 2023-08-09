package uk.co.imshowcase.imshowcase.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import uk.co.eyrewiut.imshowcase.databinding.FragmentScanBinding
import java.util.concurrent.Executors
import uk.co.eyrewiut.imshowcase.R

typealias BarcodeListener = (barcodes: List<Barcode>) -> Unit

/*
 * The following regex pattern is copied from a post by Sébastien Dubois.
 * Accessed: 20 Jan 2023
 * Available at: https://dsebastien.medium.com/kotlin-uuid-bean-validation-4565813424ad
 * Author: Sébastien Dubois.
 */
const val UUID_REGEXP = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"

class ScanFragment : MainFragment() {
    private var _viewBinding: FragmentScanBinding? = null
    private val viewBinding get() = _viewBinding!!
    /*
    * The backbone of this file was copied with modifications from the CameraX getting started guide
    * Accessed: 21 Oct 2022
    * Available at: https://developer.android.com/codelabs/camerax-getting-started
    * Author: "a Googler" (anonymous google employee)
    */
    private lateinit var cameraExecutor: ExecutorService
    private var hasScanned = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasScanned = false

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                hasScanned = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentScanBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        viewBinding.browseProjectsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_global_projectsListFragment)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            startCamera()
        } else {
//            Toast.makeText(context,
//                "Permissions not granted by the user.",
//                Toast.LENGTH_SHORT).show()
            Snackbar.make(viewBinding.root, "Please grant camera permission to use this feature.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Grant") {
                    ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                }
                .show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.cameraView.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setImageQueueDepth(1)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRAnalyser(onBarcodeScanned))
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                Toast.makeText(context, "Error starting camera", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
    /* === Copied code ends here === */

    private val onBarcodeScanned = { barcodes: List<Barcode> ->
        if (barcodes.isNotEmpty()) {
            val scanResult = barcodes[0].rawValue
            
            val isValid = scanResult != null && Regex(UUID_REGEXP).matches(scanResult)

            if (!hasScanned && isValid) {

                hasScanned = true

                val navController = findNavController()
                navController.navigate(
                    R.id.viewProjectFragment,
                    bundleOf("PROJECT_IDENTIFIER" to scanResult)
                )
            }
        }
    }

    // Originally implemented to allow tokens to be passed through the qr code, removed because api token needed without scanning
/*    private fun parseToken(token: String): DecodedJWT {
        val algorithm = Algorithm.HMAC256("im-sh0wc@s3-2021")
        val verifier = JWT.require(algorithm)
            .withClaim("token", BiPredicate { claim, _ -> !claim.isNull && !claim.isMissing })
            .withClaim("sub", BiPredicate { claim, _ -> !claim.isNull && !claim.isMissing })
            .build() //Reusable verifier instance

        return verifier.verify(token)
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        private const val TAG = "QRScanner"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).toTypedArray()
    }

    // Analyses each from from the camera and checks for QR codes
    private class QRAnalyser(private val listener: BarcodeListener) : ImageAnalysis.Analyzer {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        /*
        * This code is based on a guide on using the android ml-kit barcode scanning features
        * Accessed at: 21 Oct 2022
        * Available at: https://developers.google.com/ml-kit/vision/barcode-scanning/android
        * Author: Google
        */
        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                val result = scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        listener(barcodes)
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Failure while reading barcodes", it)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        }
    }
}