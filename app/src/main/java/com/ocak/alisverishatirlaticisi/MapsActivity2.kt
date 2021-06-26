package com.ocak.alisverishatirlaticisi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ocak.alisverishatirlaticisi.databinding.ActivityMaps2Binding

class MapsActivity2 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMaps2Binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    var latitude =""
    var longitude =""
    var alisverisLokasyon =""
    var alisverisId=""
    var bilgiId=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        alisverisLokasyon = intent.getStringExtra("alisverisYer").toString()
        alisverisId = intent.getStringExtra("alisverisId").toString()
        bilgiId = intent.getStringExtra("bilgiId").toString()

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
        menuInflater.inflate(R.menu.konum_guncelle,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.alisverisKonumGuncelle){
            guncelleKonum()
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

            Toast.makeText(applicationContext,"Artık bu alışveriş yerini güncelleyebilirsin!", Toast.LENGTH_SHORT).show()

        }
    }


    fun guncelleKonum(){

        val kullanici = auth.currentUser?.uid.toString()

        val hashMap = HashMap<String,Any>()
        hashMap.put("latitude",latitude)
        hashMap.put("longitude",longitude)

        database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisverisId).collection("AlisverisBilgileri").document(bilgiId).update(hashMap).addOnCompleteListener {

            if (it.isSuccessful){

                Toast.makeText(this,"Alışveriş konumun güncellendi!",Toast.LENGTH_SHORT).show()
            }



        }.addOnFailureListener {

            Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()


        }


    }



}