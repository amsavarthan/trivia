package com.amsavarthan.game.trivia.helper

import com.amsavarthan.game.trivia.config.Config.CORRECT_WEIGHTAGE
import com.amsavarthan.game.trivia.config.Config.STREAK_WEIGHTAGE
import com.amsavarthan.game.trivia.data.models.GameResult

sealed class Stat(val name: String) {
    object Missed : Stat("Missed")
    object Streak : Stat("Streak")
    object Correct : Stat("Correct")
    object Incorrect : Stat("Incorrect")
}

class StatsHelper(
    private val gameResult: List<GameResult>
) {

    companion object {
        fun getDefaultStats() = mapOf(
            Stat.Streak to 0,
            Stat.Correct to 0,
            Stat.Incorrect to 0,
            Stat.Missed to 0
        )
    }


    fun getStats(): Map<Stat, Int> {
        val result = getDefaultStats().toMutableMap()

        result[Stat.Correct] = gameResult.count { it.isCorrect }
        result[Stat.Missed] = gameResult.count { it.givenAnswer.isNullOrBlank() }
        result[Stat.Incorrect] = gameResult.size.minus(
            result[Stat.Correct]!! + result[Stat.Missed]!!
        )
        result[Stat.Streak] = calculateStreak()

        return result
    }

    private fun calculateStreak(): Int {
        var maxStreak = -1
        var streak = -1
        gameResult.forEach {
            streak = if (it.isCorrect) streak.inc() else -1
            if (streak > maxStreak) maxStreak = streak
        }

        return if (maxStreak > 0) maxStreak else 0
    }

    fun getPoints(): Int {
        val streak = calculateStreak()
        val correct = gameResult.count { it.isCorrect }
        return (streak * STREAK_WEIGHTAGE) + (correct * CORRECT_WEIGHTAGE)
    }

}