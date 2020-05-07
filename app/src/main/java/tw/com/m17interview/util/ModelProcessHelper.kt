package tw.com.m17interview.util

import tw.com.m17interview.constants.DEFAULT_VIEW_TYPE
import tw.com.m17interview.constants.GRID_VIEW_TYPE
import tw.com.m17interview.viewmodel.ItemViewModel
import tw.com.m17interview.model.network.User
import kotlin.random.Random

/**
 * 關於此次作業，我提出了可能會遇到的問題．
 * 也就是當資料量不足的時候，如果還是顯示 1st style的 UI，則介面會留下空白．
 *
 * 當時我提出了兩種解決方案．
 * 但後來我想到了第三種解決方案，也就是在資料量不足的時候，不要顯示為 1st style 的UI.
 *
 * 但因為人資回信都要半天以上，避免此次作業做不完，
 * 所以我直接採用了第三種解決方案．
 */
object ModelProcessHelper {

    fun convertToItemViewModel(userList: List<User>): List<ItemViewModel> {

        val items = mutableListOf<ItemViewModel>()
        var index = 0;
        val random = Random(100)
        while (index < userList.size) {

            val viewType = random.nextInt(3) + 1

            when (viewType) {
                GRID_VIEW_TYPE -> {

                    // pick two users
                    if (index + 1 < userList.size) {
                        val user1 = userList[index]
                        val user2 = userList[index + 1]
                        items.add(
                            ItemViewModel(
                                viewType,
                                user1.id,
                                listOf(user1, user2)
                            )
                        )
                        index+=2
                    } else {
                        val user = userList[index]
                        items.add(
                            ItemViewModel(
                                DEFAULT_VIEW_TYPE,
                                user.id,
                                listOf(user)
                            )
                        )
                        index++
                    }
                }
                else -> {
                    val user = userList[index]
                    items.add(
                        ItemViewModel(
                            viewType,
                            user.id,
                            listOf(user)
                        )
                    )
                    index++
                }
            }
        }
        return items
    }
}