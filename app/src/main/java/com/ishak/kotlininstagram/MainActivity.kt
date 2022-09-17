package com.ishak.kotlininstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ishak.kotlininstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //auth=FirebaseAuth.getInstance() iki şekilde yazılabilir
        auth = Firebase.auth

        val curenUser=auth.currentUser
        if(curenUser!=null){
            val intent=Intent(this@MainActivity,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    fun signInClicked(view:View){
        var email=binding.txtEmail.text.toString()
        var password=binding.txtPassword.text.toString()
        if(email.equals("")||password.equals("")){
            Toast.makeText(this,"enter e-mail or password",Toast.LENGTH_LONG).show()

        }
        else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {

                val intent=Intent(this@MainActivity,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {


                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }


    fun signUpClicked(view:View){

        var email=binding.txtEmail.text.toString()
        var password=binding.txtPassword.text.toString()

        if(email.equals("")||password.equals("")){
            Toast.makeText(this,"enter e-mail or password",Toast.LENGTH_LONG).show()

        }
        else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {

                val intent=Intent(this@MainActivity,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {

                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }
}