package cvdevelopers.takehome.utils

import android.app.Application

class ViewModelStringProvider (private val context: Application){

    fun getString(resourceId: Int): String = context.getString(resourceId)

}
