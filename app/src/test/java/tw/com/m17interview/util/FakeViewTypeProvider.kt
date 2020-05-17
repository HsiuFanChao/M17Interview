package tw.com.m17interview.util

import tw.com.m17interview.constants.DEFAULT_VIEW_TYPE

class FakeViewTypeProvider: RandomViewTypeProvider {
    override fun getNextViewType(): Int = DEFAULT_VIEW_TYPE
}