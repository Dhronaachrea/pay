package com.skilrock.paypr.paypr_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.ContactModel

class ContactsAdapter(val context: Context, private val list:  ArrayList<ContactModel>,
                      private val operation:(ContactModel) -> Unit) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_contact, parent, false)

        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contactModel        = list[position]
        holder.tvInitial.text   = contactModel.nameInitial
        holder.tvName.text      = contactModel.name
        holder.tvNumber.text    = contactModel.mobileNumber

        holder.llContactRow.setOnClickListener {
            operation(contactModel)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvInitial       = itemView.findViewById(R.id.tvContactInitial) as MaterialTextView
        val tvName          = itemView.findViewById(R.id.tvContactName) as MaterialTextView
        val tvNumber        = itemView.findViewById(R.id.tvContactNumber) as MaterialTextView
        val llContactRow    = itemView.findViewById(R.id.llContactRow) as LinearLayoutCompat
    }

}