package com.skilrock.paypr.paypr_app.ui.activity

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.paypr.R
import com.skilrock.paypr.paypr_app.adapter.ContactsAdapter
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_contacts.*


class ContactsActivity : AppCompatActivity() {

    private var selectedContactData: ContactModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        loadContacts()
    }

    private fun loadContacts() {
        Loader.showLoader(this)
        val contactList: ArrayList<ContactModel>

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
        } else {
            contactList = getContacts()
            Loader.dismiss()

            if (contactList.isNotEmpty()) {
                contactList.sortBy { it.name }

                rvContacts.layoutManager = LinearLayoutManager(this)
                rvContacts.setHasFixedSize(true)
                val adapter = ContactsAdapter(this, contactList) {
                    tvSelectedName.text = it.name
                    tvSelectedContactNumber.text = it.mobileNumber
                    selectedContactData = it
                }
                rvContacts.adapter = adapter
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                Toast.makeText(this, getString(R.string.permission_must_grant_contact), Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }
    }

    private fun getContacts(): ArrayList<ContactModel> {
        val contactList = ArrayList<ContactModel>()
        val resolver: ContentResolver = contentResolver
        val cursorContact = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        cursorContact?.let { cursor ->
            prepareContactList(cursor, contactList)
        } ?: cursorContact?.close()

        return contactList
    }

    private fun prepareContactList(cursor: Cursor, contactList: ArrayList<ContactModel>) {
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                if (phoneNumber > 0) {
                    val cursorPhoneContact = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null
                    )

                    cursorPhoneContact?.let { cursorPhone ->
                        prepareContactModel(cursorPhone, contactList, id, name)
                    } ?: cursorPhoneContact?.close()
                }
            }
        } else {
            PayprErrorDialog(this, getString(R.string.contacts), getString(R.string.no_contacts_available)) { onBackPressed() }.showDialog()
        }
        cursor.close()
    }

    private fun prepareContactModel(cursorPhone: Cursor, contactList: ArrayList<ContactModel>, id: String, name: String) {
        if(cursorPhone.count > 0) {
            while (cursorPhone.moveToNext()) {
                val phoneNumValue = cursorPhone.getString(
                    cursorPhone.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
                )
                val contactData = ContactModel()
                contactData.id = id
                contactData.mobileNumber = deleteCountry(phoneNumValue)
                contactData.name = name
                contactData.nameInitial = getNameInitial(name)
                contactList.add(contactData)
            }
        }
        cursorPhone.close()
    }

    private fun deleteCountry(phoneStr: String) : String {
        val regex           = Regex("[^0-9]")
        val mobileNumber    = regex.replace(phoneStr, "")
        return if (mobileNumber.isNotBlank()) mobileNumber.takeLast(7)
        else ""
    }

    private fun getNameInitial(name: String) : String {
        return if (name.isNotBlank()) {
            name.substring(0, 1)
        } else
            "#"
    }

    fun onClickBack(view: View) { onBackPressed() }

    fun onDoneClick(view: View) {
        selectedContactData?.let {
            val returnIntent = Intent()
            returnIntent.putExtra("destinationAccount", it.mobileNumber)
            returnIntent.putExtra("name", it.name)
            returnIntent.putExtra("merchantId", MERCHANT_ID_INFINITI)
            setResult(RESULT_OK, returnIntent)
            finish()
        } ?: run {
            PayprErrorDialog(
                this,
                getString(R.string.contacts),
                getString(R.string.no_contact_selected)
            ) { }.showDialog()
        }
    }
}