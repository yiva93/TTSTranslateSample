package com.ttstranslate.feature.develop

import android.os.Bundle
import android.view.View
import com.ttstranslate.databinding.FragmentDevelopBinding
import com.ttstranslate.global.ui.fragment.BaseFragment
import com.ttstranslate.global.ui.fragment.FlowFragment
import com.ttstranslate.global.utils.BindingProvider
import com.ttstranslate.global.utils.common.addSystemWindowInsetToMargin
import com.ttstranslate.global.utils.common.setInvisible
import com.ttstranslate.global.utils.common.setVisible

class DevelopFragment : BaseFragment<FragmentDevelopBinding>() {

    override val bindingProvider: BindingProvider<FragmentDevelopBinding> =
        FragmentDevelopBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.innerContainer.addSystemWindowInsetToMargin(top = true, bottom = true)
        initBackButton()
    }

    private fun initBackButton() {
        with(binding) {
            if (parentFragment is FlowFragment) {
                backButton.setVisible()
                backButton.setOnClickListener { onBackPressed() }
            } else {
                backButton.setInvisible()
            }
        }
    }
}
