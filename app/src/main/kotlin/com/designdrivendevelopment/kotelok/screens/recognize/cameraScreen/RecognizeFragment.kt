package com.designdrivendevelopment.kotelok.screens.recognize.cameraScreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture

class RecognizeFragment : Fragment() {
    private var previewView: PreviewView? = null
    private var recognizeButton: Button? = null
    private val viewModel: RecognizeViewModel by viewModels()
    private var lastRecognizedText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recognize, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        requireActivity().title = getString(R.string.title_recognize_text)
        val context = requireContext()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context).configureCamera(
            lifecycleOwner = this,
            context = context,
            previewView = previewView!!
        )
        viewModel.recognizedText.observe(this) { text ->
            lastRecognizedText = text
        }
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    // Кто знает, что именно тут прилетит
    @Suppress("TooGenericExceptionCaught")
    private fun ListenableFuture<ProcessCameraProvider>.configureCamera(
        lifecycleOwner: LifecycleOwner,
        context: Context,
        previewView: PreviewView,
    ): ListenableFuture<ProcessCameraProvider> {
        addListener(
            {
                val preview = Preview.Builder()
                    .build()
                    .apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }

                try {
                    get().apply {
                        val textAnalyzer = ImageAnalysis.Builder().build().apply {
                            setAnalyzer(
                                ContextCompat.getMainExecutor(context),
                                AnalyzeTextProcessor(
                                    onRecognitionEnd = { recognizedText ->
                                        viewModel.onTextRecognized(recognizedText.text)
                                    }
                                )
                            )
                        }
                        unbindAll()
                        bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
                        bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            textAnalyzer
                        )
                    }
                } catch (e: Exception) {
                    Snackbar.make(
                        context,
                        view!!,
                        "Упс, произошла какая-то ошибка, попробуйте снова",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            },
            ContextCompat.getMainExecutor(context)
        )
        return this
    }

    private fun setupListeners() {
        recognizeButton?.setOnClickListener {
            val bundle = Bundle().apply {
                putString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, lastRecognizedText)
            }
            setFragmentResult(FragmentResult.RecognizeTab.OPEN_RECOGNIZED_WORDS_FRAGMENT_KEY, bundle)
        }
    }

    private fun initViews(view: View) {
        previewView = view.findViewById(R.id.preview_view)
        recognizeButton = view.findViewById(R.id.recognize_button)
    }

    private fun clearViews() {
        previewView = null
        recognizeButton = null
    }

    companion object {
        const val OPEN_RECOGNIZE_TAG = "open_recognize"

        @JvmStatic
        fun newInstance() = RecognizeFragment()
    }
}
