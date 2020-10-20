package cvdevelopers.takehome.dagger.components

import cvdevelopers.takehome.MainActivity
import cvdevelopers.takehome.dagger.modules.FragmentSubcomponentsModule
import cvdevelopers.takehome.dagger.scopes.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [FragmentSubcomponentsModule::class])
interface ActivityComponent {

    fun inject(target: MainActivity)

    fun fragmentComponent(): FragmentComponent.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivityComponent
    }
}
