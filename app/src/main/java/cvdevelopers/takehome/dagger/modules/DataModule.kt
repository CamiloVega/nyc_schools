package cvdevelopers.takehome.dagger.modules

import android.app.Application
import cvdevelopers.takehome.cache.TakehomeDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

  @Provides
  @Singleton
  fun providesDatabase(application: Application) = TakehomeDatabase.getDatabase(application)

  @Provides
  @Singleton
  fun providesSchoolCacheDao(database: TakehomeDatabase) = database.schoolCacheDao()
}