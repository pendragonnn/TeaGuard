package com.example.teaguard.foundation.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.teaguard.data.remote.response.DiseaseDetailResponseItem
import com.example.teaguard.databinding.ItemDiseaseBinding

class DiseaseDetailAdapter : ListAdapter<DiseaseDetailResponseItem, DiseaseDetailAdapter.ViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: DiseaseDetailAdapter.OnItemClickCallback
    inner class ViewHolder(private val binding: ItemDiseaseBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DiseaseDetailResponseItem) {
            binding.apply {
                val baseUrl = "http://100.87.136.13:5000"
                val imageUrl = baseUrl + data.diseaseImgPreview
                Log.d("DiseaseDetailAdapter", "bind: $imageUrl")
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .into(imgDisease)
                titleDisease.text = data.diseaseName
                descDisease.text = data.diseaseExplanation
            }
        }
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: DiseaseDetailResponseItem)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DiseaseDetailResponseItem>() {
            override fun areItemsTheSame(oldItem: DiseaseDetailResponseItem, newItem: DiseaseDetailResponseItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DiseaseDetailResponseItem, newItem: DiseaseDetailResponseItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diseaseDetail = getItem(position)
        holder.bind(diseaseDetail)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(diseaseDetail)
        }
    }
}