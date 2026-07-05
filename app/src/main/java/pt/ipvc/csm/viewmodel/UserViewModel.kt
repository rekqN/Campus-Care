package pt.ipvc.csm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipvc.csm.data.local.CategoryEntity
import pt.ipvc.csm.data.local.NotificationEntity
import pt.ipvc.csm.data.local.RequestWithDetails
import pt.ipvc.csm.data.local.StatusHistoryWithAuthor
import pt.ipvc.csm.data.repository.CsmRepository
import pt.ipvc.csm.data.repository.OpResult

/** Backs the user-facing screens: the current user's requests, categories, create/cancel. */
@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModel(private val repository: CsmRepository) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> =
        repository.categories()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val myRequests: StateFlow<List<RequestWithDetails>> =
        repository.currentUserId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList()) else repository.requestsByUser(id)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> =
        repository.currentUserId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList()) else repository.notificationsForUser(id)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val unreadNotifications: StateFlow<Int> =
        repository.currentUserId
            .flatMapLatest { id ->
                if (id == null) flowOf(0) else repository.unreadCountForUser(id)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun markNotificationsRead() {
        viewModelScope.launch {
            val uid = repository.currentUserId.first() ?: return@launch
            repository.markNotificationsRead(uid)
        }
    }

    fun requestDetails(id: Long): Flow<RequestWithDetails?> = repository.requestDetails(id)

    fun statusHistory(id: Long): Flow<List<StatusHistoryWithAuthor>> = repository.statusHistory(id)

    fun createRequest(
        categoryId: Long?,
        title: String,
        location: String,
        description: String,
        photoPaths: List<String>,
        onResult: (OpResult) -> Unit
    ) {
        viewModelScope.launch {
            val uid = repository.currentUserId.first()
            if (uid == null) {
                onResult(OpResult.Error("Sessão inválida."))
                return@launch
            }
            onResult(repository.createRequest(uid, categoryId, title, location, description, photoPaths))
        }
    }

    fun cancelRequest(requestId: Long, onResult: (OpResult) -> Unit) {
        viewModelScope.launch {
            val uid = repository.currentUserId.first()
            if (uid == null) {
                onResult(OpResult.Error("Sessão inválida."))
                return@launch
            }
            onResult(repository.cancelRequest(requestId, uid))
        }
    }
}
