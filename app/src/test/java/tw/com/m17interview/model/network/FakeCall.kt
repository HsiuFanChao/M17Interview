package tw.com.m17interview.model.network

import com.google.gson.Gson
import okhttp3.*
import okio.Timeout

class FakeCall(private val userName: String, private val initialFromIndex: Int, private val pageSize: Int): Call {

    private var fromIndex = initialFromIndex

    override fun enqueue(responseCallback: Callback) {
        //TODO("Not yet implemented")
    }

    override fun isExecuted(): Boolean = true

    override fun timeout(): Timeout = Timeout()

    override fun clone() = FakeCall(
        userName,
        initialFromIndex,
        pageSize
    )

    override fun isCanceled() = false

    override fun cancel() {}

    override fun request(): Request = Request.Builder().url("http://127.0.0.1").build()

    override fun execute(): Response {
        if (FakeSearchResults.failureMsg.isNullOrEmpty()) {
            return makeOkResult()
        }
        return makeErrorResult()
    }

    /**
     * Generate an error result.
     *
     * ```
     * HTTP/1.1 500 Bad server day
     * Content-type: application/json
     *
     * {"cause": "not sure"}
     * ```
     */
    private fun makeErrorResult(): Response {
        return Response.Builder()
            .code(500)
            .request(request()) // cannot be null
            .protocol(Protocol.HTTP_1_1)
            .message("Bad server day")
            .body(
                ResponseBody.create(
                MediaType.get("application/json"),
                Gson().toJson(mapOf("cause" to "not sure"))))
            .build()
    }

    /**
     * Generate a success response.
     *
     * ```
     * HTTP/1.1 200 OK
     * Content-type: application/json
     *
     * "$random_string"
     * ```
     */
    private fun makeOkResult(): Response {

        val users = FakeSearchResults.get(
            userName,
            fromIndex = fromIndex,
            pageSize = pageSize
        )
        fromIndex += users.size

        return Response.Builder()
            .code(200)
            .request(request()) // cannot be null
            .protocol(Protocol.HTTP_1_1)
            .message("OK")
            .body(
                ResponseBody.create(
                    MediaType.get("application/json"),
                    Gson().toJson(NetworkResponse(users), NetworkResponse::class.java)
                )
            )
            .build()
    }
}