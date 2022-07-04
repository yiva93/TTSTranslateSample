package com.ttstranslate.feature.translate

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import com.ttstranslate.R
import com.ttstranslate.Screens
import com.ttstranslate.databinding.FragmentTranslateBinding
import com.ttstranslate.domain.model.Translation
import com.ttstranslate.domain.model.enums.Language
import com.ttstranslate.feature.recognition.VoiceRecognitionDialogFragment
import com.ttstranslate.global.dispatcher.notifier.Notifier
import com.ttstranslate.global.ui.fragment.BaseFragment
import com.ttstranslate.global.utils.BindingProvider
import com.ttstranslate.global.utils.common.addSystemWindowInsetToMargin
import com.ttstranslate.global.utils.common.applyAll
import com.ttstranslate.global.utils.common.getDrawableCompat
import com.ttstranslate.global.utils.common.setGone
import com.ttstranslate.global.utils.common.setVisible
import com.ttstranslate.global.utils.common.swap
import com.ttstranslate.global.utils.common.syncToEnd
import com.ttstranslate.global.utils.permissions.RecordAudioPermissionHandler
import com.ttstranslate.global.utils.showDialog
import com.ttstranslate.global.utils.viewObserve
import com.ttstranslate.global.viewmodel.LoadState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class TranslateFragment : BaseFragment<FragmentTranslateBinding>(), TextToSpeech.OnInitListener {
    private val viewModel: TranslateViewModel by viewModel()
    private val notifier: Notifier by inject()
    private val recordAudioPermissionHandler: RecordAudioPermissionHandler by inject()

    override val bindingProvider: BindingProvider<FragmentTranslateBinding> =
        FragmentTranslateBinding::inflate

    private lateinit var textToSpeech: TextToSpeech
    private var ttsRussianSupported: Boolean = false
    private var ttsEnglishSupported: Boolean = false
    private var ttsPlaying: Boolean = false
    private var playingResult: Boolean = false

    private fun getLanguageSupported(language: Language): Boolean = when (language) {
        Language.RU -> ttsRussianSupported
        Language.EN -> ttsEnglishSupported
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTts()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.container.addSystemWindowInsetToMargin(top = true)
        binding.characterCountTextView.text = String.format(
            Locale.getDefault(),
            getString(R.string.character_count_format),
            0, CHARACTER_LIMIT
        )
        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewObserve(viewModel.loadLiveData) {
            when (it) {
                is LoadState.Loading -> binding.progressAnimation.setVisible()
                else -> binding.progressAnimation.setGone()
            }
        }
        viewObserve(viewModel.translationLiveData) { translationResult ->
            updateTranslationResult(translationResult)
        }
        viewObserve(viewModel.languageLiveData) { languageResult ->
            with(binding) {
                translateFromTextView.text = getString(languageResult.first.nameRes)
                translateToTextView.text = getString(languageResult.second.nameRes)
            }
        }
        viewObserve(viewModel.recognitionLiveData) { recognitionResult ->
            binding.inputEditText.syncToEnd(recognitionResult)
        }
    }

    private fun updateTranslationResult(translationResult: Translation?) {
        with(binding) {
            inputEditText.syncToEnd(translationResult?.textFrom)
            resultTextView.text = translationResult?.result
            copyResultButton.isEnabled = !translationResult?.result.isNullOrBlank()
            playResultButton.isEnabled = viewModel.languageLiveData.value?.let {
                !translationResult?.result.isNullOrBlank() && getLanguageSupported(it.second)
            } ?: false
            favoriteResultButton.apply {
                isEnabled = !translationResult?.result.isNullOrBlank()
                setImageDrawable(
                    requireContext().getDrawableCompat(
                        if (translationResult?.favoriteId != null) R.drawable.ic_favorite_filled
                        else R.drawable.ic_favorite
                    )
                )
            }
        }
    }

    private fun startRecognitionWithPermissionRequest() {
        recordAudioPermissionHandler.checkPermissions(activity) { permissionsGranted ->
            if (permissionsGranted) {
                viewModel.languageLiveData.value?.let {
                    showDialog(
                        VoiceRecognitionDialogFragment::class,
                        VoiceRecognitionDialogFragment.KEY_LANGUAGE to it.first
                    )
                }
            } else {
                notifier.sendActionMessage(
                    R.string.record_audio_permission_request,
                    R.string.settings_btn
                ) {
                    navigation.startFlow(Screens.Action.appSettings())
                }
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            favoriteResultButton.setOnClickListener {
                viewModel.onFavoriteClick()
            }
            copyResultButton.setOnClickListener {
                val clipboardManager =
                    getSystemService(requireContext(), ClipboardManager::class.java)
                val clip =
                    ClipData.newPlainText(resultTextView.text.trim(), resultTextView.text.trim())
                clipboardManager?.setPrimaryClip(clip)
                notifier.sendMessage(R.string.copy_success)
            }
            recordTextFromButton.setOnClickListener {
                startRecognitionWithPermissionRequest()
            }
            inputEditText.apply {
                addTextChangedListener {
                    characterCountTextView.text = String.format(
                        Locale.getDefault(),
                        getString(R.string.character_count_format),
                        it?.length ?: 0, CHARACTER_LIMIT
                    )
                    viewModel.updateSearchQuery(it.toString().trim())
                    clearButton.isEnabled = !it.isNullOrEmpty()
                    playFromButton.isEnabled = viewModel.languageLiveData.value?.let { value ->
                        !it.isNullOrEmpty() && getLanguageSupported(value.second)
                    } ?: false
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        viewModel.updateSearchQuery(text.toString().trim())
                        true
                    } else false
                }
            }
            clearButton.setOnClickListener {
                inputEditText.syncToEnd("")
                resultTextView.text = ""
                applyAll(playFromButton, playResultButton, copyResultButton, favoriteResultButton) {
                    isEnabled = false
                }
                favoriteResultButton.setImageDrawable(requireContext().getDrawableCompat(R.drawable.ic_favorite))
            }
            swapLanguageButton.setOnClickListener {
                viewModel.toggleTranslateLanguages()
                val (query, result) = inputEditText.text swap resultTextView.text
                inputEditText.syncToEnd(query.toString())
                resultTextView.text = result
            }
            playFromButton.setOnClickListener {
                if (ttsPlaying && !playingResult) {
                    textToSpeech.stop()
                    onSpeakEnd()
                } else {
                    val text = inputEditText.text
                    if (!text.isNullOrBlank() && viewModel.languageLiveData.value != null) {
                        playResultButton.isEnabled = false
                        playFromButton.setImageDrawable(requireContext().getDrawableCompat(R.drawable.ic_pause))
                        playingResult = false
                        playTts(text.toString(), viewModel.languageLiveData.value!!.first)
                    }
                }
            }
            playResultButton.setOnClickListener {
                if (ttsPlaying && playingResult) {
                    textToSpeech.stop()
                    onSpeakEnd()
                } else {
                    val text = resultTextView.text
                    if (!text.isNullOrBlank() && viewModel.languageLiveData.value != null) {
                        playFromButton.isEnabled = false
                        playResultButton.setImageDrawable(requireContext().getDrawableCompat(R.drawable.ic_pause))
                        playingResult = true
                        playTts(text.toString(), viewModel.languageLiveData.value!!.second)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        textToSpeech.stop()
        super.onBackPressed()
    }

    private fun playTts(text: String, language: Language) {
        textToSpeech.language = language.locale
        val languageSupported = getLanguageSupported(language)
        if (languageSupported)
            textToSpeech.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
            )
        else onSpeakEnd()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ttsEnglishSupported =
                textToSpeech.isLanguageAvailable(Language.EN.locale) != TextToSpeech.LANG_MISSING_DATA
                        && textToSpeech.isLanguageAvailable(Language.EN.locale) != TextToSpeech.LANG_NOT_SUPPORTED
            ttsRussianSupported =
                textToSpeech.isLanguageAvailable(Language.RU.locale) != TextToSpeech.LANG_MISSING_DATA
                        && textToSpeech.isLanguageAvailable(Language.RU.locale) != TextToSpeech.LANG_NOT_SUPPORTED
        } else {
            ttsEnglishSupported = false
            ttsRussianSupported = false
            binding.playResultButton.isEnabled = false
            binding.playFromButton.isEnabled = false
        }
    }


    private fun initTts() {
        textToSpeech = TextToSpeech(requireContext(), this)
        textToSpeech.setSpeechRate(1.0F)
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(p0: String?) {
                ttsPlaying = true
            }

            override fun onDone(p0: String?) {
                onSpeakEnd()
            }

            override fun onError(utteranceId: String?) {
                onSpeakEnd()
            }
        })
    }

    private fun onSpeakEnd() {
        ttsPlaying = false
        with(binding) {
            viewModel.languageLiveData.value?.let {
                val (languageFrom, languageTo) = it
                requireActivity().runOnUiThread {
                    playFromButton.isEnabled = getLanguageSupported(languageFrom)
                    playFromButton.setImageDrawable(requireContext().getDrawableCompat(R.drawable.ic_sound))
                    playResultButton.isEnabled = getLanguageSupported(languageTo)
                    playResultButton.setImageDrawable(requireContext().getDrawableCompat(R.drawable.ic_sound))
                }
            }
        }
    }

    companion object {
        private const val CHARACTER_LIMIT = 250
    }
}