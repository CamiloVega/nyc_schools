package cvdevelopers.takehome.datarepository

import androidx.paging.DataSource
import cvdevelopers.takehome.cache.dao.SchoolCacheDao
import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.models.SchoolScores
import cvdevelopers.test.utils.TestUtils
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.MaybeSubject
import io.reactivex.subjects.SingleSubject
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class SchoolDataRepositoryTest {

    @MockK
    lateinit var schoolCacheDao: SchoolCacheDao

    lateinit var SUT: SchoolDataRepository

    @Before
    fun setUp() {
        TestUtils.setupRxjavaForOneThreadedTest()
        MockKAnnotations.init(this)
        SUT = SchoolDataRepository(schoolCacheDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllSchoolsInCache calls the school dao and gets all the schools data factory, unmodified`() {
        val resourceFactoryMock = mockk<DataSource.Factory<Int, School>>()
        every { schoolCacheDao.getAllSchool() } returns resourceFactoryMock
        val result = SUT.getAllSchoolsInCache()
        verify { schoolCacheDao.getAllSchool() }
        assertEquals(resourceFactoryMock, result)
    }

    @Test
    fun `getSchoolForDbn calls the school dao to get the school for dbn if a school is found, then it returns it to the subscriber`() {
        val schoolSubject = SingleSubject.create<School>()
        val mockSchool = mockk<School>()
        val schoolDbn = "schoolDBN"
        every { schoolCacheDao.getSchoolForDbn(schoolDbn) } returns schoolSubject
        val result = SUT.getSchoolForDbn(schoolDbn).test()
        result.assertNoValues()
        schoolSubject.onSuccess(mockSchool)
        result.assertValue(mockSchool)
    }

    @Test
    fun `getSchoolForDbn calls the school dao to get the school for dbn if there is an error, the error is propagated to the subscriber`() {
        val cacheError = Throwable("Error fetching school from cache")
        val schoolDbn = "schoolDBN"
        every { schoolCacheDao.getSchoolForDbn(schoolDbn) } returns Single.error(cacheError)
        val result = SUT.getSchoolForDbn(schoolDbn).test()
        result.assertNoValues()
        result.assertError(cacheError)
    }

    @Test
    fun `getScoresForDbn calls the school dao to get the school for dbn if a school is found, then it returns it to the subscriber`() {
        val scoresSubject = MaybeSubject.create<SchoolScores>()
        val mockScores = mockk<SchoolScores>()
        val schoolDbn = "schoolDBN"
        every { schoolCacheDao.getScoresForDbn(schoolDbn) } returns scoresSubject
        val result = SUT.getScoresForDbn(schoolDbn).test()
        result.assertNoValues()
        scoresSubject.onSuccess(mockScores)
        result.assertValue(mockScores)
        result.assertNoErrors()
    }

    @Test
    fun `getScoresForDbn calls the school dao to get the school for dbn if the scores for a school are not found, then it emits no value to the subscriber`() {
        val schoolDbn = "schoolDBN"
        every { schoolCacheDao.getScoresForDbn(schoolDbn) } returns Maybe.empty()
        val result = SUT.getScoresForDbn(schoolDbn).test()
        result.assertNoValues()
        result.assertComplete()
        result.assertNoErrors()
    }

    @Test
    fun `getScoresForDbn calls the school dao to get the school for dbn,  if there is an error, the error is propagated to the subscriber`() {
        val cacheError = Throwable("Error fetching scores from cache")
        val schoolDbn = "schoolDBN"
        every { schoolCacheDao.getScoresForDbn(schoolDbn) } returns Maybe.error(cacheError)
        val result = SUT.getScoresForDbn(schoolDbn).test()
        result.assertNoValues()
        result.assertError(cacheError)
    }


    @Test
    fun `clearSchoolsCache calls the cache to clear all data`() {
        every { schoolCacheDao.clearSchoolsCache() } returns Completable.complete()
        every { schoolCacheDao.clearScoresCache() } returns Completable.complete()
        val result = SUT.clearSchoolsCache().test()
        verify { schoolCacheDao.clearSchoolsCache() }
        verify { schoolCacheDao.clearScoresCache() }
        result.assertComplete()
        result.assertNoErrors()
    }

    @Test
    fun `clearSchoolsCache calls the cache to clear all data, if there is an error, the subscriber gets the error`() {
        val cacheError = Throwable("Error clearing scores from cache")
        every { schoolCacheDao.clearSchoolsCache() } returns Completable.complete()
        every { schoolCacheDao.clearScoresCache() } returns Completable.error(cacheError)
        val result = SUT.clearSchoolsCache().test()
        verify { schoolCacheDao.clearSchoolsCache() }
        verify { schoolCacheDao.clearScoresCache() }
        result.assertError(cacheError)
    }
}