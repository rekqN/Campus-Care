package pt.ipvc.csm.ui.screens.user

import androidx.compose.ui.res.stringResource
import pt.ipvc.csm.R
import pt.ipvc.csm.ui.components.roleLabel
import pt.ipvc.csm.ui.theme.CsmTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipvc.csm.data.local.UserEntity
import pt.ipvc.csm.ui.components.DangerPillButton
import pt.ipvc.csm.ui.components.IconTile
import pt.ipvc.csm.ui.components.UserAvatar
import pt.ipvc.csm.ui.theme.CsmBlue

/** Profile tab shared by both the user and admin homes. */
@Composable
fun ProfileTab(
    user: UserEntity,
    darkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    language: String,
    onSetLanguage: (String) -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    onExport: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.nav_profile), fontSize = 20.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)

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
                        Text(roleLabel(user.role), fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textSecondary)
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
                Text(stringResource(R.string.edit_profile), fontSize = 14.sp, color = CsmTheme.colors.textPrimary, modifier = Modifier.weight(1f))
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
            Column {
                Row(
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    IconTile(Icons.Outlined.DarkMode, tileSize = 38.dp, corner = 11.dp)
                    Text(stringResource(R.string.dark_mode), fontSize = 14.sp, color = CsmTheme.colors.textPrimary, modifier = Modifier.weight(1f))
                    Switch(checked = darkMode, onCheckedChange = onToggleDarkMode)
                }

                HorizontalDivider(color = CsmTheme.colors.divider, modifier = Modifier.padding(horizontal = 14.dp))

                Row(
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    IconTile(Icons.Outlined.Translate, tileSize = 38.dp, corner = 11.dp)
                    Text(stringResource(R.string.language), fontSize = 14.sp, color = CsmTheme.colors.textPrimary, modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, CsmTheme.colors.outline, RoundedCornerShape(50))
                    ) {
                        LanguageOption("PT", selected = language == "pt") { onSetLanguage("pt") }
                        LanguageOption("EN", selected = language == "en") { onSetLanguage("en") }
                    }
                }

                if (onExport != null) {
                    HorizontalDivider(color = CsmTheme.colors.divider, modifier = Modifier.padding(horizontal = 14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onExport() }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        IconTile(Icons.Outlined.Download, tileSize = 38.dp, corner = 11.dp)
                        Text(stringResource(R.string.export_my_data), fontSize = 14.sp, color = CsmTheme.colors.textPrimary, modifier = Modifier.weight(1f))
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = CsmTheme.colors.textTertiary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        DangerPillButton(
            text = stringResource(R.string.logout),
            onClick = onLogout,
            leadingIcon = Icons.AutoMirrored.Outlined.Logout
        )
    }
}

@Composable
private fun LanguageOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) Color.White else CsmTheme.colors.textMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) CsmBlue else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}
