package cvdevelopers.takehome.pagination

import cvdevelopers.takehome.api.SchoolApiEndpoint
import cvdevelopers.takehome.cache.dao.SchoolCacheDao
import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.models.SchoolScores
import cvdevelopers.takehome.utils.ErrorMessagesDispatcher
import cvdevelopers.test.utils.TestUtils
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Completable
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class SchoolListBoundaryCallbackTest {

    @MockK
    lateinit var schoolCacheDao: SchoolCacheDao
    @MockK
    lateinit var schoolApiEndpoint: SchoolApiEndpoint

    var errorMessagesDispatcherSender = ErrorMessagesDispatcher()

    lateinit var SUT: SchoolListBoundaryCallback

    @Before
    fun setUp() {
        TestUtils.setupRxjavaForOneThreadedTest()
        MockKAnnotations.init(this)
        SUT = SchoolListBoundaryCallback(schoolCacheDao, schoolApiEndpoint, errorMessagesDispatcherSender)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `onZeroItemsLoaded fetches both api and school scores and inserts them on the data base`() {
        val schoolListMock = mockk<List<School>>()
        val scoresListMock = mockk<List<SchoolScores>>()
        every { schoolApiEndpoint.getSchools() } returns Single.just(schoolListMock)
        every { schoolApiEndpoint.getSchoolsSatScores() } returns Single.just(scoresListMock)
        every { schoolCacheDao.insertSchoolList(schoolListMock) } returns Completable.complete()
        every { schoolCacheDao.insertSchoolScores(scoresListMock) } returns Completable.complete()
        SUT.onZeroItemsLoaded()
        verify { schoolApiEndpoint.getSchools() }
        verify { schoolApiEndpoint.getSchoolsSatScores() }
        verify { schoolCacheDao.insertSchoolList(schoolListMock) }
        verify { schoolCacheDao.insertSchoolScores(scoresListMock) }
    }

    @Test
    fun `onZeroItemsLoaded if there is an error on API, school cache is not called, the correct message is seen on the errorMessageSender`() {
        val schoolListMock = mockk<List<School>>()
        val scoresListMock = mockk<List<SchoolScores>>()
        val apiError = Throwable("Error fetching schools")
        every { schoolApiEndpoint.getSchools() } returns Single.error(apiError)
        every { schoolApiEndpoint.getSchoolsSatScores() } returns Single.just(scoresListMock)
        every { schoolCacheDao.insertSchoolList(schoolListMock) } returns Completable.complete()
        every { schoolCacheDao.insertSchoolScores(scoresListMock) } returns Completable.complete()
        val errorMessageObserver = errorMessagesDispatcherSender.observeErrorMessages().test()
        SUT.onZeroItemsLoaded()
        verify { schoolApiEndpoint.getSchools() }
        verify(exactly = 0) { schoolCacheDao.insertSchoolList(schoolListMock) }
        val errorMessage = errorMessageObserver.values()[0]
        assertEquals("Error fetching and storing school data", errorMessage.errorMessage)
        assertEquals(apiError, errorMessage.error)
    }

    @Test
    fun `onZeroItemsLoaded if there is an error on Storing on the Cache, the correct message is seen on the errorMessageSender`() {
        val schoolListMock = mockk<List<School>>()
        val scoresListMock = mockk<List<SchoolScores>>()
        val cacheError = Throwable("Error storing schools")
        every { schoolApiEndpoint.getSchools() } returns Single.just(schoolListMock)
        every { schoolApiEndpoint.getSchoolsSatScores() } returns Single.just(scoresListMock)
        every { schoolCacheDao.insertSchoolList(schoolListMock) } returns Completable.error(cacheError)
        every { schoolCacheDao.insertSchoolScores(scoresListMock) } returns Completable.complete()
        val errorMessageObserver = errorMessagesDispatcherSender.observeErrorMessages().test()
        SUT.onZeroItemsLoaded()
        verify { schoolApiEndpoint.getSchools() }
        verify { schoolCacheDao.insertSchoolList(schoolListMock) }
        val errorMessage = errorMessageObserver.values()[0]
        assertEquals("Error fetching and storing school data", errorMessage.errorMessage)
        assertEquals(cacheError, errorMessage.error)
    }

}