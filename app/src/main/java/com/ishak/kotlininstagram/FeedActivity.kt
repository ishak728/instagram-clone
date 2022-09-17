package com.ishak.kotlininstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ishak.kotlininstagram.databinding.ActivityFeedBinding

class FeedActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var binding: ActivityFeedBinding
    private lateinit var postArrayList:ArrayList<Post>
    private lateinit var feedAdapter: FeedAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=Firebase.auth
        db=Firebase.firestore
        postArrayList=ArrayList<Post>()

        getData()
        feedAdapter= FeedAdapter(postArrayList)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        feedAdapter= FeedAdapter(postArrayList)
        binding.recyclerView.adapter=feedAdapter
    }
    fun getData(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error!=null){
                Toast.makeText(this@FeedActivity,error.localizedMessage, Toast.LENGTH_LONG).show()
            }
            else{
                if(value!=null){
                    if(!value.isEmpty){
                        postArrayList.clear()
                        val documents=value.documents
                        for(document in documents){
                            val comment=document.get("comment") as String
                            val userEmail=document.get("userEmail")as String
                            val downloadUrl=document.get("downloadUrl")as String
                            val post=Post(userEmail,comment,downloadUrl)
                            postArrayList.add(post)
                        }
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)

        return super.onCreateOptionsMenu(menu)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId==R.id.add_post){
            val intent=Intent(this,UploadActivity::class.java)
            startActivity(intent)
        }
        else if(item.itemId==R.id.log_out){
            auth.signOut()
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}