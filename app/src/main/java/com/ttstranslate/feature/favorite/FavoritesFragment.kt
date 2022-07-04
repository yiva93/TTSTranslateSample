package com.ttstranslate.feature.favorite

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.paged.ExperimentalPagedSupport
import com.mikepenz.fastadapter.paged.PagedModelAdapter
import com.ttstranslate.R
import com.ttstranslate.databinding.FragmentFavoritesBinding
import com.ttstranslate.domain.model.Translation
import com.ttstranslate.global.ui.EmptyDividerDecoration
import com.ttstranslate.global.ui.SimpleClickEventHook
import com.ttstranslate.global.ui.fragment.BaseFragment
import com.ttstranslate.global.utils.BindingProvider
import com.ttstranslate.global.utils.common.addSystemWindowInsetToMargin
import com.ttstranslate.global.utils.createFastAdapter
import com.ttstranslate.global.utils.viewObserve
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(ExperimentalPagedSupport::class)
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {
    private val viewModel: FavoritesViewModel by viewModel()

    override val bindingProvider: BindingProvider<FragmentFavoritesBinding> =
        FragmentFavoritesBinding::inflate

    private lateinit var itemAdapter: PagedModelAdapter<Translation, IItem<*>>
    private lateinit var fastAdapter: FastAdapter<IItem<*>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.container.addSystemWindowInsetToMargin(top = true)
        bindRecycler()
        initObservers()
        initListeners()
    }

    private fun initAdapter() {
        itemAdapter = PagedModelAdapter<Translation, IItem<*>>(
            asyncDifferConfig = AsyncDifferConfig.Builder(object :
                DiffUtil.ItemCallback<Translation>() {
                override fun areItemsTheSame(oldItem: Translation, newItem: Translation): Boolean {
                    return oldItem.favoriteId == newItem.favoriteId
                }

                override fun areContentsTheSame(
                    oldItem: Translation,
                    newItem: Translation
                ): Boolean {
                    return oldItem.textFrom == newItem.textFrom
                }
            }).build(),
            interceptor = {
                FavoriteItem(it) { id ->
                    viewModel.onRemoveFavorite(id)
                }
            })
        fastAdapter = createFastAdapter(itemAdapter)
        fastAdapter.apply {
            setHasStableIds(true)
            addEventHook(SimpleClickEventHook(R.id.itemContainer) { _, faItem, _ ->
                if (faItem is FavoriteItem)
                    viewModel.onFavoriteItemClick(faItem.favorite)
            })
        }
    }

    private fun bindRecycler() {
        binding.recyclerView.apply {
            adapter = fastAdapter
            addItemDecoration(
                EmptyDividerDecoration(
                    requireContext(),
                    R.dimen.baseline_grid_small,
                    applyOutsideDecoration = false
                )
            )
        }
    }

    private fun initObservers() {
        viewObserve(viewModel.pagingDataInitialized) {
            if (it)
                viewObserve(viewModel.favoritesLiveData) { data ->
                    itemAdapter.submitList(data)
                }
        }
    }

    private fun initListeners() {

    }
}
