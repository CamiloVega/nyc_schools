package cvdevelopers.takehome.utils

import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelStringProvider @Inject constructor(private val context: Application){

    fun getString(resourceId: Int): String = context.getString(resourceId)

}
