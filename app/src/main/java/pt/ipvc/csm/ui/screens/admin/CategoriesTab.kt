package pt.ipvc.csm.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.CircleShape
import pt.ipvc.csm.data.local.CategoryEntity
import pt.ipvc.csm.data.local.CategoryWithCount
import pt.ipvc.csm.data.repository.OpResult
import pt.ipvc.csm.ui.components.IconTile
import pt.ipvc.csm.ui.components.PrimaryButton
import pt.ipvc.csm.ui.components.categoryIconKeys
import pt.ipvc.csm.ui.components.iconForKey
import pt.ipvc.csm.ui.screens.user.EmptyHint
import pt.ipvc.csm.ui.theme.CsmBlue
import pt.ipvc.csm.ui.theme.CsmBlueContainer
import pt.ipvc.csm.ui.theme.CsmBlueDark
import pt.ipvc.csm.ui.theme.CsmError
import pt.ipvc.csm.ui.theme.CsmOutline
import pt.ipvc.csm.ui.theme.CsmSurfaceFill
import pt.ipvc.csm.ui.theme.CsmTextPrimary
import pt.ipvc.csm.ui.theme.CsmTextSecondary
import pt.ipvc.csm.ui.theme.CsmTextTertiary
import pt.ipvc.csm.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesTab(
    categories: List<CategoryWithCount>,
    adminViewModel: AdminViewModel
) {
    var sheetOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<CategoryEntity?>(null) }
    var name by remember { mutableStateOf("") }
    var iconKey by remember { mutableStateOf(categoryIconKeys.first()) }
    var error by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf<CategoryEntity?>(null) }
    val sheetState = rememberModalBottomSheetState()

    fun openNew() {
        editing = null; name = ""; iconKey = categoryIconKeys.first(); error = null; sheetOpen = true
    }

    fun openEdit(category: CategoryEntity) {
        editing = category; name = category.name
        iconKey = category.iconKey ?: categoryIconKeys.first(); error = null; sheetOpen = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Categorias",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = CsmTextPrimary,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
            )
            if (categories.isEmpty()) {
                EmptyHint("Ainda não há categorias. Toca no botão + para criar a primeira.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories, key = { it.category.id }) { item ->
                        CategoryRow(item = item, onEdit = { openEdit(item.category) })
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { openNew() },
            containerColor = CsmBlue,
            contentColor = Color.White,
            shape = RoundedCornerShape(19.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nova categoria")
        }
    }

    if (sheetOpen) {
        ModalBottomSheet(onDismissRequest = { sheetOpen = false }, sheetState = sheetState) {
            CategorySheet(
                isEditing = editing != null,
                name = name,
                onNameChange = { name = it; error = null },
                selectedIcon = iconKey,
                onIconSelect = { iconKey = it },
                error = error,
                onCancel = { sheetOpen = false },
                onSave = {
                    val callback: (OpResult) -> Unit = { result ->
                        if (result is OpResult.Error) error = result.message else sheetOpen = false
                    }
                    val current = editing
                    if (current == null) adminViewModel.addCategory(name, iconKey, callback)
                    else adminViewModel.updateCategory(current, name, iconKey, callback)
                },
                onDelete = { confirmDelete = editing }
            )
        }
    }

    confirmDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { confirmDelete = null },
            title = { Text("Eliminar categoria") },
            text = { Text("Eliminar \"${category.name}\"? Os pedidos existentes ficam sem categoria.") },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.deleteCategory(category) { }
                    confirmDelete = null
                    sheetOpen = false
                }) { Text("Eliminar", color = CsmError) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = null }) { Text("Voltar") }
            }
        )
    }
}

@Composable
private fun CategoryRow(item: CategoryWithCount, onEdit: () -> Unit) {
    Surface(
        onClick = onEdit,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(iconForKey(item.category.iconKey), tileSize = 38.dp, corner = 11.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(item.category.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CsmTextPrimary)
                Text("${item.requestCount} pedidos", fontSize = 11.sp, color = CsmTextTertiary)
            }
            Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = CsmTextTertiary, modifier = Modifier.size(20.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategorySheet(
    isEditing: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    selectedIcon: String,
    onIconSelect: (String) -> Unit,
    error: String?,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            if (isEditing) "Editar categoria" else "Nova categoria",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = CsmTextPrimary
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nome") },
            singleLine = true,
            isError = error != null,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Ícone", fontSize = 12.sp, color = CsmTextSecondary)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categoryIconKeys.forEach { key ->
                val selected = key == selectedIcon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) CsmBlueContainer else CsmSurfaceFill)
                        .border(
                            width = if (selected) 1.5.dp else 0.dp,
                            color = if (selected) CsmBlue else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onIconSelect(key) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        iconForKey(key),
                        contentDescription = null,
                        tint = if (selected) CsmBlueDark else CsmTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        if (error != null) {
            Text(error, color = CsmError, fontSize = 12.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) { Text("Cancelar", color = CsmTextSecondary) }
            PrimaryButton(
                text = "Guardar",
                onClick = onSave,
                modifier = Modifier.weight(1f)
            )
        }

        if (isEditing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .clickable { onDelete() }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = null, tint = CsmError, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text("Eliminar categoria", color = CsmError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
