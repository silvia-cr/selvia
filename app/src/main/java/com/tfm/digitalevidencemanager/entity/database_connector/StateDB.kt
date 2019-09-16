package com.tfm.digitalevidencemanager.entity.database_connector

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import com.tfm.digitalevidencemanager.Local
import com.tfm.digitalevidencemanager.Local.TAG
import com.tfm.digitalevidencemanager.entity.interfaces.DeviceContract
import com.tfm.digitalevidencemanager.entity.interfaces.StateContract
import com.tfm.digitalevidencemanager.entity.interfaces.StateInterface
import com.tfm.digitalevidencemanager.entity.model.SomeRowsReturned
import net.sqlcipher.Cursor
import net.sqlcipher.DatabaseUtils
import java.util.*

/**
 * State connections to database.
 *
 * This class contains the methods to use the *state* table in database.
 *
 * @property context the context used to connect with database.
 */
class StateDB(context: Context) : StateInterface {

    private val dbHelper = DBHelper(context)

    companion object {
        val SQL_CREATE_TABLE =
            "CREATE TABLE ${StateContract.StateEntry.TABLE_NAME} (" +
                    // Parent state
                    "${StateContract.StateEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY, " +
                    "${StateContract.StateEntry.COLUMN_NAME_EVIDENCE} INTEGER NOT NULL, " +
                    "${StateContract.StateEntry.COLUMN_NAME_STATE} INTEGER NOT NULL, " +
                    "${StateContract.StateEntry.COLUMN_NAME_DATE} INTEGER NOT NULL, " +
                    "${StateContract.StateEntry.COLUMN_NAME_USER} INTEGER NOT NULL, " +
                    "${StateContract.StateEntry.COLUMN_NAME_DEVICE} INTEGER NOT NULL, " +

                    // Child states
                    "${StateContract.StateEntry.COLUMN_NAME_ORIGIN_PATH} TEXT, " +
                    "${StateContract.StateEntry.COLUMN_NAME_DESTINATION_DEVICE} INTEGER, " +
                    "${StateContract.StateEntry.COLUMN_NAME_CAN_DELETE} INTEGER, " + // bool
                    "${StateContract.StateEntry.COLUMN_NAME_FIELD} TEXT, " +
                    "${StateContract.StateEntry.COLUMN_NAME_OLD_VALUE} TEXT, " +
                    "${StateContract.StateEntry.COLUMN_NAME_NEW_VALUE} TEXT, " +

                    // Constraints
                    "CHECK(${StateContract.StateEntry.COLUMN_NAME_CAN_DELETE} = 0 OR ${StateContract.StateEntry.COLUMN_NAME_CAN_DELETE} = 1), " +
                    "CHECK (${getAllConstraints()}) " +

                    //Foreign keys
                    "FOREIGN KEY(${StateContract.StateEntry.COLUMN_NAME_DEVICE}) " +
                    "REFERENCES ${DeviceContract.DeviceEntry.TABLE_NAME}(${DeviceContract.DeviceEntry.COLUMN_NAME_ID})" +
                    ")"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${StateContract.StateEntry.TABLE_NAME}"

        private fun getAllConstraints(): String {
            var constraints = ""
            for (state in StateContract.states) {
                constraints += getConstraintsByState(
                    state.key
                ) + " OR "
            }

            return constraints.substring(0, constraints.length - 3)
        }

        private fun getConstraintsByState(state: Int): String {
            val requiredFields = StateContract.StatesFieldsList[state]
            var checkSQL = "(${StateContract.StateEntry.COLUMN_NAME_STATE} = ${state} AND "

            for (field in allStatesFields()) {
                if (requiredFields.contains(field)) {
                    checkSQL += "${field} IS NOT NULL AND "
                } else {
                    checkSQL += "${field} IS NULL AND "
                }
            }

            return checkSQL.substring(0, checkSQL.length - 4) + ")"
        }

        private fun allStatesFields(): List<String> {
            var statesAllFieldsList = arrayOf<String>()
            for (stateFields in StateContract.StatesFieldsList) {
                statesAllFieldsList = statesAllFieldsList + stateFields
            }
            return statesAllFieldsList.distinct()
        }
    }

    /**
     * Insert a new row in *state* table.
     *
     * @param values the values to store in the new row.
     * @return the id of the row.
     */
    override fun insert(values: ContentValues): Long {
        val db = dbHelper.getWritableDatabase(Local.db_password)

        val id = db.insert(StateContract.StateEntry.TABLE_NAME, null, values)
        db.close()

        return id
    }

    /**
     * Delete a row from *state* table by its id.
     * @param id the id of the row to delete.
     * @return operation result.
     */
    override fun deleteByID(id: Long): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val whereClause = "${StateContract.StateEntry.COLUMN_NAME_ID}=?"
        val whereValues = arrayOf(id.toString())
        val deletedRows: Int

        try {
            deletedRows = db.delete(StateContract.StateEntry.TABLE_NAME, whereClause, whereValues)
        } finally {
            db.close()
        }

