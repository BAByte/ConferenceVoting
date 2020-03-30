package com.cvte.maxhub.mvvmsample.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cvte.maxhub.mvvmsample.R
import com.cvte.maxhub.mvvmsample.databinding.ItemVotingContentLayoutBinding
import com.cvte.maxhub.mvvmsample.models.data.VotingContent

class SetVotingContentAdapter(private val votingContents: MutableList<VotingContent>) :
    RecyclerView.Adapter<SetVotingContentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: ItemVotingContentLayoutBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemVotingContentLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_voting_content_layout,
            parent,
            false
        )

        return ViewHolder(
            binding.root
        ).also { it.binding = binding }
    }

    override fun getItemCount(): Int {
        return votingContents.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = "选项${position + 1}:"
        holder.binding.number = number
        holder.binding.content = votingContents[position]
    }
}