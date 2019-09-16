package com.tfm.digitalevidencemanager.api

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.tfm.digitalevidencemanager.Local
import com.tfm.digitalevidencemanager.entity.interfaces.StateContract
import com.tfm.digitalevidencemanager.entity.model.*
import com.tfm.digitalevidencemanager.secure_element.SecureElement
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.time.Instant
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.collections.ArrayList

/**
 *  A digital evidence manager
 *
 *  This class represents a digital evidence manager.
 *
 *  @property context the context used to connect with database.
 */
class DigitalEvidenceManager(val context: Context) {

    private val TAG = this.javaClass.simpleName

    /**
     *  Acquire a new *evidence*.
     *
     *  @param name the name of the *evidence* used by user to identify it easily.
     *  @param priority the priority of the *evidence*.
     *  @param severity the severity of the *evidence*.
     *  @param originalFile the file we want to acquire as an *evidence*.
     *  @param user the *user* who is going to acquire the *evidence*.
     *  @param device the *device* which is going to acquire the *evidence*.
     *  @return operation result
     */
    fun acquire(
        name: String,
        priority: PriorityEnum?,
        severity: SeverityEnum?,
        originalFile: File,
        user: User,
        device: Device
    ): Boolean {

        if (!originalFile.exists()) {
            throw FileNotExist("File ${originalFile.absolutePath} don't exists. We cannot acquire the file.")
        }

        val fileId = System.currentTimeMillis()
        val identifier = device.uuid.plus('_').plus(fileId)
        val type = Utils.getFileType(originalFile)
        val metadata = Utils.getMetadatafromFile(originalFile)
        var success = true

        val fileHash = Utils.getHashFromFile(originalFile)
        val cipherResult = Utils.encryptFile(this.context, originalFile, identifier)

        if (cipherResult) {
            val se = SecureElement()
            val seId = se.store(this.context, fileHash)

            if (seId != null) {
                try {
                    val evidence = Evidence(
                        null,
                        identifier,
                        seId,
                        fileHash,
                        name,
                        severity?.id,
                        priority?.id,
                        type,
                        metadata
                    )
                    evidence.save(this.context)

                    val acquired = State(
                        null,
                        evidence,
                        StateContract.ACQUIRED,
                        Date.from(Instant.now()),
                        user,
                        device,
                        originalFile.absolutePath
                    )
                    acquired.save(this.context)
                    Log.i(
                        TAG,
                        "[Original file: ${originalFile.absolutePath}] File was correctly saved with identifier ${evidence.identifier}"
                    )

                } catch (e: ErrorSavingModel) {
                    success = false
                    Log.e(TAG, e.message, e)
                }

            } else {
                success = false
                Log.i(
                    TAG,
                    "[Original file: ${originalFile.absolutePath}] Hash [$fileHash] of the file [$fileId] cannot be saved in secure element"
                )
            }
        } else {
            success = false
            Log.i(TAG, "[Original file: ${originalFile.absolutePath}] The file cannot be cipher")
        }

        return success
    }

    /**
     * Get the *device* with the *uuid* provided in params or create one if it doesn't exist.
     *
     * @param uuid the uuid identifier of the *device*.
     * @return the *device* with this uuid.
     *
     */
    fun getDevice(uuid: String): Device {
        var device = Device.getDevice(this.context, uuid)

        if (device == null) {
            device = Device(null, uuid, false, "")
            device.save(this.context)
        }

        return device
    }

    /**
     * Get the server with the *uuid* provided in params or create one if it doesn't exist.
     *
     * @param uuid the uuid identifier of the server.
     * @return the server with this uuid or null.
     *
     */
    fun getServer(): Device? {
        return Device.getServer(this.context)
    }

    /**
     * Get an *user* from database, or create one if the database is empty.
     *
     * .note: This function is temporal. We need to identify the *user* and get the real *user*, not always the same.
     *
     * @param context the context used to connect with database.
     * @return a *user*.
     */
    fun getOrCreateUser(): User {

        val userList = User.getAll(this.context)
        val user: User

        if (userList.isNotEmpty()) {
            user = userList[0]
        } else {
            user = User(null, "123456789", "username", System.currentTimeMillis().toString())
            user.save(this.context)
        }

        return user
    }

