package cvdevelopers.takehome.dagger.modules

import android.app.Application
import cvdevelopers.takehome.cache.TakehomeDatabase
import cvdevelopers.takehome.cache.dao.SchoolCacheDao
import cvdevelopers.takehome.datarepository.SchoolDataRepository
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
  fun providesSchoolCacheDao(database: TakehomeDatabase): SchoolCacheDao = database.schoolCacheDao()

  @Provides
  @Singleton
  fun providesSchoolDataRepository(schoolCacheDao: SchoolCacheDao) = SchoolDataRepository(schoolCacheDao)
}