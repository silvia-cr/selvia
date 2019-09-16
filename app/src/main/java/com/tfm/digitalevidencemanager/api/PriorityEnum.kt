package com.tfm.digitalevidencemanager.api

import com.tfm.digitalevidencemanager.entity.interfaces.EvidenceContract

enum class PriorityEnum(val id: Int, val value: String) {
    HIGH(EvidenceContract.PRIORITY_HIGH, "High"),
    MEDIUM(EvidenceContract.PRIORITY_MEDIUM, "Medium"),
    LOW(EvidenceContract.PRIORITY_LOW, "Low");

    override fun toString(): String {
        return value
    }

    companion object {
        fun getValueByID(id : Int) : String {
            val priorityEnum = PriorityEnum.values().filter { it.id == id }
            return priorityEnum[0].value
        }
    }
}