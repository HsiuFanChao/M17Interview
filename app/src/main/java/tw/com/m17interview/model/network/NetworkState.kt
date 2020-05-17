package tw.com.m17interview.model.network

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

sealed class NetworkState(val status: Status,
                          val msg: String? = null) {

    override fun equals(other: Any?): Boolean {
        if (other is NetworkState) {
            return status == other.status && msg == other.msg
        }
        return false
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (msg?.hashCode() ?: 0)
        return result
    }

    object LOADED: NetworkState(Status.SUCCESS)
    object LOADING: NetworkState(Status.RUNNING)
    class FAILED(msg: String? = ""): NetworkState(Status.FAILED, msg)
}