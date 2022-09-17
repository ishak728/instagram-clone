package com.ishak.kotlininstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ishak.kotlininstagram.databinding.ActivityUploadBinding
import java.util.*

class UploadActivity : AppCompatActivity() {
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityUploadBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var firestore:FirebaseFirestore
    private lateinit var storage:FirebaseStorage
    var selectedPictureUri:Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLauncher()
        auth=Firebase.auth
        firestore=Firebase.firestore
        storage=Firebase.storage
    }

    fun uploadClicked(view:View){


        val uuid=UUID.randomUUID()
        var imageName="$uuid.jpg"

        val reference=storage.reference
        //val imageReference=reference.child("images/image.jpg")
        val imageReference=reference.child("images").child(imageName)
        if(selectedPictureUri!=null){

            imageReference.putFile(selectedPictureUri!!).addOnSuccessListener{

                val uploadPictureReference=storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {

                    val downloadUrl=it.toString()
                    if(auth.currentUser!=null){

                        val postMap= hashMapOf<String,Any>()
                        postMap.put("downloadUrl",downloadUrl)
                        postMap.put("userEmail",auth.currentUser!!.email!!)
                        postMap.put("comment",binding.commentText.text.toString())
                        postMap.put("date",Timestamp.now())
                        firestore.collection("Posts").add(postMap).addOnSuccessListener {

                            val intent=Intent(this@UploadActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener {

                            Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener{

                Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }
    }

    fun imageClicked(view:View){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"give permission",Snackbar.LENGTH_INDEFINITE).setAction("give"){
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else{
            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }
    fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode== RESULT_OK){
                val intentFromResult=result.data
                if(intentFromResult!=null){
                    selectedPictureUri=intentFromResult.data
                    selectedPictureUri?.let {

                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                Toast.makeText(this@UploadActivity,"permission needed",Toast.LENGTH_LONG).show()
            }
        }
    }
}