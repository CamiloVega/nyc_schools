package cvdevelopers.takehome.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cvdevelopers.takehome.cache.dao.SchoolCacheDao
import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.models.SchoolScores

@Database(entities = [School::class, SchoolScores::class], version = 1)
abstract class TakehomeDatabase: RoomDatabase() {

  abstract fun schoolCacheDao(): SchoolCacheDao

  companion object {
    @Volatile
    private var INSTANCE: TakehomeDatabase? = null

    fun getDatabase(context: Context): TakehomeDatabase {
      val tempInstance = INSTANCE
      if (tempInstance != null) {
        return tempInstance
      }
      synchronized(this) {
        val instance = Room.databaseBuilder(
            context.applicationContext,
            TakehomeDatabase::class.java,
            "room_database"
        ).build()
        INSTANCE = instance
        return instance
      }
    }
  }
}