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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_alisveris_bilgileri_guncelle.*
import kotlinx.android.synthetic.main.activity_alisveris_bilgisi_ekle.*
import java.util.*

class AlisverisBilgileriGuncelleActivity : AppCompatActivity() {



    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    var alisverisTarih = ""
    var alisverisYeri =""
    var alisverisId = ""
    var alisverisBilgisiId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alisveris_bilgileri_guncelle)

        database = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        alisverisYeri = intent.getStringExtra("alisverisYer").toString()
        alisverisId = intent.getStringExtra("alisveris").toString()
        alisverisBilgisiId= intent.getStringExtra("bilgiId").toString()


        val takvim = Calendar.getInstance()

        val yil = takvim.get(Calendar.YEAR)
        val ay = takvim.get(Calendar.MONTH)
        val gun = takvim.get(Calendar.DATE)

        alisverisTarihiGuncelle.setOnClickListener {

            val dpd= DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, yil, ay, gun ->

                alisverisTarih=""+gun+"/"+ay+"/"+yil
            },yil,ay,gun)
            dpd.show()

        }


    }



    fun  konumGuncelle(view: View){

            val intent = Intent(this,MapsActivity2::class.java)
            intent.putExtra("alisverisYer",alisverisYeri)
            intent.putExtra("alisverisId",alisverisId)
            intent.putExtra("bilgiId",alisverisBilgisiId)
            this.startActivity(intent)

    }


   fun tarihGuncelle(view: View){

       val kullanici=auth.currentUser?.uid.toString()


       if(alisverisTarih != ""){

           database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisverisId).collection("AlisverisBilgileri").document(alisverisBilgisiId).update("tarih",alisverisTarih).addOnCompleteListener {

               if (it.isSuccessful){

                   Toast.makeText(this,"Alışveriş Tarihi güncellendi!", Toast.LENGTH_SHORT).show()

               }

           }.addOnFailureListener {

               Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()

           }


       }else{

           Toast.makeText(this,"Tarih seçmelisin!",Toast.LENGTH_SHORT).show()
           return


       }


   }


}