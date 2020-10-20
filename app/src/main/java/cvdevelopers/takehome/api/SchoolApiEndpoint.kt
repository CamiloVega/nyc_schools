package cvdevelopers.takehome.api

import cvdevelopers.takehome.models.School
import cvdevelopers.takehome.models.SchoolScores
import io.reactivex.Single
import retrofit2.http.GET

interface SchoolApiEndpoint {

    @GET("/resource/s3k6-pzi2.json")
    fun getSchools(): Single<List<School>>

    @GET("/resource/f9bf-2cp4.json")
    fun getSchoolsSatScores(): Single<List<SchoolScores>>

    companion object {
        const val SERVER = "https://data.cityofnewyork.us/"
    }
}