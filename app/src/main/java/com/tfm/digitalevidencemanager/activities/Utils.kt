package com.tfm.digitalevidencemanager.activities

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.tfm.digitalevidencemanager.entity.model.Evidence
import java.io.File
import java.io.FileNotFoundException

class Utils {

    companion object {
        fun getPathFromUri(context: Context, uri: Uri): String {
            val projection = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = context.contentResolver.query(uri, projection, null, null, null)

            var path:String? = null
            if(cursor!=null){
                val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                path = cursor.getString(columnIndex)
                cursor.close()
            }

            if (path == null) {
                path = ""
            }
            return path
        }

        fun getFilepathFromEvidence(context: Context, evidence: Evidence) : String {
            val path = context.filesDir.toString()
            return path.plus("/${evidence.identifier}.evd")
        }

        fun deleteFilesFromFolder(path: String) : Boolean {
            val folder = File(path)

            if(!folder.exists()){
                throw FileNotFoundException("$path: File not found")
            }

            folder.listFiles().forEach {
                it.delete()
            }

            return true
        }
    }

}