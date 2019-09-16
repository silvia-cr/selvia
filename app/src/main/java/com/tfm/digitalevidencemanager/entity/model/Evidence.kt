package com.tfm.digitalevidencemanager.entity.model

import android.content.ContentValues
import android.content.Context
import com.tfm.digitalevidencemanager.entity.database_connector.EvidenceDB
import com.tfm.digitalevidencemanager.entity.interfaces.EvidenceContract

/**
 * The representation of a *evidence*.
 *
 * This class represent a *evidence* object stored in database.
 *
 * @property id the database id of the *evidence*.
 * @property identifier the *evidence* identifier.
 * @property idSE the secure element id of the *evidence*.
 * @property name the name of the *evidence*.
 * @property severity the severity of the *evidence*.
 * @property priority the priority of the *evidence*.
 * @property type the file type or extension of the original file.
 * @property metadata the metadata of the original file.
 */
class Evidence(
    var id: Long?,
    val identifier: String,
    val idSE: String,
    val hash: String,
    val name: String,
    var severity: Int?,
    var priority: Int?,
    var type: String,
    val metadata: String
) {

    /**
     * Store or update the *evidence* information into the database.
     * Only severity and priority can be updated.
     *
     * @param context the context used to connect with database.
     */
    fun save(context: Context) {
        val db = EvidenceDB(context)
        var correct = true
        val values = ContentValues()

        if (this.severity != null) {
            values.put(EvidenceContract.EvidenceEntry.COLUMN_NAME_SEVERITY, this.severity)
        }

        if (this.priority != null) {
            values.put(EvidenceContract.EvidenceEntry.COLUMN_NAME_PRIORITY, this.priority)
        }

        if (this.id == null) {
            values.put(EvidenceContract.EvidenceEntry.COLUMN_NAME_ID_SE, this.idSE)
            values.put(EvidenceContract.EvidenceEntry.COLUMN_NAME_HASH, this.hash)
            values.put(EvidenceContract.EvidenceEntry.COLUMN_NAME_IDENTIFIER, this.identifier)
            values.put(EvidenceContract.EvidenceEntry.COLUMN_NAME_TYPE, this.type)
            values.put(EvidenceContract.EvidenceEntry.COLUMN_NAME_NAME, this.name)
            values.put(EvidenceContract.EvidenceEntry.COLUMN_NAME_METADATA, this.metadata)

            this.id = db.insert(values)

            if (this.id!! < 0) {
                correct = false
            }
        } else {
            correct = db.update(this.id!!, values)
        }

        if (!correct) {
            throw ErrorSavingModel("Model ${this.javaClass.simpleName} not saved properly with this data [${values}]")
        }
    }

    /**
     * Get true if the *evidence* can be deleted.
     *
     * @param context the context.
     * @return true if the evidence can be deleted, false otherwise.
     */
    fun getCanDeleteEvidence(context: Context): Boolean {
        val db = EvidenceDB(context)
        var delete = false
        if(this.id != null){
           delete = db.getCanDeleteEvidence(this.id!!)
        }
        return delete
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Evidence

        if (id != other.id) return false
        if (identifier != other.identifier) return false
        if (idSE != other.idSE) return false
        if (hash != other.hash) return false
        if (name != other.name) return false
        if (severity != other.severity) return false
        if (priority != other.priority) return false
        if (type != other.type) return false
        if (metadata != other.metadata) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + identifier.hashCode()
        result = 31 * result + idSE.hashCode()
        result = 31 * result + hash.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (severity ?: 0)
        result = 31 * result + (priority ?: 0)
        result = 31 * result + type.hashCode()
        result = 31 * result + metadata.hashCode()
        return result
    }


    override fun toString(): String {
        return "Evidence(id=$id, idSE='$idSE', hash='$hash', name='$name', severity=$severity, priority=$priority, type='$type', metadata='$metadata')"
    }

    companion object {

        /**
         * Delete *evidence* from database.
         *
         * @param context the context used to connect with database.
         * @param id the id of the *evidences*.
         * @return operation result.
         */
        fun delete(context: Context, id: Long): Boolean {
            val db = EvidenceDB(context)
            return db.deleteByID(id)
        }

        /**
         * Get a *evidence* from database.
         *
         * @param context the context used to connect with database.
         * @param id the id of the *evidence*.
         * @return the *evidence*.
         */
        fun getEvidence(context: Context, id: Long): Evidence? {
            val db = EvidenceDB(context)

            val values = db.getByID(id)
            return storeValues(values)
        }

        private fun storeValues(values: ContentValues?): Evidence? {
            if (values != null) {
                val valuesId = values.getAsLong(EvidenceContract.EvidenceEntry.COLUMN_NAME_ID)
                val valuesIdentifier = values.getAsString(EvidenceContract.EvidenceEntry.COLUMN_NAME_IDENTIFIER)
                val valuesIdSe = values.getAsString(EvidenceContract.EvidenceEntry.COLUMN_NAME_ID_SE)
                val valuesHash = values.getAsString(EvidenceContract.EvidenceEntry.COLUMN_NAME_HASH)
                val valuesType = values.getAsString(EvidenceContract.EvidenceEntry.COLUMN_NAME_TYPE)
                val valuesPriority = values.getAsInteger(EvidenceContract.EvidenceEntry.COLUMN_NAME_PRIORITY)
                val valuesSeverity = values.getAsInteger(EvidenceContract.EvidenceEntry.COLUMN_NAME_SEVERITY)
                val valuesName = values.getAsString(EvidenceContract.EvidenceEntry.COLUMN_NAME_NAME)
                val valuesMetadata = values.getAsString(EvidenceContract.EvidenceEntry.COLUMN_NAME_METADATA)

                return Evidence(
                    valuesId,
                    valuesIdentifier,
                    valuesIdSe,
                    valuesHash,
                    valuesName,
                    valuesSeverity,
                    valuesPriority,
                    valuesType,
                    valuesMetadata
                )
            }
            return null
        }

        /**
         * Delete all *evidences* from database.
         *
         * @param context the context used to connect with database.
         * @return operation result.
         */
        fun deleteAll(context: Context): Boolean {
            val db = EvidenceDB(context)
            return db.deleteAll()
        }

        /**
         * Get all the *evidences* from database.
         *
         * @param context the context used to connect with database.
         * @return the *evidences* list.
         */
        fun getAll(context: Context) : List<Evidence> {
            val db = EvidenceDB(context)
            val valuesList =  db.getAll()

            val evidenceList = ArrayList<Evidence?>()
            for(value in valuesList){
                evidenceList.add(storeValues(value))
            }

            return evidenceList.filterNotNull()
        }

        /**
         * Get all the *evidences* that can be deleted.
         *
         * @param context the context used to connect with database.
         * @return the *evidences* list.
         */
        fun getCanDeleteEvidences(context: Context): List<Evidence> {
            val db = EvidenceDB(context)
            val list = ArrayList<Evidence?>()

            val valuesList = db.getCanDeleteEvidences()

            for (value in valuesList) {
                list.add(Evidence.getEvidence(context, value))
            }

            return list.filterNotNull()
        }

        /**
         * Get all the *evidences* that need be synchronized with the server.
         *
         * @param context the context used to connect with database.
         * @return the *evidences* list.
         */
        fun getNeedSyncEvidences(context: Context): List<Evidence> {
            val db = EvidenceDB(context)
            val list = ArrayList<Evidence?>()

            val valuesList = db.getNeedSyncEvidences()

            for (value in valuesList) {
                list.add(Evidence.getEvidence(context, value))
            }

            return list.filterNotNull()
        }

        /**
         * Get the values list of all *evidences* from the given keys.
         *
         * @param keys
         * @return list with *evidence* values rows.
         */
        fun getValuesList(context: Context, keys: List<String>): List<ContentValues> {
            val db = EvidenceDB(context)
            val list = ArrayList<ContentValues?>()

            val valuesList = db.getValues(keys)

            for (value in valuesList) {
                list.add(value)
            }

            return list.filterNotNull()
        }
    }

}