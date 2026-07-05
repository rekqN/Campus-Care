package pt.ipvc.csm.ui.screens.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ipvc.csm.R
import pt.ipvc.csm.data.local.UserEntity
import pt.ipvc.csm.ui.components.rememberCsvExporter
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.CsmBlueContainer
import pt.ipvc.csm.ui.theme.CsmBlueDark
import pt.ipvc.csm.viewmodel.AuthViewModel
import pt.ipvc.csm.viewmodel.UserViewModel

private enum class UserTab(val labelRes: Int, val icon: ImageVector) {
    HOME(R.string.nav_home, Icons.Outlined.Home),
    REQUESTS(R.string.nav_requests, Icons.Outlined.Assignment),
    HISTORY(R.string.nav_history, Icons.Outlined.History),
    PROFILE(R.string.nav_profile, Icons.Outlined.Person)
}

@Composable
fun UserHomeScreen(
    user: UserEntity,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    onOpenRequest: (Long) -> Unit,
    onNewRequest: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tab = UserTab.entries[tabIndex]
    val requests by userViewModel.myRequests.collectAsState()
    val darkMode by authViewModel.darkMode.collectAsState()
    val unreadCount by userViewModel.unreadNotifications.collectAsState()
    val language by authViewModel.language.collectAsState()
    val exportCsv = rememberCsvExporter()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = CsmTheme.colors.surface) {
                UserTab.entries.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = { Text(stringResource(item.labelRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CsmBlueDark,
                            selectedTextColor = CsmTheme.colors.textPrimary,
                            indicatorColor = CsmBlueContainer,
                            unselectedIconColor = CsmTheme.colors.textMuted,
                            unselectedTextColor = CsmTheme.colors.textMuted
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (tab == UserTab.REQUESTS) {
                FloatingActionButton(
                    onClick = onNewRequest,
                    containerColor = CsmBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(19.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_request))
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (tab) {
                UserTab.HOME -> UserHomeTab(
                    user = user,
                    requests = requests,
                    unreadCount = unreadCount,
                    onNewRequest = onNewRequest,
                    onOpenRequest = onOpenRequest,
                    onSeeAllRequests = { tabIndex = UserTab.REQUESTS.ordinal },
                    onOpenNotifications = onOpenNotifications
                )
                UserTab.REQUESTS -> MyRequestsTab(
                    requests = requests,
                    onOpenRequest = onOpenRequest
                )
                UserTab.HISTORY -> HistoryTab(
                    requests = requests,
                    onOpenRequest = onOpenRequest
                )
                UserTab.PROFILE -> ProfileTab(
                    user = user,
                    darkMode = darkMode,
                    onToggleDarkMode = authViewModel::setDarkMode,
                    language = language,
                    onSetLanguage = authViewModel::setLanguage,
                    onEditProfile = onEditProfile,
                    onLogout = { authViewModel.logout() },
                    onExport = { exportCsv(requests, false, "meus-pedidos") }
                )
            }
        }
    }
}
