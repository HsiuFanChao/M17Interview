package tw.com.m17interview.koin

import okhttp3.OkHttpClient
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tw.com.m17interview.MainViewModel
import tw.com.m17interview.model.Repository
import tw.com.m17interview.model.network.NetworkService
import tw.com.m17interview.model.network.SearchApi
import tw.com.m17interview.util.ModelProcessHelper
import java.util.concurrent.Executors

val appModule = module {

    single {
        ModelProcessHelper()
    }

    single {

        val networkExecutor = Executors.newFixedThreadPool(1)
        val okHttpClient = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val searchApi = retrofit.create(SearchApi::class.java)

        NetworkService(networkExecutor, okHttpClient, searchApi)
    }

    single {
        Repository(get(), get())
    }

    viewModel {
        MainViewModel(get())
    }
}