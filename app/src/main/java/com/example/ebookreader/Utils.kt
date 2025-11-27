package com.example.ebookreader

import java.security.MessageDigest

/**
 * A utility object that provides common helper functions.
 */
object Utils {
    /**
     * Computes the SHA-256 hash of a given string.
     *
     * @param base The string to be hashed.
     * @return The hexadecimal representation of the SHA-256 hash.
     */
    fun sha256(base: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(base.toByteArray(Charsets.UTF_8))
        return hash.fold("") { str, it -> str + "%02x".format(it) }
    }
}
