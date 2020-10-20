package cvdevelopers.takehome.dagger.modules

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.navArgs
import cvdevelopers.takehome.dagger.vm.ViewModelKey
import cvdevelopers.takehome.ui.schooldetails.SchoolDetailsFragment
import cvdevelopers.takehome.ui.schooldetails.SchoolDetailsFragmentArgs
import cvdevelopers.takehome.ui.schooldetails.SchoolDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named

@Module(includes = [FragmentDetailsModule.ProvidesModule::class])
abstract class FragmentDetailsModule(private val fragment: SchoolDetailsFragment) {

  @Binds
  @IntoMap
  @ViewModelKey(SchoolDetailsViewModel::class)
  abstract fun providesSchoolDetailsViewModel(director: SchoolDetailsViewModel): ViewModel

  @Module
  object ProvidesModule {
    @Provides
    @Named("schoolDbn")
    fun providesSchoolDbn(fragment: SchoolDetailsFragment) = fragment.navArgs<SchoolDetailsFragmentArgs>().value.schoolDbn
  }
}