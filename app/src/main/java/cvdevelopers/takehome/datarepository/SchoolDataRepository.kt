package cvdevelopers.takehome.datarepository

import cvdevelopers.takehome.cache.dao.SchoolCacheDao
import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.models.SchoolScores
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class SchoolDataRepository (
        private val schoolCacheDao: SchoolCacheDao
) {

    fun getAllSchoolsInCache() = schoolCacheDao.getAllSchool()

    fun getSchoolForDbn(schoolDbn: String): Single<School> = schoolCacheDao.getSchoolForDbn(schoolDbn).subscribeOn(Schedulers.io())

    fun getScoresForDbn(schoolDbn: String): Maybe<SchoolScores> = schoolCacheDao.getScoresForDbn(schoolDbn).subscribeOn(Schedulers.io())

    fun clearSchoolsCache() = Completable.mergeArray(
            schoolCacheDao.clearSchoolsCache(),
            schoolCacheDao.clearScoresCache())
            .subscribeOn(Schedulers.io())
}