package com.ttstranslate.global.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ttstranslate.global.utils.BindingProvider
import com.ttstranslate.global.utils.common.hideKeyboard
import com.ttstranslate.global.viewmodel.NavigationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


abstract class BaseBottomSheetFragment<B : ViewBinding> : BottomSheetDialogFragment() {
    protected val navigation: NavigationViewModel by viewModel()

    private val flowParent
        get() = getParent(this)

    protected abstract val bindingProvider: BindingProvider<B>

    private var _binding: B? = null
    protected val binding get() = _binding!!

    protected lateinit var bottomSheetView: View
        private set
    protected lateinit var bottomSheet: BottomSheetBehavior<View>
        private set

    open val themeRes: Int? = null

    protected open val isExpanded = false

    protected open val hasInput = false

    private fun getParent(fragment: Fragment): FlowFragment {
        return when {
            fragment is FlowFragment -> fragment
            fragment.parentFragment == null ->
                throw IllegalStateException("Fragment must have FlowFragment or Activity parent")
            else -> getParent(fragment.requireParentFragment())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingProvider(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                bottomSheetView = findViewById(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheetView.setBackgroundColor(Color.TRANSPARENT)
                bottomSheet = BottomSheetBehavior.from(bottomSheetView).apply {
                    if (isExpanded) {
                        if (hasInput) {
                            // isFitToContents = false
                            peekHeight = bottomSheetView.height
                            bottomSheetView.parent.parent.requestLayout()
                        }
                        skipCollapsed = true
                        state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            if (hasInput) {
                setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                            or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dialog?.window?.navigationBarColor = getColorAttribute(R.attr.backgroundColor)
        hideKeyboard()
        _binding = null
    }

    override fun getTheme(): Int = themeRes ?: super.getTheme()

    protected fun dismissAll() {
        when (parentFragment) {
            !is BaseBottomSheetFragment<*> -> dismiss()
            else -> (parentFragment as BaseBottomSheetFragment<*>).dismissAll()
        }
    }
}
