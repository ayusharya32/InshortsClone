package com.appsbyayush.quicknews.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appsbyayush.quicknews.R
import com.appsbyayush.quicknews.databinding.ListItemQueryBinding
import com.appsbyayush.quicknews.models.Query
import com.appsbyayush.quicknews.util.Constants.NO_IMAGE

class QueryAdapter(
    private val queryList: List<Query>,
    private val onItemClickListener: (Query) -> Unit
): RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {

    inner class QueryViewHolder(private val binding: ListItemQueryBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClickListener(queryList[bindingAdapterPosition])
            }
        }

        fun bind(query: Query) {
            binding.apply {
                tvName.text = query.name
                if(query.queryImage == NO_IMAGE) {
                    ivQuery.visibility = View.GONE
                    clQuery.setBackgroundResource(R.drawable.gradient_background)
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.dark_grey_50))


                    val scale = root.context.resources.displayMetrics.density
                    val horizontalPadding = getPaddingInPx(12, scale)
                    val verticalPadding = getPaddingInPx(45, scale)
                    clQuery.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)

                    val params = clQuery.layoutParams
                    params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                    clQuery.layoutParams = params

                    val paramsCard = cardView.layoutParams
                    paramsCard.width = -1
                    cardView.layoutParams = paramsCard
                } else {
                    ivQuery.setImageResource(query.queryImage)
                }
            }
        }

        private fun getPaddingInPx(paddingInDp: Int, scale: Float) = (paddingInDp * scale + 0.5f).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryViewHolder {
        val binding = ListItemQueryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QueryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QueryViewHolder, position: Int) {
        val currentQuery = queryList[position]
        holder.bind(currentQuery)
    }

    override fun getItemCount() = queryList.size
}