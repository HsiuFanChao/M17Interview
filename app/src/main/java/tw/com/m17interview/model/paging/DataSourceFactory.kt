package tw.com.m17interview.model.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import tw.com.m17interview.model.network.NetworkService
import tw.com.m17interview.util.ModelProcessHelper
import tw.com.m17interview.viewmodel.ItemViewModel

class DataSourceFactory(
    private val networkService: NetworkService,
    private val modelProcessHelper: ModelProcessHelper,
    private val userName: String) : DataSource.Factory<String, ItemViewModel>() {

    val dataSource = MutableLiveData<PagedUserDataSource>()

    override fun create(): DataSource<String, ItemViewModel> {

        val source = PagedUserDataSource(networkService, modelProcessHelper, userName)
        dataSource.postValue(source)
        return source
    }
}