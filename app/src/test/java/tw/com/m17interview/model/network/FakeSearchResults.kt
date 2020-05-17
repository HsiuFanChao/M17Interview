package tw.com.m17interview.model.network

object FakeSearchResults {

    // Key: user input
    // Value: Returned value from API side
    private val model = mutableMapOf<String, MutableList<User>>()

    /**
     * Act as a failure signal.
     * If it is null or empty, return SUCCESS response, otherwise return FAILED response
     */
    var failureMsg: String? = null

    fun get(query: String, fromIndex: Int, pageSize: Int): List<User> {

        val list: List<User> = model[query]?: listOf()

        return if (fromIndex < list.size) {
            if (fromIndex + pageSize <= list.size) {
                list.subList(fromIndex, fromIndex + pageSize)
            } else {
                list.subList(fromIndex, list.size)
            }
        } else listOf()
    }

    fun add(userName: String, users: List<User>) {
        val existingUsers = model.getOrPut(userName) {
            mutableListOf()
        }
        existingUsers.addAll(users)
    }

    fun clearAll() {
        model.clear()
    }
}