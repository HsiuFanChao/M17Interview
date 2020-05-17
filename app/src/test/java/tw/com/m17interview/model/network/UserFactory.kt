package tw.com.m17interview.model.network

import java.util.concurrent.atomic.AtomicInteger

class UserFactory {

    private val counter = AtomicInteger(0)

    private fun createUser(userName : String) : User {

        val id = counter.incrementAndGet()

        return User(
            login = userName,
            id = id,
            avatarUrl = ""
        )
    }

    fun createUserList(userName: String, length: Int): List<User> {
        return (0 until length).map { createUser(userName + it) }
    }
}