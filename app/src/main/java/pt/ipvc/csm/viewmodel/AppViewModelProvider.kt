package pt.ipvc.csm.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pt.ipvc.csm.CsmApplication

/**
 * Factory that builds every ViewModel with the shared [CsmRepository] from the AppContainer.
 * Used from composables via `viewModel(factory = AppViewModelProvider.Factory)`.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { AuthViewModel(csmApplication().container.repository) }
        initializer { UserViewModel(csmApplication().container.repository) }
        initializer { AdminViewModel(csmApplication().container.repository) }
    }
}

/** Retrieves the Application instance from the ViewModel [CreationExtras]. */
fun CreationExtras.csmApplication(): CsmApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CsmApplication
