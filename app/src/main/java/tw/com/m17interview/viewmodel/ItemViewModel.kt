package tw.com.m17interview.viewmodel

import tw.com.m17interview.model.network.User

/**
 * A view model that is used by each item in recycler view
 *
 * @param viewType Used by recycler view's adapter
 * @param id Identifier
 * @param data A list of users that should be displayed
 */
data class ItemViewModel(val viewType: Int, val id: Int, val data: List<User>)