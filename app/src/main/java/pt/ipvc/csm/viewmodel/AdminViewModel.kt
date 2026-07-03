package pt.ipvc.csm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipvc.csm.data.local.CategoryEntity
import pt.ipvc.csm.data.local.CategoryWithCount
import pt.ipvc.csm.data.local.RequestWithDetails
import pt.ipvc.csm.data.local.StatusHistoryWithAuthor
import pt.ipvc.csm.data.repository.CsmRepository
import pt.ipvc.csm.data.repository.OpResult
import pt.ipvc.csm.model.RequestStatus

/** Backs the admin screens: all requests, category management, and state changes. */
class AdminViewModel(private val repository: CsmRepository) : ViewModel() {

    val allRequests: StateFlow<List<RequestWithDetails>> =
        repository.allRequests()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categories: StateFlow<List<CategoryWithCount>> =
        repository.categoriesWithCounts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun requestDetails(id: Long): Flow<RequestWithDetails?> = repository.requestDetails(id)

    fun statusHistory(id: Long): Flow<List<StatusHistoryWithAuthor>> = repository.statusHistory(id)

    fun changeStatus(requestId: Long, status: RequestStatus, onResult: (OpResult) -> Unit) {
        viewModelScope.launch {
            val adminId = repository.currentUserId.first()
            if (adminId == null) {
                onResult(OpResult.Error("Sessão inválida."))
                return@launch
            }
            onResult(repository.changeStatus(requestId, status, adminId))
        }
    }

    fun deleteRequest(requestId: Long, onResult: (OpResult) -> Unit) {
        viewModelScope.launch { onResult(repository.deleteRequest(requestId)) }
    }

    fun addCategory(name: String, iconKey: String?, onResult: (OpResult) -> Unit) {
        viewModelScope.launch { onResult(repository.addCategory(name, iconKey)) }
    }

    fun updateCategory(
        category: CategoryEntity,
        name: String,
        iconKey: String?,
        onResult: (OpResult) -> Unit
    ) {
        viewModelScope.launch { onResult(repository.updateCategory(category, name, iconKey)) }
    }

    fun deleteCategory(category: CategoryEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            onDone()
        }
    }
}
