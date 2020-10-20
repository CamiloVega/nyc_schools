package cvdevelopers.takehome.ui.schoollist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.arch.core.util.Function
import androidx.paging.DataSource
import cvdevelopers.takehome.datarepository.SchoolDataRepository
import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.pagination.SchoolListBoundaryCallback
import cvdevelopers.takehome.utils.ErrorMessage
import cvdevelopers.takehome.utils.ErrorMessagesDispatcher
import cvdevelopers.test.utils.TestUtils
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class SchoolListViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @MockK
    lateinit var schoolDataRepository: SchoolDataRepository

    @MockK
    lateinit var boundaryCallback: SchoolListBoundaryCallback

    private val errorMessagesListener = ErrorMessagesDispatcher()

    private lateinit var SUT: SchoolListViewModel

    @Before
    fun setUp() {
        TestUtils.setupRxjavaForOneThreadedTest()
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `on Init the view model sets up live data and the mapping model for school to school display data matches`() {
        val mappingFunctionSlot = CapturingSlot<Function<School, SchoolDisplayData>>()
       setupMocks(mappingFunctionSlot)
        val mockedSchool = mockk<School>()
        every { mockedSchool.school_name } returns "SCHOOL_NAME"
        every { mockedSchool.borough } returns "BOROUGH"
        every { mockedSchool.overview_paragraph } returns "OVERVIEW_PARAGRAPH"
        every { mockedSchool.dbn } returns "SCHOOL_DBN"
        SUT = SchoolListViewModel(schoolDataRepository, boundaryCallback, errorMessagesListener)
        val displayData = mappingFunctionSlot.captured.apply(mockedSchool)
        val navigationTestObserver = SUT.observeNavigationData().test()
        //VERIFY mapping function maps the correct data
        assertEquals("SCHOOL_NAME", displayData.schoolName)
        assertEquals("BOROUGH", displayData.schoolBorough)
        assertEquals("OVERVIEW_PARAGRAPH", displayData.schoolSummary)
        //verify that with no interaction, navigation observable does not have values
        navigationTestObserver.assertNoValues()
        //verify that invoking the onClick for the display data creates a navigate to school event with the correct DBN
        displayData.onClick.invoke()
        val viewEvent = navigationTestObserver.values()[0]
        assertEquals(SchoolListViewModel.ViewEvent.NavigateToSchool("SCHOOL_DBN"), viewEvent)
    }

    @Test
    fun `on Init, observing the error messages if there is an error, we emit an error event on live data for the view to display it`() {
        setupMocks()
        val errorMessage = ErrorMessage("Error Message", Throwable("Throwable error"))
        SUT = SchoolListViewModel(schoolDataRepository, boundaryCallback, errorMessagesListener)
        val navigationTestObserver = SUT.observeNavigationData().test()
        navigationTestObserver.assertNoValues()
        errorMessagesListener.sentErrorMessage(errorMessage)
        navigationTestObserver.assertValueCount(1)
        val result = navigationTestObserver.values()[0]
        assertEquals(SchoolListViewModel.ViewEvent.DisplayErrorMessage("Error Message"), result)
    }

    @Test
    fun `on Init, if start observing errors after the emission of the error, there are no values on the event observer`() {
        //the purpose of this test is to make sure that if we recreate the fragment, the view event is not captured again (show de dialog again)
        setupMocks()
        val errorMessage = ErrorMessage("Error Message", Throwable("Throwable error"))
        SUT = SchoolListViewModel(schoolDataRepository, boundaryCallback, errorMessagesListener)

        errorMessagesListener.sentErrorMessage(errorMessage)
        val navigationTestObserver = SUT.observeNavigationData().test()
        navigationTestObserver.assertNoValues()
    }

    @Test
    fun `after clearing the View model, no values are emitted even after a new error message is sent`() {
        //the purpose of this test is to make sure that if we recreate the fragment, the view event is not captured again (show de dialog again)
        setupMocks()
        val errorMessage = ErrorMessage("Error Message", Throwable("Throwable error"))
        SUT = SchoolListViewModel(schoolDataRepository, boundaryCallback, errorMessagesListener)
        SUT.onCleared()
        val navigationTestObserver = SUT.observeNavigationData().test()
        errorMessagesListener.sentErrorMessage(errorMessage)
        navigationTestObserver.assertNoValues()
    }

    private fun setupMocks(capturingSlot: CapturingSlot<Function<School, SchoolDisplayData>>? = null) {
        val mockSchoolDataFactory = mockk<DataSource.Factory<Int, School>>()
        val mockDisplayData = mockk<DataSource.Factory<Int, SchoolDisplayData>>()
        every { schoolDataRepository.getAllSchoolsInCache() } returns mockSchoolDataFactory
        every { mockSchoolDataFactory.map<SchoolDisplayData>(capturingSlot?.let { capture(it) } ?: any() )} returns mockDisplayData
    }
}