package cvdevelopers.takehome.pagination

import androidx.paging.PagedList
import cvdevelopers.takehome.api.SchoolApiEndpoint
import cvdevelopers.takehome.cache.dao.SchoolCacheDao
import cvdevelopers.takehome.dagger.scopes.FragmentScope
import cvdevelopers.takehome.ui.schoollist.SchoolDisplayData
import cvdevelopers.takehome.utils.ErrorMessage
import cvdevelopers.takehome.utils.IErrorMessagesSender
import io.reactivex.Completable
import javax.inject.Inject

@FragmentScope
class SchoolListBoundaryCallback @Inject constructor(
        private val schoolCacheDao: SchoolCacheDao,
        private val schoolApiEndpoint: SchoolApiEndpoint,
        private val errorMessagesSender: IErrorMessagesSender
): PagedList.BoundaryCallback<SchoolDisplayData>() {

    private var isLoading = false

    override fun onZeroItemsLoaded() {
        isLoading = true
        fetchSchoolsFromApi()
    }

    private fun fetchAndStoreSchools() = schoolApiEndpoint.getSchools()
            .flatMapCompletable { schoolCacheDao.insertSchoolList(it) }

    private fun fetchAndStoreSchoolsScores() = schoolApiEndpoint.getSchoolsSatScores()
            .flatMapCompletable { schoolCacheDao.insertSchoolScores(it) }

    private fun fetchSchoolsFromApi() =
            Completable.mergeArray(
                    fetchAndStoreSchools(),
                    fetchAndStoreSchoolsScores()
            )
            .subscribe({
                isLoading = false
            },{
                errorMessagesSender.sentErrorMessage(ErrorMessage("Error fetching and storing school data", it))
            })

}