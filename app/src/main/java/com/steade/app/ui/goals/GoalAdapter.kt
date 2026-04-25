package com.steade.app.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steade.app.data.Goal
import com.steade.app.databinding.ItemGoalBinding

class GoalAdapter(
    private var goals: List<Goal>,
    private val onProgress: (Goal) -> Unit,
    private val onEdit: (Goal) -> Unit,
    private val onDelete: (Goal) -> Unit
) : RecyclerView.Adapter<GoalAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = goals[position]
        val b = holder.binding

        b.tvTitle.text = goal.title
        b.tvDescription.text = goal.description ?: ""
        b.tvValues.text = "${goal.current_value.toInt()} / ${goal.target_value.toInt()} ${goal.unit ?: ""}".trim()
        b.progressBar.progress = goal.progress
        b.tvStatus.text = goal.status.replace("-", " ").replaceFirstChar { it.uppercase() }
        b.tvDeadline.text = goal.deadline?.let { "Due: $it" } ?: ""

        b.btnProgress.setOnClickListener { onProgress(goal) }
        b.btnEdit.setOnClickListener { onEdit(goal) }
        b.btnDelete.setOnClickListener { onDelete(goal) }
    }

    override fun getItemCount() = goals.size

    fun update(newList: List<Goal>) {
        goals = newList
        notifyDataSetChanged()
    }
}
