package tw.com.m17interview.model

import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import tw.com.m17interview.model.network.NetworkService
import tw.com.m17interview.model.paging.DataSourceFactory

class Repository(private val networkService: NetworkService) {

    fun searchUser(userName: String): RepoResult {

        val factory = DataSourceFactory(networkService, userName)

        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setPrefetchDistance(4)
            .build()

        val livePagedList = LivePagedListBuilder(factory, config).build()

        return RepoResult(
            pagedList = livePagedList,
            networkState = factory.dataSource.switchMap { it.networkState },
            retry = { factory.dataSource.value?.executeRetry() },
            initLoadState = factory.dataSource.switchMap { it.initialLoad }
        )
    }
}