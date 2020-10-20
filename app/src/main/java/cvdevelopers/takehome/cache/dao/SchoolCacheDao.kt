package cvdevelopers.takehome.cache.dao

import androidx.paging.DataSource
import androidx.room.*
import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.models.SchoolScores
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single


@Dao
interface SchoolCacheDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertSchoolList(schoolList: List<School>): Completable

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertSchoolScores(schoolScore: List<SchoolScores>): Completable

  @Query("SELECT * FROM school")
  fun getAllSchool(): DataSource.Factory<Int, School>

  @Query("DELETE FROM school")
  fun clearSchoolsCache(): Completable

  @Query("DELETE FROM schoolscores")
  fun clearScoresCache(): Completable

  @Query("SELECT *  FROM school WHERE dbn = :dbn")
  fun getSchoolForDbn(dbn: String): Single<School>

  @Query("SELECT *  FROM schoolscores WHERE dbn = :dbn")
  fun getScoresForDbn(dbn: String): Maybe<SchoolScores>

}