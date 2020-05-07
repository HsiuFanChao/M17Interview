package tw.com.m17interview.model.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.Request
import tw.com.m17interview.model.network.NetworkResponse
import tw.com.m17interview.model.network.NetworkService
import tw.com.m17interview.model.network.NetworkState
import tw.com.m17interview.util.ModelProcessHelper.convertToItemViewModel
import tw.com.m17interview.viewmodel.ItemViewModel
import java.io.IOException

class PagedUserDataSource(private val networkService: NetworkService,
                          private val userName: String): PageKeyedDataSource<String, ItemViewModel>() {

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    private var retry: (() -> Any)? = null

    fun executeRetry() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            networkService.networkExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, ItemViewModel>) {

        if (userName.isEmpty()) {
            callback.onResult(listOf(), null, null)
            return
        }

        initialLoad.postValue(NetworkState.LOADING)

        try {

            val response = networkService.searchApi.searchUser(query = userName).execute()
            if (response.isSuccessful) {
                val nextPageUrl = parseNextPageUrl(response.headers())
                val userList = response.body()?.items ?: listOf()

                retry = null
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)

                callback.onResult(convertToItemViewModel(userList), null, nextPageUrl)
            } else {
                initialLoad.postValue(NetworkState.error("error code: ${response.code()}"))
                networkState.postValue(NetworkState.error("error code: ${response.code()}"))
                retry = {
                    loadInitial(params, callback)
                }
            }

        } catch (ioException: IOException) {
            initialLoad.postValue(NetworkState.error(ioException.message))
            networkState.postValue(NetworkState.error(ioException.message))
            retry = {
                loadInitial(params, callback)
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, ItemViewModel>) {

        val url = params.key
        if (url.isEmpty()) {
            return
        }
        networkState.postValue(NetworkState.LOADING)

        try {
            val rawResponse = networkService.okHttpClient.newCall(Request.Builder().url(url).build()).execute()
            if (rawResponse.isSuccessful) {
                networkState.postValue(NetworkState.LOADED)
                retry = null

                val nextPageUrl = parseNextPageUrl(rawResponse.headers())
                val response: NetworkResponse = Gson().fromJson(rawResponse.body()?.string(), NetworkResponse::class.java)
                val userList = response.items

                callback.onResult(convertToItemViewModel(userList), nextPageUrl)
            } else {
                networkState.postValue(NetworkState.error("error code: ${rawResponse.code()}"))
                retry = {
                    loadAfter(params, callback)
                }
            }
        } catch (ioException: IOException) {
            networkState.postValue(NetworkState.error(ioException.message))
            retry = {
                loadAfter(params, callback)
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, ItemViewModel>) {
        // no need
    }

    private fun parseNextPageUrl(headers: Headers): String? {

        // link: <https://api.github.com/search/users?q=tom&page=2>; rel="next", <https://api.github.com/search/users?q=tom&page=34>; rel="last"
        val links = headers.get("link")?.split("; rel=\"next\"")
        if (!links.isNullOrEmpty()){

            val prevLinkWithBracket = links[0] // <https://api.github.com/search/users?q=tom&page=2>

            return if (prevLinkWithBracket.isNotEmpty() && prevLinkWithBracket.length > 1) {
                prevLinkWithBracket.substring(1, prevLinkWithBracket.length - 1)
            } else null
        }
        return null
    }
}