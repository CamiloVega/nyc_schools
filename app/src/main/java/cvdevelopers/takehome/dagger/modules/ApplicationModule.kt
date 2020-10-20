package cvdevelopers.takehome.dagger.modules

import android.app.Application
import cvdevelopers.takehome.TakeHomeApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val app: TakeHomeApplication) {

    @Provides
    @Singleton
    fun provideApplication(): Application = app

}