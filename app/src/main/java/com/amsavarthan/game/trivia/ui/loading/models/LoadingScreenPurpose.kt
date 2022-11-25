package com.amsavarthan.game.trivia.ui.loading.models

import com.amsavarthan.game.trivia.ui.createGame.models.GameConfiguration
import com.amsavarthan.game.trivia.ui.createGame.models.toRouteString
import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer

sealed class LoadingScreenPurpose(val id: String) {
    class CreateRoom(val configuration: List<GameConfiguration>) :
        LoadingScreenPurpose(id = "create")

    class JoinRoom(val gameCode: String) : LoadingScreenPurpose(id = "join")
}

@NavTypeSerializer
class LoadingScreenPurposeSerializer : DestinationsNavTypeSerializer<LoadingScreenPurpose> {
    override fun toRouteString(value: LoadingScreenPurpose): String {
        return when (value) {
            is LoadingScreenPurpose.CreateRoom -> {
                "${value.id}_${value.configuration.toRouteString()}"
            }
            is LoadingScreenPurpose.JoinRoom -> {
                "${value.id}_${value.gameCode}"
            }
        }
    }

    override fun fromRouteString(routeStr: String): LoadingScreenPurpose {
        val type = routeStr.substringBefore('_')
        val data = routeStr.substringAfter("_")
        return when (type) {
            "create" -> {
                val configurations = data.split('_')
                    .map {
                        GameConfiguration.parse(it)
                    }
                LoadingScreenPurpose.CreateRoom(configurations)
            }
            "join" -> {
                LoadingScreenPurpose.JoinRoom(gameCode = data)
            }
            else -> throw IllegalStateException("Invalid route $routeStr")
        }

    }
}