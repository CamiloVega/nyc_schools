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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cvdevelopers.githubstalker.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.school_list_fragment.*
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.scope.viewModel
import org.koin.core.qualifier.named


class SchoolListFragment : Fragment() {

    private val disposable = CompositeDisposable()

    private val myScope = getKoin().createScope("listFragmentScope", named("ListFragmentScope"))

    private val viewModel: SchoolListViewModel by myScope.viewModel(this)

    private lateinit var adapter: SchoolListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
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