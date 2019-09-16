package com.tfm.digitalevidencemanager.entity.database_connector

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.tfm.digitalevidencemanager.Local
import com.tfm.digitalevidencemanager.entity.model.SomeRowsReturned
import com.tfm.digitalevidencemanager.entity.interfaces.UserContract
import com.tfm.digitalevidencemanager.entity.interfaces.UserInterface
import net.sqlcipher.Cursor
import net.sqlcipher.DatabaseUtils

/**
 * User connections to database.
 *
 * This class contains the methods to use the *user* table in database.
 *
 * @property context the context used to connect with database.
 */
class UserDB(context: Context) : UserInterface {

    companion object {
        const val SQL_CREATE_TABLE =
            "CREATE TABLE ${UserContract.UserEntry.TABLE_NAME} (" +
                    "${UserContract.UserEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY, " +
                    "${UserContract.UserEntry.COLUMN_NAME_NAME} TEXT NOT NULL, " +
                    "${UserContract.UserEntry.COLUMN_NAME_DNI} TEXT NOT NULL, " +
                    "${UserContract.UserEntry.COLUMN_NAME_IDENTIFICATION} TEXT NOT NULL UNIQUE" +
                    ")"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${UserContract.UserEntry.TABLE_NAME}"
    }

    private val dbHelper = DBHelper(context)

    /**
     * Insert a new row in *user* table.
     *
     * @param values the values to store in the new row.
     * @return the id of the row.
     */
    override fun insert(values: ContentValues): Long {
        val db = dbHelper.getWritableDatabase(Local.db_password)

        val id = db.insert(UserContract.UserEntry.TABLE_NAME, null, values)
        db.close()

        return id
    }

    /**
     * Delete a row from *user* table by its id.
     * @param id the id of the row to delete.
     * @return operation result.
     */
    override fun deleteByID(id: Long): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val whereClause = "${UserContract.UserEntry.COLUMN_NAME_ID}=?"
        val whereValues = arrayOf(id.toString())
        val deletedRows : Int

        try {
            deletedRows = db.delete(UserContract.UserEntry.TABLE_NAME, whereClause, whereValues)
        } finally {
            db.close()
        }

        return deletedRows == 1
    }

    /**
     * Delete all *user* rows from the database.
     *
     * @return operation result.
     */
    override fun deleteAll(): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)

        val rows: Int
        val cursor: Cursor

        try {
            db.delete(UserContract.UserEntry.TABLE_NAME, null, null)
            cursor = db.query(UserContract.UserEntry.TABLE_NAME, null, null, null, null, null, null)
            rows = cursor.count

            cursor.close()
        } finally {
            db.close()
        }

        return rows == 0
    }

    /**
     * Delete a group of *user* rows by theirs ids.
     *
     * @param ids the rows id to delete.
     * @return operation result.
     */
    override fun deleteSomeByIDs(ids: List<Long>): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val idsStr = ids.joinToString(",","(",")")
        val whereClause = "${UserContract.UserEntry.COLUMN_NAME_ID} IN " + idsStr
        val deletedRows : Int

        try {
            deletedRows = db.delete(UserContract.UserEntry.TABLE_NAME, whereClause, null)
        } finally {
            db.close()
        }

        return deletedRows == ids.size
    }

    /**
     * Get a *user* row by its identification.
     *
     * @param id the identification of the *user*.
     * @return the *user* row or null.
     */
    override fun getByIdentification(id: String): ContentValues? {
        val user = ContentValues()
        val selection = "${UserContract.UserEntry.COLUMN_NAME_IDENTIFICATION}=?"
        val args = arrayOf(id)

        val cursor = getUsers(null, selection, args)

        if (cursor.moveToFirst()) {
            DatabaseUtils.cursorRowToContentValues(cursor, user)
        }else{
            cursor.close()
            return null
        }

        if (cursor.moveToNext()) {
            throw SomeRowsReturned("[${this.javaClass.simpleName}] [Identification: ${id}] There are some objects with this identifier")
        }

        cursor.close()
        return user
    }


    /**
     * Get a *user* row by its row id.
     *
     * @param id the id of the *user* row.
     * @return the *user* row or null.
     */
    override fun getByID(id: Long): ContentValues? {
        val user = ContentValues()
        val selection = "${UserContract.UserEntry.COLUMN_NAME_ID}=?"
        val args = arrayOf(id.toString())

        val cursor = getUsers(null, selection, args)

        if (cursor.moveToFirst()) {
            DatabaseUtils.cursorRowToContentValues(cursor, user)
        }else{
            cursor.close()
            return null
        }

        if (cursor.moveToNext()) {
            throw SomeRowsReturned("[${this.javaClass.simpleName}] [ID: ${id}] There are some objects with this identifier")
        }

        cursor.close()
        return user
    }

    private fun getUsers(columns: Array<String>?, selection: String?, args: Array<String>?): Cursor {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val cursor: Cursor
        try {
            cursor = db.query(UserContract.UserEntry.TABLE_NAME, columns, selection, args, null, null, null)
            cursor.moveToFirst()
        } finally {
            db.close()
        }

        return cursor
    }

    /**
     * Get all *users*.
     *
     * @return list with *users* rows.
     */
    override fun getAll(): List<ContentValues>{
        val usersList = ArrayList<ContentValues>()
        var user : ContentValues
        val cursor = getUsers(null, null, null)

        if (cursor.moveToFirst()) {
            do{
                user = ContentValues()
                DatabaseUtils.cursorRowToContentValues(cursor, user)
                usersList.add(user)
            }while (cursor.moveToNext())
        }

        cursor.close()
        return usersList
    }
}