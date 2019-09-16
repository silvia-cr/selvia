package com.tfm.digitalevidencemanager.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.Settings.Secure
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.tfm.digitalevidencemanager.Local
import com.tfm.digitalevidencemanager.R
import com.tfm.digitalevidencemanager.api.DigitalEvidenceManager
import com.tfm.digitalevidencemanager.api.PriorityEnum
import com.tfm.digitalevidencemanager.entity.database_connector.DBHelper
import com.tfm.digitalevidencemanager.secure_element.SecureElementConnector
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.layout_alert_add_server.view.*
import kotlinx.android.synthetic.main.layout_alert_set_evidence_info.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import java.io.File


class MainMenuActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName

    private lateinit var evidenceName: String
    private lateinit var filePath: String
    private lateinit var alertView: View
    private var evidencePriority: PriorityEnum? = null

    fun secureElement(){
        val se = SecureElementConnector(this)
        val result = se.write(ContentValues(), "pepe")
        Log.d(Local.TAG, result.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
    }

    fun onClick(view: View) {
        when (view.id) {

            button_add_server.id -> {
                addNewServerAlert()
            }

            button_acquire.id -> {
                if (askForPermissions()) {
//                    secureElement()
                    getFile()
                } else {
                    toast(getString(R.string.toast_check_permissions))
                }
            }

            button_list_evidences.id -> {
                val listIntent = Intent(this, EvidencesListActivity::class.java)
                startActivity(listIntent)
            }

            button_send_to_server.id -> {
                sendToServer()
            }

            button_sync.id -> {
                synchronizeWithServer()
            }

            button_delete_history.id -> {
                deleteHistoryAlert()
            }

            button_clean_history.id -> {
                cleanHistoryAlert()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constants.REQUEST_CODE_WRITE_PERMISSION -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    toast(getString(R.string.toast_you_will_continue))
                } else {
                    toast(getString(R.string.toast_set_permission))
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == Constants.REQUEST_CODE_READ_FILE && resultCode == Activity.RESULT_OK) {

            val uri: Uri?
            if (resultData != null) {
                uri = resultData.data
                this.filePath = Utils.getPathFromUri(this, uri)

                showNameDialog()
            }
        }
    }

    private fun showNameDialog() {
        val onClickDialog = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    this.evidenceName = alertView.text_evidence_name.text.toString()
                    val priorityRadioButton = alertView.alert_priority_radio_group.checkedRadioButtonId
                    this.evidencePriority = getPriorityByRadioButtonId(priorityRadioButton)
                    acquireEvidence()
                }
            }
        }
        alertView = layoutInflater.inflate(R.layout.layout_alert_set_evidence_info, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(alertView)
        builder.setPositiveButton(getString(R.string.alert_positive_button), onClickDialog)
        builder.setNegativeButton(getString(R.string.alert_negative_button), onClickDialog)

        val dialog = builder.create()
        dialog.show()
    }

    private fun getPriorityByRadioButtonId(radioButtonId: Int): PriorityEnum? {
        var priority:PriorityEnum? = null
        when(radioButtonId){
            R.id.alert_priority_high -> priority = PriorityEnum.HIGH
            R.id.alert_priority_medium -> priority = PriorityEnum.MEDIUM
            R.id.alert_priority_low -> priority = PriorityEnum.LOW
        }

        return priority
    }

    private fun askForPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.REQUEST_CODE_WRITE_PERMISSION
            )
            return false
        } else {
            return true
        }
    }

    private fun getFile() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("*/*")
        startActivityForResult(intent, Constants.REQUEST_CODE_READ_FILE)
    }

    private fun acquireEvidence() {
        var ok : Boolean
        val context = this

        val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_wait_acquire))
        progressDialog.setCancelable(false)
        progressDialog.show()

        try {
            doAsync {
                val file = File(context.filePath)
                val dem = DigitalEvidenceManager(context)
                val user = dem.getOrCreateUser()

                val uuidDevice = Secure.getString(getContentResolver(), Secure.ANDROID_ID)
                val device = dem.getDevice(uuidDevice)
                ok = dem.acquire(context.evidenceName, context.evidencePriority, null, file, user, device)
                runOnUiThread {
                    progressDialog.dismiss()
                    if (ok) {
                        alert {
                            title = getString(R.string.alert_evidence_ok)
                            positiveButton(getString(R.string.alert_btn_accept)) {}
                        }.show()
                    } else {
                        alert {
                            title = getString(R.string.alert_evidence_fail)
                            positiveButton(getString(R.string.alert_btn_accept)) {}
                        }.show()
                    }
                }
            }
        } catch (e: Exception) {
            progressDialog.dismiss()
            Log.e(Local.TAG, e.message, e)

            alert {
                title = getString(R.string.alert_evidence_fail)
                positiveButton(getString(R.string.alert_btn_accept)) {}
            }.show()
        }
    }

    private fun sendToServer() {
        val context = this

        val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_wait_send_to_server_list))
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
                    val list = dem.getNeedSyncEvidencesList()

                    ok = dem.sendToServer(list, user, device, server)
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
                        positiveButton(getString(R.string.alert_btn_accept)) {}
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

    private fun addNewServerAlert(){
        val onClickDialog = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val serverIp = alertView.text_add_server_ip.text.toString()
                    addNewServer(serverIp)
                }
            }
        }
        alertView = layoutInflater.inflate(R.layout.layout_alert_add_server, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(alertView)
        builder.setPositiveButton(getString(R.string.alert_positive_button), onClickDialog)
        builder.setNegativeButton(getString(R.string.alert_negative_button), onClickDialog)

        val dialog = builder.create()
        dialog.show()
    }

    private fun addNewServer(serverIp: String) {
        val context = this

        val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_wait_add_server))
        progressDialog.setCancelable(false)
        progressDialog.show()

        try {
            doAsync {

                val dem = DigitalEvidenceManager(context)
                val ok = dem.addNewServer(context, serverIp)

                var message = getString(R.string.alert_add_server_ok)
                if(!ok){
                    message = getString(R.string.alert_add_server_fail)
                }

                runOnUiThread {
                    progressDialog.dismiss()

                    alert {
                        title = message
                        positiveButton(getString(R.string.alert_btn_accept)) {}
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

    private fun deleteHistoryAlert() {
        alert {
            title = getString(R.string.alert_delete_history)
            positiveButton(getString(R.string.alert_btn_accept)) {
                deleteHistory()
            }
            negativeButton(getString(R.string.alert_negative_button)) {}
        }.show()
    }


    private fun synchronizeWithServer() {
        val context = this

        val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_wait_sync_server))
        progressDialog.setCancelable(false)
        progressDialog.show()

        try {
            doAsync {

                val dem = DigitalEvidenceManager(context)
                val list = dem.getNeedSyncEvidencesList()

                var message = getString(R.string.alert_sync_server_ok_empty)
                if(!list.isEmpty()){
                    message = getString(R.string.alert_sync_server_ok_elements)
                }

                runOnUiThread {
                    progressDialog.dismiss()

                    alert {
                        title = message
                        positiveButton(getString(R.string.alert_btn_accept)) {}
                    }.show()
                }
            }
        } catch (e: Exception) {
            progressDialog.dismiss()
            Log.e(Local.TAG, e.message, e)

            alert {
                title = getString(R.string.alert_sync_server_fail)
                positiveButton(getString(R.string.alert_btn_accept)) {}
            }.show()
        }
    }

    private fun deleteHistory() {
        val context = this

        val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_wait_delete_history))
        progressDialog.setCancelable(false)
        progressDialog.show()

        try {
            doAsync {

                val dem = DigitalEvidenceManager(context)
                val user = dem.getOrCreateUser()

                val uuidDevice = Secure.getString(getContentResolver(), Secure.ANDROID_ID)
                val device = dem.getDevice(uuidDevice)

                val list = dem.getCanDeleteEvidencesList()
                val path = context.filesDir.toString()

                val ok = dem.deleteHistory(list, user, device, path)

                var message = getString(R.string.alert_delete_history_ok)
                if(!ok){
                    message = getString(R.string.alert_delete_history_fail)
                }

                runOnUiThread {
                    progressDialog.dismiss()

                    alert {
                        title = message
                        positiveButton(getString(R.string.alert_btn_accept)) {}
                    }.show()
                }
            }
        } catch (e: Exception) {
            progressDialog.dismiss()
            Log.e(Local.TAG, e.message, e)

            alert {
                title = getString(R.string.alert_delete_history_fail)
                positiveButton(getString(R.string.alert_btn_accept)) {}
            }.show()
        }
    }

    private fun cleanHistoryAlert() {
        alert {
            title = getString(R.string.alert_clean_history)
            positiveButton(getString(R.string.alert_btn_accept)) {
                showBiometricalPrompt()
            }
            negativeButton(getString(R.string.alert_negative_button)) {}
        }.show()
    }

    private fun showBiometricalPrompt(){
        val biometricPromptBuilder = BiometricPrompt.Builder(this)
        biometricPromptBuilder.setTitle(getString(R.string.biometric_prompt_history_title))
        biometricPromptBuilder.setSubtitle(getString(R.string.biometric_prompt_history_subtitle))
        biometricPromptBuilder.setDescription(getString(R.string.biometric_prompt_history_description))
        biometricPromptBuilder.setNegativeButton(
            getString(R.string.biometric_prompt_negative_button),
            this.mainExecutor,
            DialogInterface.OnClickListener({ _, _->})
        )
        val biometricPrompt = biometricPromptBuilder.build()
        val authenticationCallback = this.getAuthenticationCallback()

        biometricPrompt.authenticate(CancellationSignal(), this.mainExecutor, authenticationCallback)
    }

    private fun getAuthenticationCallback(): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                cleanHistory()
            }
        }
    }

    private fun cleanHistory(){
        val context = this

        val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_wait_delete_history))
        progressDialog.setCancelable(false)
        progressDialog.show()

        try {
            doAsync {
                val dem = DigitalEvidenceManager(context)
                val user = dem.getOrCreateUser()

                val uuidDevice = Secure.getString(getContentResolver(), Secure.ANDROID_ID)
                val device = dem.getDevice(uuidDevice)
                val server = dem.getServer()

                var success = false

                if (server != null){
                    var list = dem.getNeedSyncEvidencesList()
                    success = dem.sendToServer(list, user, device, server)

                    if (success){
                        val databasePath = context.dataDir.toString().plus("/databases/${DBHelper.DATABASE_NAME}")
                        val dbFile = File(databasePath)

                        if(dbFile.exists()){
                            success = dem.acquire("database_file", PriorityEnum.HIGH, null, dbFile, user, device)

                            if (success){
                                // send only the database file to the server
                                list = dem.getNeedSyncEvidencesList()
                                success = dem.sendToServer(list, user, device, server)

                                if(success){
                                    // delete all files
                                    val filesPath = context.filesDir.toString()
                                    success = Utils.deleteFilesFromFolder(filesPath)

                                    if(!success){
                                        Log.i(TAG, "[Clean history] Error deleting files")
                                    }

                                    success = deleteDatabase(DBHelper.DATABASE_NAME)
                                }else{
                                    Log.i(TAG, "[Clean history] Error sending database file to server")
                                }
                            }else{
                                Log.i(TAG, "[Clean history] Error acquiring database file")
                            }
                        }else{
                            success = false
                            Log.i(TAG, "[Clean history] Database file not found")
                        }
                    }else{
                        Log.i(TAG, "[Clean history] Error sending evidences to server")
                    }
                }else{
                    Log.i(TAG, "[Clean history] Server not found")
                }

                var message = getString(R.string.alert_clean_history_ok)
                if(!success){
                    message = getString(R.string.alert_clean_history_fail)
                }

                runOnUiThread {
                    progressDialog.dismiss()

                    alert {
                        title = message
                        positiveButton(getString(R.string.alert_btn_accept)) {}
                    }.show()
                }
            }
        } catch (e: Exception) {
            progressDialog.dismiss()
            Log.e(Local.TAG, e.message, e)

            alert {
                title = getString(R.string.alert_clean_history_fail)
                positiveButton(getString(R.string.alert_btn_accept)) {}
            }.show()
        }
    }
}
