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
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_kayit_ol.*

class KayitOlActivity : AppCompatActivity() {

    var secilenGorsel: Uri? = null
    var secilenBitmap: Bitmap? = null

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kayit_ol)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

    }

    fun createUser(view: View) {

        val kullaniciAdi = createuserNameText.text.toString()
        val kullaniciSoyad = soyadText.text.toString()
        val kullaniciEmail = createEmailText.text.toString()
        val kullaniciSifre = createPasswordText.text.toString()
        val kullaniciSifreTwo = createPasswordTwoText.text.toString()
        val kullanici = kullaniciAdi + " " + kullaniciSoyad



        if (kullaniciAdi != "" && kullaniciEmail != "" && kullaniciSifre != "" && kullaniciSifreTwo != "" && kullaniciSoyad != "" && secilenGorsel != null) {


            if (kullaniciSifreTwo.equals(kullaniciSifre) == false) {
                Toast.makeText(this, "Şifrenizi tekrar giriniz!", Toast.LENGTH_LONG).show()
                return
            }

            auth.createUserWithEmailAndPassword(kullaniciEmail, kullaniciSifre)
                .addOnCompleteListener {
                    if (it.isSuccessful) {



                                veritabaninaKullaniciyiKaydet()
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()


                            }



                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }


        } else {
            Toast.makeText(this, "Boş yer bırakmayınız!", Toast.LENGTH_LONG).show()
        }


    }


    fun gorselEkle(view: View) {


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
                    userimageView.setImageBitmap(secilenBitmap)


                } else {
                    secilenBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                    userimageView.setImageBitmap(secilenBitmap)
                }

            }

        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    fun veritabaninaKullaniciyiKaydet() {

        val uuid = auth.currentUser!!.uid.toString()
        val gorselIsmi = "${uuid}"
        val reference = storage.reference

        val gorselReference = reference.child("Userimages").child(gorselIsmi).child("avatar.jpg")
        if (secilenGorsel != null) {
            gorselReference.putFile(secilenGorsel!!).addOnSuccessListener {
                val yuklenenGorselReference =
                    FirebaseStorage.getInstance().reference.child("Userimages").child(gorselIsmi)
                        .child("avatar.jpg")
                yuklenenGorselReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it
                    val guncelKullaniciAdi = createuserNameText.text.toString() + " " + soyadText.text.toString()

                    val guncelKullanici = auth.currentUser

                    val profilguncellemeIstegi = userProfileChangeRequest {
                        photoUri = downloadUrl
                        displayName = guncelKullaniciAdi
                    }

                    if (guncelKullanici != null){
                        guncelKullanici.updateProfile(profilguncellemeIstegi).addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(this,"Başarıyla kayıt yapıldı.", Toast.LENGTH_LONG).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }


                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }


            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }

        }

    }


}
