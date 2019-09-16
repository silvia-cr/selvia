package com.tfm.digitalevidencemanager.entity.interfaces

import android.content.ContentValues
import com.tfm.digitalevidencemanager.entity.model.SomeRowsReturned

/**
 * Device connections to database.
 */
interface DeviceInterface {

    /**
     * Insert a new row in *device* table.
     *
     * @param values the values to store in the new row.
     * @return the id of the row.
     */
    fun insert(values : ContentValues) : Long

    /**
     * Delete a row from *device* table by its id.
     * @param id the id of the row to delete.
     * @return operation result.
     */
    fun deleteByID(id : Long) : Boolean

    /**
     * Delete all *device* rows from the database.
     *
     * @return operation result.
     */
    fun deleteAll() : Boolean

    /**
     * Delete a group of *device* rows by theirs ids.
     *
     * @param ids the rows id to delete.
     * @return operation result.
     */
    fun deleteSomeByIDs(ids : List<Long>) : Boolean

    /**
     * Get a *device* row by its uuid.
     *
     * @param uuid the uuid of the *device*.
     * @return the *device* row or null.
     */
    @Throws(SomeRowsReturned::class)
    fun getByUUID(uuid : String) : ContentValues?

    /**
     * Get a *device* row by its row id.
     *
     * @param id the id of the *device* row.
     * @return the *device* row or null.
     */
    @Throws(SomeRowsReturned::class)
    fun getByID(id : Long) : ContentValues?

    /**
     * Get a server *device* row. If we have some *devices* returns the first
     * TODO: select the server
     *
     * @return the *device* row or null.
     */
    fun getServer() : ContentValues?

    /**
     * Get all *devices*.
     *
     * @return list with *devices* rows.
     */
    fun getAll(): List<ContentValues>
}