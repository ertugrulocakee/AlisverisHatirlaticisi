package com.ocak.alisverishatirlaticisi

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.base.MoreObjects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_alisveris_bilgisi_ekle.*
import kotlinx.android.synthetic.main.activity_urun_olustur.*
import java.util.*

class AlisverisBilgisiEkleActivity : AppCompatActivity() {

    var secilenGorsel: Uri? = null
    var secilenBitmap : Bitmap?= null


    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    var alisverisTarihi = ""
    var alisverisYeri =""
    var alisverisId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alisveris_bilgisi_ekle)

        database = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        alisverisYeri = intent.getStringExtra("alisverisYer").toString()
        alisverisId = intent.getStringExtra("id").toString()

        val takvim = Calendar.getInstance()

        val yil = takvim.get(Calendar.YEAR)
        val ay = takvim.get(Calendar.MONTH)
        val gun = takvim.get(Calendar.DATE)

        alisverisTarihiEkle.setOnClickListener {

            val dpd= DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, yil, ay, gun ->

                alisverisTarihi=""+gun+"/"+ay+"/"+yil
            },yil,ay,gun)
            dpd.show()
        }


    }


   fun fisEkle(view: View){

       if (ContextCompat.checkSelfPermission(
               this,
               Manifest.permission.READ_EXTERNAL_STORAGE
           ) != PackageManager.PERMISSION_GRANTED
       ) {
           ActivityCompat.requestPermissions(
               this,
               arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
               1
           )
       } else {
           val galeriIntent =
               Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
           startActivityForResult(galeriIntent, 2)

       }


   }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)

            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            secilenGorsel = data.data

            if (secilenGorsel != null) {
                if (Build.VERSION.SDK_INT >= 28) {

                    val source = ImageDecoder.createSource(this.contentResolver, secilenGorsel!!)
                    secilenBitmap = ImageDecoder.decodeBitmap(source)
                    fisEkle.setImageBitmap(secilenBitmap)


                } else {
                    secilenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                    fisEkle.setImageBitmap(secilenBitmap)
                }

            }

        }


        super.onActivityResult(requestCode, resultCode, data)
    }


   fun  konumEkle(view: View){

       if (alisverisTarihi!="" && secilenGorsel != null){


           val intent = Intent(this,MapsActivity::class.java)
           intent.putExtra("alisverisTarihi",alisverisTarihi)
           intent.putExtra("alisverisFisi",secilenGorsel.toString())
           intent.putExtra("alisverisYer",alisverisYeri)
           intent.putExtra("alisverisId",alisverisId)
           this.startActivity(intent)





       }else{

           Toast.makeText(this,"Eksik işlem var!",Toast.LENGTH_SHORT).show()
           return
       }



   }





}