package com.tfm.digitalevidencemanager.entity.interfaces

import android.provider.BaseColumns

object UserContract {

    object UserEntry : BaseColumns {
        const val TABLE_NAME = "user"
        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_NAME= "name"
        const val COLUMN_NAME_DNI = "dni"
        const val COLUMN_NAME_IDENTIFICATION = "identification"
    }
}