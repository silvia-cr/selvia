package com.tfm.digitalevidencemanager.entity.model

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.tfm.digitalevidencemanager.entity.database_connector.DeviceDB
import com.tfm.digitalevidencemanager.entity.interfaces.DeviceContract
import kotlin.random.Random

/**
 * The representation of a *device*.
 *
 * This class represent a *device* object stored in database.
 *
 * @property id the database id of the *device*.
 * @property uuid the uuid of the real *device*.
 * @property isServer indicate if this *device* is a server.
 * @property connection the connection method (for example, an IP if it is a server).
 */
class Device(var id: Long?, val uuid: String, val isServer: Boolean, var connection: String?) {

    /**
     * Store the *device* information into the database.
     *
     * @param context the context used to connect with database.
     */
    fun save(context: Context) {
        val db = DeviceDB(context)
        val values = ContentValues()

        if (this.connection != null) {
            values.put(DeviceContract.DeviceEntry.COLUMN_NAME_CONNECTION, this.connection)
        }

        values.put(DeviceContract.DeviceEntry.COLUMN_NAME_UUID, this.uuid)
        values.put(DeviceContract.DeviceEntry.COLUMN_NAME_IS_SERVER, this.isServer)

        this.id = db.insert(values)

        if (this.id!! < 0) {
            throw ErrorSavingModel("Model ${this.javaClass.simpleName} not saved properly with this data [${values}]")
        }
    }

    /**
     * Do ping to the *device*.
     *
     * @param context the context used to connect with device.
     * @return the ms to connect to the server
     */
    fun ping(context: Context):Int{
        return Random.nextInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Device

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString(): String {
        return "Device(id=$id, uuid='$uuid', is_server=$isServer)"
    }

    companion object {

        /**
         * Delete some *devices* from database.
         *
         * @param context the context used to connect with database.
         * @param ids the ids of the *devices*.
         * @return operation result.
         */
        fun deleteSome(context: Context, ids: List<Long>): Boolean {
            val db = DeviceDB(context)
            return db.deleteSomeByIDs(ids)
        }

        /**
         * Get a *device* from database.
         *
         * @param context the context used to connect with database.
         * @param id the id of the *device*.
         * @return the *device*.
         */
        fun getDevice(context: Context, id: Long): Device? {
            val db = DeviceDB(context)

            val values = db.getByID(id)
            return storeValues(values)
        }

        /**
         * Get a *device* from database.
         *
         * @param context the context used to connect with database.
         * @param uuid the uuid of the *device*.
         * @return the *device*.
         */
        fun getDevice(context: Context, uuid: String): Device? {
            val db = DeviceDB(context)

            val values = db.getByUUID(uuid)
            return storeValues(values)
        }

        private fun storeValues(values: ContentValues?): Device? {
            if (values != null) {
                val valuesId = values.getAsLong(DeviceContract.DeviceEntry.COLUMN_NAME_ID)
                val valuesUuid = values.getAsString(DeviceContract.DeviceEntry.COLUMN_NAME_UUID)
                val valuesIsServer = values.getAsBoolean(DeviceContract.DeviceEntry.COLUMN_NAME_IS_SERVER)
                val valuesIp = values.getAsString(DeviceContract.DeviceEntry.COLUMN_NAME_CONNECTION)
                return Device(valuesId, valuesUuid, valuesIsServer, valuesIp)
            }
            return null
        }

        /**
         * Delete all *devices* from database.
         *
         * @param context the context used to connect with database.
         * @return operation result.
         */
        fun deleteAll(context: Context): Boolean {
            val db = DeviceDB(context)
            return db.deleteAll()
        }

        /**
         * Delete a *device* from database.
         *
         * @param context the context used to connect with database.
         * @param id the id of the *device*.
         * @return operation result.
         */
        fun deleteByID(context: Context, id: Long): Boolean {
            val db = DeviceDB(context)
            return db.deleteByID(id)
        }

        /**
         * Get a server *device* from database. It does a ping over each server and returns the device with less time
         *
         * @param context the context used to connect with database.
         * @param id the id of the *device*.
         * @return the *device*.
         */
        fun getServer(context: Context): Device? {
            val list = this.getAll(context)

            if(!list.isEmpty()){
                var selected = list.get(0)
                var time = 5000

                list.forEach {
                    val newTime = it.ping(context)
                    if(newTime < time){
                        time = newTime
                        selected = it
                    }
                }
                return selected
            }else{
                return null
            }
        }

        /**
         * Get all the *devices* from database.
         *
         * @param context the context used to connect with database.
         * @return the *device* list.
         */
        fun getAll(context: Context) : List<Device> {
            val db = DeviceDB(context)
            val valuesList =  db.getAll()

            val deviceList = ArrayList<Device?>()
            for(value in valuesList){
                deviceList.add(storeValues(value))
            }

            return deviceList.filterNotNull()
        }
    }

}