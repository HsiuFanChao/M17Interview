package tw.com.m17interview

import androidx.lifecycle.*
import tw.com.m17interview.model.Repository

class MainViewModel(private val repo: Repository): ViewModel() {

    private val searchCount = MutableLiveData<Int>()

    private var userInput = ""

    private val repoResult = searchCount.map { repo.searchUser(userInput) }

    val pagedList = repoResult.switchMap { it.pagedList }
    val networkState = repoResult.switchMap { it.networkState }
    val initLoadState = repoResult.switchMap { it.initLoadState }

    fun searchUser(userInput: String) {

        this.userInput = userInput
        val oldCount = searchCount.value
        if (oldCount != null) {
            searchCount.value = oldCount+1
        } else {
            searchCount.value = 1
        }
    }

    fun retry() {
        repoResult.value?.retry?.invoke()
    }
}