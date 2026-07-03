package pt.ipvc.csm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipvc.csm.data.local.UserEntity
import pt.ipvc.csm.data.repository.CsmRepository
import pt.ipvc.csm.data.repository.OpResult
import pt.ipvc.csm.model.Role

/** Tri-state used to drive the top-level navigation (avoids a login flash during session restore). */
sealed interface AuthState {
    data object Loading : AuthState
    data object LoggedOut : AuthState
    data class LoggedIn(val user: UserEntity) : AuthState
}

/** Handles authentication, the current session's user, and profile editing. */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModel(private val repository: CsmRepository) : ViewModel() {

    val authState: StateFlow<AuthState> =
        repository.currentUserId
            .flatMapLatest { id ->
                if (id == null) flowOf(AuthState.LoggedOut)
                else repository.observeUser(id).map { user ->
                    if (user == null) AuthState.LoggedOut else AuthState.LoggedIn(user)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthState.Loading)

    fun login(email: String, password: String, onResult: (OpResult) -> Unit) {
        viewModelScope.launch { onResult(repository.login(email, password)) }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        role: Role,
        onResult: (OpResult) -> Unit
    ) {
        viewModelScope.launch { onResult(repository.register(name, email, password, role)) }
    }

    fun updateProfile(
        userId: Long,
        name: String,
        email: String,
        newPassword: String?,
        role: Role,
        photoUri: String?,
        onResult: (OpResult) -> Unit
    ) {
        viewModelScope.launch {
            onResult(repository.updateProfile(userId, name, email, newPassword, role, photoUri))
        }
    }

    fun logout(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.logout()
            onDone()
        }
    }
}
