package pt.ipvc.csm.util

import android.content.Context
import android.net.Uri
import pt.ipvc.csm.data.local.RequestWithDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Builds the request list as CSV and writes it to a location the user picks. The picking/writing is
 * driven by [pt.ipvc.csm.ui.components.rememberCsvExporter], which uses the Storage Access Framework
 * ("Save as" dialog): this works on any device with no account and no runtime permission, unlike a
 * share sheet (which only lists apps that can receive the file, e.g. cloud storage/email).
 */
object ExportUtils {

    /** Timestamped file name, e.g. "meus-pedidos-20260705-143000.csv". */
    fun fileName(baseName: String): String {
        val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        return "$baseName-$stamp.csv"
    }

    /** Writes [csv] to the document [uri] returned by the create-document picker. */
    fun writeCsvToUri(context: Context, uri: Uri, csv: String) {
        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(csv.toByteArray(Charsets.UTF_8))
        }
    }

    fun buildRequestsCsv(requests: List<RequestWithDetails>, includeAuthor: Boolean): String {
        val sb = StringBuilder()
        sb.append('﻿') // UTF-8 BOM so Excel shows accented characters correctly

        val header = mutableListOf("ID", "Titulo", "Categoria", "Localizacao", "Descricao", "Estado", "Prioridade")
        if (includeAuthor) header.add("Autor")
        header.add("Data de criacao")
        header.add("Ultima atualizacao")
        sb.append(header.joinToString(",") { escape(it) }).append("\r\n")

        for (item in requests) {
            val r = item.request
            val row = mutableListOf(
                r.id.toString(),
                r.title,
                item.categoryName ?: "",
                r.location,
                r.description,
                r.status.ptLabel,
                r.priority.ptLabel
            )
            if (includeAuthor) row.add(item.userName)
            row.add(DateUtils.formatDateTime(r.createdAt))
            row.add(DateUtils.formatDateTime(r.updatedAt))
            sb.append(row.joinToString(",") { escape(it) }).append("\r\n")
        }
        return sb.toString()
    }

    /** RFC 4180 CSV escaping: wrap in quotes if needed, and double any embedded quotes. */
    private fun escape(field: String): String {
        val needsQuotes = field.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        val escaped = field.replace("\"", "\"\"")
        return if (needsQuotes) "\"$escaped\"" else escaped
    }
}
