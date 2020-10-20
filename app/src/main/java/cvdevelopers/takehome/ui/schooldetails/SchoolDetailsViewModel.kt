package cvdevelopers.takehome.ui.schooldetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cvdevelopers.githubstalker.R
import cvdevelopers.takehome.datarepository.SchoolDataRepository
import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.models.SchoolScores
import cvdevelopers.takehome.utils.ViewModelStringProvider
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import javax.inject.Named

class SchoolDetailsViewModel @Inject constructor(
        private val dataRepository: SchoolDataRepository,
        private val viewModelStringProvider: ViewModelStringProvider,
        @Named("schoolDbn")
        private val schoolDbn: String
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val displayLiveData = MutableLiveData<SchoolDisplayData>()
    private val scoresLiveData = MutableLiveData<ScoresDisplayData>()

    fun observerDisplayData() = displayLiveData as LiveData<SchoolDisplayData>
    fun observerScoresData() = scoresLiveData as LiveData<ScoresDisplayData>

    init {
        dataRepository.getSchoolForDbn(schoolDbn)
                .map { it.toDisplayData() }
                .subscribe({
                    displayLiveData.postValue(it)
                }, {
                    //at this point information about the school was not found. Would be products decision of the best case for user experience
                    Log.e("SchoolDetailsViewModel", "error fetching school Data", it)
                }).addTo(compositeDisposable)

        dataRepository.getScoresForDbn(schoolDbn)
                .switchIfEmpty(Maybe.just(SchoolScores()))
                .map { it.toDisplayData() }
                .subscribe({
                    scoresLiveData.postValue(it)
                }, {
                    Log.e("SchoolDetailsViewModel", "error fetching school Data", it)
                }).addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    private fun School.toDisplayData() = SchoolDisplayData(
            schoolName = school_name,
            summary = overview_paragraph.orEmpty(),
            extraCurricularActivities = extracurricular_activities.orEmpty()
    )

    private fun SchoolScores.toDisplayData() = ScoresDisplayData(
            mathSAT = satMathAvgScore ?: viewModelStringProvider.getString(R.string.not_reported),
            readingSAT = satCriticalReadingAvgScore ?: viewModelStringProvider.getString(R.string.not_reported),
            writingSAT = satWritingAvgScore ?: viewModelStringProvider.getString(R.string.not_reported)
    )
}

data class SchoolDisplayData(val schoolName: String, val summary: String, val extraCurricularActivities: String)
data class ScoresDisplayData(val mathSAT: String, val readingSAT: String, val writingSAT: String)