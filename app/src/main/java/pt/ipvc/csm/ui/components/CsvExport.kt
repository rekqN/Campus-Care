package pt.ipvc.csm.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import pt.ipvc.csm.data.local.RequestWithDetails
import pt.ipvc.csm.util.ExportUtils

/**
 * Returns a callback that exports the given requests as a CSV to a location the user chooses in the
 * system "Save as" dialog (Storage Access Framework). No account and no storage permission are
 * needed on any Android version — this replaces the old share sheet, which on a bare device only
 * offered cloud apps (OneDrive/Gmail) that require signing in.
 *
 * Usage:
 * ```
 * val exportCsv = rememberCsvExporter()
 * // later, e.g. in an onClick:
 * exportCsv(requests, includeAuthor = true, "todos-os-pedidos")
 * ```
 */
@Composable
fun rememberCsvExporter(): (List<RequestWithDetails>, Boolean, String) -> Unit {
    val context = LocalContext.current
    // The picker is async, so hold the CSV built at click time until a location comes back.
    var pendingCsv by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        val csv = pendingCsv
        pendingCsv = null
        if (uri != null && csv != null) ExportUtils.writeCsvToUri(context, uri, csv)
    }
    return { requests, includeAuthor, baseName ->
        pendingCsv = ExportUtils.buildRequestsCsv(requests, includeAuthor)
        launcher.launch(ExportUtils.fileName(baseName))
    }
}
