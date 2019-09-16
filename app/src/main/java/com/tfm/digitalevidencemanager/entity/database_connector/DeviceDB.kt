package com.tfm.digitalevidencemanager.entity.database_connector

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import com.tfm.digitalevidencemanager.Local
import com.tfm.digitalevidencemanager.Local.TAG
import com.tfm.digitalevidencemanager.entity.interfaces.DeviceContract
import com.tfm.digitalevidencemanager.entity.interfaces.DeviceInterface
import com.tfm.digitalevidencemanager.entity.model.Device
import com.tfm.digitalevidencemanager.entity.model.SomeRowsReturned
import net.sqlcipher.Cursor
import net.sqlcipher.DatabaseUtils

/**
 * Device connections to database.
 *
 * This class contains the methods to use the *device* table in database.
 *
 * @property context the context used to connect with database.
 */
class DeviceDB(context: Context) : DeviceInterface {

    companion object {
        const val SQL_CREATE_TABLE =
            "CREATE TABLE ${DeviceContract.DeviceEntry.TABLE_NAME} (" +
                    "${DeviceContract.DeviceEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY, " +
                    "${DeviceContract.DeviceEntry.COLUMN_NAME_UUID} TEXT UNIQUE NOT NULL, " +
                    "${DeviceContract.DeviceEntry.COLUMN_NAME_IS_SERVER} INTEGER NOT NULL, " +
                    "${DeviceContract.DeviceEntry.COLUMN_NAME_CONNECTION} TEXT " +
                    "CHECK(${DeviceContract.DeviceEntry.COLUMN_NAME_IS_SERVER} = 0 OR ${DeviceContract.DeviceEntry.COLUMN_NAME_IS_SERVER} = 1)" +
                    ")"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${DeviceContract.DeviceEntry.TABLE_NAME}"
    }

    private val dbHelper = DBHelper(context)

    /**
     * Insert a new row in *device* table.
     *
     * @param values the values to store in the new row.
     * @return the id of the row.
     */
    override fun insert(values: ContentValues): Long {
        val db = dbHelper.getWritableDatabase(Local.db_password)

        val id = db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, values)
        db.close()

        return id
    }

    /**
     * Delete a row from *device* table by its id.
     * @param id the id of the row to delete.
     * @return operation result.
     */
    override fun deleteByID(id: Long): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val whereClause = "${DeviceContract.DeviceEntry.COLUMN_NAME_ID}=?"
        val whereValues = arrayOf(id.toString())
        val deletedRows : Int

        try {
            deletedRows = db.delete(DeviceContract.DeviceEntry.TABLE_NAME, whereClause, whereValues)
        } finally {
            db.close()
        }

        return deletedRows == 1
    }

    /**
     * Delete all *device* rows from the database.
     *
     * @return operation result.
     */
    override fun deleteAll(): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)

        val rows: Int
        val cursor: Cursor

        try {
            db.delete(DeviceContract.DeviceEntry.TABLE_NAME, null, null)
            cursor = db.query(DeviceContract.DeviceEntry.TABLE_NAME, null, null, null, null, null, null)
            rows = cursor.count

            cursor.close()
        } finally {
            db.close()
        }

        return rows == 0
    }

    /**
     * Delete a group of *device* rows by theirs ids.
     *
     * @param ids the rows id to delete.
     * @return operation result.
     */
    override fun deleteSomeByIDs(ids : List<Long>): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val idsStr = ids.joinToString(",","(",")")
        val whereClause = "${DeviceContract.DeviceEntry.COLUMN_NAME_ID} IN " + idsStr
        val deletedRows : Int

        try {
            deletedRows = db.delete(DeviceContract.DeviceEntry.TABLE_NAME, whereClause, null)
        } finally {
            db.close()
        }

        return deletedRows == ids.size
    }

    /**
     * Get a *device* row by its uuid.
     *
     * @param uuid the uuid of the *device*.
     * @return the *device* row or null.
     */
    override fun getByUUID(uuid: String): ContentValues? {
        val device = ContentValues()
        val selection = "${DeviceContract.DeviceEntry.COLUMN_NAME_UUID}=?"
        val args = arrayOf(uuid)

        val cursor = getDevices(null, selection, args)

        if (cursor.moveToFirst()) {
            DatabaseUtils.cursorRowToContentValues(cursor, device)
        }else{
            return null
        }
        if (cursor.moveToNext()) {
            throw SomeRowsReturned("[${this.javaClass.simpleName}] [UUID: ${uuid}] There are some objects with this identifier")
        }

        cursor.close()

        return device
    }

    /**
     * Get a *device* row by its row id.
     *
     * @param id the id of the *device* row.
     * @return the *device* row or null.
     */
    override fun getByID(id: Long): ContentValues? {
        val device = ContentValues()
        val selection = "${DeviceContract.DeviceEntry.COLUMN_NAME_ID}=?"
        val args = arrayOf(id.toString())

        val cursor = getDevices(null, selection, args)

        if (cursor.moveToFirst()) {
            DatabaseUtils.cursorRowToContentValues(cursor, device)
        }else{
            cursor.close()
            return null
        }

        if (cursor.moveToNext()) {
            throw SomeRowsReturned("[${this.javaClass.simpleName}] [ID: ${id}] There are some objects with this identifier")
        }

        cursor.close()
        return device
    }

    /**
     * Get a server *device* row. If we have some *devices* returns the first
     *
     * @return the *device* row or null.
     */
    override fun getServer(): ContentValues? {
        val device = ContentValues()
        val selection = "${DeviceContract.DeviceEntry.COLUMN_NAME_IS_SERVER}=?"
        val args = arrayOf("1")

        val cursor = getDevices(null, selection, args)

        if (cursor.moveToFirst()) {
            DatabaseUtils.cursorRowToContentValues(cursor, device)
        }else{
            cursor.close()
            return null
        }

        cursor.close()
        return device
    }

    private fun getDevices(columns: Array<String>?, selection: String?, args: Array<String>?): Cursor {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val cursor: Cursor
        try {
            cursor = db.query(DeviceContract.DeviceEntry.TABLE_NAME, columns, selection, args, null, null, null)
            cursor.moveToFirst()

        } finally {
            db.close()
        }

        return cursor
    }

    /**
     * Get all *evidences*.
     *
     * @return list with *devices* rows.
     */
    override fun getAll(): List<ContentValues> {
        val evidencesList = ArrayList<ContentValues>()

        var state: ContentValues

        val cursor = getDevices(null, null, null)

        if (cursor.moveToFirst()) {
            do {
                state = ContentValues()
                DatabaseUtils.cursorRowToContentValues(cursor, state)
                evidencesList.add(state)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return evidencesList
    }
}
