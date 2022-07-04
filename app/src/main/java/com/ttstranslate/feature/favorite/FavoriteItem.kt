package com.ttstranslate.feature.favorite

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.postDelayed
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.ttstranslate.R
import com.ttstranslate.databinding.ItemFavoriteBinding
import com.ttstranslate.domain.model.Translation
import com.zerobranch.layout.SwipeLayout

private const val SWIPE_CLOSE_DELAY_MS = 1000L

class FavoriteItem(
    val favorite: Translation,
    val onDelete: ((Long?) -> Unit)? = null,
) :
    ModelAbstractBindingItem<Translation, ItemFavoriteBinding>(favorite) {
    override var identifier: Long = favorite.favoriteId ?: 0

    override val type: Int = R.id.favoriteItem

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemFavoriteBinding = ItemFavoriteBinding.inflate(inflater, parent, false)

    override fun bindView(binding: ItemFavoriteBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        with(binding) {
            textFrom.text = favorite.textFrom
            textResult.text = favorite.result
        }
        binding.root.setOnActionsListener(object : SwipeLayout.SwipeActionsListener {
            override fun onOpen(direction: Int, isContinuous: Boolean) {
                favorite.favoriteId?.let { id ->
                    onDelete?.invoke(id) ?: closeDelayed()
                } ?: closeDelayed()
            }

            fun closeDelayed() {
                Handler(Looper.getMainLooper())
                    .postDelayed(SWIPE_CLOSE_DELAY_MS) { binding.root.close() }
            }

            override fun onClose() {
            }
        })
    }
}