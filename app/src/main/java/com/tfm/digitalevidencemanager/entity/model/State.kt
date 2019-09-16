package com.tfm.digitalevidencemanager.entity.model

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.tfm.digitalevidencemanager.entity.database_connector.StateDB
import com.tfm.digitalevidencemanager.entity.interfaces.StateContract
import java.util.*

/**
 * The representation of a *state* of an *evidence*.
 *
 * This class represent a *state* stored in database. Each *evidence* can have multiples *states*.
 *
 * @property id the database id of the *device*.
 * @property evidence the *evidence* associate to the *state*.
 * @property idState the identifier of the *state*.
 * @property dateState the creation time of the *state* in UTC time.
 * @property user the *user* who creates the *state*.
 * @property device the *device* which creates the *state*.
 * @property originPath the path of the original file of the evidence.
 * @property destinationDevice the destination *device* that the *evidence* will be transmitted.
 * @property canDelete the response of the server to if the evidence can be deleted yet.
 * @property field the field to be modified.
 * @property oldValue the old value of the field to be modified.
 * @property newValue the new value of the field to be modified.
 */
class State(
    var id: Long?,
    val evidence: Evidence,
    val idState: Int,
    val dateState: Date,
    val user: User,
    val device: Device,
    val originPath: String?,
    val destinationDevice: Device?,
    val canDelete: Boolean?,
    val field: String?,
    val oldValue: String?,
    val newValue: String?
) {

    /**
     * The *state* ACQUIRED constructor.
     * This *state* is created when a new *evidence* is acquired.
     * @param id the database id of the *device*.
     * @param evidence the *evidence* associate to the *state*.
     * @param idState the identifier of the *state*.
     * @param dateState the creation time of the *state* in UTC time.
     * @param user the *user* who creates the *state*.
     * @param device the *device* which creates the *state*.
     * @param originPath the path of the original file of the evidence.
     */
    constructor(
        id: Long?,
        evidence: Evidence,
        idState: Int,
        dateState: Date,
        user: User,
        device: Device,
        originPath: String
    ) : this(
        id,
        evidence,
        idState,
        dateState,
        user,
        device,
        originPath,
        null,
        null,
        null,
        null,
        null
    ) {
        if (idState != StateContract.ACQUIRED) {
            throw InvalidState("The state must be ${StateContract.ACQUIRED} but it was ${idState}")
        }
    }

    /**
     * The *state* DELETED_FILE and DELETED_SE constructors.
     * This *state* is created when a *evidence* is deleted from the filesystem or secure element.
     * @param id the database id of the *device*.
     * @param evidence the *evidence* associate to the *state*.
     * @param idState the identifier of the *state*.
     * @param dateState the creation time of the *state* in UTC time.
     * @param user the *user* who creates the *state*.
     * @param device the *device* which creates the *state*.
     */
    constructor(
        id: Long?,
        evidence: Evidence,
        idState: Int,
        dateState: Date,
        user: User, device: Device
    ) : this(
        id,
        evidence,
        idState,
        dateState,
        user,
        device,
        null,
        null,
        null,
        null,
        null,
        null
    ) {
        if (idState != StateContract.DELETED_SE && idState != StateContract.DELETED_FILE) {
            throw InvalidState("The state must be ${StateContract.DELETED_SE} or ${StateContract.DELETED_FILE} but it was ${idState}")
        }
    }

    /**
     * The *state* SEND constructor.
     * This *state* is created when a *evidence* is sending to another similar *device* (not server).
     * @param id the database id of the *device*.
     * @param evidence the *evidence* associate to the *state*.
     * @param idState the identifier of the *state*.
     * @param dateState the creation time of the *state* in UTC time.
     * @param user the *user* who creates the *state*.
     * @param device the *device* which creates the *state*.
     * @param destinationDevice the destination *device* that the *evidence* will be transmitted.
     */
    constructor(
        id: Long?,
        evidence: Evidence,
        idState: Int,
        dateState: Date,
        user: User,
        device: Device,
        destinationDevice: Device
    ) : this(
        id,
        evidence,
        idState,
        dateState,
        user,
        device,
        null,
        destinationDevice,
        null,
        null,
        null,
        null
    ) {
        if (idState != StateContract.SEND) {
            throw InvalidState("The state must be ${StateContract.SEND} but it was ${idState}")
        }
    }

    /**
     * The *state* SEND_TO_SERVER constructor.
     * This *state* is created when a *evidence* is sending to server.
     * @param id the database id of the *device*.
     * @param evidence the *evidence* associate to the *state*.
     * @param idState the identifier of the *state*.
     * @param dateState the creation time of the *state* in UTC time.
     * @param user the *user* who creates the *state*.
     * @param device the *device* which creates the *state*.
     * @param destinationDevice the destination *device* that the *evidence* will be transmitted.
     * @param canDelete the response of the server to if the evidence can be deleted yet.
     */
    constructor(
        id: Long?,
        evidence: Evidence,
        idState: Int,
        dateState: Date,
        user: User,
        device: Device,
        destinationDevice: Device,
        canDelete: Boolean
    ) : this(
        id,
        evidence,
        idState,
        dateState,
        user,
        device,
        null,
        destinationDevice,
        canDelete,
        null,
        null,
        null
    ) {
        if (idState != StateContract.SEND_TO_SERVER) {
            throw InvalidState("The state must be ${StateContract.SEND_TO_SERVER} but it was ${idState}")
        }
    }

    /**
     * The *state* MODIFIED constructor.
     * This *state* is created when a *evidence* is modified. The only fields to modify are severity and priority.
     * @param id the database id of the *device*.
     * @param evidence the *evidence* associate to the *state*.
     * @param idState the identifier of the *state*.
     * @param dateState the creation time of the *state* in UTC time.
     * @param user the *user* who creates the *state*.
     * @param device the *device* which creates the *state*.
     * @param field the field to be modified.
     * @param oldValue the old value of the field to be modified.
     * @param newValue the new value of the field to be modified.
     */
    constructor(
        id: Long?,
        evidence: Evidence,
        idState: Int,
        dateState: Date,
        user: User,
        device: Device,
        field: String,
        oldValue: String,
        newValue: String
    ) : this(
        id,
        evidence,
        idState,
        dateState,
        user,
        device,
        null,
        null,
        null,
        field,
        oldValue,
        newValue
    ) {
        if (idState != StateContract.MODIFIED) {
            throw InvalidState("The state must be ${StateContract.MODIFIED} but it was ${idState}")
        }
    }

    /**
     * Store the *satte* information into the database.
     *
     * @param context the context used to connect with database.
     */
    fun save(context: Context) {
        val db = StateDB(context)

        if (evidence.id == null) {
            evidence.save(context)
        }

        if (user.id == null) {
            user.save(context)
        }

        if (device.id == null) {
            device.save(context)
        }

        if (destinationDevice != null && destinationDevice.id == null) {
            destinationDevice.save(context)
        }

        val values = ContentValues()
        values.put(StateContract.StateEntry.COLUMN_NAME_EVIDENCE, this.evidence.id)
        values.put(StateContract.StateEntry.COLUMN_NAME_STATE, this.idState)
        values.put(StateContract.StateEntry.COLUMN_NAME_DATE, this.dateState.time)
        values.put(StateContract.StateEntry.COLUMN_NAME_USER, this.user.id)
        values.put(StateContract.StateEntry.COLUMN_NAME_DEVICE, this.device.id)
        values.put(StateContract.StateEntry.COLUMN_NAME_ORIGIN_PATH, this.originPath)
        values.put(StateContract.StateEntry.COLUMN_NAME_DESTINATION_DEVICE, this.destinationDevice?.id)
        values.put(StateContract.StateEntry.COLUMN_NAME_CAN_DELETE, this.canDelete)
        values.put(StateContract.StateEntry.COLUMN_NAME_FIELD, this.field)
        values.put(StateContract.StateEntry.COLUMN_NAME_OLD_VALUE, this.oldValue)
        values.put(StateContract.StateEntry.COLUMN_NAME_NEW_VALUE, this.newValue)

        this.id = db.insert(values)

        if (this.id!! < 0) {
            throw ErrorSavingModel("Model ${this.javaClass.simpleName} not saved properly with this data [${values}]")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as State

        if (evidence != other.evidence) return false
        if (idState != other.idState) return false
        if (dateState != other.dateState) return false
        if (user != other.user) return false
        if (device != other.device) return false
        if (originPath != other.originPath) return false
        if (destinationDevice != other.destinationDevice) return false
        if (canDelete != other.canDelete) return false
        if (field != other.field) return false
        if (oldValue != other.oldValue) return false
        if (newValue != other.newValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = evidence.hashCode()
        result = 31 * result + idState
        result = 31 * result + dateState.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + device.hashCode()
        result = 31 * result + (originPath?.hashCode() ?: 0)
        result = 31 * result + (destinationDevice?.hashCode() ?: 0)
        result = 31 * result + (canDelete?.hashCode() ?: 0)
        result = 31 * result + (field?.hashCode() ?: 0)
        result = 31 * result + (oldValue?.hashCode() ?: 0)
        result = 31 * result + (newValue?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "State(id=$id, evidence=$evidence, idState=$idState, dateState=$dateState, user=$user, device=$device, originPath=$originPath, destinationDevice=$destinationDevice, canDelete=$canDelete, field=$field, oldValue=$oldValue, newValue=$newValue)"
    }


    companion object {

        /**
         * Delete some *states* from database.
         *
         * @param context the context used to connect with database.
         * @param ids the ids of the *states*.
         * @return operation result.
         */
        fun deleteSome(context: Context, ids: List<Long>): Boolean {
            val db = StateDB(context)
            return db.deleteSomeByIDs(ids)
        }

        /**
         * Get a *state* from database.
         *
         * @param context the context used to connect with database.
         * @param id the id of the *state*.
         * @return the *state*.
         */
        fun getState(context: Context, id: Long): State? {
            val db = StateDB(context)

            val values = db.getByID(id)
            return this.storeValues(context, values)
        }

        /**
         * Get all the *states* from a given *evidence*.
         *
         * @param context the context used to connect with database.
         * @param evidence the *evidence* to get the *states*.
         * @return the *states* list.
         */
        fun getByEvidence(context: Context, evidence: Evidence): List<State> {
            val db = StateDB(context)
            val list = ArrayList<State?>()
            if (evidence.id != null) {
                val valuesList = db.getByEvidence(evidence.id!!)

                for (value in valuesList) {
                    list.add(this.storeValues(context, value, evidence))
                }
            }
            return list.filterNotNull()
        }

        private fun storeValues(context: Context, values: ContentValues?, originEvidence : Evidence? = null): State? {
            if (values != null) {
                val id = values.getAsLong(StateContract.StateEntry.COLUMN_NAME_ID)

                var evidence = originEvidence
                if(evidence == null){
                    evidence = Evidence.getEvidence(context, values.getAsLong(StateContract.StateEntry.COLUMN_NAME_EVIDENCE))!!
                }

                val idState = values.getAsInteger(StateContract.StateEntry.COLUMN_NAME_STATE)
                val dateState = Date(values.getAsLong(StateContract.StateEntry.COLUMN_NAME_DATE))
                val user = User.getUser(context, values.getAsLong(StateContract.StateEntry.COLUMN_NAME_USER))!!
                val device = Device.getDevice(context, values.getAsLong(StateContract.StateEntry.COLUMN_NAME_DEVICE))!!
                val originPath = values.getAsString(StateContract.StateEntry.COLUMN_NAME_ORIGIN_PATH)

                val destinationDeviceId = values.getAsLong(StateContract.StateEntry.COLUMN_NAME_DESTINATION_DEVICE)
                var destinationDevice : Device? = null
                if(destinationDeviceId != null){
                    destinationDevice = Device.getDevice(context, destinationDeviceId)
                }

                val canDelete = values.getAsBoolean(StateContract.StateEntry.COLUMN_NAME_CAN_DELETE)
                val field = values.getAsString(StateContract.StateEntry.COLUMN_NAME_FIELD)
                val oldValue = values.getAsString(StateContract.StateEntry.COLUMN_NAME_OLD_VALUE)
                val newValue = values.getAsString(StateContract.StateEntry.COLUMN_NAME_NEW_VALUE)

                return State(
                    id,
                    evidence,
                    idState,
                    dateState,
                    user,
                    device,
                    originPath,
                    destinationDevice,
                    canDelete,
                    field,
                    oldValue,
                    newValue
                )
            }
            return null
        }

        /**
         * Delete all the *states* of an *evidence*.
         *
         * @param context the context used to connect with database.
         * @param evidence the *evidence* to delete the *states*.
         * @return the *state*.
         */
        fun deleteByEvidence(context: Context, evidence: Evidence): Boolean {
            val db = StateDB(context)
            var success = false

            if (evidence.id != null) {
                success = db.deleteByEvidence(evidence.id!!)
            }

            return success
        }

        /**
         * Delete all *states* from database.
         *
         * @param context the context used to connect with database.
         * @return operation result.
         */
        fun deleteAll(context: Context): Boolean {
            val db = StateDB(context)
            return db.deleteAll()
        }

        /**
         * Delete a *state* from database.
         *
         * @param context the context used to connect with database.
         * @param id the id of the *state*.
         * @return operation result.
         */
        fun deleteByID(context: Context, id: Long): Boolean {
            val db = StateDB(context)
            return db.deleteByID(id)
        }

        /**
         * Get all concrete *state* of an *evidence*.
         *
         * @param context the context used to connect with database.
         * @param evidence the evidence
         * @param state the id of the *state*.
         * @return operation result.
         */
        fun getByEvidenceAndState(context: Context, evidence: Evidence, state: Int): List<State> {
            val db = StateDB(context)
            val list = ArrayList<State?>()
            if (evidence.id != null) {
                val valuesList = db.getByEvidenceAndState(evidence.id!!, state)

                for (value in valuesList) {
                    list.add(storeValues(context, value, evidence))
                }
            }
            return list.filterNotNull()
        }

        /**
         * Get the values list of the *states* with id from the given keys.
         *
         * @param keys keys of the values
         * @param state the id of the state
         * @return list with *evidence* values rows.
         */
        fun getValuesList(context: Context, keys: List<String>, state: Int): List<ContentValues> {
            val db = StateDB(context)
            val list = ArrayList<ContentValues?>()

            val valuesList = db.getValues(keys, state)

            for (value in valuesList) {
                list.add(value)
            }

            return list.filterNotNull()
        }

        fun getAll(context: Context) {
            val db = StateDB(context)
            db.getAll()
        }
    }

}