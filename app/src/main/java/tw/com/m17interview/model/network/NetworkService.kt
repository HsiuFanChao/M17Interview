package tw.com.m17interview.model.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class NetworkService {

    val networkExecutor: Executor by lazy {
        Executors.newFixedThreadPool(1)
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    val searchApi: SearchApi by lazy {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SearchApi::class.java)
    }
}