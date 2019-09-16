package com.tfm.digitalevidencemanager.activities

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.tfm.digitalevidencemanager.R
import com.tfm.digitalevidencemanager.api.DigitalEvidenceManager
import com.tfm.digitalevidencemanager.api.StateEnum
import com.tfm.digitalevidencemanager.entity.interfaces.EvidenceContract
import com.tfm.digitalevidencemanager.entity.interfaces.StateContract
import kotlinx.android.synthetic.main.activity_evidences_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import java.text.SimpleDateFormat

class EvidencesListActivity : AppCompatActivity(){
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapterEvidenceList.ViewHolder>? = null
    private var list : List<ContentValues> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evidences_list)

        layoutManager = LinearLayoutManager(this)
        recycler_view.layoutManager = layoutManager

        val progressDialog = indeterminateProgressDialog(getString(R.string.dialog_list_wait))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val context = this

        doAsync {
            list = getEvidencesList()
            runOnUiThread {
                progressDialog.dismiss()
                adapter = RecyclerAdapterEvidenceList(context, list)
                recycler_view.adapter = adapter
            }
        }
    }

    private fun getEvidencesList(): List<ContentValues> {
        val dem = DigitalEvidenceManager(this)
        val evidenceKeysList = listOf(
            EvidenceContract.EvidenceEntry.COLUMN_NAME_ID,
            EvidenceContract.EvidenceEntry.COLUMN_NAME_NAME,
            EvidenceContract.EvidenceEntry.COLUMN_NAME_TYPE,
            EvidenceContract.EvidenceEntry.COLUMN_NAME_IDENTIFIER
        )
        val evidencesValuesList = dem.getEvidencesValuesList(evidenceKeysList)

        val stateKeysList = listOf(
            StateContract.StateEntry.COLUMN_NAME_EVIDENCE,
            StateContract.StateEntry.COLUMN_NAME_DATE
        )
        val statesValuesList = dem.getStateValuesList(stateKeysList, StateEnum.ACQUIRED.id)

        val valuesList = ArrayList<ContentValues>()

        var values : ContentValues
        evidencesValuesList.forEach {
            values = ContentValues()
            values.put(Constants.CARD_EVIDENCE_ID, it.getAsLong(EvidenceContract.EvidenceEntry.COLUMN_NAME_ID))

            var name = it.getAsString(EvidenceContract.EvidenceEntry.COLUMN_NAME_NAME)
            if (name.isEmpty()){
                name = "-"
            }
            values.put(Constants.CARD_EVIDENCE_NAME, name)

            var date = searchStateByEvidence(statesValuesList, it.getAsLong(EvidenceContract.EvidenceEntry.COLUMN_NAME_ID))
            if (name.isEmpty()){
                date = "-"
            }
            values.put(Constants.CARD_EVIDENCE_DATE, date)

            values.put(Constants.CARD_EVIDENCE_TYPE, it.getAsString(EvidenceContract.EvidenceEntry.COLUMN_NAME_TYPE))
            values.put(Constants.CARD_EVIDENCE_IDENTIFIER, it.getAsString(EvidenceContract.EvidenceEntry.COLUMN_NAME_IDENTIFIER))

            valuesList.add(values)
        }

        return valuesList
    }

    private fun searchStateByEvidence(statesList : List<ContentValues>, evidenceId : Long) : String {
        var time = -1L
        var date = ""

        var count = 0
        while(time < 0 && count < statesList.size){
            if(statesList[count].getAsLong(StateContract.StateEntry.COLUMN_NAME_EVIDENCE) == evidenceId){
                time = statesList[count].getAsLong(StateContract.StateEntry.COLUMN_NAME_DATE)
                val format = SimpleDateFormat(Constants.DATE_FORMAT)
                date = format.format(time)
            }

            count++
        }

        return date
    }
}
