package com.appdev.scanner.HiltModule

import android.content.Context
import com.appdev.scanner.Utils.BarcodeScanner
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Singleton
    @Provides
    fun fireBaseDBInstance(): FirebaseDatabase = Firebase.database

    @Provides
    fun provideBarcodeScanner(@ApplicationContext appContext: Context) = BarcodeScanner(appContext)
}