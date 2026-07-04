package pt.ipvc.csm.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.ipvc.csm.data.local.UserEntity
import pt.ipvc.csm.model.Role
import pt.ipvc.csm.ui.components.CsmLogo
import pt.ipvc.csm.ui.screens.admin.AdminHomeScreen
import pt.ipvc.csm.ui.screens.admin.AdminRequestDetailScreen
import pt.ipvc.csm.ui.screens.auth.EditProfileScreen
import pt.ipvc.csm.ui.screens.auth.LoginScreen
import pt.ipvc.csm.ui.screens.auth.RegisterScreen
import pt.ipvc.csm.ui.screens.user.NewRequestScreen
import pt.ipvc.csm.ui.screens.user.NotificationsScreen
import pt.ipvc.csm.ui.screens.user.RequestDetailScreen
import pt.ipvc.csm.ui.screens.user.UserHomeScreen
import pt.ipvc.csm.viewmodel.AdminViewModel
import pt.ipvc.csm.viewmodel.AppViewModelProvider
import pt.ipvc.csm.viewmodel.AuthState
import pt.ipvc.csm.viewmodel.AuthViewModel
import pt.ipvc.csm.viewmodel.UserViewModel

/** Central route names. Detail routes carry the request id. */
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val EDIT_PROFILE = "edit_profile"
    const val NEW_REQUEST = "new_request"
    const val NOTIFICATIONS = "notifications"
    const val REQUEST_DETAIL = "request_detail/{requestId}"
    const val ADMIN_REQUEST_DETAIL = "admin_request_detail/{requestId}"

    fun requestDetail(id: Long) = "request_detail/$id"
    fun adminRequestDetail(id: Long) = "admin_request_detail/$id"
}

/**
 * Root of the app. The session tri-state decides what is shown: a splash while the session is
 * restored, the auth flow when logged out, or the role-appropriate app when logged in.
 */
@Composable
fun CsmApp() {
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val authState by authViewModel.authState.collectAsState()

    when (val state = authState) {
        AuthState.Loading -> SplashScreen()
        AuthState.LoggedOut -> AuthNavHost(authViewModel)
        is AuthState.LoggedIn -> MainNavHost(state.user, authViewModel)
    }
}

@Composable
private fun SplashScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(contentAlignment = Alignment.Center) {
            CsmLogo(size = 64.dp)
        }
    }
}

/** Login / register, shown when no user is logged in. */
@Composable
private fun AuthNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * The logged-in app. Home routes by role; detail/new-request/edit-profile are pushed on top.
 * Because admin routes are only reachable from the admin home (and vice-versa), the role gating
 * is enforced by construction.
 */
@Composable
private fun MainNavHost(user: UserEntity, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val idArg = listOf(navArgument("requestId") { type = NavType.LongType })

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            if (user.role == Role.ADMIN) {
                val adminViewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
                AdminHomeScreen(
                    user = user,
                    adminViewModel = adminViewModel,
                    authViewModel = authViewModel,
                    onOpenRequest = { navController.navigate(Routes.adminRequestDetail(it)) },
                    onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) }
                )
            } else {
                val userViewModel: UserViewModel = viewModel(factory = AppViewModelProvider.Factory)
                UserHomeScreen(
                    user = user,
                    userViewModel = userViewModel,
                    authViewModel = authViewModel,
                    onOpenRequest = { navController.navigate(Routes.requestDetail(it)) },
                    onNewRequest = { navController.navigate(Routes.NEW_REQUEST) },
                    onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                    onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) }
                )
            }
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                authViewModel = authViewModel,
                user = user,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.NEW_REQUEST) {
            val userViewModel: UserViewModel = viewModel(factory = AppViewModelProvider.Factory)
            NewRequestScreen(
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() },
                onCreated = { navController.popBackStack() }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            val userViewModel: UserViewModel = viewModel(factory = AppViewModelProvider.Factory)
            NotificationsScreen(
                userViewModel = userViewModel,
                onOpenRequest = { navController.navigate(Routes.requestDetail(it)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REQUEST_DETAIL, arguments = idArg) { entry ->
            val id = entry.arguments?.getLong("requestId") ?: return@composable
            val userViewModel: UserViewModel = viewModel(factory = AppViewModelProvider.Factory)
            RequestDetailScreen(
                userViewModel = userViewModel,
                requestId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_REQUEST_DETAIL, arguments = idArg) { entry ->
            val id = entry.arguments?.getLong("requestId") ?: return@composable
            val adminViewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
            AdminRequestDetailScreen(
                adminViewModel = adminViewModel,
                requestId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
