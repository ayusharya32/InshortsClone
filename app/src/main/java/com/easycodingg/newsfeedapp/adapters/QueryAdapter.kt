package com.easycodingg.newsfeedapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.easycodingg.newsfeedapp.R
import com.easycodingg.newsfeedapp.databinding.ListItemQueryBinding
import com.easycodingg.newsfeedapp.models.Query
import com.easycodingg.newsfeedapp.util.Constants.NO_IMAGE

class QueryAdapter(
    private val queryList: List<Query>,
    private val onItemClickListener: (Query) -> Unit
): RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {

    inner class QueryViewHolder(private val binding: ListItemQueryBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClickListener(queryList[adapterPosition])
            }
        }

        fun bind(query: Query) {
            binding.apply {
                tvName.text = query.name
                if(query.queryImage == NO_IMAGE) {
                    ivQuery.visibility = View.GONE
                    clQuery.setBackgroundResource(R.drawable.gradient_background)
                    tvName.setTextColor(Color.WHITE)

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