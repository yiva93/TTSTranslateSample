package com.ttstranslate.global.ui.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Slide
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.ttstranslate.global.navigation.AppNavigator
import com.ttstranslate.R
import com.ttstranslate.databinding.LayoutContainerBinding
import com.ttstranslate.global.navigation.setLaunchScreen
import com.ttstranslate.global.utils.BindingProvider
import com.ttstranslate.global.viewmodel.NavigationViewModel

abstract class FlowFragment : BaseFragment<LayoutContainerBinding>() {
    val viewModelOwner
        get() = ViewModelOwner.from(this)

    val currentFragment
        get() = childFragmentManager.findFragmentById(R.id.container) as? BaseFragment<*>

    internal val flowNavigation: NavigationViewModel by viewModel()

    override val bindingProvider: BindingProvider<LayoutContainerBinding> =
        LayoutContainerBinding::inflate

    open val launchScreen: Screen? = null

    private val navigator: AppNavigator by lazy {
        object : AppNavigator(requireActivity(), childFragmentManager, R.id.container) {
            override fun setupFragmentTransaction(
                screen: FragmentScreen,
                fragmentTransaction: FragmentTransaction,
                currentFragment: Fragment?,
                nextFragment: Fragment
            ) {
                //fix incorrect order lifecycle callback of MainFlowFragment
                fragmentTransaction.setReorderingAllowed(true)

                // currentFragment?.exitTransition = null // Fade(Fade.OUT)
                // nextFragment?.enterTransition = null // Slide(Gravity.END)
                nextFragment.enterTransition = Slide(Gravity.END).apply {
                    duration = 200L
                }
            }

            override fun activityBack() {
                navigation.finishFlow()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (childFragmentManager.fragments.isEmpty()) {
            launchScreen?.let(navigator::setLaunchScreen)
        }
    }

    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: navigation.exit()
    }

    override fun onResume() {
        super.onResume()
        navigation.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigation.navigatorHolder.removeNavigator()
        super.onPause()
    }
}
