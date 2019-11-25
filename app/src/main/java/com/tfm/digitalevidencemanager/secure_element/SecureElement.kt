package com.tfm.digitalevidencemanager.secure_element

import android.content.ContentValues
import android.content.Context


class SecureElement() {

    fun save(context: Context) {

    }

    fun get(context: Context, alias: String): String {
        val sec = SecureElementConnector(context)
        return sec.read(alias).toString()
    }

    fun store(context: Context, text : String) : String?{
        val sec = SecureElementConnector(context)
        return sec.write(ContentValues(), text)
    }
}