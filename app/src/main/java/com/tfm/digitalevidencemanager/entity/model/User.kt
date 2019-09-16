package com.tfm.digitalevidencemanager.entity.model

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.tfm.digitalevidencemanager.entity.database_connector.EvidenceDB
import com.tfm.digitalevidencemanager.entity.interfaces.UserContract
import com.tfm.digitalevidencemanager.entity.database_connector.UserDB

/**
 * The representation of a *user*.
 *
 * This class represent a *user* object stored in database.
 *
 * @property id the database id of the *user*.
 * @property dni the dni of the real *user*.
 * @property name the name of the *user*.
 * @property identification the identification of the real *user*.
 */
class User(var id: Long?, val dni: String, val name: String, val identification: String) {

    /**
     * Store the *user* information into the database.
     *
     * @param context the context used to connect with database.
     */
    fun save(context: Context) {
        val db = UserDB(context)

        val values = ContentValues()
        values.put(UserContract.UserEntry.COLUMN_NAME_DNI, this.dni)
        values.put(UserContract.UserEntry.COLUMN_NAME_NAME, this.name)
        values.put(UserContract.UserEntry.COLUMN_NAME_IDENTIFICATION, this.identification)

        this.id = db.insert(values)

        if (this.id!! < 0) {
            throw ErrorSavingModel("Model ${this.javaClass.simpleName} not saved properly with this data [${values}]")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (identification != other.identification) return false

        return true
    }

    override fun hashCode(): Int {
        return identification.hashCode()
    }

    override fun toString(): String {
        return "User(id=$id, dni='$dni', name='$name', identification='$identification')"
    }

    companion object {

        /**
         * Delete some *users* from database.
         *
         * @param context the context used to connect with database.
         * @param ids the ids of the *users*.
         * @return operation result.
         */
        fun deleteSome(context: Context, ids: List<Long>): Boolean {
            val db = UserDB(context)
            return db.deleteSomeByIDs(ids)
        }

        /**
         * Get a *user* from database.
         *
         * @param context the context used to connect with database.
         * @param id the id of the *user*.
         * @return the *user*.
         */
        fun getUser(context: Context, id: Long): User? {
            val db = UserDB(context)

            val values = db.getByID(id)
            return storeValues(values)
        }

        /**
         * Get a *user* from database.
         *
         * @param context the context used to connect with database.
         * @param identification the identification of the *user*.
         * @return the *user*.
         */
        fun getUser(context: Context, identification: String): User? {
            val db = UserDB(context)

            val values = db.getByIdentification(identification)
            return storeValues(values)
        }

        private fun storeValues(values: ContentValues?): User? {
            if (values != null) {
                val valuesId = values.getAsLong(UserContract.UserEntry.COLUMN_NAME_ID)
                val valuesDni = values.getAsString(UserContract.UserEntry.COLUMN_NAME_DNI)
                val valuesName = values.getAsString(UserContract.UserEntry.COLUMN_NAME_NAME)
                val valuesIdentification = values.getAsString(UserContract.UserEntry.COLUMN_NAME_IDENTIFICATION)
                return User(valuesId, valuesDni, valuesName, valuesIdentification)
            }
            return null
        }

        /**
         * Delete all *users* from database.
         *
         * @param context the context used to connect with database.
         * @return operation result.
         */
        fun deleteAll(context: Context): Boolean {
            val db = UserDB(context)
            return db.deleteAll()
        }

        /**
         * Delete a *user* from database.
         *
         * @param context the context used to connect with database.
         * @param id the id of the *device*.
         * @return operation result.
         */
        fun deleteByID(context: Context, id: Long): Boolean {
            val db = UserDB(context)
            return db.deleteByID(id)
        }

        /**
         * Get all the *users* from database.
         *
         * @param context the context used to connect with database.
         * @return the *users* list.
         */
        fun getAll(context: Context) : List<User> {
            val db = UserDB(context)
            val valuesList =  db.getAll()

            val userList = ArrayList<User?>()
            for(value in valuesList){
                userList.add(User.storeValues(value))
            }

            return userList.filterNotNull()
        }
    }

}