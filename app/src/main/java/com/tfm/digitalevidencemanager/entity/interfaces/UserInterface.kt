package com.tfm.digitalevidencemanager.entity.interfaces

import android.content.ContentValues
import com.tfm.digitalevidencemanager.entity.model.SomeRowsReturned

/**
 * User connections to database.
 */
interface UserInterface {

    /**
     * Insert a new row in *user* table.
     *
     * @param values the values to store in the new row.
     * @return the id of the row.
     */
    fun insert(values : ContentValues) : Long

    /**
     * Delete a row from *user* table by its id.
     * @param id the id of the row to delete.
     * @return operation result.
     */
    fun deleteByID(id : Long) : Boolean

    /**
     * Delete all *user* rows from the database.
     *
     * @return operation result.
     */
    fun deleteAll() : Boolean

    /**
     * Delete a group of *user* rows by theirs ids.
     *
     * @param ids the rows id to delete.
     * @return operation result.
     */
    fun deleteSomeByIDs(ids : List<Long>) : Boolean

    /**
     * Get a *user* row by its identification.
     *
     * @param id the identification of the *user*.
     * @return the *user* row or null.
     */
    @Throws(SomeRowsReturned::class)
    fun getByIdentification(id : String) : ContentValues?

    /**
     * Get a *user* row by its row id.
     *
     * @param id the id of the *user* row.
     * @return the *user* row or null.
     */
    @Throws(SomeRowsReturned::class)
    fun getByID(id : Long) : ContentValues?

    /**
     * Get all *users*.
     *
     * @return list with *users* rows.
     */
    fun getAll(): List<ContentValues>
}