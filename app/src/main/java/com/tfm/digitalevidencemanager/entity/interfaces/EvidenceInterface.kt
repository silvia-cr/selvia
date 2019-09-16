package com.tfm.digitalevidencemanager.entity.interfaces

import android.content.ContentValues
import com.tfm.digitalevidencemanager.entity.model.SomeRowsReturned

/**
 * Evidence connections to database.
 */
interface EvidenceInterface {

    /**
     * Insert a new row in *evidence* table.
     *
     * @param values the values to store in the new row.
     * @return the id of the row.
     */
    fun insert(values: ContentValues): Long

    /**
     * Update a *evidence* row by its row id.
     *
     * @param id the id of the *evidence* row.
     * @return operation result.
     */
    fun update(id: Long, values: ContentValues): Boolean

    /**
     * Delete a row from *evidence* table by its id.
     * @param id the id of the row to delete.
     * @return operation result.
     */
    fun deleteByID(id: Long): Boolean

    /**
     * Delete all *evidence* rows from the database.
     *
     * @return operation result.
     */
    fun deleteAll(): Boolean

    /**
     * Delete a group of *evidence* rows by theirs ids.
     *
     * @param ids the rows id to delete.
     * @return operation result.
     */
    fun deleteSomeByIDs(ids: List<Long>): Boolean

    /**
     * Get a *evidence* row by its row id.
     *
     * @param id the id of the *evidence* row.
     * @return the *evidence* row or null.
     */
    @Throws(SomeRowsReturned::class)
    fun getByID(id: Long): ContentValues?

    /**
     * Get all *evidences*.
     *
     * @return list with *evidences* rows.
     */
    fun getAll(): List<ContentValues>

    /**
     * Get all the *evidence* ids that it can be deleted.
     *
     * An *evidence* can be deleted if its last stored *state* was SEND_TO_SERVER and CAN_DELETE is true.
     *
     * @return the list of the *evidences* id.
     */
    fun getCanDeleteEvidences() : List<Long>

    /**
     * Get true if the *evidence* can be deleted.
     *
     * @param id the evidence.
     * @return true if the evidence can be deleted, false otherwise.
     */
    fun getCanDeleteEvidence(id:Long) : Boolean

    /**
     * Get all the *evidence* ids that need be synchronized with the server.
     *
     * An *evidence* need to be synchronized with the server if its last stored *state* was not SEND_TO_SERVER.
     *
     * @return the list of the *evidences* id.
     */
    fun getNeedSyncEvidences() : List<Long>

    /**
     * Get the values of the given keys of all *evidence* from database.
     *
     * @param keys
     * @return list with *evidence* values rows.
     */
    fun getValues(keys : List<String>): List<ContentValues>
}