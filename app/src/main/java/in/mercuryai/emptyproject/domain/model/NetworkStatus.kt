package `in`.mercuryai.chat.domain.model

sealed class NetworkStatus {

    data object Connected: NetworkStatus()
    data object DisConnected: NetworkStatus()

}