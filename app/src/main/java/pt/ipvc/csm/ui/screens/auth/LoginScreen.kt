package pt.ipvc.csm.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.HorizontalDivider
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
import pt.ipvc.csm.ui.components.CsmLogo
import pt.ipvc.csm.ui.components.CsmPasswordField
import pt.ipvc.csm.ui.components.CsmTextField
import pt.ipvc.csm.ui.components.OutlinedPillButton
import pt.ipvc.csm.ui.components.PrimaryButton
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmError
import pt.ipvc.csm.viewmodel.AuthViewModel

private fun isEmailValid(email: String): Boolean =
    email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val emailError = submitted && !isEmailValid(email)
    val passwordError = submitted && password.isBlank()

    fun submit() {
        submitted = true
        formError = null
        if (!isEmailValid(email) || password.isBlank()) return
        loading = true
        authViewModel.login(email, password) { result ->
            loading = false
            if (result is OpResult.Error) formError = result.message
            // On success, the app switches automatically via authState.
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        CsmLogo(size = 60.dp)
        Spacer(Modifier.height(14.dp))
        Text("CSM", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = CsmTheme.colors.textPrimary)
        Text(
            "CAMPUS SERVICES MANAGEMENT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = CsmTheme.colors.textTertiary,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(Modifier.height(36.dp))
        Text(
            "Iniciar sessão",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = CsmTheme.colors.textPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        CsmTextField(
            value = email,
            onValueChange = { email = it; formError = null },
            label = "Email",
            isError = emailError,
            errorText = if (email.isBlank()) "Indica o teu email." else "Email inválido.",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )

        CsmPasswordField(
            value = password,
            onValueChange = { password = it; formError = null },
            isError = passwordError,
            errorText = "Indica a tua password.",
            imeAction = ImeAction.Done,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        if (formError != null) {
            Text(
                text = formError!!,
                color = CsmError,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
        PrimaryButton(text = "Entrar", onClick = ::submit, loading = loading)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                "ou",
                color = CsmTheme.colors.textTertiary,
                fontSize = 11.5.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        OutlinedPillButton(text = "Criar conta", onClick = onNavigateToRegister)
        Spacer(Modifier.height(16.dp))
    }
}
