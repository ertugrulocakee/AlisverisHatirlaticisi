package com.ocak.alisverishatirlaticisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

private lateinit var auth: FirebaseAuth
private lateinit var database : FirebaseFirestore




override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    auth = Firebase.auth
    database = FirebaseFirestore.getInstance()


    val currentUser = auth.currentUser


    if(currentUser != null){

        val intent = Intent(this,AlisverisListesiActivity::class.java)
        startActivity(intent)
        finish()

    }

}


fun kayitOl(view: View){

    val intent = Intent(this, KayitOlActivity::class.java)
    startActivity(intent)

}



fun girisYap(view: View){


    val email = eMailText.text.toString()
    val sifre = sifreText.text.toString()

    if (sifre != "" && email != "") {


        auth.signInWithEmailAndPassword(email, sifre).addOnCompleteListener {
            if (it.isSuccessful) {


                    val guncelKullanici = auth.currentUser!!.displayName




                    Toast.makeText(this, "Hoşgeldiniz ${guncelKullanici} !", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, AlisverisListesiActivity::class.java)
                    startActivity(intent)
                    finish()

            }


        }.addOnFailureListener {
            Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }else{
        Toast.makeText(applicationContext,"Boş kısım bırakmayın !", Toast.LENGTH_LONG).show()
    }

}


}