    /**
     * Get all the *evidences* stored in database.
     *
     * @return all the *evidences*.
     */
    fun getEvidencesList(): List<Evidence> {
        return Evidence.getAll(this.context)
    }

    /**
     * Get the values from the given keys of all the *evidences* stored in database.
     *
     * @return all the *evidences* values.
     */
    fun getEvidencesValuesList(keys: List<String>): List<ContentValues> {
        return Evidence.getValuesList(this.context, keys)
    }

    /**
     * Get the values from the given keys of all the *states* stored in database.
     *
     * @return all the *evidences* values.
     */
    fun getStateValuesList(keys: List<String>, state: Int): List<ContentValues> {
        return State.getValuesList(this.context, keys, state)
    }

    /**
     * Get the *evidence* with the *id* provided in params.
     *
     * @param id the id of the *evidence*.
     * @return the *evidence* with this id.
     *
     */
    fun getEvidence(id: Long): Evidence? {
        return Evidence.getEvidence(context, id)
    }

    /**
     * Get the values from the given keys of all the *states* stored in database.
     *
     * @return all the *evidences* values.
     */
    fun getStateByEvidence(evidence: Evidence): List<State> {
        return State.getByEvidence(context, evidence)
    }

    /**
     * Get all the possibles fields of a state.
     *
     * @return all the possibles fields of a state.
     */
    fun getStateFields(): List<String> {
        val commonFields = StateContract.stateBaseFieldList
        val nonCommonFieldsByState = StateContract.StatesFieldsList

        val fieldList = ArrayList<String>()
        fieldList.addAll(commonFields)
        nonCommonFieldsByState.forEach {
            fieldList.addAll(it)
        }

        return fieldList.distinct()
    }

    /**
     * Get true if the *evidence* can be deleted.
     *
     * @param id the id of the *evidence*.
     * @return the *evidence* with this id.
     *
     */
    fun getCanDeleteEvidence(evidence: Evidence): Boolean {
        return evidence.getCanDeleteEvidence(context)
    }

    /**
     * Delete a *evidence* from the filesystem.
     *
     * @param evidenceId the id of the *evidence*.
     * @return operation result.
     */
    fun deleteEvidenceFile(evidence: Evidence, user: User, device: Device, evidencePath: String): Boolean {
        var success: Boolean
        try {
            val evidenceFile = File(evidencePath)

            if (!evidenceFile.exists()) {
                throw FileNotExist("File ${evidenceFile.absolutePath} don't exists. We cannot delete the file.")
            }

            success = evidenceFile.delete()

            if (success) {
                val deleted = State(
                    null,
                    evidence,
                    StateContract.DELETED_FILE,
                    Date.from(Instant.now()),
                    user,
                    device
                )
                deleted.save(this.context)
            }

        } catch (e: ErrorSavingModel) {
            success = false
            Log.e(TAG, e.message, e)
        }

        return success
    }

    /**
     * Delete a *evidence* from the filesystem.
     *
     * @param evidenceId the id of the *evidence*.
     * @return operation result.
     */
    fun evidenceFileExists(evidencePath: String): Boolean {
        val file = File(evidencePath)

        return file.exists()
    }

