package com.amsavarthan.game.trivia.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class EnergyRefillWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
//    private val datastore: GameDatastore
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val ARG_REFILL_WORKER_MESSAGE = "message"
    }

    override suspend fun doWork(): Result {
//        val energy = datastore.energyPreferencesFlow.toList().last()
//        Log.d("DEBUG", "1energy is $energy")
//        if (energy < 5) datastore.increaseEnergy()
//        Log.d("DEBUG", "2energy is $energy")
        return Result.success()
    }


}