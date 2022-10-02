package bfa.blair.favdish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bfa.blair.favdish.model.entities.RandomDish
import bfa.blair.favdish.model.network.RandomDishAPIService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishViewModel : ViewModel() {

    private val randomRecipeApiService = RandomDishAPIService()

    private val compositeDisposable = CompositeDisposable()

    val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishRespose = MutableLiveData<RandomDish.Recipes>()
    val randomDishLoadingError = MutableLiveData<Boolean>()

    fun getRandomRecipeFromAPI() {
        loadRandomDish.value = true
        compositeDisposable.add(
            randomRecipeApiService.getRandomDish()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RandomDish.Recipes>() {
                    override fun onSuccess(value: RandomDish.Recipes) {
                        loadRandomDish.value = false
                        randomDishRespose.value = value
                        randomDishLoadingError.value = false
                    }

                    override fun onError(e: Throwable) {
                        loadRandomDish.value = false
                        randomDishLoadingError.value = true
                        e.printStackTrace()
                    }

                })
        )

    }
}