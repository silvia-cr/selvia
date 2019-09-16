package com.tfm.digitalevidencemanager.api

import com.tfm.digitalevidencemanager.entity.interfaces.StateContract

enum class StateEnum (val id: Int, val value: String){
    ACQUIRED(StateContract.ACQUIRED, "Acquired"),
    DELETED_FILE(StateContract.DELETED_FILE, "Deleted file"),
    DELETED_SE(StateContract.DELETED_SE, "Deleted from SE"),
    SEND(StateContract.SEND, "Send"),
    SEND_TO_SERVER(StateContract.SEND_TO_SERVER, "Send to server"),
    MODIFIED(StateContract.MODIFIED, "Modified");

    override fun toString(): String {
        return value
    }

    companion object {
        fun getValueByID(id : Int) : String {
            val StateEnum = StateEnum.values().filter { it.id == id }
            return StateEnum[0].value
        }
    }
}