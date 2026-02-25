package `in`.mercuryai.emptyproject.domain.repository

import `in`.mercuryai.emptyproject.domain.model.NetworkStatus
import kotlinx.coroutines.flow.StateFlow

interface NetworkConnectivityObserver {

  val networkStatus: StateFlow<NetworkStatus>


}