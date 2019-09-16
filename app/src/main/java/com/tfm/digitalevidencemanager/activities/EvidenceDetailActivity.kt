package com.tfm.digitalevidencemanager.activities

import android.content.ContentValues
import android.os.Bundle
import android.provider.Settings.Secure
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.tfm.digitalevidencemanager.Local
import com.tfm.digitalevidencemanager.R
import com.tfm.digitalevidencemanager.api.DigitalEvidenceManager
import com.tfm.digitalevidencemanager.api.PriorityEnum
import com.tfm.digitalevidencemanager.api.SeverityEnum
import com.tfm.digitalevidencemanager.api.StateEnum
import com.tfm.digitalevidencemanager.entity.model.Evidence
import kotlinx.android.synthetic.main.activity_evidence_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.yesButton

class EvidenceDetailActivity : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapterStateList.ViewHolder>? = null
    private var statesList: List<ContentValues> = listOf()
    private val dem = DigitalEvidenceManager(this)
    private var evidenceId : Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evidence_detail)

        layoutManager = LinearLayoutManager(this)
        recycler_view_states.layoutManager = layoutManager

        button_remove_evidence.isEnabled = false
        button_remove_evidence.isClickable = false

        val intent = getIntent()
        evidenceId = intent.getLongExtra(Constants.EVIDENCE_ID, -1)

        if (evidenceId < 0) {
            alert(getString(R.string.alert_something_wrong)) {
                yesButton { onBackPressed() }
            }
        } else {
            val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_list_wait))
            progressDialog.setCancelable(false)
            progressDialog.show()

            val context = this

            doAsync {
                val evidence = dem.getEvidence(evidenceId)
                runOnUiThread {
                    progressDialog.dismiss()

                    if (evidence == null) {
                        alert(getString(R.string.alert_something_wrong)) {
                            yesButton { onBackPressed() }
                        }
                    } else {
                        text_view_id.text = evidence.id.toString()
                        var name = evidence.name
                        if (name.isEmpty()) {
                            name = "-"
                        }
                        text_view_name.text = name

                        text_view_type.text = evidence.type

                        val priorityId = evidence.priority
                        val priority: String
                        when (priorityId) {
                            null -> priority = "-"
                            else -> priority = PriorityEnum.getValueByID(priorityId)
                        }
                        text_view_priority.text = priority

                        val severityId = evidence.severity
                        val severity: String
                        when (severityId) {
                            null -> severity = "-"
                            else -> severity = SeverityEnum.getValueByID(severityId)
                        }
                        text_view_severity.text = severity

                        text_view_metadata.text = evidence.metadata
                    }
                }
            }

            doAsync {
                val evidence = dem.getEvidence(evidenceId)

                if (evidence != null) {
                    statesList = getStatesList(evidence)
                    val canDelete = dem.getCanDeleteEvidence(evidence)
                    val fileExists = dem.evidenceFileExists(Utils.getFilepathFromEvidence(context, evidence))
                    runOnUiThread {
                        text_view_states_loading.text = ""
                        adapter = RecyclerAdapterStateList(context, statesList)
                        recycler_view_states.adapter = adapter

                        if (!canDelete && fileExists){
                            button_remove_evidence.isEnabled = true
                            button_remove_evidence.isClickable = true
                        }
                    }
                }
            }
        }
    }

    private fun getStatesList(evidence: Evidence) : List<ContentValues>{
        val states = dem.getStateByEvidence(evidence)

        val valuesList = ArrayList<ContentValues>()
        var values : ContentValues

        states.forEach {
            values = ContentValues()

            values.put(getString(R.string.text_view_state_name), StateEnum.getValueByID(it.idState))
            values.put(getString(R.string.text_view_state_date), it.dateState.toString())
            values.put(getString(R.string.text_view_state_user), it.user.dni)
            values.put(getString(R.string.text_view_state_device), it.device.uuid)

            if (it.originPath != null && it.originPath.isNotEmpty()){
                values.put(getString(R.string.text_view_state_origin_path), it.originPath)
            }

            if (it.destinationDevice != null){
                values.put(getString(R.string.text_view_state_destination_device), it.destinationDevice.uuid)
            }

            if (it.canDelete != null){
                values.put(getString(R.string.text_view_state_can_delete), it.canDelete)
            }

            if (it.field != null){
                values.put(getString(R.string.text_view_state_field), it.field)
                values.put(getString(R.string.text_view_state_old_value), it.oldValue)
                values.put(getString(R.string.text_view_state_new_value), it.newValue)
            }

            valuesList.add(values)
        }

        return valuesList
    }

    fun onClick(view: View) {
        when (view.id) {

            button_send_evidence.id -> {
                sendToServer()
            }

            button_remove_evidence.id -> {
                deleteEvidenceButton()
            }
        }
    }

    private fun sendToServer() {
        val context = this

        val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_wait_send_to_server))
        progressDialog.setCancelable(false)
        progressDialog.show()

        try {
            doAsync {

                val dem = DigitalEvidenceManager(context)
                val user = dem.getOrCreateUser()

                val uuidDevice = Secure.getString(getContentResolver(), Secure.ANDROID_ID)
                val device = dem.getDevice(uuidDevice)
                val server = dem.getServer()

                var ok = false

                if (server != null){
                    val evidence = dem.getEvidence(evidenceId)

                    if(evidence != null){
                        val list = listOf(evidence)
                        ok = dem.sendToServer(list, user, device, server)
                    }
                }

                var message = getString(R.string.alert_send_to_server_list_ok)
                if(!ok && server != null){
                    message = getString(R.string.alert_send_to_server_list_fail)
                }else if(!ok){
                    message = getString(R.string.alert_send_to_server_not_server)
                }

                runOnUiThread {
                    progressDialog.dismiss()

                    alert {
                        title = message
                        positiveButton(getString(R.string.alert_btn_accept)) {
                            onBackPressed()
                        }
                    }.show()
                }
            }
        } catch (e: Exception) {
            progressDialog.dismiss()
            Log.e(Local.TAG, e.message, e)

            alert {
                title = getString(R.string.alert_send_to_server_list_fail)
                positiveButton(getString(R.string.alert_btn_accept)) {}
            }.show()
        }
    }

    private fun deleteEvidenceButton(){
        val evidenceId = this.evidenceId
        val context = this
        alert {
            title = getString(R.string.alert_sure_delete_evidence)

            positiveButton(getString(R.string.alert_btn_accept)) {
                val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_delete_evidence))
                progressDialog.setCancelable(false)
                progressDialog.show()

                try{
                    doAsync {
                        val user = dem.getOrCreateUser()
                        val uuidDevice = Secure.getString(getContentResolver(), Secure.ANDROID_ID)
                        val device = dem.getDevice(uuidDevice)
                        val evidence = dem.getEvidence(evidenceId)

                        var deleted = false
                        if(evidence != null){
                            val path = Utils.getFilepathFromEvidence(context, evidence)
                            deleted = dem.deleteEvidenceFile(evidence, user, device, path)
                        }

                        runOnUiThread {
                            progressDialog.dismiss()

                            if (deleted) {
                                alert(getString(R.string.alert_evidence_deleted)) {
                                    yesButton { onBackPressed() }
                                }.show()
                            } else {
                                alert(getString(R.string.alert_something_wrong)) {
                                    yesButton { onBackPressed() }
                                }.show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(Local.TAG, e.message, e)
                    alert(getString(R.string.alert_something_wrong)) {
                        yesButton { onBackPressed() }
                    }.show()
                }
            }

            negativeButton(R.string.alert_negative_button) {}
        }.show()
    }
}
