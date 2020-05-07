package tw.com.m17interview.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import tw.com.m17interview.model.network.NetworkState
import tw.com.m17interview.viewmodel.ItemViewModel

data class RepoResult(
    // The LiveData of paged lists for the UI to observe
    val pagedList: LiveData<PagedList<ItemViewModel>>,

    // Represents the network request status to show to the user
    val networkState: LiveData<NetworkState>,

    // Represents the refresh status to show to the user.
    val initLoadState: LiveData<NetworkState>,

    // Provide a retry function for caller usage.
    // (Similar to java callback, return this to viewModel)
    val retry: () -> Unit)