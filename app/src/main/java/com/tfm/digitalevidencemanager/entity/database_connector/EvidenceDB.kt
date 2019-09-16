package com.tfm.digitalevidencemanager.entity.database_connector

import android.content.ContentValues
import android.content.Context
import com.tfm.digitalevidencemanager.Local
import com.tfm.digitalevidencemanager.entity.interfaces.EvidenceContract
import com.tfm.digitalevidencemanager.entity.interfaces.EvidenceInterface
import com.tfm.digitalevidencemanager.entity.interfaces.StateContract
import com.tfm.digitalevidencemanager.entity.model.SomeRowsReturned
import net.sqlcipher.Cursor
import net.sqlcipher.DatabaseUtils

/**
 * Evidence connections to database.
 *
 * This class contains the methods to use the *evidence* table in database.
 *
 * @property context the context used to connect with database.
 */
class EvidenceDB(context: Context) : EvidenceInterface {

    companion object {
        const val SQL_CREATE_TABLE =
            "CREATE TABLE ${EvidenceContract.EvidenceEntry.TABLE_NAME} (" +
                    "${EvidenceContract.EvidenceEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY, " +
                    "${EvidenceContract.EvidenceEntry.COLUMN_NAME_IDENTIFIER} TEXT UNIQUE, " +
                    "${EvidenceContract.EvidenceEntry.COLUMN_NAME_ID_SE} TEXT UNIQUE, " +
                    "${EvidenceContract.EvidenceEntry.COLUMN_NAME_HASH} TEXT, " +
                    "${EvidenceContract.EvidenceEntry.COLUMN_NAME_NAME} TEXT NOT NULL, " +
                    "${EvidenceContract.EvidenceEntry.COLUMN_NAME_TYPE} TEXT NOT NULL, " +
                    "${EvidenceContract.EvidenceEntry.COLUMN_NAME_PRIORITY} INTEGER, " +
                    "${EvidenceContract.EvidenceEntry.COLUMN_NAME_SEVERITY} INTEGER, " +
                    "${EvidenceContract.EvidenceEntry.COLUMN_NAME_METADATA} TEXT" +
                    ")"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${EvidenceContract.EvidenceEntry.TABLE_NAME}"
    }

    private val dbHelper = DBHelper(context)

    /**
     * Insert a new row in *evidence* table.
     *
     * @param values the values to store in the new row.
     * @return the id of the row.
     */
    override fun insert(values: ContentValues): Long {
        val db = dbHelper.getWritableDatabase(Local.db_password)

        val id = db.insert(EvidenceContract.EvidenceEntry.TABLE_NAME, null, values)
        db.close()

        return id
    }

    /**
     * Update a *evidence* row by its row id.
     *
     * @param id the id of the *evidence* row.
     * @return operation result.
     */
    override fun update(id: Long, values: ContentValues): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val whereClause = "${EvidenceContract.EvidenceEntry.COLUMN_NAME_ID}=?"
        val whereValues = arrayOf(id.toString())

        val affectedRows = db.update(EvidenceContract.EvidenceEntry.TABLE_NAME, values, whereClause, whereValues)
        db.close()

