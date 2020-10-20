package cvdevelopers.takehome.ui.schooldetails

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cvdevelopers.githubstalker.R
import cvdevelopers.takehome.datarepository.SchoolDataRepository
import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.models.SchoolScores
import cvdevelopers.takehome.utils.ViewModelStringProvider
import cvdevelopers.test.utils.TestUtils
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.subjects.MaybeSubject
import io.reactivex.subjects.SingleSubject
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class SchoolDetailsViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @MockK
    lateinit var dataRepository: SchoolDataRepository

    @MockK
    lateinit var viewModelStringProvider: ViewModelStringProvider

    private val schoolDataSubject = SingleSubject.create<School>()
    private val scoresDataSubject = MaybeSubject.create<SchoolScores>()


    private lateinit var SUT: SchoolDetailsViewModel

    @Before
    fun setUp() {
        TestUtils.setupRxjavaForOneThreadedTest()
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `on init, system observes scores and school data `() {
        setupMocks()
        SUT = SchoolDetailsViewModel(dataRepository, viewModelStringProvider, SCHOOL_DBN)
        verify { dataRepository.getSchoolForDbn(SCHOOL_DBN) }
        verify { dataRepository.getScoresForDbn(SCHOOL_DBN) }
    }

    @Test
    fun `when data repository returns a school live data shows the correct display data`() {
        setupMocks()
        val mockSchool = mockk<School>()
        every { mockSchool.school_name } returns "SCHOOL_NAME"
        every { mockSchool.overview_paragraph } returns "OVERVIEW"
        every { mockSchool.extracurricular_activities } returns "EXTRA_CURRICULAR"
        SUT = SchoolDetailsViewModel(dataRepository, viewModelStringProvider, SCHOOL_DBN)
        schoolDataSubject.onSuccess(mockSchool)
        val result = SUT.observerDisplayData().value!!
        assertEquals("SCHOOL_NAME", result.schoolName)
        assertEquals("OVERVIEW", result.summary)
        assertEquals("EXTRA_CURRICULAR", result.extraCurricularActivities)
    }

    @Test
    fun `when data repository returns an error school live data does not emit any value`() {
        setupMocks()
        SUT = SchoolDetailsViewModel(dataRepository, viewModelStringProvider, SCHOOL_DBN)
        schoolDataSubject.onError(Throwable("Error fetching school"))
        val result = SUT.observerDisplayData().value
        assertNull(result)
    }

    @Test
    fun `when data repository returns a scores for a school live data shows the correct display data`() {
        setupMocks()
        val mockScores = mockk<SchoolScores>()
        every { mockScores.satCriticalReadingAvgScore } returns "READING"
        every { mockScores.satMathAvgScore } returns "MATH"
        every { mockScores.satWritingAvgScore } returns "WRITING"
        SUT = SchoolDetailsViewModel(dataRepository, viewModelStringProvider, SCHOOL_DBN)
        scoresDataSubject.onSuccess(mockScores)
        val result = SUT.observerScoresData().value!!
        assertEquals("READING", result.readingSAT)
        assertEquals("MATH", result.mathSAT)
        assertEquals("WRITING", result.writingSAT)
    }

    @Test
    fun `if a score has a field as null, the string displayed is Not Reported`() {
        setupMocks()
        val mockScores = mockk<SchoolScores>()
        every { mockScores.satCriticalReadingAvgScore } returns null
        every { mockScores.satMathAvgScore } returns "MATH"
        every { mockScores.satWritingAvgScore } returns "WRITING"
        SUT = SchoolDetailsViewModel(dataRepository, viewModelStringProvider, SCHOOL_DBN)
        scoresDataSubject.onSuccess(mockScores)
        val result = SUT.observerScoresData().value!!
        assertEquals(NOT_REPORTED_STRING, result.readingSAT)
    }

    @Test
    fun `if a score for a school returns empty, the string displayed is Not Reported`() {
        setupMocks()
        SUT = SchoolDetailsViewModel(dataRepository, viewModelStringProvider, SCHOOL_DBN)
        scoresDataSubject.onComplete()
        val result = SUT.observerScoresData().value!!
        assertEquals(NOT_REPORTED_STRING, result.readingSAT)
        assertEquals(NOT_REPORTED_STRING, result.mathSAT)
        assertEquals(NOT_REPORTED_STRING, result.writingSAT)
    }

    private fun setupMocks() {
        every { dataRepository.getSchoolForDbn(SCHOOL_DBN) } returns schoolDataSubject
        every { dataRepository.getScoresForDbn(SCHOOL_DBN) } returns scoresDataSubject
        every { viewModelStringProvider.getString(R.string.not_reported) } returns NOT_REPORTED_STRING
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    companion object {
        private const val SCHOOL_DBN = "SCHOOL_DBN"
        private const val NOT_REPORTED_STRING = "Not reported"
    }
}