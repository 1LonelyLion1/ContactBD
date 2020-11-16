package com.example.contactbd

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.contactbd.databas.Contact
import com.example.contactbd.databas.ContactDao
import com.example.contactbd.databas.ContactDataBase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var db: ContactDataBase? = null
    private var contactDAO: ContactDao? = null



    private var contacts: MutableList<Contact> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionIfNecessary()
    }

    private fun isPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS).toString()
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissionIfNecessary() {
        if (!isPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 42)
        }
        else{
            loadAndShowBD()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
           42 -> loadAndShowBD()
           else -> tv_text.text = getString(R.string.messagePermissionsDenied)
        }

    }


    private fun loadAndShowBD(){
        Observable.fromCallable {

            db = ContactDataBase.getContactDataBase(context = this) //Почемуто крашит приложуху((

            contactDAO = db?.contactDAO()

            val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val cursor: Cursor? = contentResolver
                    ?.query(uri, null, null, null, null)

            var id = 0

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val name: String =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phone =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    contacts.add(Contact(id, name, phone))

                    id++
                }
            }

            cursor?.close()

            contactDAO?.insertContact(contacts)


            db?.contactDAO()?.getContacts()

        }.doOnNext { MutableList ->
            var finalString = ""
            MutableList?.map { finalString += it.name + " " + it.phone + "\n" }


            Handler(Looper.getMainLooper()).post {
                 run {

                    tv_text.text = finalString
                    tv_text.movementMethod = ScrollingMovementMethod()
                }
             }

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

    }

}