        return affectedRows == 1
    }

    /**
     * Delete a row from *evidence* table by its id.
     * @param id the id of the row to delete.
     * @return operation result.
     */
    override fun deleteByID(id: Long): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val whereClause = "${EvidenceContract.EvidenceEntry.COLUMN_NAME_ID}=?"
        val whereValues = arrayOf(id.toString())
        val deletedRows: Int

        try {
            deletedRows = db.delete(EvidenceContract.EvidenceEntry.TABLE_NAME, whereClause, whereValues)
        } finally {
            db.close()
        }

        return deletedRows == 1
    }

    /**
     * Delete all *evidence* rows from the database.
     *
     * @return operation result.
     */
    override fun deleteAll(): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)

        val rows: Int
        val cursor: Cursor

        try {
            db.delete(EvidenceContract.EvidenceEntry.TABLE_NAME, null, null)
            cursor = db.query(EvidenceContract.EvidenceEntry.TABLE_NAME, null, null, null, null, null, null)
            rows = cursor.count

            cursor.close()
        } finally {
            db.close()
        }

        return rows == 0
    }

    /**
     * Delete a group of *evidence* rows by theirs ids.
     *
     * @param ids the rows id to delete.
     * @return operation result.
     */
    override fun deleteSomeByIDs(ids: List<Long>): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val idsStr = ids.joinToString(",", "(", ")")
        val whereClause = "${EvidenceContract.EvidenceEntry.COLUMN_NAME_ID} IN " + idsStr
        val deletedRows: Int

        try {
            deletedRows = db.delete(EvidenceContract.EvidenceEntry.TABLE_NAME, whereClause, null)
        } finally {
            db.close()
        }

        return deletedRows == ids.size
    }

    /**
     * Get a *evidence* row by its row id.
     *
     * @param id the id of the *evidence* row.
     * @return the *evidence* row or null.
     */
    override fun getByID(id: Long): ContentValues? {
        val evidence = ContentValues()
        val selection = "${EvidenceContract.EvidenceEntry.COLUMN_NAME_ID}=?"
        val args = arrayOf(id.toString())

        val cursor = getEvidences(null, selection, args)

        if (cursor.moveToFirst()) {
            DatabaseUtils.cursorRowToContentValues(cursor, evidence)
        } else {
            cursor.close()
            return null
        }

        if (cursor.moveToNext()) {
            throw SomeRowsReturned("[${this.javaClass.simpleName}] [ID: ${id}] There are some objects with this identifier")
        }

        cursor.close()
        return evidence
    }

    private fun getEvidences(columns: Array<String>?, selection: String?, args: Array<String>?): Cursor {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val cursor: Cursor
        try {
            cursor = db.query(EvidenceContract.EvidenceEntry.TABLE_NAME, columns, selection, args, null, null, null)
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
        return getList(null, null, null)
    }

    /**
     * Get all the *evidence* ids that it can be deleted.
     *
     * An *evidence* can be deleted if its last stored *state* was SEND_TO_SERVER and CAN_DELETE is true.
     *
     * @return the list of the *evidences* id.
     */
    override fun getCanDeleteEvidences(): List<Long> {

        val canDeleteList = ArrayList<Long>()
        var evidence: ContentValues
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val cursor: Cursor
        try {
            cursor = db.rawQuery(
                "SELECT ${StateContract.StateEntry.COLUMN_NAME_EVIDENCE}, MAX(${StateContract.StateEntry.COLUMN_NAME_DATE}), " +
                        "${StateContract.StateEntry.COLUMN_NAME_STATE}, ${StateContract.StateEntry.COLUMN_NAME_CAN_DELETE} " +
                        "FROM ${StateContract.StateEntry.TABLE_NAME} " +
                        "GROUP BY ${StateContract.StateEntry.COLUMN_NAME_EVIDENCE}", null
            )

            if (cursor.moveToFirst()) {
                do {
                    evidence = ContentValues()
                    DatabaseUtils.cursorRowToContentValues(cursor, evidence)
                    if (evidence.getAsInteger(StateContract.StateEntry.COLUMN_NAME_STATE) == StateContract.SEND_TO_SERVER &&
                        evidence.getAsBoolean(StateContract.StateEntry.COLUMN_NAME_CAN_DELETE)
                    ) {
                        canDeleteList.add(evidence.getAsLong(StateContract.StateEntry.COLUMN_NAME_EVIDENCE))
                    }

                } while (cursor.moveToNext())
            }

            cursor.close()
        } finally {
            db.close()
        }

        return canDeleteList
    }

    /**
     * Get true if the *evidence* can be deleted.
     *
     * @param id the evidence.
     * @return true if the evidence can be deleted, false otherwise.
     */
    override fun getCanDeleteEvidence(id: Long): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val cursor: Cursor
        var delete = false
        try {
            cursor = db.rawQuery(
                "SELECT MAX(${StateContract.StateEntry.COLUMN_NAME_DATE}), " +
                        "${StateContract.StateEntry.COLUMN_NAME_STATE}, ${StateContract.StateEntry.COLUMN_NAME_CAN_DELETE} " +
                        "FROM ${StateContract.StateEntry.TABLE_NAME} " +
                        "WHERE ${StateContract.StateEntry.COLUMN_NAME_EVIDENCE} = $id ", null
            )

            if (cursor.moveToFirst()) {
                val evidence = ContentValues()
                DatabaseUtils.cursorRowToContentValues(cursor, evidence)

                if (evidence.getAsInteger(StateContract.StateEntry.COLUMN_NAME_STATE) == StateContract.SEND_TO_SERVER &&
                    evidence.getAsBoolean(StateContract.StateEntry.COLUMN_NAME_CAN_DELETE)
                ) {
                    delete = true
                }

                if (cursor.moveToNext()) {
                    delete = false
                }
            }

            cursor.close()
        } finally {
            db.close()
        }

        return delete
    }

    /**
     * Get all the *evidence* ids that need be synchronized with the server.
     *
     * An *evidence* need to be synchronized with the server if its last stored *state* was not SEND_TO_SERVER.
     *
     * @return the list of the *evidences* id.
     */
    override fun getNeedSyncEvidences(): List<Long> {
        val needSyncList = ArrayList<Long>()
        var evidence: ContentValues
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val cursor: Cursor
        try {
            cursor = db.rawQuery(
                "SELECT ${StateContract.StateEntry.COLUMN_NAME_EVIDENCE}, MAX(${StateContract.StateEntry.COLUMN_NAME_DATE}), " +
                        "${StateContract.StateEntry.COLUMN_NAME_STATE} " +
                        "FROM ${StateContract.StateEntry.TABLE_NAME} " +
                        "GROUP BY ${StateContract.StateEntry.COLUMN_NAME_EVIDENCE}", null
            )

            if (cursor.moveToFirst()) {
                do {
                    evidence = ContentValues()
                    DatabaseUtils.cursorRowToContentValues(cursor, evidence)
                    if (evidence.getAsInteger(StateContract.StateEntry.COLUMN_NAME_STATE) != StateContract.SEND_TO_SERVER) {
                        needSyncList.add(evidence.getAsLong(StateContract.StateEntry.COLUMN_NAME_EVIDENCE))
                    }

                } while (cursor.moveToNext())
            }

            cursor.close()
        } finally {
            db.close()
        }

        return needSyncList
    }

    /**
     * Get the values of given keys of all *evidences* from database.
     *
     * @param keys the keys of the values
     * @return list with *evidence* values rows.
     */
    override fun getValues(keys: List<String>): List<ContentValues> {
        val columns = keys.toTypedArray()
        return getList(columns, null, null)
    }

    private fun getList(columns: Array<String>?, selection: String?, args: Array<String>?): List<ContentValues> {
        val evidencesList = ArrayList<ContentValues>()

        var state: ContentValues

        val cursor = getEvidences(columns, selection, args)

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