package pt.ipvc.csm.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

/**
 * Copies a picked image into the app's private internal storage so it keeps working after
 * restart (the temporary Photo Picker Uri would otherwise expire). Returns the saved file path.
 */
object PhotoStorage {

    fun savePhoto(context: Context, source: Uri): String? {
        return try {
            val dir = File(context.filesDir, "photos").apply { mkdirs() }
            val dest = File(dir, "${UUID.randomUUID()}.jpg")
            val input = context.contentResolver.openInputStream(source) ?: return null
            input.use { stream ->
                dest.outputStream().use { output -> stream.copyTo(output) }
            }
            dest.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun delete(path: String?) {
        if (!path.isNullOrBlank()) runCatching { File(path).delete() }
    }
}
