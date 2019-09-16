package com.tfm.digitalevidencemanager.activities

import android.content.ContentValues
import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.tfm.digitalevidencemanager.R


class RecyclerAdapterStateList(private val context: Context, private val states: List<ContentValues>) : RecyclerView.Adapter<RecyclerAdapterStateList.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.layout_state_card_view, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return states.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val contentValues = states[i]

        var tableRow : TableRow
        var labelView : TextView
        var textView : TextView

        contentValues.keySet().forEach {
            tableRow = TableRow(context)

            labelView = TextView(context)
            textView = TextView(context)
            labelView.typeface = Typeface.DEFAULT_BOLD

            labelView.text = it.plus(": ")
            textView.text = contentValues.getAsString(it)

            tableRow.addView(labelView)
            tableRow.addView(textView)

            viewHolder.stateItemList.addView(tableRow)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stateItemList = this.itemView.findViewById(R.id.state_item_list) as TableLayout
    }
}