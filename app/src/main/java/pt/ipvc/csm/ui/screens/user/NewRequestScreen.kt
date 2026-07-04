package pt.ipvc.csm.ui.screens.user

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipvc.csm.data.local.CategoryEntity
import pt.ipvc.csm.data.repository.OpResult
import pt.ipvc.csm.ui.components.PrimaryButton
import pt.ipvc.csm.ui.components.iconForKey
import pt.ipvc.csm.ui.theme.CsmTheme
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.CsmError
import pt.ipvc.csm.util.PhotoStorage
import pt.ipvc.csm.viewmodel.UserViewModel
import java.io.File

private const val DESCRIPTION_MAX = 500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val categories by userViewModel.categories.collectAsState()

    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

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

    val categoryError = submitted && selectedCategory == null
    val titleError = submitted && title.isBlank()
    val locationError = submitted && location.isBlank()
    val descriptionError = submitted && description.isBlank()

    fun submit() {
        submitted = true
        formError = null
        if (selectedCategory == null || title.isBlank() || location.isBlank() || description.isBlank()) return
        loading = true
        userViewModel.createRequest(
            categoryId = selectedCategory!!.id,
            title = title,
            location = location,
            description = description,
            photoUri = photoUri
        ) { result ->
            loading = false
            when (result) {
                is OpResult.Error -> formError = result.message
                OpResult.Success -> onCreated()
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
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = CsmTheme.colors.textPrimary)
            }
            Text("Novo pedido", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = CsmTheme.colors.textPrimary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(2.dp))

            if (categories.isEmpty()) {
                Text(
                    "Ainda não existem categorias. Pede a um administrador para criar categorias antes de submeteres um pedido.",
                    color = CsmTheme.colors.textMuted,
                    fontSize = 13.sp
                )
            }

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded && categories.isNotEmpty() }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    isError = categoryError,
                    enabled = categories.isNotEmpty(),
                    leadingIcon = selectedCategory?.let {
                        { Icon(iconForKey(it.iconKey), contentDescription = null) }
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            leadingIcon = { Icon(iconForKey(category.iconKey), contentDescription = null) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; formError = null },
                label = { Text("Título") },
                isError = titleError,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it; formError = null },
                label = { Text("Localização") },
                isError = locationError,
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = CsmBlue) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= DESCRIPTION_MAX) { description = it; formError = null } },
                label = { Text("Descrição") },
                isError = descriptionError,
                minLines = 3,
                supportingText = {
                    Text(
                        "${description.length}/$DESCRIPTION_MAX",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 10.5.sp,
                        color = CsmTheme.colors.textMuted
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Fotografia (opcional)", fontSize = 12.sp, color = CsmTheme.colors.textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (photoUri != null) {
                    Box(modifier = Modifier.size(84.dp)) {
                        AsyncImage(
                            model = File(photoUri!!),
                            contentDescription = "Fotografia",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(84.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .align(Alignment.TopEnd)
                                .size(20.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xCC14161B))
                                .clickable {
                                    PhotoStorage.delete(photoUri)
                                    photoUri = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = "Remover", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, CsmTheme.colors.outline, RoundedCornerShape(14.dp))
                        .clickable {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.AddAPhoto, contentDescription = null, tint = CsmBlue, modifier = Modifier.size(24.dp))
                    Text("Adicionar", fontSize = 10.sp, color = CsmTheme.colors.textMuted)
                }
            }

            if (formError != null) {
                Text(formError!!, color = CsmError, fontSize = 12.sp)
            }

            Spacer(Modifier.height(4.dp))
            PrimaryButton(
                text = "Submeter pedido",
                onClick = ::submit,
                enabled = categories.isNotEmpty(),
                loading = loading,
                leadingIcon = Icons.AutoMirrored.Outlined.Send
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
