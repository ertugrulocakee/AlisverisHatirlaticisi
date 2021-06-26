package com.ocak.alisverishatirlaticisi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_alisveris_guncelle.*

class AlisverisGuncelleActivity : AppCompatActivity() {

    private lateinit var database : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    var alisverisAd =""
    var alisverisYer =""
    var alisverisNot =""
    var alisverisId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alisveris_guncelle)

        database= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()

        alisverisId= intent.getStringExtra("id").toString()
    }


    fun adGuncelle(view: View){

        val kullanici = auth.currentUser?.uid.toString()

        alisverisAd = alisverisAdiGuncelle.text.toString()

        if (alisverisAd!=""){

            database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisverisId).update("alisverisAdi",alisverisAd).addOnCompleteListener {

                if (it.isSuccessful){

                    Toast.makeText(this,"Alışveriş adınız güncellendi!",Toast.LENGTH_SHORT).show()


                }



            }.addOnFailureListener {

                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }



        }else{

            Toast.makeText(this,"Ad girmemişsin!",Toast.LENGTH_SHORT).show()
            return


        }



    }


    fun yerGuncelle(view: View){

        val kullanici = auth.currentUser?.uid.toString()

        alisverisYer = alisverisYapicakYerIsmiGuncelle.text.toString()

        if (alisverisYer!=""){

            database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisverisId).update("alisverisYeri",alisverisYer).addOnCompleteListener {

                if (it.isSuccessful){

                    Toast.makeText(this,"Alışveriş yeriniz güncellendi!",Toast.LENGTH_SHORT).show()


                }



            }.addOnFailureListener {

                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }



        }else{

            Toast.makeText(this,"Yer girmemişsin!",Toast.LENGTH_SHORT).show()
            return


        }




    }


    fun notuGuncelle(view: View){


        val kullanici = auth.currentUser?.uid.toString()

        alisverisNot = alisverisNotuGuncelle.text.toString()

        if (alisverisNot!=""){

            database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisverisId).update("alisverisNotu",alisverisNot).addOnCompleteListener {

                if (it.isSuccessful){

                    Toast.makeText(this,"Alışveriş notunuz güncellendi!",Toast.LENGTH_SHORT).show()


                }



            }.addOnFailureListener {

                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }



        }else{

            Toast.makeText(this,"Not girmemişsin!",Toast.LENGTH_SHORT).show()
            return


        }








    }





}