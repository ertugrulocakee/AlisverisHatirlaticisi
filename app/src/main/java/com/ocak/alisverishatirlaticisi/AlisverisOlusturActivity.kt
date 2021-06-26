package com.ocak.alisverishatirlaticisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_alisveris_olustur.*
import java.util.*

class AlisverisOlusturActivity : AppCompatActivity() {

    var alisverisAdi=""
    var alisverisNotu=""
    var alisverisYapilacakYer=""

    private lateinit var auth: FirebaseAuth
    private  lateinit var  database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alisveris_olustur)
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }


    fun  alisverisOlustur(view: View){

        alisverisAdi = alisverisAdiOlustur.text.toString()
        alisverisNotu = alisverisNotuOlustur.text.toString()
        alisverisYapilacakYer = alisverisYapicakYerİsmiEkle.text.toString()

        if (alisverisAdi!="" && alisverisNotu!="" && alisverisYapilacakYer !=""){

            val kullanici = auth.currentUser?.uid.toString()

            val hashMap = hashMapOf<String,Any>()
            hashMap.put("alisverisAdi",alisverisAdi)
            hashMap.put("alisverisNotu",alisverisNotu)
            hashMap.put("alisverisYeri",alisverisYapilacakYer)

            database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").add(hashMap).addOnCompleteListener {
                if (it.isSuccessful){

                    Toast.makeText(this,"Alışveriş oluşturuldu",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,AlisverisListesiActivity::class.java)
                    startActivity(intent)
                    finish()

                }

            }


        }else{

            Toast.makeText(this,"Ekşik işlem var!", Toast.LENGTH_SHORT).show()
            return
        }



    }








}