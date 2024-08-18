package com.skilrock.paypr.utility

import android.graphics.Bitmap
import android.net.Uri


class ContactModel {

    var id: String?             = null
    var name: String?           = null
    var mobileNumber: String?   = null
    var nameInitial: String?    = null
    private var photo: Bitmap?  = null
    private var photoURI: Uri?  = null

    override fun toString(): String {
        return "ContactModel(id=$id, name=$name, mobileNumber=$mobileNumber, nameInitial=$nameInitial, photo=$photo, photoURI=$photoURI)"
    }
}