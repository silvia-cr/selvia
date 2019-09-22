package com.tfm.digitalevidencemanager.api

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.tfm.digitalevidencemanager.Local
import java.io.*
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

/**
 * Util methods.
 *
 * This class contains util methods to use in *DigitalEviedenceManager*.
 */
class Utils {

    companion object {

        /**
         * Encrypt the file provided and save it in the private folder of the app using the filename provided.
         *
         * @param context the context used to save the file in the private folder of the app.
         * @param file the file to encrypt.
         * @param filename the filename of the encrypted file .
         * @result operation result.
         */
        fun encryptFile(context: Context, file: File, filename: String): Boolean {
            val encryptedFilename = filename.plus(Constants.EVIDENCE_EXTENSION)
            var result = true

            val inputStream = FileInputStream(file)
            val outputStream = context.openFileOutput(encryptedFilename, Context.MODE_PRIVATE)

            try {
                val keyGenerator = KeyGenerator.getInstance(Constants.CIPHER_ALGORITHM, Constants.PROVIDER)
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(Constants.ALIAS, KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setIsStrongBoxBacked(true)
                    .build()

                keyGenerator.init(keyGenParameterSpec)
                val secretKey = keyGenerator.generateKey()

                val cipher = Cipher.getInstance(Constants.CIPHER_SUITE)
                cipher.init(Cipher.ENCRYPT_MODE, secretKey)

                val inputBytes = ByteArray(file.length().toInt())
                inputStream.read(inputBytes)

                val outputBytes = cipher.doFinal(inputBytes)
                outputStream.write(outputBytes)

            } catch (e: GeneralSecurityException) {
                result = false
                Log.e(Local.TAG, "Error in encrypt file '" + file.name + "': " + e.message)
            } catch (e: IOException) {
                result = false
                Log.e(Local.TAG, "Error with I/O while encrypting: " + e.message)
            } finally {
                inputStream.close()
                outputStream.close()
            }
            return result
        }

        /**
         * Get the metadata from a file.
         *
         * @param file the file to obtain the metadata.
         * @return the metadata information.
         */
        fun getMetadatafromFile(file: File): String {
            val fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)

            val gson = Gson()
            val attributesValues = mapOf<String, String>(
                Constants.FILE_NAME to file.name,
                Constants.FILE_CREATION_DATE to fileAttributes.creationTime().toString(),
                Constants.FILE_LAST_ACCESS_DATE to fileAttributes.lastAccessTime().toString(),
                Constants.FILE_SIZE to fileAttributes.size().toString(),
                Constants.FILE_SIZE_UNIT to Constants.FILE_UNIT,
                Constants.FILE_LAST_MODIFIED_DATE to fileAttributes.lastModifiedTime().toString(),
                Constants.FILE_IS_DIRECTORY to fileAttributes.isDirectory.toString(),
                Constants.FILE_KEY to fileAttributes.fileKey().toString()
            )
            return gson.toJson(attributesValues)
        }

        /**
         * The hash of a file.
         *
         * @param file the file to obtain the hash
         * @return the hash of the file
         */
        fun getHashFromFile(file: File): String {
            val md = MessageDigest.getInstance(Constants.HASH_TYPE)
            md.update(file.readBytes())
            val hash = md.digest().toTypedArray()

            return byteArrayToHexString(hash)
        }

        private fun byteArrayToHexString(array: Array<Byte>): String {
            val result = StringBuilder(array.size * 2)

            var toAppend: String
            for (byte in array) {
                toAppend = String.format("%2X", byte).replace(' ', '0')
                result.append(toAppend)
            }

            return result.toString()
        }

        /**
         * The type or extension of the file
         *
         * @param file the file to get the type
         * @return the file type or extension
         */
        fun getFileType(file: File): String {
            var type = file.extension

            if (type.isEmpty()) {
                type = Constants.FILE_TYPE
            }

            return type
        }

        /**
         * Get the evidence file from filesystem
         *
         * @param filename the name of the file
         * @return the content of the file as string
         */
        fun getFile(filename: String): String? {
            val file = File(filename)
            return file.readText(Charset.defaultCharset())
        }

        /**
         * Encrypt the given String with the given public key
         *
         * @param string the string to encrypt
         * @param publicKey the public key
         * @return the encrypted string
         */
        fun encryptString(string: String, publicKey: PublicKey): String {

            val cipher: Cipher = Cipher.getInstance(Constants.CIPHER_SUITE)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedMessage = cipher.doFinal(string.toByteArray(Charset.defaultCharset()))

            return Base64.encodeToString(encryptedMessage, Base64.DEFAULT)
        }

        /**
         * Get the public key as File
         *
         * @param uuid the uuid of the server
         * @return the public key
         */
        fun getPublicKey(uuid: String) : PublicKey {
            val publicKeyContentFile = getFile("${uuid}_public_key.pem")
            val keyBytes: ByteArray = Base64.decode(publicKeyContentFile, Base64.DEFAULT)
            val spec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(Constants.CIPHER_ALGORITHM)

            return keyFactory.generatePublic(spec)
        }
    }

}