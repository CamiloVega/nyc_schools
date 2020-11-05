package cvdevelopers.takehome

import android.app.Application
import com.facebook.stetho.Stetho
import cvdevelopers.takehome.koin.dataModule
import cvdevelopers.takehome.koin.fragmentModule
import cvdevelopers.takehome.koin.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TakeHomeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TakeHomeApplication)
            // declare modules to use
            modules(networkModule, dataModule, fragmentModule)
        }
        Stetho.initializeWithDefaults(this)
    }

}