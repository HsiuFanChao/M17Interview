package tw.com.m17interview.koin

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tw.com.m17interview.MainViewModel
import tw.com.m17interview.model.Repository
import tw.com.m17interview.model.network.NetworkService

val appModule = module {

    single {
        NetworkService()
    }

    single {
        Repository(get())
    }

    viewModel {
        MainViewModel(get())
    }
}