        return deletedRows == 1
    }

    /**
     * Delete all *state* rows from the database.
     *
     * @return operation result.
     */
    override fun deleteAll(): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)

        val rows: Int
        val cursor : Cursor

        try {
            db.delete(StateContract.StateEntry.TABLE_NAME, null, null)
            cursor = db.query(StateContract.StateEntry.TABLE_NAME, null, null, null, null, null, null)
            rows = cursor.count

            cursor.close()
        } finally {
            db.close()

        }

        return rows == 0
    }

    /**
     * Delete a group of *state* rows by theirs ids.
     *
     * @param ids the rows id to delete.
     * @return operation result.
     */
    override fun deleteSomeByIDs(ids: List<Long>): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val idsStr = ids.joinToString(",", "(", ")")
        val whereClause = "${StateContract.StateEntry.COLUMN_NAME_ID} IN " + idsStr
        val deletedRows: Int

        try {
            deletedRows = db.delete(StateContract.StateEntry.TABLE_NAME, whereClause, null)
        } finally {
            db.close()
        }

        return deletedRows == ids.size
    }

    /**
     * Get a *state* row by its row id.
     *
     * @param id the id of the *state* row.
     * @return the *state* row or null.
     */
    override fun getByID(id: Long): ContentValues? {
        val state = ContentValues()
        val selection = "${StateContract.StateEntry.COLUMN_NAME_ID}=?"
        val args = arrayOf(id.toString())

        val cursor = getStates(null, selection, args)

        if (cursor.moveToFirst()) {
            DatabaseUtils.cursorRowToContentValues(cursor, state)
        } else {
            cursor.close()
            return null
        }

        if (cursor.moveToNext()) {
            throw SomeRowsReturned("[${this.javaClass.simpleName}] [ID: ${id}] There are some objects with this identifier")
        }

        cursor.close()
        return state
    }

    /**
     * Get the *state* rows associates to an *evidence*.
     *
     * @param evidence_id the id of the *evidence*.
     * @return the list of the *states* associates to this *evidence*.
     */
    override fun getByEvidence(evidence_id: Long): List<ContentValues> {
        val selection = "${StateContract.StateEntry.COLUMN_NAME_EVIDENCE}=?"
        val args = arrayOf(evidence_id.toString())

        return getList(null, selection, args)
    }

    /**
     * Get all the *state* rows associates to an *evidence* and a *state*.
     *
     * @param evidence_id the id of the *evidence*.
     * @param state the identifier of the *state*.
     * @return the list of the *states* with this identifier associates to this *evidence*.
     */
    override fun getByEvidenceAndState(evidence_id: Long, state: Int): List<ContentValues> {
        val selection = "${StateContract.StateEntry.COLUMN_NAME_EVIDENCE}=? AND" +
                " ${StateContract.StateEntry.COLUMN_NAME_STATE}=?"
        val args = arrayOf(evidence_id.toString(), state.toString())

        return getList(null, selection, args)
    }

    /**
     * Delete all *states* associates to an *evidence*.
     *
     * @param evidence_id the id of the evidence.
     * @return operation result.
     */
    override fun deleteByEvidence(evidence_id: Long): Boolean {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val whereClause = "${StateContract.StateEntry.COLUMN_NAME_EVIDENCE}=?"
        val whereValues = arrayOf(evidence_id.toString())
        val ok: Boolean

        try {
            db.delete(StateContract.StateEntry.TABLE_NAME, whereClause, whereValues)
            ok = true
        } finally {
            db.close()
        }

        return ok
    }

    private fun getList(columns: Array<String>?, selection: String?, args: Array<String>?): List<ContentValues> {
        val statesList = ArrayList<ContentValues>()

        var state: ContentValues

        val cursor = getStates(columns, selection, args)

        if (cursor.moveToFirst()) {
            do {
                state = ContentValues()
                DatabaseUtils.cursorRowToContentValues(cursor, state)
                statesList.add(state)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return statesList
    }

    private fun getStates(columns: Array<String>?, selection: String?, args: Array<String>?): Cursor {
        val db = dbHelper.getWritableDatabase(Local.db_password)
        val cursor: Cursor
        try {
            cursor = db.query(StateContract.StateEntry.TABLE_NAME, columns, selection, args, null, null, null)
            cursor.moveToFirst()

        } finally {
            db.close()
        }

        return cursor
    }

    /**
     * Get the values of given keys of all *states* from database.
     *
     * @param keys
     * @return list with *states* values rows.
     */
    override fun getValues(keys: List<String>, state: Int): List<ContentValues> {
        val columns = keys.toTypedArray()

        val selection = "${StateContract.StateEntry.COLUMN_NAME_STATE}=?"
        val args = arrayOf(state.toString())

        return getList(columns, selection, args)
    }

    fun getAll() {
        val cursor = getStates(null, null, null)
        val state = ContentValues()
        if (cursor.moveToFirst()) {
            do {
                DatabaseUtils.cursorRowToContentValues(cursor, state)
                Log.d(TAG, state.toString())
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

}