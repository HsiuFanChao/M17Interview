package tw.com.m17interview.model.network

import com.google.gson.annotations.SerializedName

data class User(@SerializedName("login") val login: String,
                @SerializedName("id") val id: Int,
                @SerializedName("avatar_url") val avatarUrl: String)

data class NetworkResponse(@SerializedName("items")val items: List<User>)