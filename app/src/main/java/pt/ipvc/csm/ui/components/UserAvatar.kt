package pt.ipvc.csm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pt.ipvc.csm.ui.theme.CsmBlueContainer
import pt.ipvc.csm.ui.theme.CsmBlueDark
import java.io.File

/** Builds up to two uppercase initials from a name. */
fun initialsOf(name: String): String =
    name.trim().split(" ").filter { it.isNotBlank() }
        .take(2).joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

/** Circular avatar showing the user's photo, or their initials as a fallback. */
@Composable
fun UserAvatar(
    name: String,
    photoUri: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    if (photoUri != null) {
        AsyncImage(
            model = File(photoUri),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(CsmBlueContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initialsOf(name),
                color = CsmBlueDark,
                fontSize = (size.value * 0.36f).sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
