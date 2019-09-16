package com.tfm.digitalevidencemanager.activities

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tfm.digitalevidencemanager.R


class RecyclerAdapterEvidenceList(private val context: Context, private val evidences: List<ContentValues>) : RecyclerView.Adapter<RecyclerAdapterEvidenceList.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.layout_evidence_card_view, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return evidences.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.evidenceName.text = evidences[i].getAsString(Constants.CARD_EVIDENCE_NAME)
        viewHolder.evidenceDate.text = evidences[i].getAsString(Constants.CARD_EVIDENCE_DATE)
        viewHolder.evidenceType.text = evidences[i].getAsString(Constants.CARD_EVIDENCE_TYPE)
        viewHolder.evidenceIdentifier.text = evidences[i].getAsString(Constants.CARD_EVIDENCE_IDENTIFIER)

        viewHolder.itemView.setOnClickListener {
            val detailIntent = Intent(context, EvidenceDetailActivity::class.java)
            detailIntent.putExtra(Constants.EVIDENCE_ID, evidences[i].getAsLong(Constants.CARD_EVIDENCE_ID))
            startActivity(context, detailIntent, Bundle.EMPTY)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val evidenceName = this.itemView.findViewById(R.id.card_name) as TextView
        val evidenceDate = this.itemView.findViewById(R.id.card_date) as TextView
        val evidenceType = this.itemView.findViewById(R.id.card_type) as TextView
        val evidenceIdentifier = this.itemView.findViewById(R.id.card_identification) as TextView
    }
}