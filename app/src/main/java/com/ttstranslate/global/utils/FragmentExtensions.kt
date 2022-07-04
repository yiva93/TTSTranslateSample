package com.ttstranslate.global.utils

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlin.reflect.KClass

inline fun <reified T : Fragment> FragmentManager.instantiateFragment(
    context: Context,
    fragmentClass: KClass<out T>,
    args: Bundle = bundleOf()
): T = fragmentFactory.instantiate(context.classLoader, fragmentClass.qualifiedName!!)
    .apply { arguments = args } as T

inline fun <reified T : DialogFragment> Fragment.showDialog(
    dialogClass: KClass<out T>,
    vararg args: Pair<String, Any?>
): DialogFragment {
    val fragment =
        childFragmentManager.instantiateFragment(requireContext(), dialogClass, bundleOf(*args))
    fragment.show(childFragmentManager, dialogClass.simpleName)
    return fragment
}

inline fun <reified T : Any?> Fragment.argument(key: String): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        requireArguments().get(key) as T
    }
}