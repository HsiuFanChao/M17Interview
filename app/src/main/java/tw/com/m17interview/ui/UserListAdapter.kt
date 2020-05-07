package tw.com.m17interview.ui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import tw.com.m17interview.constants.BANNER_VIEW_TYPE
import tw.com.m17interview.constants.DEFAULT_VIEW_TYPE
import tw.com.m17interview.constants.GRID_VIEW_TYPE
import tw.com.m17interview.viewmodel.ItemViewModel

class UserListAdapter: PagedListAdapter<ItemViewModel, ViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ItemViewModel>() {
            override fun areItemsTheSame(oldItem: ItemViewModel, newItem: ItemViewModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ItemViewModel, newItem: ItemViewModel): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            GRID_VIEW_TYPE -> GridStyleViewHolder.inflate(parent)
            BANNER_VIEW_TYPE -> BannerStyleViewHolder.inflate(parent)
            else -> BlockStyleViewHolder.inflate(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.viewType ?: DEFAULT_VIEW_TYPE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}