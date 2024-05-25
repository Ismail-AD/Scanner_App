package com.appdev.scanner.ViewModel

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.scanner.ModelClass.QrCodeData
import com.appdev.scanner.Repository.Repository
import com.appdev.scanner.Utils.ReturnedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomViewModel @Inject constructor(
    val repository: Repository,
) : ViewModel() {

    private var _qrCodeDataState = MutableStateFlow(QrCodeDataState())
    val qrCodeDataState: StateFlow<QrCodeDataState> get() = _qrCodeDataState


    fun saveQrCodeData(label: String, onComplete: (String?) -> Unit) {
        repository.saveQrCodeDataToFirebase(label, onComplete)
    }

    fun getListData() = repository.fetchQrCodeDataFromFirebase()

    data class QrCodeDataState(
        val listData: MutableList<QrCodeData> = mutableListOf(),
        val error: String = "",
        val loadingState: Boolean = false,
    )

}