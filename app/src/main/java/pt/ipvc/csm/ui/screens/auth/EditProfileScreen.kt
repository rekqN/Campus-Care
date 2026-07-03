package pt.ipvc.csm.ui.screens.auth

import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipvc.csm.data.local.UserEntity
import pt.ipvc.csm.data.repository.OpResult
import pt.ipvc.csm.model.Role
import pt.ipvc.csm.ui.components.CsmPasswordField
import pt.ipvc.csm.ui.components.CsmTextField
import pt.ipvc.csm.ui.components.DangerPillButton
import pt.ipvc.csm.ui.components.RoleSegmented
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.CsmBlueContainer
import pt.ipvc.csm.ui.theme.CsmBlueDark
import pt.ipvc.csm.ui.theme.CsmError
import pt.ipvc.csm.ui.theme.CsmTextPrimary
import pt.ipvc.csm.ui.theme.CsmTextSecondary
import pt.ipvc.csm.util.PhotoStorage
import pt.ipvc.csm.viewmodel.AuthViewModel
import java.io.File

private fun initialsOf(name: String): String =
    name.trim().split(" ").filter { it.isNotBlank() }
        .take(2).joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel,
    user: UserEntity,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var newPassword by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(user.role) }
    var photoUri by remember { mutableStateOf(user.photoUri) }
    var submitted by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val emailValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val nameError = submitted && name.isBlank()
    val emailError = submitted && !emailValid
    val passwordError = submitted && newPassword.isNotBlank() && newPassword.length < 6

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val saved = withContext(Dispatchers.IO) { PhotoStorage.savePhoto(context, uri) }
                if (saved != null) photoUri = saved
            }
        }
    }

    fun save() {
        submitted = true
        formError = null
        if (name.isBlank() || !emailValid || (newPassword.isNotBlank() && newPassword.length < 6)) return
        loading = true
        authViewModel.updateProfile(
            userId = user.id,
            name = name,
            email = email,
            newPassword = newPassword.ifBlank { null },
            role = role,
            photoUri = photoUri
        ) { result ->
            loading = false
            when (result) {
                is OpResult.Error -> formError = result.message
                OpResult.Success -> onBack()
            }
        }
    }

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
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = CsmTextPrimary)
            }
            Text(
                "Editar perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = CsmTextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                if (loading) "A guardar…" else "Guardar",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = CsmBlue,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(enabled = !loading) { save() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Box(contentAlignment = Alignment.Center) {
                if (photoUri != null) {
                    AsyncImage(
                        model = File(photoUri!!),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(CsmBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            initialsOf(name),
                            color = CsmBlueDark,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Text(
                "Alterar foto",
                color = CsmBlue,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .padding(8.dp)
            )

            CsmTextField(
                value = name,
                onValueChange = { name = it; formError = null },
                label = "Nome",
                isError = nameError,
                errorText = "Indica o teu nome.",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            CsmTextField(
                value = email,
                onValueChange = { email = it; formError = null },
                label = "Email",
                isError = emailError,
                errorText = if (email.isBlank()) "Indica o teu email." else "Email inválido.",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            )
            CsmPasswordField(
                value = newPassword,
                onValueChange = { newPassword = it; formError = null },
                label = "Nova password (opcional)",
                isError = passwordError,
                errorText = "Mínimo 6 caracteres.",
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            )

            Text(
                "Tipo de perfil",
                fontSize = 12.sp,
                color = CsmTextSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 8.dp)
            )
            RoleSegmented(selected = role, onSelect = { role = it })

            if (formError != null) {
                Text(
                    text = formError!!,
                    color = CsmError,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                )
            }

            Spacer(Modifier.height(28.dp))
            DangerPillButton(
                text = "Terminar sessão",
                onClick = { authViewModel.logout() },
                leadingIcon = Icons.AutoMirrored.Outlined.Logout
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
