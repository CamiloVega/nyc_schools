package cvdevelopers.takehome.koin

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import cvdevelopers.takehome.api.SchoolApiEndpoint
import cvdevelopers.takehome.cache.TakehomeDatabase
import cvdevelopers.takehome.datarepository.SchoolDataRepository
import cvdevelopers.takehome.pagination.SchoolListBoundaryCallback
import cvdevelopers.takehome.ui.schooldetails.SchoolDetailsViewModel
import cvdevelopers.takehome.ui.schoollist.SchoolListViewModel
import cvdevelopers.takehome.utils.ErrorMessagesDispatcher
import cvdevelopers.takehome.utils.ViewModelStringProvider
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = module {
  single { GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .create()
  }

  single {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)

    OkHttpClient.Builder()
      .addNetworkInterceptor(StethoInterceptor())
      .addNetworkInterceptor(logging)
      .build()
  }

  single {
    Retrofit.Builder()
        .baseUrl(SchoolApiEndpoint.SERVER)
        .client(get())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(GsonConverterFactory.create(get()))
        .build()
  }

  single {
    get<Retrofit>().create(SchoolApiEndpoint::class.java)
  }

}

val dataModule = module {
  single {
    TakehomeDatabase.getDatabase(get())
  }

  single {
    get<TakehomeDatabase>().schoolCacheDao()
  }

  single {
    SchoolDataRepository(get())
  }
}

val fragmentModule = module {


  single {
    ViewModelStringProvider(get())
  }

  scope(named("ListFragmentScope")) {

    scoped { ErrorMessagesDispatcher() }

    viewModel { SchoolListViewModel(get(), get(), get<ErrorMessagesDispatcher>()) }

    scoped {
      SchoolListBoundaryCallback(get(), get(), get<ErrorMessagesDispatcher>())
    }
  }

  viewModel { (schoolDbn: String) -> SchoolDetailsViewModel(get(), get(), schoolDbn) }
}