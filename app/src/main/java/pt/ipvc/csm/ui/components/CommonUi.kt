package pt.ipvc.csm.ui.components

import androidx.compose.ui.res.stringResource
import pt.ipvc.csm.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.input.ImeAction
import pt.ipvc.csm.model.Role
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.CsmBlueContainer
import pt.ipvc.csm.ui.theme.CsmBlueDark
import pt.ipvc.csm.ui.theme.CsmDanger

/** A rounded square tile holding an icon — used by request cards, detail headers, categories. */
@Composable
fun IconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tileSize: Dp = 42.dp,
    corner: Dp = 13.dp,
    background: Color = CsmTheme.colors.surfaceFill,
    tint: Color = CsmTheme.colors.textSecondary,
    iconSize: Dp = 22.dp
) {
    Box(
        modifier
            .size(tileSize)
            .clip(RoundedCornerShape(corner))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(iconSize))
    }
}

/** Outlined text field with the app's rounded style and an optional inline error line. */
@Composable
fun CsmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            singleLine = singleLine,
            minLines = minLines,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
            trailingIcon = trailingIcon
        )
        if (isError && !errorText.isNullOrBlank()) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 14.dp, top = 4.dp)
            )
        }
    }
}

/** Password field with a show/hide toggle. */
@Composable
fun CsmPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.password),
    isError: Boolean = false,
    errorText: String? = null,
    imeAction: ImeAction = ImeAction.Done
) {
    var visible by remember { mutableStateOf(false) }
    CsmTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        isError = isError,
        errorText = errorText,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Icon(
                imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                contentDescription = if (visible) "Ocultar password" else "Mostrar password",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { visible = !visible }
                    .padding(4.dp)
            )
        }
    )
}

/** Filled pill-shaped primary action button. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(50)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = LocalContentColor.current,
                strokeWidth = 2.dp
            )
        } else {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

/** Outlined pill-shaped button; defaults to the brand color, override for danger actions. */
@Composable
fun OutlinedPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    contentColor: Color = CsmBlue,
    borderColor: Color = CsmBlue
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(19.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

/** Danger-styled outlined pill (logout, cancel, delete). */
@Composable
fun DangerPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) = OutlinedPillButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    leadingIcon = leadingIcon,
    contentColor = CsmDanger,
    borderColor = Color(0xFFE7C4C0)
)

/** Segmented control to pick the account profile (Utilizador / Administrador). */
@Composable
fun RoleSegmented(
    selected: Role,
    onSelect: (Role) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, CsmTheme.colors.outline, RoundedCornerShape(50))
    ) {
        Role.entries.forEachIndexed { index, role ->
            if (index > 0) {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(CsmTheme.colors.outline)
                )
            }
            val active = role == selected
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (active) CsmBlueContainer else Color.Transparent)
                    .clickable { onSelect(role) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (active) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = null,
                        tint = CsmBlueDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    roleLabel(role),
                    color = if (active) CsmBlueDark else CsmTheme.colors.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/** A selectable filter pill (used for status filters, sort, etc.). */
@Composable
fun CsmFilterChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) CsmBlueContainer else Color.Transparent)
            .then(
                if (selected) Modifier
                else Modifier.border(1.dp, CsmTheme.colors.outline, RoundedCornerShape(50))
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (selected) {
            Icon(
                Icons.Outlined.Check,
                contentDescription = null,
                tint = CsmBlueDark,
                modifier = Modifier.size(16.dp)
            )
        } else if (leadingIcon != null) {
            Icon(
                leadingIcon,
                contentDescription = null,
                tint = CsmTheme.colors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            label,
            color = if (selected) CsmBlueDark else CsmTheme.colors.textSecondary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
