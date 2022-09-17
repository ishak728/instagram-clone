package com.ishak.kotlininstagram

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ishak.kotlininstagram.databinding.RecyclerRowBinding
import com.squareup.picasso.Picasso

class FeedAdapter(val postList:ArrayList<Post>):RecyclerView.Adapter<FeedAdapter.PostHolder>() {
    class PostHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerViewEmail.text=postList.get(position).email
        holder.binding.recyclerComment.text=postList.get(position).comment
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImage)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}