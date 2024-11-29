package com.example.bscscor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterAssessmentFees(private val fees: List<String>) :
    RecyclerView.Adapter<AdapterAssessmentFees.FeeViewHolder>() {

    class FeeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val feeTextView: TextView = view.findViewById(R.id.textitem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.textrecyclerview, parent, false)
        return FeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeeViewHolder, position: Int) {
        holder.feeTextView.text = fees[position]
    }

    override fun getItemCount(): Int = fees.size
}
