package `in`.mercuryai.chat.domain.repository

import `in`.mercuryai.chat.domain.model.NetworkStatus
import kotlinx.coroutines.flow.StateFlow

interface NetworkConnectivityObserver {

  val networkStatus: StateFlow<NetworkStatus>


}