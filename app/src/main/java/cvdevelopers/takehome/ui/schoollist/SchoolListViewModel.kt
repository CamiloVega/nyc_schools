package cvdevelopers.takehome.ui.schoollist

import android.util.Log
import androidx.lifecycle.ViewModel
import cvdevelopers.takehome.datarepository.SchoolDataRepository
import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import cvdevelopers.takehome.dagger.scopes.FragmentScope
import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.pagination.SchoolListBoundaryCallback
import cvdevelopers.takehome.utils.IErrorMessagesListener
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

@FragmentScope
class SchoolListViewModel @Inject constructor(
        private val schoolDataRepository: SchoolDataRepository,
        private val boundaryCallback: SchoolListBoundaryCallback,
        private val errorMessagesListener: IErrorMessagesListener
) : ViewModel() {

    private val eventLiveData = PublishSubject.create<ViewEvent>()

    var listLiveData: LiveData<PagedList<SchoolDisplayData>>

    fun observeNavigationData() = eventLiveData as Observable<ViewEvent>

    private val disposable = CompositeDisposable()

    init {
        val config = PagedList.Config.Builder()
                .setPageSize(INITIAL_PAGE_SIZE)
                .setInitialLoadSizeHint(INITIAL_PAGE_SIZE)
                .setEnablePlaceholders(true)
                .build()

        listLiveData = LivePagedListBuilder<Int, SchoolDisplayData>(
                schoolDataRepository.getAllSchoolsInCache().map { it.toDisplayData() },
                config)
                .setBoundaryCallback(boundaryCallback).build()

        errorMessagesListener.observeErrorMessages()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    eventLiveData.onNext(ViewEvent.DisplayErrorMessage(it.errorMessage))
                }, {
                    Log.e("SchoolListViewModel", "Error observing error messages")
                }).addTo(disposable)

    }

    private fun School.toDisplayData() = SchoolDisplayData(
            schoolName = school_name,
            schoolBorough = borough?.trim().orEmpty(),
            schoolSummary = overview_paragraph.orEmpty(),
            onClick = {
                eventLiveData.onNext(ViewEvent.NavigateToSchool(dbn))
            }
    )

    public override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    companion object {
        private const val INITIAL_PAGE_SIZE = 10
    }

    sealed class ViewEvent {
        data class NavigateToSchool(val schoolDbn: String) : ViewEvent()
        data class DisplayErrorMessage(val message: String) : ViewEvent()
    }
}


