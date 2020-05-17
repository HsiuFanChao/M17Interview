package tw.com.m17interview.model.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import tw.com.m17interview.constants.PAGE_SIZE

interface SearchApi {

    @GET("search/users")
    fun searchUser(@Query("q") query: String,
                   @Query("per_page") perPage: Int = PAGE_SIZE): Call<NetworkResponse>

}