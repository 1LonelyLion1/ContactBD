package com.example.contactbd

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.contactbd.databas.Contact
import com.example.contactbd.databas.ContactDao
import com.example.contactbd.databas.ContactDataBase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers



class MainActivity : AppCompatActivity() {

    private var db: ContactDataBase? = null
    private var contactDAO: ContactDao? = null


    private var contacts: MutableList<Contact> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvTextView: TextView = findViewById(R.id.tv_text)

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

                    contacts.add(Contact( id,name, phone))

                    id++
                }
            }
            cursor?.close()

            contactDAO?.insertContact(contacts)


            db?.contactDAO()?.getContacts()

        }.doOnNext {
                MutableList ->
            var finalString = ""
            MutableList?.map { finalString += it.name + " " + it.phone + "\n" }

            val handler = Handler(Looper.getMainLooper())

            handler.post {
                run {

                    tvTextView.text = finalString
                }
            }



        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()


    }
}