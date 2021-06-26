package com.ocak.alisverishatirlaticisi

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.common.base.MoreObjects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_alisveris_detayi.*
import kotlinx.android.synthetic.main.activity_alisveris_listesi.*
import kotlinx.android.synthetic.main.recycler_row_urunler.*
import java.util.*
import kotlin.collections.ArrayList

class AlisverisDetayiActivity : AppCompatActivity() , OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {



    var secilenAlisveris = ""
    var alisverisId = ""
    var alisverisYeri =""
    var urunListesi = ArrayList<Urun>()
    var bilgiId=""
    var alinanUrunSayisi : Int = 0
    var urunSayisi : Int = 0
    var urunListesiString =""
    var alisverisNotu =""
    var alisverisTarihi =""
    var alisverisFisi = ""


    private  lateinit var recyclerAdapter: UrunListesiAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var mMap: GoogleMap



    lateinit var alarmManager: AlarmManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alisveris_detayi)

        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapDetail) as SupportMapFragment
        mapFragment.getMapAsync(this)

        alisverisDetayiTarih.visibility = View.GONE
        alisverisDetayiFis.visibility = View.GONE

        mapDetail.view?.visibility = View.GONE



        secilenAlisveris = intent.getStringExtra("ad").toString()
        alisverisId= intent.getStringExtra("id").toString()


        verileriAl()

        urunleriAl()

        val layoutManager = LinearLayoutManager(this)
        recyclerViewUrunler.layoutManager = layoutManager
        recyclerAdapter = UrunListesiAdapter(urunListesi)
        recyclerViewUrunler.adapter = recyclerAdapter


        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        btn_create.setOnClickListener {
            val second = edt_timer.text.toString().toInt() * 1000 * 60
            val intent = Intent(this, Receiver::class.java)
            intent.putExtra("secilenAlisveris",secilenAlisveris)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + second, pendingIntent)
            Toast.makeText(this,"Alışveriş için alarm kuruldu!",Toast.LENGTH_LONG).show()
        }

        btn_cancel.setOnClickListener {
            val intent = Intent(this, Receiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
        }



    }



    fun verileriAl() {

        val kullanici = auth.currentUser?.uid.toString()

        database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").whereEqualTo("alisverisAdi",secilenAlisveris).addSnapshotListener { snapshot, exception ->

            if (exception != null){

                Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()

            }else{

                if (snapshot != null){

                    if(snapshot.isEmpty == false){


                        val documents = snapshot.documents

                        for( document in documents){


                            val alisverisAdi = document.get("alisverisAdi") as String
                             alisverisYeri = document.get("alisverisYeri") as String
                             alisverisNotu = document.get("alisverisNotu") as String


                            alisverisDetayiAd.text= alisverisAdi
                            alisverisDetayiYer.text= "Alışveriş yeri:"+" "+alisverisYeri
                            alisverisDetayiNot.text=alisverisNotu


                        }


                    }
                }
            }



        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.urun_ekle,menu)


        return super.onCreateOptionsMenu(menu)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.urunEkle){

            val intent = Intent(this, UrunOlusturActivity::class.java)
            intent.putExtra("alisveris",alisverisId)
            this.startActivity(intent)

        }

        if (item.itemId == R.id.bilgi_ekle){

            val intent =Intent(this,AlisverisBilgisiEkleActivity::class.java)
            intent.putExtra("id",alisverisId)
            intent.putExtra("alisverisYer",alisverisYeri)
            this.startActivity(intent)


        }

        if(item.itemId == R.id.bilgiGuncelle){

            if(bilgiId!=""){

                val intent = Intent(this,AlisverisBilgileriGuncelleActivity::class.java)
                intent.putExtra("bilgiId",bilgiId)
                intent.putExtra("alisveris",alisverisId)
                intent.putExtra("alisverisYer",alisverisYeri)
                this.startActivity(intent)

            }else{

             Toast.makeText(this,"Güncellenecek bir bilgi yok!",Toast.LENGTH_SHORT).show()

            }


        }

        if(item.itemId == R.id.alisverisPaylas){

         var gonderilecekText = "Alışveriş adı: "+secilenAlisveris+"\n"+"Alışveriş yeri: "+alisverisYeri+"\n"+"Alışveriş notu: "+alisverisNotu+"\n"

          if (urunListesiString!=""){

              gonderilecekText += "Ürün Listesi: "+urunListesiString+"\n"

          }

          if (alisverisTarihi != ""){

              gonderilecekText += "Alışveriş Tarihi: "+alisverisTarihi

          }

         if(alisverisFisi == ""){

             val gonder = Intent(Intent.ACTION_SEND)
             gonder.putExtra(Intent.EXTRA_SUBJECT,"Alışveriş Listem")
             gonder.putExtra(Intent.EXTRA_TEXT,gonderilecekText)
             gonder.setType("text/*")
             this.startActivity(Intent.createChooser(gonder,"Alışveriş Listesini Paylaş..."))

         }else{

             val gonder = Intent(Intent.ACTION_SEND)
             gonder.putExtra(Intent.EXTRA_SUBJECT,"Alışveriş Listem")
             gonder.putExtra(Intent.EXTRA_TEXT,gonderilecekText)
             gonder.putExtra(Intent.EXTRA_STREAM,Uri.parse(alisverisFisi))
             gonder.setType("application/image")
             this.startActivity(Intent.createChooser(gonder,"Alışveriş Listesini Paylaş..."))


         }

        }


        return super.onOptionsItemSelected(item)
    }

   fun urunleriAl(){

       val kullanici = auth.currentUser?.uid.toString()

       database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisverisId).collection("Urunler").addSnapshotListener { snapshot, exception ->

           if (exception != null){

               Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()

           }else{

               if (snapshot != null){

                   if(snapshot.isEmpty == false){


                       val documents = snapshot.documents

                       urunSayisi = documents.size

                       urunListesi.clear()

                       for( document in documents){


                           val urunAdi = document.get("urunadi") as String
                           val urunAdeti = document.get("urunadeti") as String
                           val urunFiyati = document.get("urunfiyati") as String
                           val urunGorseli = document.get("urungorseli")as String
                           val urunId = document.id
                           val urunAlindiDurumu = document.get("urunDurumu") as Boolean
                           val urunAdetBilgisi = "Adeti: "+urunAdeti
                           val urunFiyatBilgisi = "Fiyatı: "+urunFiyati+" "+"TL"

                           urunListesiString += urunAdi+" "+urunAdetBilgisi+" "+urunFiyatBilgisi+" "

                           if (urunAlindiDurumu == true){

                               alinanUrunSayisi = alinanUrunSayisi+1

                           }


                           val indirilen = Urun(urunAdi,urunAdetBilgisi,urunFiyatBilgisi,urunId,alisverisId,urunGorseli,urunAlindiDurumu)
                           urunListesi.add(indirilen)


                       }

                       urunSayisiBilgisi.text = urunSayisi.toString()+" "+"tane ürünün"+" "+alinanUrunSayisi+" "+"tanesini aldın."

                       recyclerAdapter.notifyDataSetChanged()


                   }
               }
           }



       }


   }

    override fun onMapReady(p0: GoogleMap) {

        val kullanici = auth.currentUser?.uid.toString()

      mMap = p0

        database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisverisId).collection("AlisverisBilgileri").addSnapshotListener { snapshot, exception ->

            if (exception != null){

                Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()

            }else{

                if (snapshot != null){

                    if(snapshot.isEmpty == false){


                        val documents = snapshot.documents



                        for( document in documents){

                            alisverisDetayiTarih.visibility = View.VISIBLE
                            alisverisDetayiFis.visibility = View.VISIBLE

                            alisverisTarihi = document.get("tarih") as String
                             alisverisFisi = document.get("fis") as String
                            val latitude = document.get("latitude") as String
                            val longitude = document.get("longitude") as String

                             bilgiId = document.id

                            alisverisDetayiTarih.text = "Alışveriş tarihi:"+" "+alisverisTarihi
                            Picasso.get().load(alisverisFisi).into(alisverisDetayiFis)

                            if (latitude != "" && longitude != ""){

                                mapDetail.view?.visibility = View.VISIBLE

                            }


                            val latitudeDouble = latitude.toDouble()
                            val longitudeDouble = longitude.toDouble()



                            val chosenLocation = LatLng(latitudeDouble,longitudeDouble)
                        val marker = mMap.addMarker(MarkerOptions().position(chosenLocation).title(alisverisYeri))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chosenLocation,17f))
                            marker.tag = 0

                            mMap.setOnMarkerClickListener(this@AlisverisDetayiActivity)




                        }




                    }



                }
            }



        }



    }


    class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val secilen = intent?.getStringExtra("secilenAlisveris")

            if (secilen!=null){

                Toast.makeText(context,"${secilen} alışverişini unutma!",Toast.LENGTH_LONG).show()
            }



            val vibrator : Vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(1000)



        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {

        var clickCount = p0?.tag as Int

        if (clickCount != null){

           clickCount = clickCount+1
            p0.tag=clickCount


            Toast.makeText(this,p0.title+" "+clickCount.toString()+" "+"kez tıklandı.",Toast.LENGTH_SHORT).show()

        }

        return false

    }


}




