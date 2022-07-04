package com.ttstranslate.feature.splash

import android.animation.Animator
import android.os.Bundle
import android.view.View
import com.ttstranslate.Screens
import com.ttstranslate.databinding.FragmentSplashBinding
import com.ttstranslate.global.ui.fragment.BaseFragment
import com.ttstranslate.global.utils.BindingProvider

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override var isLightStatusBar = true

    override val bindingProvider: BindingProvider<FragmentSplashBinding> =
        FragmentSplashBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAnimation()

    }

    private fun setupAnimation() {
        binding.lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                navigation.newRootFlow(Screens.Flow.navigation())
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
    }
}