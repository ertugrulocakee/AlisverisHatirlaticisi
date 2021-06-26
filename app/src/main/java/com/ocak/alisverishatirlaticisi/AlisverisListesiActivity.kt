package com.ocak.alisverishatirlaticisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_alisveris_listesi.*

class AlisverisListesiActivity : AppCompatActivity() {

    var alisverisListesi = ArrayList<Alisveris>()

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private  lateinit var recyclerAdapter: AlisverisListesiAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alisveris_listesi)

        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        verileriAl()

        val layoutManager = LinearLayoutManager(this)
        reyclerViewAlisverisler.layoutManager = layoutManager
        recyclerAdapter = AlisverisListesiAdapter(alisverisListesi)
        reyclerViewAlisverisler.adapter = recyclerAdapter


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.alisveris_ekle,menu)


        return super.onCreateOptionsMenu(menu)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.alisverisEkle){

            val intent = Intent(this, AlisverisOlusturActivity::class.java)
            startActivity(intent)

        }

        return super.onOptionsItemSelected(item)
    }

    fun verileriAl(){


        val kullanici = auth.currentUser?.uid.toString()

        database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").addSnapshotListener { snapshot, exception ->

            if(exception != null ){
                Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }else{

                if(snapshot != null){

                    if(snapshot.isEmpty == false){

                        val documents = snapshot.documents

                        alisverisListesi.clear()

                        for ( document in documents){


                            val alisverisAdi = document.get("alisverisAdi") as String
                            val alisverisId =document.id

                            val indirilen = Alisveris(alisverisAdi,alisverisId)

                            alisverisListesi.add(indirilen)


                        }

                        recyclerAdapter.notifyDataSetChanged()


                    }

                }


            }

        }


    }






}