package com.tfm.digitalevidencemanager.entity.interfaces

import android.provider.BaseColumns

object StateContract {
    /**
     *
     * This table represent the state of a evidence.
     * A evidence can have multiples states but only one 'acquired', one 'deleted_file', one 'deleted_se' and one 'send_to_Server'.
     * A evidence can have multiples 'send' states.
     *
     * To simulate inheritance from StateEvidence table, we have a unique table with all child fields that are not null.
     * To restrict that you can only complete the correct fields by the correct state, we add some constraint to this table.
     *
     * The state 'acquired' is used when we acquired a new evidence.
     * The state 'deleted_file' is used when we delete a cipher evidence from the filesystem
     * The state 'deleted_se' is used when we delete a evidence from the secure_element
     * The state 'send' is used when we send the evidence to another similar device
     * The state 'send_to_server' is used when we send the evidence to server
     * The state 'synchronized' is used when we request to the server if we need to send something
     * The state 'modified' is used to know if a evidence has been edited after send to server
     *
     * If we want to add a new state:
     *  - Add new state to 'states' map
     *  - Add fields to COLUMN_NAME_X constants with new fields name
     *  - Create the newStateFieldList with the fields of the new state
     *  - Add newStateFieldList to 'StatesFieldsList' list
     *
     *  The new constraint of this new state will be created automatically
     */

    const val ACQUIRED = 0
    const val DELETED_FILE = 1
    const val DELETED_SE = 2
    const val SEND = 3
    const val SEND_TO_SERVER = 4
    const val MODIFIED = 5

    val states = mapOf(
        ACQUIRED to "acquired",
        DELETED_FILE to "deleted_file",
        DELETED_SE to "deleted_se",
        SEND to "send",
        SEND_TO_SERVER to "send_to_server",
        MODIFIED to "modified"
    )

    object StateEntry : BaseColumns {
        // Parent state
        const val TABLE_NAME = "state"
        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_EVIDENCE = "id_evidence"
        const val COLUMN_NAME_STATE = "state"
        const val COLUMN_NAME_DATE = "date_state"
        const val COLUMN_NAME_USER = "id_user"
        const val COLUMN_NAME_DEVICE = "id_device"

        // Child states
        const val COLUMN_NAME_ORIGIN_PATH = "origin_path"                       // acquired
        const val COLUMN_NAME_DESTINATION_DEVICE = "id_destination_device"      // send, send_to_server,
        const val COLUMN_NAME_CAN_DELETE = "can_delete"                         // send_to_server
        const val COLUMN_NAME_FIELD = "field"                                   // modified
        const val COLUMN_NAME_OLD_VALUE = "old_value"                           // modified
        const val COLUMN_NAME_NEW_VALUE = "new_value"                           // modified
    }

    val stateBaseFieldList = listOf(
        StateEntry.COLUMN_NAME_ID,
        StateEntry.COLUMN_NAME_EVIDENCE,
        StateEntry.COLUMN_NAME_STATE,
        StateEntry.COLUMN_NAME_DATE,
        StateEntry.COLUMN_NAME_USER,
        StateEntry.COLUMN_NAME_DEVICE
    )
    private val StateAcquiredFieldList = arrayOf(StateEntry.COLUMN_NAME_ORIGIN_PATH)
    private val StateDeletedFileFieldList = arrayOf<String>()
    private val StateDeletedSeFieldList = arrayOf<String>()
    private val StateSendFieldList = arrayOf(StateEntry.COLUMN_NAME_DESTINATION_DEVICE)
    private val StateSendToServerFieldList = arrayOf(
        StateEntry.COLUMN_NAME_DESTINATION_DEVICE,
        StateEntry.COLUMN_NAME_CAN_DELETE
    )
    private val StateModifiedFieldList = arrayOf(
        StateEntry.COLUMN_NAME_FIELD,
        StateEntry.COLUMN_NAME_OLD_VALUE,
        StateEntry.COLUMN_NAME_NEW_VALUE
    )

    val StatesFieldsList = listOf(
        StateAcquiredFieldList,
        StateDeletedFileFieldList,
        StateDeletedSeFieldList,
        StateSendFieldList,
        StateSendToServerFieldList,
        StateModifiedFieldList
    )
}