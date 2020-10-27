package cvdevelopers.takehome.ui.schoollist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cvdevelopers.githubstalker.R
import cvdevelopers.takehome.MainActivity
import cvdevelopers.takehome.dagger.vm.ViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.school_list_fragment.*
import javax.inject.Inject

class SchoolListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val disposable = CompositeDisposable()

    private lateinit var viewModel: SchoolListViewModel

    private lateinit var adapter: SchoolListAdapter

    override fun onAttach(context: Context) {
        (activity as MainActivity).activityComponent.fragmentComponent().create().inject(this)
        super.onAttach(context)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SchoolListViewModel::class.java)
        adapter = SchoolListAdapter {
            viewModel.onSchoolClicked(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.school_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.adapter = adapter
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.observeNavigationData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleViewEvent(it)
                }.addTo(disposable)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)
    }

    private fun handleViewEvent(event: SchoolListViewModel.ViewEvent) {
        when(event) {
            is SchoolListViewModel.ViewEvent.NavigateToSchool -> navigateToSchool(event.schoolDbn)
            is SchoolListViewModel.ViewEvent.DisplayErrorMessage -> displayErrorMessage(event.message)
        }
    }

    private fun displayErrorMessage(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.error))
                    setMessage(message)
            setNegativeButton(R.string.ok) { _, _ -> }
            create().show()
        }
    }

    override fun onDestroyView() {
        disposable.clear()
        super.onDestroyView()
    }

    private fun navigateToSchool(schoolDbn: String) {
        findNavController().navigate(SchoolListFragmentDirections.actionSchoolListFragmentToSchoolDetailsFragment(schoolDbn))
    }

}