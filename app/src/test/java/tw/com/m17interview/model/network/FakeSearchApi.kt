package tw.com.m17interview.model.network

import okhttp3.Headers
import retrofit2.Call
import retrofit2.Response
import retrofit2.mock.Calls
import tw.com.m17interview.model.network.FakeSearchResults.failureMsg
import tw.com.m17interview.model.network.FakeSearchResults.get
import java.io.IOException

class FakeSearchApi: SearchApi {

    override fun searchUser(query: String, perPage: Int): Call<NetworkResponse> {

        failureMsg?.let {
            return Calls.failure(IOException(it))
        }

        val users: List<User> = get(query, fromIndex = 0, pageSize = perPage)
        val networkResponse = NetworkResponse(users)

        //todo It's a little bit dirty...
        val headers = Headers.of("link", "<https://localhost>; rel=\"next\", <https://12345>; rel=\"last\"")
        return Calls.response(Response.success(networkResponse, headers))
    }
}
