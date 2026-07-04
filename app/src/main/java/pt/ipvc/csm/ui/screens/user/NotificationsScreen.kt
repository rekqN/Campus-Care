package pt.ipvc.csm.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipvc.csm.data.local.NotificationEntity
import pt.ipvc.csm.ui.components.IconTile
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.util.DateUtils
import pt.ipvc.csm.viewmodel.UserViewModel

@Composable
fun NotificationsScreen(
    userViewModel: UserViewModel,
    onOpenRequest: (Long) -> Unit,
    onBack: () -> Unit
) {
    val notifications by userViewModel.notifications.collectAsState()

    // Opening the screen marks everything as read (clears the badge).
    LaunchedEffect(Unit) { userViewModel.markNotificationsRead() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = CsmTheme.colors.textPrimary)
            }
            Text("Notificações", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
        }

        if (notifications.isEmpty()) {
            EmptyHint("Ainda não tens notificações.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    NotificationRow(
                        notification = notification,
                        onClick = { notification.requestId?.let(onOpenRequest) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(notification: NotificationEntity, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = CsmTheme.colors.surface,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(Icons.Outlined.Notifications)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    notification.message,
                    fontSize = 13.5.sp,
                    color = CsmTheme.colors.textPrimary
                )
                Text(
                    DateUtils.formatRelative(notification.createdAt),
                    fontSize = 11.sp,
                    color = CsmTheme.colors.textFaint,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = CsmTheme.colors.textTertiary
            )
        }
    }
}
