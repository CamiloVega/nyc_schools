package cvdevelopers.takehome.ui.schooldetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import cvdevelopers.githubstalker.R
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.school_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SchoolDetailsFragment : Fragment() {

    private  val viewModel: SchoolDetailsViewModel by viewModel { parametersOf(navArgs<SchoolDetailsFragmentArgs>().value.schoolDbn) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.school_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        viewModel.observerDisplayData().observe(viewLifecycleOwner, Observer {
            school_name.text = it.schoolName
            summary_value.text = it.summary
            extra_curricular_value.text = it.extraCurricularActivities
            toolbar.title = it.schoolName
        })
        viewModel.observerScoresData().observe(viewLifecycleOwner, Observer {
            sat_math_value.text = it.mathSAT
            sat_reading_value.text = it.readingSAT
            sat_writing_value.text = it.writingSAT
        })
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

}