package com.tfm.digitalevidencemanager.entity.interfaces

import android.provider.BaseColumns

object DeviceContract {

    object DeviceEntry : BaseColumns {
        const val TABLE_NAME = "device"
        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_UUID= "uuid"
        const val COLUMN_NAME_IS_SERVER = "is_server"
        const val COLUMN_NAME_CONNECTION = "connection"
    }
}