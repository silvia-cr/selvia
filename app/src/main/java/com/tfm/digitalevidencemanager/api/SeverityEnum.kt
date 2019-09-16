package com.tfm.digitalevidencemanager.api

import com.tfm.digitalevidencemanager.entity.interfaces.EvidenceContract

enum class SeverityEnum (val id: Int, val value: String) {
    CRITICAL(EvidenceContract.SEVERITY_CRITICAL, "Critical"),
    MAJOR(EvidenceContract.SEVERITY_MAJOR, "Major"),
    MEDIUM(EvidenceContract.SEVERITY_MEDIUM, "Medium"),
    MINOR(EvidenceContract.SEVERITY_MINOR, "Minor");

    override fun toString(): String {
        return value
    }

    companion object {
        fun getValueByID(id : Int) : String {
            val severityEnum = SeverityEnum.values().filter { it.id == id }
            return severityEnum[0].value
        }
    }

}