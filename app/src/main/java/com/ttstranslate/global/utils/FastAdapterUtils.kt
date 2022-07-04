package com.ttstranslate.global.utils

import androidx.fragment.app.Fragment
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun createFastAdapter(vararg adapter: IAdapter<*>) = FastAdapter.with(adapter.toList())

inline fun <reified T : IItem<*>> Fragment.itemAdapter() = ItemAdapterDelegate<Fragment, T>()

inline fun <reified T : IItem<*>> Fragment.fastAdapter(noinline provider: () -> List<IAdapter<out T>>) =
    FastAdapterDelegate<Fragment, T>(provider)

inline fun <reified T : IItem<*>> Fragment.fastItemAdapter() = ItemAdapterDelegate<Fragment, T>()

inline fun <reified T : IItem<*>> FastAdapter.ViewHolder<*>.itemAdapter() =
    ItemAdapterDelegate<FastAdapter.ViewHolder<*>, T>()

inline fun <reified T : IItem<*>> FastAdapter.ViewHolder<*>.fastAdapter(noinline provider: () -> List<IAdapter<T>>) =
    FastAdapterDelegate<FastAdapter.ViewHolder<*>, T>(provider)

inline fun <reified T : IItem<*>> FastAdapter.ViewHolder<*>.fastItemAdapter() =
    FastItemAdapterDelegate<FastAdapter.ViewHolder<*>, T>()

class ItemAdapterDelegate<P, T : IItem<*>> : ReadOnlyProperty<P, IAdapter<T>> {
    private val itemAdapter: ItemAdapter<T> by lazy {
        ItemAdapter()
    }

    override fun getValue(thisRef: P, property: KProperty<*>): ItemAdapter<T> = itemAdapter
}

class FastAdapterDelegate<P, T : IItem<*>>(private val itemAdaptersProvider: () -> List<IAdapter<out T>>) :
    ReadOnlyProperty<P, FastAdapter<T>> {
    private val fastAdapter: FastAdapter<T> by lazy {
        FastAdapter.with(itemAdaptersProvider())
    }

    override fun getValue(thisRef: P, property: KProperty<*>): FastAdapter<T> = fastAdapter
}

class FastItemAdapterDelegate<P, T : IItem<*>> : ReadOnlyProperty<P, FastItemAdapter<T>> {
    private val fastItemAdapter: FastItemAdapter<T> by lazy {
        FastItemAdapter()
    }

    override fun getValue(thisRef: P, property: KProperty<*>): FastItemAdapter<T> =
        fastItemAdapter
}
