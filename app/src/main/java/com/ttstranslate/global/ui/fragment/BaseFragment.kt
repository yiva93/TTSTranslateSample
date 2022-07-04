package com.ttstranslate.global.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.ttstranslate.global.utils.common.hideKeyboard
import com.ttstranslate.global.utils.BindingProvider
import com.ttstranslate.global.viewmodel.NavigationViewModel

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    open var isLightStatusBar: Boolean = true
        protected set
    open var isLightNavigationBar: Boolean = true
        protected set

    private val flowParent
        get() = this as? FlowFragment ?: getParent(this)

    val navigation: NavigationViewModel
        get() = flowParent.flowNavigation

    protected val fragmentScope
        get() = lifecycleScope

    protected val viewScope
        get() = viewLifecycleOwner.lifecycleScope

    protected abstract val bindingProvider: BindingProvider<B>

    private var _binding: B? = null
    protected val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        _binding = null
    }

    open fun onBackPressed() {
        navigation.exit()
    }

    override fun onResume() {
        super.onResume()
        initStatusAndNavigationBar()
    }

    private fun initStatusAndNavigationBar() {
        if (this is FlowFragment || childFragmentManager.fragments.isNotEmpty()) {
            return
        }
        updateSystemBarsColors()
    }

    fun updateSystemBarsColors() {
        applyStatusBarMode()
        applyNavigationBarMode()
    }

    fun setStatusBarMode(isLightStatusBar: Boolean) {
        this.isLightStatusBar = isLightStatusBar
        applyStatusBarMode()
    }

    private fun applyStatusBarMode() {
        var flags = requireActivity().window.decorView.systemUiVisibility

        flags = if (isLightStatusBar) {
            flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }

        requireActivity().window.decorView.systemUiVisibility = flags
    }

    private fun applyNavigationBarMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.apply {
                var flags = window.decorView.systemUiVisibility
                flags = if (isLightNavigationBar) {
                    flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }
                window.decorView.systemUiVisibility = flags
            }
        }
    }
}
