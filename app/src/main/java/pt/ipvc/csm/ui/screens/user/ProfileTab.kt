package pt.ipvc.csm.ui.screens.user

import pt.ipvc.csm.ui.theme.CsmTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipvc.csm.data.local.UserEntity
import pt.ipvc.csm.ui.components.DangerPillButton
import pt.ipvc.csm.ui.components.IconTile
import pt.ipvc.csm.ui.components.UserAvatar

/** Profile tab shared by both the user and admin homes. */
@Composable
fun ProfileTab(
    user: UserEntity,
    darkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Perfil", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CsmTheme.colors.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                UserAvatar(name = user.name, photoUri = user.photoUri, size = 56.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
                    Text(user.email, fontSize = 12.sp, color = CsmTheme.colors.textTertiary)
                    Row(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(CsmTheme.colors.surfaceFill)
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(user.role.ptLabel, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textSecondary)
                    }
                }
            }
        }

        Surface(
            onClick = onEditProfile,
            shape = RoundedCornerShape(16.dp),
            color = CsmTheme.colors.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IconTile(Icons.Outlined.Edit, tileSize = 38.dp, corner = 11.dp)
                Text("Editar perfil", fontSize = 14.sp, color = CsmTheme.colors.textPrimary, modifier = Modifier.weight(1f))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = CsmTheme.colors.textTertiary
                )
            }
        }

        // Definições
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CsmTheme.colors.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IconTile(Icons.Outlined.DarkMode, tileSize = 38.dp, corner = 11.dp)
                Text("Modo escuro", fontSize = 14.sp, color = CsmTheme.colors.textPrimary, modifier = Modifier.weight(1f))
                Switch(checked = darkMode, onCheckedChange = onToggleDarkMode)
            }
        }

        Spacer(Modifier.weight(1f))
        DangerPillButton(
            text = "Terminar sessão",
            onClick = onLogout,
            leadingIcon = Icons.AutoMirrored.Outlined.Logout
        )
    }
}
