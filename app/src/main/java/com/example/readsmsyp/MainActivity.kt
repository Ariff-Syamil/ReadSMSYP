package com.example.readsmsyp

import android.app.ListActivity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.CursorAdapter
import com.example.readsmsyp.databinding.ActivityMainBinding

class MainActivity : ListActivity() {
    private lateinit var binding: ActivityMainBinding

    // content here refers to content provider to have access to SMS
    val SMS = Uri.parse("content://sms")

    //code permission declared by us for sms
    val PERMISSIONS_REQUEST_READ_SMS = 1

    // SMS will be stored inside a database in Android system
    // We will retrieve the SMSs and shows it inside our list (ListActivity)
    // The db structure of the SMS is defined as follows

    object SmsColumns {
        val ID = "_id"
        val ADDRESS = "address"
        val DATE = "date"
        val BODY = "body"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_SMS
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            readSMS()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_SMS),
                PERMISSIONS_REQUEST_READ_SMS
            )
        }
    }

    private fun readSMS() {

        val cursor = contentResolver.query(
            SMS, arrayOf(
                SmsColumns.ID,
                SmsColumns.ADDRESS,
                SmsColumns.DATE,
                SmsColumns.BODY
            ),
            null,
            null,
            SmsColumns.DATE + "  DESC"
        )

        val adapter = SmsCursorAdapter(this, cursor!!, true)
        listAdapter = adapter

    }

    private inner class SmsCursorAdapter(context: Context, c: Cursor, autoRequery: Boolean) :
        CursorAdapter(context, c, autoRequery) {
        // Untuk tunjuk apa //onCreateViewHolder
        override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
            return View.inflate(context, R.layout.activity_main, null)
        }

        // Untuk link element dan data //onBindViewHolder
        override fun bindView(view: View?, context: Context?, cursor: Cursor?) {

            // Untuk sms_origin TextView, tunjuk data dari database dalam column address
            view!!.findViewById<TextView>(R.id.sms_origin).text =
                cursor!!.getString(cursor!!.getColumnIndexOrThrow(SmsColumns.ADDRESS))
            view.findViewById<TextView>(R.id.sms_body).text =
                cursor!!.getString(cursor.getColumnIndexOrThrow(SmsColumns.BODY))
            view.findViewById<TextView>(R.id.sms_date).text =
                cursor!!.getString(cursor.getColumnIndexOrThrow(SmsColumns.DATE))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // For the case of readSMS
            PERMISSIONS_REQUEST_READ_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readSMS()
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}