package com.appdev.scanner.Repository

import com.appdev.scanner.ModelClass.QrCodeData
import com.appdev.scanner.Utils.BarcodeScanner
import com.appdev.scanner.Utils.ReturnedResult
import com.appdev.scanner.Utils.getCurrentTimeAndDate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class Repository @Inject constructor(
    private val RealtimeDBObject: FirebaseDatabase,
) {
    fun saveQrCodeDataToFirebase(qrCodeLabel: String, onComplete: (String?) -> Unit) {
        val currentTimeAndDate = System.currentTimeMillis()
        val qrCodeData = QrCodeData(qrCodeLabel, currentTimeAndDate)

        RealtimeDBObject.reference.child("qrcodes").push().setValue(qrCodeData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete("Data saved successfully")
                } else {
                    onComplete("Error saving data:  ${task.exception?.localizedMessage}")
                }
            }
    }

    fun fetchQrCodeDataFromFirebase(): Flow<MutableList<QrCodeData>> =
        callbackFlow {
            val database = RealtimeDBObject.reference.child("qrcodes")

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val list: MutableList<QrCodeData> = mutableListOf()
                    for (snapshot in dataSnapshot.children) {
                        val qrCodeData = snapshot.getValue(QrCodeData::class.java)
                        qrCodeData?.let { data ->
                            list.add(data)
                        }
                    }
                    trySend(list)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            database.addListenerForSingleValueEvent(listener)
            awaitClose {
                database.removeEventListener(listener)
                close()
            }
        }

}