package com.tfm.digitalevidencemanager.secure_element

import android.content.ContentValues

interface SecureElementInterface {

    fun read(alias : String) : ContentValues
    fun write(values : ContentValues, alias: String) : String?
}