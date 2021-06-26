package com.ocak.alisverishatirlaticisi

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.reycler_view_row_alisverisler.view.*

class AlisverisListesiAdapter (var alisverisList : ArrayList<Alisveris>) : RecyclerView.Adapter<AlisverisListesiAdapter.AlisverisListesiViewHolder>(){

   var auth = FirebaseAuth.getInstance()
    var database = FirebaseFirestore.getInstance()

    class AlisverisListesiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlisverisListesiViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view= inflater.inflate(R.layout.reycler_view_row_alisverisler,parent,false)
        return AlisverisListesiAdapter.AlisverisListesiViewHolder(view)

    }

    override fun onBindViewHolder(holder: AlisverisListesiViewHolder, position: Int) {

        holder.itemView.recycler_row_alisveris_ad.text = alisverisList.get(position).alisverisAdi

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AlisverisDetayiActivity::class.java)
            intent.putExtra("ad",alisverisList.get(position).alisverisAdi)
            intent.putExtra("id",alisverisList.get(position).alisverisId)
            holder.itemView.context.startActivity(intent)

        }


        holder.itemView.silAlisverisButonu.setOnClickListener {

            val string = alisverisList.get(position).alisverisId
            val kullanici = auth.currentUser?.uid.toString()

            database.collection("Alisverisler").document(kullanici).collection("Alisverislerim").document(string).delete().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(holder.itemView.context, "Seçilen alışveriş başarıyla silindi", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(holder.itemView.context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }



        }

        holder.itemView.guncelleAlisverisButonu.setOnClickListener {


            val intent = Intent(holder.itemView.context,AlisverisGuncelleActivity::class.java)
            intent.putExtra("id",alisverisList.get(position).alisverisId)
            holder.itemView.context.startActivity(intent)



        }




    }


    override fun getItemCount(): Int {
        return alisverisList.size
    }


}
