package pt.ipvc.csm.ui.screens.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import pt.ipvc.csm.data.local.UserEntity
import pt.ipvc.csm.ui.screens.user.ProfileTab
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmBlueContainer
import pt.ipvc.csm.ui.theme.CsmBlueDark
import pt.ipvc.csm.util.ExportUtils
import pt.ipvc.csm.viewmodel.AdminViewModel
import pt.ipvc.csm.viewmodel.AuthViewModel

private enum class AdminTab(val label: String, val icon: ImageVector) {
    PANEL("Painel", Icons.Outlined.Dashboard),
    REQUESTS("Pedidos", Icons.Outlined.Assignment),
    CATEGORIES("Categorias", Icons.Outlined.Category),
    PROFILE("Perfil", Icons.Outlined.Person)
}

@Composable
fun AdminHomeScreen(
    user: UserEntity,
    adminViewModel: AdminViewModel,
    authViewModel: AuthViewModel,
    onOpenRequest: (Long) -> Unit,
    onEditProfile: () -> Unit
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tab = AdminTab.entries[tabIndex]
    val requests by adminViewModel.allRequests.collectAsState()
    val categories by adminViewModel.categories.collectAsState()
    val darkMode by authViewModel.darkMode.collectAsState()
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = CsmTheme.colors.surface) {
                AdminTab.entries.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (tab) {
                AdminTab.PANEL -> AdminDashboardTab(
                    requests = requests,
                    onOpenRequest = onOpenRequest,
                    onSeeAllRequests = { tabIndex = AdminTab.REQUESTS.ordinal },
                    onExport = {
                        ExportUtils.shareRequestsCsv(
                            context = context,
                            requests = requests,
                            includeAuthor = true,
                            baseFileName = "todos-os-pedidos"
                        )
                    }
                )
                AdminTab.REQUESTS -> AllRequestsTab(
                    requests = requests,
                    onOpenRequest = onOpenRequest
                )
                AdminTab.CATEGORIES -> CategoriesTab(
                    categories = categories,
                    adminViewModel = adminViewModel
                )
                AdminTab.PROFILE -> ProfileTab(
                    user = user,
                    darkMode = darkMode,
                    onToggleDarkMode = authViewModel::setDarkMode,
                    onEditProfile = onEditProfile,
                    onLogout = { authViewModel.logout() }
                )
            }
        }
    }
}
