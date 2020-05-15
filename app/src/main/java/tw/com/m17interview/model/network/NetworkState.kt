package tw.com.m17interview.model.network

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

//@Suppress("DataClassPrivateConstructor")
//data class NetworkState private constructor(
//    val status: Status,
//    val msg: String? = null) {
//    companion object {
//        val LOADED =
//            NetworkState(Status.SUCCESS)
//        val LOADING =
//            NetworkState(Status.RUNNING)
//        fun error(msg: String?) = NetworkState(
//            Status.FAILED,
//            msg
//        )
//    }
//}

sealed class NetworkState(val status: Status,
                          val msg: String? = null) {

    object LOADED: NetworkState(Status.SUCCESS)
    object LOADING: NetworkState(Status.RUNNING)
    class FAILED(msg: String?): NetworkState(Status.FAILED, msg)
}