package com.ttstranslate.feature.recognition

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import com.ttstranslate.R
import com.ttstranslate.databinding.FragmentVoiceRecognitionDialogBinding
import com.ttstranslate.domain.model.enums.Language
import com.ttstranslate.global.dispatcher.error.ErrorHandler
import com.ttstranslate.global.dispatcher.notifier.Notifier
import com.ttstranslate.global.speech.SpeechRecognitionService
import com.ttstranslate.global.ui.fragment.BaseBottomSheetFragment
import com.ttstranslate.global.utils.BindingProvider
import com.ttstranslate.global.utils.argument
import com.ttstranslate.global.utils.common.setInvisible
import com.ttstranslate.global.utils.common.setVisible
import com.ttstranslate.global.utils.dialog.alert.AlertDialogAction
import com.ttstranslate.global.utils.dialog.showDefaultAlertDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.pow

class VoiceRecognitionDialogFragment :
    BaseBottomSheetFragment<FragmentVoiceRecognitionDialogBinding>(),
    RecognitionListener {
    override val bindingProvider: BindingProvider<FragmentVoiceRecognitionDialogBinding> =
        FragmentVoiceRecognitionDialogBinding::inflate

    override val isExpanded: Boolean = true

    private val viewModel: VoiceRecognitionViewModel by viewModel()
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()

    private val locale: Language? by argument(KEY_LANGUAGE)

    private var speaking: Boolean = false

    private var speechRecognitionService: SpeechRecognitionService? = null
    private var speechRecognizerIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locale?.let(viewModel::updateLocale)
        initSpeechRecognition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.languageLiveData.value?.let {
            binding.languageTagTextView.text = it.tag
        }
        initOnClickListeners()
    }

    private fun initSpeechRecognition() {
        if (speechRecognitionService == null) {
            speechRecognitionService = SpeechRecognitionService(requireContext(), errorHandler)
        }
        speechRecognitionService?.removeRecognitionListener(this)
        speechRecognitionService?.addRecognitionListener(this)
        viewModel.languageLiveData.value?.let {
            createNewRecognitionIntent(it.locale.toLanguageTag())
        }
    }

    override fun onStop() {
        binding.hintTextView.text = getString(R.string.press_to_speak)
        speechRecognitionService?.release()
        super.onStop()
    }

    private fun stopRecognition() {
        speaking = false
        speechRecognitionService?.stopListening()
        with(binding) {
            voiceWaveLottieAnimationView.setInvisible()
            buttonRippleView.stopRippleAnimation()
            hintTextView.text = getString(R.string.press_to_speak)
        }
    }

    private fun startRecognition() {
        try {
            speechRecognizerIntent?.let {
                speechRecognitionService?.startListening(it)
            }
        } catch (e: SpeechRecognitionService.ServiceNotAvailableException) {
            showServiceNotAvailableDialog()
        } catch (e: SpeechRecognitionService.ServiceNotEnabledException) {
            showServiceNotEnabledDialog()
        }
    }

    private fun showServiceNotEnabledDialog() {
        context?.showDefaultAlertDialog(
            message = getString(R.string.dialog_voice_recognition_not_enabled_message),
            cancellable = true,
            actions = listOf(
                AlertDialogAction(getString(R.string.cancel)) {
                    it.dismiss()
                },
                AlertDialogAction(getString(R.string.settings_btn)) {
                    openVoiceInputSettings()
                    it.dismiss()
                },
            )
        )
    }

    private fun showServiceNotAvailableDialog() {
        context?.showDefaultAlertDialog(
            message = getString(R.string.dialog_voice_recognition_not_available_message),
            cancellable = true,
            actions = listOf(
                AlertDialogAction(getString(R.string.cancel)) {
                    it.dismiss()
                },
                AlertDialogAction(getString(R.string.start)) {
                    openOrInstallGoogleSearchApp()
                    it.dismiss()
                },
            )
        )
    }

    private fun openVoiceInputSettings() {
        try {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        } catch (e: Exception) {
            errorHandler.proceed(e) { notifier.sendMessage(R.string.voice_input_service_error) }
        }
    }

    private fun openOrInstallGoogleSearchApp() {
        val packageName = getString(R.string.google_search_package_name)
        val launchIntent = requireActivity().packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.market_uri, packageName))
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.play_store_uri, packageName))
                    )
                )
            }
        }
    }

    private fun createNewRecognitionIntent(locale: String) {
        if (speechRecognizerIntent != null) {
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireActivity().packageName)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            COMPLETE_SILENCE_LENGTH
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
            INPUT_MINIMUM_LENGTH
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
            INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH
        )
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        speechRecognizerIntent = intent
    }

    private fun initOnClickListeners() {
        with(binding) {
            buttonClose.setOnClickListener {
                dismiss()
            }
            recordVoiceButton.setOnClickListener {
                startRecognitionWithPermissionRequest()
            }
        }
    }

    private fun startRecognitionWithPermissionRequest() {
        if (speaking)
            stopRecognition()
        else
            startRecognition()
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        speaking = true
        with(binding) {
            hintTextView.text = getString(R.string.please_say_something)
            buttonRippleView.startRippleAnimation()
        }
    }

    override fun onBeginningOfSpeech() {
        with(binding) {
            voiceWaveLottieAnimationView.setVisible()
            voiceWaveLottieAnimationView.playAnimation()
        }
    }

    override fun onRmsChanged(p0: Float) {
        val dbValue =
            (10 * 10.0.pow(p0 / 10.0)) / 100
        binding.voiceWaveLottieAnimationView.scaleY =
            (dbValue + 0.2F).toFloat().coerceAtMost(1.0F)
    }

    override fun onEndOfSpeech() {
        with(binding) {
            voiceWaveLottieAnimationView.pauseAnimation()
            voiceWaveLottieAnimationView.setInvisible()
            voiceWaveLottieAnimationView.scaleY = 0F
        }
    }

    override fun onResults(results: Bundle?) {
        speaking = false
        with(binding) {
            voiceWaveLottieAnimationView.setInvisible()
            buttonRippleView.stopRippleAnimation()
            hintTextView.text = getString(R.string.press_to_speak)
        }
        val matches: ArrayList<String>? =
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            viewModel.onRecognitionEnded(matches[0])
            dismiss()
        }
    }

    override fun onError(error: Int) {
        when (error) {
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT, SpeechRecognizer.ERROR_SERVER -> {
                stopRecognition()
                speechRecognitionService?.release()
                binding.hintTextView.text = getString((R.string.recognizer_server_error))
            }
            SpeechRecognizer.ERROR_NO_MATCH,
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                stopRecognition()
                binding.hintTextView.text = getString(R.string.recognizer_no_match)
            }
            else -> {
                stopRecognition()
                binding.hintTextView.text = getString(R.string.recognizer_client_error)
            }
        }
    }


    override fun onPartialResults(partialResults: Bundle) = Unit
    override fun onEvent(eventType: Int, params: Bundle?) = Unit
    override fun onBufferReceived(buffer: ByteArray?) = Unit

    companion object {
        const val KEY_LANGUAGE = "language"
        const val COMPLETE_SILENCE_LENGTH = 3000L
        const val INPUT_MINIMUM_LENGTH = 3000L
        const val INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH = 3000L
    }
}
