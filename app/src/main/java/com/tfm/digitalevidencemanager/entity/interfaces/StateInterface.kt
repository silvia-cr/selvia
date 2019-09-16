package com.tfm.digitalevidencemanager.entity.interfaces

import android.content.ContentValues
import com.tfm.digitalevidencemanager.entity.model.SomeRowsReturned

/**
 * State connections to database.
 * States can not be modified or deleted.
 */
interface StateInterface {

    /**
     * Insert a new row in *state* table.
     *
     * @param values the values to store in the new row.
     * @return the id of the row.
     */
    fun insert(values : ContentValues) : Long

    /**
     * Delete a row from *state* table by its id.
     * @param id the id of the row to delete.
     * @return operation result.
     */
    fun deleteByID(id : Long) : Boolean

    /**
     * Delete all *state* rows from the database.
     *
     * @return operation result.
     */
    fun deleteAll() : Boolean

    /**
     * Delete a group of *state* rows by theirs ids.
     *
     * @param ids the rows id to delete.
     * @return operation result.
     */
    fun deleteSomeByIDs(ids : List<Long>) : Boolean

    /**
     * Get a *state* row by its row id.
     *
     * @param id the id of the *state* row.
     * @return the *state* row or null.
     */
    @Throws(SomeRowsReturned::class)
    fun getByID(id : Long) : ContentValues?

    /**
     * Get the *state* rows associates to an *evidence*.
     *
     * @param evidence_id the id of the *evidence*.
     * @return the list of the *states* associates to this *evidence*.
     */
    fun getByEvidence(evidence_id: Long): List<ContentValues>

    /**
     * Get all the *state* rows associates to an *evidence* and a *state*.
     *
     * @param evidence_id the id of the *evidence*.
     * @param state the identifier of the *state*.
     * @return the list of the *states* with this identifier associates to this *evidence*.
     */
    fun getByEvidenceAndState(evidence_id: Long, state: Int): List<ContentValues>

    /**
     * Delete all *states* associates to an *evidence*.
     *
     * @param evidence_id the id of the evidence.
     * @return operation result.
     */
    fun deleteByEvidence(evidence_id: Long) : Boolean

    /**
     * Get the values of the given keys of all *evidence* from database.
     *
     * @param keys the keys of the values
     * @param state the id of the state
     * @return list with *evidence* values rows.
     */
    fun getValues(keys: List<String>, state: Int): List<ContentValues>
}