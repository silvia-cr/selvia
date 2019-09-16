package com.tfm.digitalevidencemanager.integration_tests

import android.content.Context
import com.tfm.digitalevidencemanager.entity.model.Device
import com.tfm.digitalevidencemanager.entity.model.User
import com.tfm.digitalevidencemanager.entity.model.Evidence
import com.tfm.digitalevidencemanager.api.PriorityEnum
import com.tfm.digitalevidencemanager.api.SeverityEnum
import java.io.File
import java.io.FileOutputStream

object TestHelper {
    fun createUser(context: Context, dni: String, name: String, identification: String): User {
        val user = User(null, dni, name, identification)
        user.save(context)
        return user
    }

    fun createUser(context: Context): User {
        return createUser(
            context,
            "11111",
            "TestName",
            System.currentTimeMillis().toString()
        )
    }

    fun createDevice(context: Context, uuid: String, isServer: Boolean, connection: String?): Device {
        val device = Device(null, uuid, isServer, connection)
        device.save(context)
        return device
    }

    fun createDevice(context: Context): Device {
        return createDevice(
            context,
            System.currentTimeMillis().toString(),
            false,
            "0.0.0.0"
        )
    }

    fun createEvidence(
        context: Context,
        identifier: String,
        id_se: String,
        name: String,
        severity: Int,
        priority: Int,
        type: String,
        metadata: String
    ): Evidence {
        val evidence = Evidence(null, identifier, id_se, name, severity, priority, type, metadata)
        evidence.save(context)
        return evidence
    }

    fun createEvidence(context: Context): Evidence {
        return createEvidence(
            context,
            "uuid_".plus(System.currentTimeMillis()),
            System.currentTimeMillis().toString(),
            "testName",
            SeverityEnum.MINOR.id,
            PriorityEnum.HIGH.id,
            "image",
            "metadata"
        )
    }

    fun createFile(path: String, content: String): File {
        val file = File(path)

        if(!file.exists()){
            File(file.parent).mkdirs()
            file.createNewFile()
        }

        val outputStream = FileOutputStream(file)
        val outputBytes = content.toByteArray()
        outputStream.write(outputBytes)

        return file
    }

    fun deleteTestFolder(folderPath: String){
        val folder = File(folderPath)
        folder.deleteRecursively()
    }

    fun deleteTestEvidence(context: Context, time: Long){
        val path = "/data/data/com.tfm.digitalevidencemanager/files/"
        val folder = File(path)
        val files = folder.list()
        if(files.isNotEmpty()){
            val lastFile = files[files.size-1]

            // to ensure we delete the test file (we have test where we do not create files and we dont remove anything in this case)
            if (lastFile.substring(17,lastFile.length-4).toLong() > time){
                context.deleteFile(lastFile)
            }
        }
    }
}