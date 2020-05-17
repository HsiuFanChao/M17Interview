package tw.com.m17interview.model.network

import okhttp3.OkHttpClient
import java.util.concurrent.Executor

class NetworkService(val networkExecutor: Executor,
                     val okHttpClient: OkHttpClient,
                     val searchApi: SearchApi)