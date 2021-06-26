package com.ocak.alisverishatirlaticisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.net.toUri

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ocak.alisverishatirlaticisi.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var auth: FirebaseAuth
    private  lateinit var  storage: FirebaseStorage
    private lateinit var  database : FirebaseFirestore

    var latitude =""
    var longitude =""
    var alisverisTarihi=""
    var alisverisFisi=""
    var alisverisLokasyon =""
    var id=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database= FirebaseFirestore.getInstance()

        alisverisTarihi = intent.getStringExtra("alisverisTarihi").toString()
        alisverisFisi = intent.getStringExtra("alisverisFisi").toString()
        alisverisLokasyon = intent.getStringExtra("alisverisYer").toString()
        id=intent.getStringExtra("alisverisId").toString()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.bilgi_ekle,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.bilgiEkle){
            ekleAlisveris()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.setOnMapLongClickListener(mylistener)

        val istanbul = LatLng(41.0291,28.9766)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(istanbul).title("Marker in İstanbul"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(istanbul,17f))


    }

    val mylistener = object : GoogleMap.OnMapLongClickListener{

        override fun onMapLongClick(p0: LatLng?) {

            mMap.clear()
            mMap.addMarker(MarkerOptions().position(p0!!).title(alisverisLokasyon))
            latitude = p0.latitude.toString()
            longitude =p0.longitude.toString()

            Toast.makeText(applicationContext,"Artık bu alışveriş yerini kayıt edebilirsin!", Toast.LENGTH_SHORT).show()

        }
    }



    fun ekleAlisveris(){


        val uuid = UUID.randomUUID()
        val gorselIsmi = " ${uuid}.jpg"

        val reference = storage.reference
        val gorselReference = reference.child("fisgorselleri").child(gorselIsmi)
        val fisGorseliUri= alisverisFisi.toUri()

        gorselReference.putFile(fisGorseliUri!!).addOnSuccessListener {
            val yuklenenGorselinReference = FirebaseStorage.getInstance().reference.child("fisgorselleri").child(gorselIsmi)
            yuklenenGorselinReference.downloadUrl.addOnSuccessListener {

                val downloadUri = it.toString()

                val hashMap = hashMapOf<String,Any>()
                hashMap.put("tarih",alisverisTarihi)
                hashMap.put("fis",alisverisFisi)
                hashMap.put("latitude",latitude)
                hashMap.put("longitude",longitude)
                val kullanici = auth.currentUser?.uid.toString()


                database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(id).collection("AlisverisBilgileri").add(hashMap).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Alışveriş bilgileri eklendi.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, AlisverisListesiActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
                }


            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
            }



        }.addOnFailureListener {
            Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
        }



    }

}