package com.tfm.digitalevidencemanager.secure_element

import android.content.ContentValues
import android.content.Context
import android.se.omapi.SEService
import android.util.Log
import java.util.concurrent.Executors

class SecureElementConnector(val context: Context) : SecureElementInterface{

    private lateinit var seService : SEService
    private val TAG = "SE_CONNECTOR"

    private val onConnectedListener = SEService.OnConnectedListener {

        Log.i(TAG, "SE connected")

        val readers = seService.readers

        val DATA_APDU = byteArrayOf(0xA0.toByte(), 0x08, 0x00, 0x00, 0x00, 0xAA.toByte(), 0x00)
        val SELECTABLE_AID = byteArrayOf(
            0xA0.toByte(),
            0x00,
            0x00,
            0x04,
            0x76,
            0x41,
            0x6E,
            0x64,
            0x72,
            0x6F,
            0x69,
            0x64,
            0x43,
            0x54,
            0x53,
            0x31
        )

        if(readers.isNotEmpty()){
            val eSEReaders = readers.filter { it.isSecureElementPresent }
            val eSEReader = eSEReaders[0]

            Log.i(TAG, "Selected reader: ".plus(eSEReader.name))

            val session = eSEReader.openSession()
            val channel = session.openLogicalChannel(SELECTABLE_AID, 0x00.toByte())
            val selectResponse = channel.selectResponse
            val transmitResponse = channel.transmit(DATA_APDU)
        }else{
            Log.i(TAG, "There aren't readers on this device")
        }

        if (seService.isConnected){
            Log.d(TAG, "Connected")
            Log.d(TAG, seService.version + " " + seService.toString())
            val readers = seService.readers
            Log.d(TAG, "Readers: " + readers.size)
            for (reader in readers){
                Log.d(TAG, reader.name + "; SE: " + reader.isSecureElementPresent)

            }
        } else {
            Log.d(TAG, "No Connected")
        }
    }

    override fun read(alias: String): ContentValues {
        return ContentValues()
    }

    override fun write(values: ContentValues, alias: String): String? {
        //TODO: finish
        connect()

        return System.currentTimeMillis().toString()
    }

    private fun connect() : Boolean{
        val pool = Executors.newSingleThreadExecutor()
        seService = SEService(this.context, pool, this.onConnectedListener)
        return seService.isConnected
    }

}