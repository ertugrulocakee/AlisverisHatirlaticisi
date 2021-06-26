package com.ocak.alisverishatirlaticisi

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row_urunler.view.*


class UrunListesiAdapter (var urunList : ArrayList<Urun>) : RecyclerView.Adapter<UrunListesiAdapter.UrunListesiViewHolder>(){

    var auth = FirebaseAuth.getInstance()
    var database = FirebaseFirestore.getInstance()

    class UrunListesiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrunListesiViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view= inflater.inflate(R.layout.recycler_row_urunler,parent,false)
        return UrunListesiAdapter.UrunListesiViewHolder(view)

    }

    override fun onBindViewHolder(holder: UrunListesiViewHolder, position: Int) {

        holder.itemView.recycler_row_urun_ad.text = urunList.get(position).urunAdi
        holder.itemView.recycler_row_urun_adet.text = urunList.get(position).urunAdeti
        holder.itemView.recycler_row_urun_fiyat.text = urunList.get(position).urunFiyati
        holder.itemView.alindiKontrolu.isChecked = urunList.get(position).secildiBilgisi
        Picasso.get().load(urunList.get(position).urunGorseli).into(holder.itemView.recycler_row_urun_resim)


        holder.itemView.silUrunButonu.setOnClickListener {

            val string = urunList.get(position).urunId
            val alisveris = urunList.get(position).alisverisId
            val kullanici = auth.currentUser?.uid.toString()

            database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisveris).collection("Urunler").document(string).delete().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(holder.itemView.context, "Seçilen ürün başarıyla silindi", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(holder.itemView.context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }



        }

        holder.itemView.guncelleUrunButonu.setOnClickListener {

            val string = urunList.get(position).urunId
            val alisveris = urunList.get(position).alisverisId

           val intent = Intent (holder.itemView.context,UrunGuncelleActivity::class.java)
            intent.putExtra("id",string)
            intent.putExtra("alisveris",alisveris)

            holder.itemView.context.startActivity(intent)


        }


        holder.itemView.alindiKontrolu.setOnClickListener {

            if (holder.itemView.alindiKontrolu.isChecked){

                val string = urunList.get(position).urunId
                val alisveris = urunList.get(position).alisverisId
                val kullanici = auth.currentUser?.uid.toString()


                database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisveris).collection("Urunler").document(string).update("urunDurumu",true).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(holder.itemView.context, "Seçilen ürün alındı!", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(holder.itemView.context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }



            }else{

                val string = urunList.get(position).urunId
                val alisveris = urunList.get(position).alisverisId
                val kullanici = auth.currentUser?.uid.toString()


                database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(alisveris).collection("Urunler").document(string).update("urunDurumu",false).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(holder.itemView.context, "Seçilen ürün alınmadı!", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(holder.itemView.context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }





            }


        }


    }


    override fun getItemCount(): Int {
        return urunList.size
    }


}
