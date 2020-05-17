package tw.com.m17interview.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import okhttp3.*
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.*
import tw.com.m17interview.constants.PAGE_SIZE
import tw.com.m17interview.model.network.*
import tw.com.m17interview.model.network.FakeSearchResults.failureMsg
import tw.com.m17interview.util.FakeViewTypeProvider
import tw.com.m17interview.util.ModelProcessHelper
import tw.com.m17interview.viewmodel.ItemViewModel
import java.util.concurrent.Executor

class RepositoryTest {

    @Suppress("unused")
    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()

    private val networkExecutor = Executor { command -> command.run() }
    private val okHttpClient = spy(OkHttpClient.Builder().build())
    private val fakeSearchApi = FakeSearchApi()
    private val networkService = NetworkService(networkExecutor, okHttpClient, fakeSearchApi)

    private val modelProcessHelper = ModelProcessHelper(FakeViewTypeProvider())

    private val repository = Repository(networkService, modelProcessHelper)

    private val userFactory = UserFactory()

    private val query = "query"

    @After
    fun tear() {
        FakeSearchResults.clearAll()
    }

    /**
     * asserts that empty list works fine
     */
    @Test
    fun emptyList() {
        val listing = repository.searchUser(query)
        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
    }

    /**
     * asserts that a list w/ single item is loaded properly
     */
    @Test
    fun oneItem() {

        // Given
        val users = userFactory.createUserList(userName = query, length = 1)
        FakeSearchResults.add(query, users)

        // Action
        val repoResult = repository.searchUser(query)
        val pagedList = getPagedList(repoResult)

        // Assert
        val expected = modelProcessHelper.convertToItemViewModel(users)
        MatcherAssert.assertThat(pagedList, `is`(expected))
    }

    /**
     * asserts loading a full list in multiple pages
     */
    @Test
    fun verifyCompleteList() {

        // Given
        val users = userFactory.createUserList(userName = query, length = 30)
        FakeSearchResults.add(query, users)

        val fakeCall = FakeCall(
            query,
            initialFromIndex = PAGE_SIZE,
            pageSize = PAGE_SIZE
        )
        doReturn(fakeCall).`when`(okHttpClient).newCall(any())

        // Action
        val repoResult = repository.searchUser(query)
        // trigger loading of the whole list
        val pagedList = getPagedList(repoResult)
        pagedList.loadAllData()

        // Assert
        val expected = modelProcessHelper.convertToItemViewModel(users)
        MatcherAssert.assertThat(pagedList, `is`(expected))
    }

    /**
     * asserts the failure message when the initial load cannot complete
     */
    @Test
    fun failToLoadInitial() {
        //Given a failed network request
        failureMsg = "xxx"
        val listing = repository.searchUser(query)

        // Action
        // trigger load
        getPagedList(listing)

        // Assert
        val actual = getNetworkState(listing)
        val expected = NetworkState.FAILED("xxx")
        assert(expected == actual)
    }

    /**
     * asserts the retry logic when initial load request fails
     */
    @Test
    fun retryInInitialLoad() {
        // 1. Load initial, and make it failed
        // 1-1. Given
        val users = userFactory.createUserList(query, 1)
        FakeSearchResults.add(query, users)
        failureMsg = "xxx"

        // 1-2. Action
        val listing = repository.searchUser(query)
        // trigger load
        val pagedList = getPagedList(listing)

        // 1-3. Assert
        MatcherAssert.assertThat(pagedList.size, `is`(0))

        // 2. Load initial again, and make it success
        // prepare
        @Suppress("UNCHECKED_CAST")
        val networkObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.networkState.observeForever(networkObserver)

        // 2-1. Given
        failureMsg = null

        // 2-2. Action
        listing.retry()

        // 2-3. Assert
        MatcherAssert.assertThat(pagedList.size, `is`(1))
        val actual: NetworkState = getNetworkState(listing)!!
        assert(actual == NetworkState.LOADED)

        val inOrder = Mockito.inOrder(networkObserver)
        inOrder.verify(networkObserver).onChanged(NetworkState.FAILED("xxx"))
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
        inOrder.verifyNoMoreInteractions()
    }

    /**
     * asserts the retry logic when initial load succeeds but subsequent loads fails
     */
    @Test
    fun retryAfterSubsequentLoadsFails() {

        // 1. Load initial, and make it success
        // 1-1. Given
        val users = userFactory.createUserList(query, 40)
        FakeSearchResults.add(query, users)

        // 1-2. Action
        val repoResult = repository.searchUser(query)
        // trigger load
        val pagedList = getPagedList(repoResult)

        // 1-3. Assert
        MatcherAssert.assertThat(
            "test sanity, we should not load everything",
            pagedList.size < users.size, `is`(true)
        )
        assert(getNetworkState(repoResult) == NetworkState.LOADED)

        // 2. Make the subsequent call failed
        // 2-1. Given
        failureMsg = "fail"
        val fakeCall = FakeCall(
            query,
            initialFromIndex = PAGE_SIZE,
            pageSize = PAGE_SIZE
        )
        doReturn(fakeCall).`when`(okHttpClient).newCall(any())

        // 2-2. Action
        pagedList.loadAllData()

        // 2-3. Assert
        assert(getNetworkState(repoResult)?.status == NetworkState.FAILED().status)

        // 3. Make the subsequent call success
        // 3-1. Given
        failureMsg = null

        // 3-2. Action
        repoResult.retry()
        pagedList.loadAllData()

        // 3-3. Assert
        assert(getNetworkState(repoResult) == NetworkState.LOADED)

        // Note: 一次轉換40筆資料, 和分2批轉換20筆資料，random產生出的viewType會不同．
        // 所以必須要用FakeViewTypeProvider
        val expected = modelProcessHelper.convertToItemViewModel(users)
        MatcherAssert.assertThat(pagedList, `is`(expected))
    }

    /**
     * extract the latest paged list from the listing
     */
    private fun getPagedList(repoResult: RepoResult): PagedList<ItemViewModel> {
        val observer = LoggingObserver<PagedList<ItemViewModel>>()
        repoResult.pagedList.observeForever(observer)
        MatcherAssert.assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        return observer.value!!
    }

    /**
     * extract the latest network state from the listing
     */
    private fun getNetworkState(repoResult: RepoResult) : NetworkState? {
        val networkObserver = LoggingObserver<NetworkState>()
        repoResult.networkState.observeForever(networkObserver)
       //MatcherAssert.assertThat(networkState.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        return networkObserver.value
    }

    /**
     * simple observer that logs the latest value it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        var value : T? = null
        override fun onChanged(t: T?) {
            this.value = t
        }
    }

    private fun <T> PagedList<T>.loadAllData() {
        do {
            val oldSize = this.loadedCount
            this.loadAround(this.size - 1)
        } while (this.size != oldSize)
    }
}