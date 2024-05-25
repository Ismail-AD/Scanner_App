package com.appdev.scanner.Utils

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await


class BarcodeScanner(
    appContext: Context
) {

    /**
     * From the docs: If you know which barcode formats you expect to read, you can improve the
     * speed of the barcode detector by configuring it to only detect those formats.
     */
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        )
        .build()

    private val scanner = GmsBarcodeScanning.getClient(appContext, options)
    val barCodeResults = MutableStateFlow<String?>(null)

    fun updateResult() {
        barCodeResults.value = null
    }

    fun startScan() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                Log.d("CHK","Scan done $barcode")
                barCodeResults.value = barcode.rawValue
            }
            .addOnCanceledListener {
                barCodeResults.value = null
            }
            .addOnFailureListener { e ->
                Log.d("CHK","Scan fail ${e}")
                barCodeResults.value = null
            }
    }
}