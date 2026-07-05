package pt.ipvc.csm.util

import android.content.Context
import android.net.Uri
import android.util.Log
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
            Log.w("PhotoStorage", "Falha ao guardar a fotografia", e)
            null
        }
    }

    /** Copies a bundled asset image (e.g. "seed/projector.jpg") into internal storage; used by the
     *  demo seeder so seeded requests can carry photos. Returns the saved file path. */
    fun savePhotoFromAsset(context: Context, assetPath: String): String? {
        return try {
            val dir = File(context.filesDir, "photos").apply { mkdirs() }
            val dest = File(dir, "${UUID.randomUUID()}.jpg")
            context.assets.open(assetPath).use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            }
            dest.absolutePath
        } catch (e: Exception) {
            Log.w("PhotoStorage", "Falha ao copiar imagem de demonstração", e)
            null
        }
    }

    fun delete(path: String?) {
        if (!path.isNullOrBlank()) runCatching { File(path).delete() }
    }
}
