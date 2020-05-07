package tw.com.m17interview

import android.app.Application
import org.koin.core.context.startKoin
import tw.com.m17interview.koin.appModule

class M17InterviewApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(listOf(appModule))
        }
    }
}