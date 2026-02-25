package `in`.mercuryai.emptyproject.domain.model

sealed class NetworkStatus {

    data object Connected: NetworkStatus()
    data object DisConnected: NetworkStatus()

}