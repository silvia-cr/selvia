package com.tfm.digitalevidencemanager.entity.interfaces

import android.provider.BaseColumns

object EvidenceContract {

    const val PRIORITY_HIGH = 0
    const val PRIORITY_MEDIUM = 1
    const val PRIORITY_LOW = 2

    const val SEVERITY_CRITICAL = 0
    const val SEVERITY_MAJOR = 1
    const val SEVERITY_MEDIUM = 2
    const val SEVERITY_MINOR = 3

    object EvidenceEntry : BaseColumns {
        const val TABLE_NAME = "evidence"
        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_IDENTIFIER= "identifier"
        const val COLUMN_NAME_ID_SE= "idSE"
        const val COLUMN_NAME_HASH= "hash"
        const val COLUMN_NAME_SEVERITY = "severity"
        const val COLUMN_NAME_PRIORITY = "priority"
        const val COLUMN_NAME_TYPE = "type"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_METADATA = "metadata"
    }
}