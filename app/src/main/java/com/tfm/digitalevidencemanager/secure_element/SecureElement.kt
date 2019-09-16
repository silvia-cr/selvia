package com.tfm.digitalevidencemanager.secure_element

import android.content.ContentValues
import android.content.Context


class SecureElement() {

    fun save(context: Context) {

    }

    fun get(context: Context): String {
        val sec = SecureElementConnector(context)
        return sec.read("test").toString()
    }

    fun store(context: Context, text : String) : String?{
        val sec = SecureElementConnector(context)
        return sec.write(ContentValues(), text)
    }
}