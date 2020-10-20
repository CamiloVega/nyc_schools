package cvdevelopers.takehome

import android.app.Application
import com.facebook.stetho.Stetho
import cvdevelopers.takehome.dagger.components.ApplicationComponent
import cvdevelopers.takehome.dagger.components.DaggerApplicationComponent
import cvdevelopers.takehome.dagger.modules.ApplicationModule

class TakeHomeApplication : Application() {

    val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this);
        Stetho.initializeWithDefaults(this)
    }

}