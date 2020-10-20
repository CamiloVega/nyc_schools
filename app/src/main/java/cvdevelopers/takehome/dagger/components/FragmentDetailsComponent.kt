package cvdevelopers.takehome.dagger.components

import cvdevelopers.takehome.dagger.modules.FragmentDetailsModule
import cvdevelopers.takehome.ui.schooldetails.SchoolDetailsFragment
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [FragmentDetailsModule::class])
interface FragmentDetailsComponent {
    fun inject(target: SchoolDetailsFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: SchoolDetailsFragment): FragmentDetailsComponent
    }
}
