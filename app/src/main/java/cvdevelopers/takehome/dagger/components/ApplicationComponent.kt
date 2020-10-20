package cvdevelopers.takehome.dagger.components

import cvdevelopers.takehome.TakeHomeApplication
import cvdevelopers.takehome.dagger.modules.ActivitySubcomponentsModule
import cvdevelopers.takehome.dagger.modules.ApplicationModule
import cvdevelopers.takehome.dagger.modules.DataModule
import cvdevelopers.takehome.dagger.modules.NetworkClientModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by CamiloVega on 10/7/17.
 */
@Singleton
@Component(modules = arrayOf(ApplicationModule::class, NetworkClientModule::class, DataModule::class, ActivitySubcomponentsModule::class))
interface ApplicationComponent {
  fun inject(app: TakeHomeApplication)
  fun activityComponent(): ActivityComponent.Factory
}