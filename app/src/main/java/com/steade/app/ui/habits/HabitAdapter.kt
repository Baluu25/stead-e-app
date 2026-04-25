package com.steade.app.ui.habits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steade.app.data.Habit
import com.steade.app.databinding.ItemHabitBinding

class HabitAdapter(
    private var habits: List<Habit>,
    private val onComplete: (Habit) -> Unit,
    private val onUndo: (Habit) -> Unit,
    private val onEdit: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val habit = habits[position]
        val b = holder.binding

        b.tvName.text = habit.name
        b.tvCategory.text = habit.category ?: "General"
        b.tvFrequency.text = habit.frequency.replaceFirstChar { it.uppercase() }

        val done = habit.completed_today
        val target = habit.target_count
        b.tvProgress.text = "$done / $target ${habit.unit ?: ""}".trim()
        b.progressBar.max = target
        b.progressBar.progress = done

        val isDone = done >= target
        b.btnComplete.isEnabled = !isDone
        b.btnComplete.alpha = if (isDone) 0.4f else 1f
        b.btnUndo.isEnabled = done > 0
        b.btnUndo.alpha = if (done > 0) 1f else 0.4f

        b.btnComplete.setOnClickListener { onComplete(habit) }
        b.btnUndo.setOnClickListener { onUndo(habit) }
        b.btnEdit.setOnClickListener { onEdit(habit) }
        b.btnDelete.setOnClickListener { onDelete(habit) }
    }

    override fun getItemCount() = habits.size

    fun update(newList: List<Habit>) {
        habits = newList
        notifyDataSetChanged()
    }
}
