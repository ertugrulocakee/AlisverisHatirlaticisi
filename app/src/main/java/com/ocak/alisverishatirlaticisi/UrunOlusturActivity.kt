package com.ocak.alisverishatirlaticisi

import android.Manifest
import android.app.Activity
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
import kotlinx.android.synthetic.main.activity_urun_olustur.*
import java.util.*

class UrunOlusturActivity : AppCompatActivity() {


    var alisverisId=""

    var secilenGorsel: Uri? = null
    var secilenBitmap : Bitmap?= null


    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore
    private lateinit var auth : FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_urun_olustur)

        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        alisverisId=intent.getStringExtra("alisveris").toString()

    }

    fun urunResmiOlustur(view:View){

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

    fun urunEkle(view: View){

      val urunAdi = urunAdi.text.toString()
      val urunAdeti = urunAdeti.text.toString()
      val urunFiyati = urunFiyati.text.toString()
      val kullanici =  auth.currentUser?.uid.toString()

     if (!urunAdi.equals("") && !urunAdeti.equals("") && !urunFiyati.equals("") && secilenGorsel != null){


         val uuid = UUID.randomUUID()
         val gorselIsmi = " ${uuid}.jpg"

         val reference = storage.reference
         val gorselReference = reference.child("urungorselleri").child(gorselIsmi)
         val urunGorseliUri=secilenGorsel

         gorselReference.putFile(urunGorseliUri!!).addOnSuccessListener {
             val yuklenenGorselinReference = FirebaseStorage.getInstance().reference.child("urungorselleri").child(gorselIsmi)
             yuklenenGorselinReference.downloadUrl.addOnSuccessListener {

                 val downloadUri = it.toString()

                 val urunAlinmaDurumu = false


                 val hashMap = hashMapOf<String, Any>()
                 hashMap.put("urungorseli", downloadUri)
                 hashMap.put("urunadi", urunAdi)
                 hashMap.put("urunadeti", urunAdeti)
                 hashMap.put("urunfiyati", urunFiyati)
                 hashMap.put("urunDurumu",urunAlinmaDurumu)


                 database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisverisId)
                     .collection("Urunler").add(hashMap).addOnCompleteListener {

                     if (it.isSuccessful) {

                         Toast.makeText(this, "Ürün alışverişinize eklendi.", Toast.LENGTH_SHORT)
                             .show()


                     }

                 }.addOnFailureListener {
                     Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                 }


             }.addOnFailureListener {
                 Toast.makeText(this,it.localizedMessage, Toast.LENGTH_SHORT).show()
             }





                 }.addOnFailureListener{
             Toast.makeText(this,it.localizedMessage, Toast.LENGTH_SHORT).show()
         }

     }else{

         Toast.makeText(this,"Ekşik işlem var! Ürün eklenemedi!",Toast.LENGTH_SHORT).show()
         return
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
                    urunResmiOlustur.setImageBitmap(secilenBitmap)


                } else {
                    secilenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                    urunResmiOlustur.setImageBitmap(secilenBitmap)
                }

            }

        }


        super.onActivityResult(requestCode, resultCode, data)
    }





}