package cvdevelopers.takehome.dagger.components

import cvdevelopers.takehome.dagger.modules.FragmentModule
import cvdevelopers.takehome.dagger.scopes.FragmentScope
import cvdevelopers.takehome.ui.schoollist.SchoolListFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {
    fun inject(target: SchoolListFragment)

    fun fragmentDetailsComponent(): FragmentDetailsComponent.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create(): FragmentComponent
    }
}