    /**
     * Send a list of *evidences* to the server
     *
     * @param list the list of *evidences*.
     * @return operation result.
     */
    fun sendToServer(list: List<Evidence>, user: User, device: Device, destination: Device): Boolean {
        var success = true

        try {

            val server = Device.getServer(context)

            if(server != null){
                val queue = Volley.newRequestQueue(context)
                val requestFuture = RequestFuture.newFuture<JSONObject>()
                val fullUrl = "https://${server.connection}/evidences/"
                val publicKey = Utils.getPublicKey(server.uuid)
                val gson = Gson()

                list.forEach {
                    val states = State.getByEvidence(context, it)
                    val file = Utils.getFile(it.identifier)

                    val data = gson.toJson(states)
                    val jsonData = JSONObject(data)
                    jsonData.put("file", file)
                    jsonData.put("hash", it.hash)

                    val finalData = Utils.encryptString(jsonData.toString(), publicKey)
                    val finalJSonData = JSONObject(finalData)

                    val request = JsonObjectRequest(
                        Request.Method.POST,
                        fullUrl,
                        finalJSonData,
                        requestFuture,
                        requestFuture
                    )
                    queue.add(request)

                    val response = requestFuture.get()
                    val responseData = response.getJSONObject("data")
                    val canDelete = responseData.getBoolean("can_delete")

                    val sendToServer = State(
                        null,
                        it,
                        StateContract.SEND_TO_SERVER,
                        Date.from(Instant.now()),
                        user,
                        device,
                        destination,
                        canDelete
                    )

                    sendToServer.save(this.context)
                    Log.i(
                        TAG,
                        "[Evidence: ${it.identifier}] Evidence was correctly send to server}"
                    )
                }
            }else{
                success = false
                Log.e(Local.TAG, "There isn't any server in database")
            }

        } catch (e: ErrorSavingModel) {
            success = false
            Log.e(TAG, e.message, e)
        } catch (e: InterruptedException){
            success = false
            Log.e(TAG, e.message, e)
        } catch (e: ExecutionException){
            success = false
            Log.e(TAG, e.message, e)
        } catch (e: java.lang.Exception){
            success = false
            Log.e(TAG, e.message, e)
        }

        return success
    }

    /**
     * Get the list of evidences that need to be sync
     *
     * @return the list of evidences.
     */
    fun getNeedSyncEvidencesList(): List<Evidence> {
        return Evidence.getNeedSyncEvidences(context)
    }

    /**
     * Get the list of evidences that can be deleted
     *
     * @return the list of evidences.
     */
    fun getCanDeleteEvidencesList(): List<Evidence> {
        return Evidence.getCanDeleteEvidences(context)
    }

    /**
     * Add a new server *device*
     *
     * @return true if succcess.
     */
    fun addNewServer(context: Context, url: String): Boolean {
        var success = true
        try{

            val fullUrl = "https://$url/configuration/"

            val requestFuture = RequestFuture.newFuture<JSONObject>()
            val request = JsonObjectRequest(Request.Method.GET, fullUrl, JSONObject(), requestFuture, requestFuture)

            val queue = Volley.newRequestQueue(context)
            queue.add(request)

            val response = requestFuture.get()

            val data = response.getJSONObject("data")
            val uuid = data.getString("uuid")
            val server = Device(null, uuid, true, url)
            server.save(context)

            val publicKey = data.getString("public_key")
            val publicKeyFile = File(uuid.plus("_public_key.pem"))
            publicKeyFile.writeText(publicKey, Charset.defaultCharset())

        } catch (e: ErrorSavingModel) {
            success = false
            Log.e(TAG, e.message, e)
        } catch (e: InterruptedException){
            success = false
            Log.e(TAG, e.message, e)
        } catch (e: ExecutionException){
            success = false
            Log.e(TAG, e.message, e)
        } catch (e: java.lang.Exception){
            success = false
            Log.e(TAG, e.message, e)
        }

        return success
    }

    /**
     * Delete the *evidence* list from database and filesystem
     *
     * @param list the list of *evidences*.
     * @param user the *user*.
     * @param device the *device*.
     * @param path the path of the *evidence* files in the filesystem
     * @return operation result.
     */
    fun deleteHistory(list: List<Evidence>, user: User, device: Device, path: String): Boolean {
        var success = true

        try {
            list.forEach {
                val evidencePath = path.plus("/${it.identifier}.evd")

                val evidenceFile = File(evidencePath)

                if (evidenceFile.exists()) {
                    success = evidenceFile.delete()
                } else {
                    Log.i(TAG, "[Evidence ${it.identifier}] Evidence file not found")
                }

                State.deleteByEvidence(context, it)
                Evidence.delete(context, it.id!!)

                Log.i(
                    TAG,
                    "[Evidence: ${it.identifier}] Evidence deleted from history"
                )
            }

        } catch (e: Exception) {
            success = false
            Log.e(TAG, e.message, e)
        }

        return success
    }
}
