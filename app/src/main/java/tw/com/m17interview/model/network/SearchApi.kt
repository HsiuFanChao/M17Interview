package tw.com.m17interview.model.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search/users")
    fun searchUser(@Query("q") query: String,
                   @Query("per_page") perPage: Int = 20): Call<NetworkResponse>

}