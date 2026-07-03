package pt.ipvc.csm.data

import java.security.MessageDigest

/**
 * Hashes passwords with SHA-256 before storing them, so the database never keeps
 * plain-text passwords. (A production app would use a salted, slow hash such as
 * bcrypt/scrypt/Argon2 — noted in the report's critical analysis.)
 */
object PasswordHasher {
    fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, expectedHash: String): Boolean =
        hash(password) == expectedHash
}
