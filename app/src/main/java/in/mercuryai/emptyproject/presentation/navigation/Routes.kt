package `in`.mercuryai.chat.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {

    @Serializable
    data object Splash: Routes()

    @Serializable
    data object AuthScreen: Routes()

    @Serializable
    data object SignInScreen: Routes()

    @Serializable
    data object HomeScreen: Routes()
}