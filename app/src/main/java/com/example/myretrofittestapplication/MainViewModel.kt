package com.example.myretrofittestapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myretrofittestapplication.catfacts.CatFact
import com.example.myretrofittestapplication.catfacts.CatFactsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {

    private val catFactsLiveData = MutableLiveData<CatFact>()

    val catFactsLD: LiveData<CatFact> =
        catFactsLiveData

    fun fetchFact() {
        val controller = CatFactsDataSource()
        controller.fetchOne { fact -> catFactsLiveData.postValue(fact) }
    }
}