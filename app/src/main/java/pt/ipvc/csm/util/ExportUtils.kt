package pt.ipvc.csm.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import pt.ipvc.csm.data.local.RequestWithDetails
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Exports the request list to a CSV file and hands it to the Android share sheet, so the user can
 * save it, email it, open it in a spreadsheet app, send it to another device, etc.
 */
object ExportUtils {

    fun shareRequestsCsv(
        context: Context,
        requests: List<RequestWithDetails>,
        includeAuthor: Boolean,
        baseFileName: String
    ) {
        val csv = buildCsv(requests, includeAuthor)
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val file = File(dir, "$baseFileName-$stamp.csv")
        file.writeText(csv, Charsets.UTF_8)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Pedidos CSM")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Exportar pedidos"))
    }

    private fun buildCsv(requests: List<RequestWithDetails>, includeAuthor: Boolean): String {
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
