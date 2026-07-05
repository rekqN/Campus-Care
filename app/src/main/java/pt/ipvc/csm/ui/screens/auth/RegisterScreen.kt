package pt.ipvc.csm.ui.screens.auth

import androidx.compose.ui.res.stringResource
import pt.ipvc.csm.R
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import pt.ipvc.csm.data.repository.OpResult
import pt.ipvc.csm.ui.components.CsmPasswordField
import pt.ipvc.csm.ui.components.CsmTextField
import pt.ipvc.csm.ui.components.PrimaryButton
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.CsmError
import pt.ipvc.csm.viewmodel.AuthViewModel

private const val MIN_PASSWORD = 6

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val nameError = submitted && name.isBlank()
    val emailValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val emailError = submitted && !emailValid
    val passwordError = submitted && password.length < MIN_PASSWORD
    val confirmError = submitted && confirm != password

    fun submit() {
        submitted = true
        formError = null
        if (name.isBlank() || !emailValid || password.length < MIN_PASSWORD || confirm != password) return
        loading = true
        authViewModel.register(name, email, password) { result ->
            loading = false
            if (result is OpResult.Error) formError = result.message
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
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back), tint = CsmTheme.colors.textPrimary)
            }
            Text(stringResource(R.string.create_account), fontSize = 20.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            Text(
                stringResource(R.string.register_subtitle),
                fontSize = 12.5.sp,
                color = CsmTheme.colors.textMuted,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            CsmTextField(
                value = name,
                onValueChange = { name = it; formError = null },
                label = stringResource(R.string.full_name),
                isError = nameError,
                errorText = stringResource(R.string.name_required),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            )
            CsmTextField(
                value = email,
                onValueChange = { email = it; formError = null },
                label = stringResource(R.string.email),
                isError = emailError,
                errorText = if (email.isBlank()) stringResource(R.string.email_required) else stringResource(R.string.email_invalid),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            )
            CsmPasswordField(
                value = password,
                onValueChange = { password = it; formError = null },
                label = stringResource(R.string.password),
                isError = passwordError,
                errorText = stringResource(R.string.password_min, MIN_PASSWORD),
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            )
            CsmPasswordField(
                value = confirm,
                onValueChange = { confirm = it; formError = null },
                label = stringResource(R.string.confirm_password),
                isError = confirmError,
                errorText = stringResource(R.string.passwords_no_match),
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            )

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

            Spacer(Modifier.height(20.dp))
            PrimaryButton(text = stringResource(R.string.create_account), onClick = ::submit, loading = loading)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.already_have_account), fontSize = 12.5.sp, color = CsmTheme.colors.textMuted)
                Text(
                    stringResource(R.string.sign_in),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CsmBlue,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }
        }
    }
}
