package com.agent.fasttag.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FasTagViewModelFactory constructor(private val fasTagRepository: FasTagRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FastTagViewModel::class.java)) {
            FastTagViewModel(this.fasTagRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}