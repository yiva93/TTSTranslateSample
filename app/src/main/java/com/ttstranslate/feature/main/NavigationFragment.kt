package com.ttstranslate.feature.main

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import com.ttstranslate.R
import com.ttstranslate.databinding.FragmentNavigationBinding
import com.ttstranslate.feature.favorite.FavoritesFragment
import com.ttstranslate.feature.translate.TranslateFragment
import com.ttstranslate.global.ui.fragment.BaseFragment
import com.ttstranslate.global.utils.BindingProvider
import com.ttstranslate.global.utils.instantiateFragment
import com.ttstranslate.global.utils.viewObserve
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.reflect.KClass

class NavigationFragment : BaseFragment<FragmentNavigationBinding>() {
    private val viewModel: BottomNavigationViewModel by viewModel()

    override val bindingProvider: BindingProvider<FragmentNavigationBinding> =
        FragmentNavigationBinding::inflate

    private val fragmentMap: Map<BottomNavigationTab, () -> Fragment> = mapOf(
        BottomNavigationTab.TRANSLATION to {
            getFragmentByTag(
                childFragmentManager,
                BottomNavigationTab.TRANSLATION.name,
                TranslateFragment::class
            )
        },
        BottomNavigationTab.FAVORITES to {
            getFragmentByTag(
                childFragmentManager,
                BottomNavigationTab.FAVORITES.name,
                FavoritesFragment::class
            )
        }
    ).let(::EnumMap)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateTab(BottomNavigationTab.TRANSLATION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottomNavigationContainer.itemIconTintList = null
        binding.bottomNavigationContainer.setOnNavigationItemSelectedListener { item ->
            viewModel.updateTab(BottomNavigationTab[item.itemId])
            true
        }
        viewObserve(viewModel.navigationTabLiveData, ::openFragmentByTab)
    }

    private fun getFragmentByTag(
        fm: FragmentManager,
        tag: String,
        fragmentClass: KClass<out Fragment>,
        args: Bundle = bundleOf()
    ): Fragment {
        return fm.findFragmentByTag(tag)
            ?: fm.instantiateFragment(
                requireContext(),
                fragmentClass,
                args
            ).also {
                fm.commitNow {
                    add(R.id.fragmentContainer, it, tag)
                    detach(it)
                }
            }
    }

    private fun openFragmentByTab(tab: BottomNavigationTab) = childFragmentManager.commitNow {
        fragmentMap.forEach { (t, f) ->
            binding.bottomNavigationContainer.selectedItemId = tab.id
            if (t == tab)
                attach(f())
            else detach(f())
        }
    }
}