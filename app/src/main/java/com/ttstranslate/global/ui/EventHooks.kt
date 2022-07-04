package com.ttstranslate.global.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.CustomEventHook


class SimpleClickEventHook<FAItem : IItem<*>>(
    private val boundViewRes: Int,
    private val clickListener: (View, FAItem, position: Int) -> Unit
) : ClickEventHook<FAItem>() {
    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? = viewHolder.itemView
        .findViewById(this.boundViewRes)

    override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<FAItem>, item: FAItem) =
        clickListener(v, item, position)
}

class SimpleCustomEventHook<FAItem : IItem<*>>(
    private val boundViewRes: Int,
    private val eventListener: (vh: RecyclerView.ViewHolder, view: View, position: Int) -> Unit
) : CustomEventHook<FAItem>() {
    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? = viewHolder.itemView
        .findViewById(this.boundViewRes)

    override fun attachEvent(view: View, viewHolder: RecyclerView.ViewHolder) {
        view.post {
            val item = FastAdapter.getHolderAdapterItem<FAItem>(viewHolder)
            val position = viewHolder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                eventListener(viewHolder, view, position)
            }
        }
    }
}
