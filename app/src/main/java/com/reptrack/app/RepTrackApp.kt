package com.reptrack.app

import android.app.Application
import com.reptrack.app.data.repository.WorkoutRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class RepTrackApp : Application() {

    @Inject
    lateinit var repository: WorkoutRepository

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            repository.seedIfEmpty()
        }
    }
}