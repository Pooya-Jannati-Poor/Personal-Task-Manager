package ir.pooyadev.presentation.view.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    override fun onCleared() {
        super.onCleared()
        System.gc()
    }

}