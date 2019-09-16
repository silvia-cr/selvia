package com.tfm.digitalevidencemanager.api

object Constants {
    const val FILE_TYPE = "file"
    const val HASH_TYPE = "MD5"
    const val CIPHER_SUITE = "AES/GCM/NoPadding"
    const val CIPHER_ALGORITHM = "AES"
    const val EVIDENCE_EXTENSION = ".evd"
    const val ALIAS = "encrypt_data_alias"
    const val PROVIDER = "AndroidKeyStore"
    const val BLOCK_MODE = "GCM"
    const val PADDING = "NoPadding"

    // JSON Keys
    const val FILE_CREATION_DATE = "creation_date"
    const val FILE_LAST_ACCESS_DATE= "last_access"
    const val FILE_LAST_MODIFIED_DATE= "last_modified"
    const val FILE_SIZE = "size"
    const val FILE_SIZE_UNIT = "size_unit"
    const val FILE_UNIT = "bytes"
    const val FILE_NAME = "original_name"
    const val FILE_IS_DIRECTORY = "is_directory"
    const val FILE_KEY = "file_key"
}