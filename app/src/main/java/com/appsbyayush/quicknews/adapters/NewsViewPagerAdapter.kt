package com.appsbyayush.quicknews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appsbyayush.quicknews.R
import com.appsbyayush.quicknews.databinding.NewsPagerItemBinding
import com.appsbyayush.quicknews.models.Article

class NewsViewPagerAdapter(
    private var articleList: List<Article>,
    private val onBookmarkClickListener: (Article) -> Unit,
    private val onFullNewsClickListener: (Article) -> Unit,
    private val onShareClickListener: (Article) -> Unit
): RecyclerView.Adapter<NewsViewPagerAdapter.NewsViewHolder>() {

    fun submitList(list: List<Article>) {
        articleList = list
    }

    fun currentList() = articleList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsPagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount() = articleList.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentArticle = articleList[position]
        holder.bind(currentArticle)
    }

    inner class NewsViewHolder(private val binding: NewsPagerItemBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.fabBookmark.setOnClickListener {
                onBookmarkClickListener(articleList[bindingAdapterPosition])
            }
            binding.tvFullArticle.setOnClickListener {
                onFullNewsClickListener(articleList[bindingAdapterPosition])
            }
            binding.fabShare.setOnClickListener {
                onShareClickListener(articleList[bindingAdapterPosition])
            }
        }

        fun bind(currentArticle: Article) {
            binding.apply {

                Glide.with(binding.root)
                        .load(currentArticle.imageUrl)
                        .placeholder(R.drawable.dot_placeholder)
                        .centerCrop()
                        .into(ivArticleImage)

                tvArticleTitle.text = currentArticle.title
                tvArticleDescription.text = currentArticle.content

                val source = "Source - ${currentArticle.sourceName}"
                tvArticleSource.text = source

                binding.fabBookmark.setImageResource(
                    when {
                        currentArticle.isBookmarked -> R.drawable.ic_bookmark_marked
                        else -> R.drawable.ic_bookmark_unmarked
                    }
                )
            }
        }
    }

}