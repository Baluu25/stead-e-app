package com.steade.app.ui.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steade.app.data.Achievement
import com.steade.app.databinding.ItemAchievementBinding

class AchievementAdapter(private var items: List<Achievement>) :
    RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding

        b.tvName.text = item.name
        b.tvDescription.text = item.description
        b.tvType.text = item.achievement_type
        b.progressBar.max = item.threshold_value
        b.progressBar.progress = item.progress
        b.tvProgress.text = "${item.progress} / ${item.threshold_value}"

        val unlocked = item.unlocked_at != null
        b.tvUnlocked.text = if (unlocked) "Unlocked" else "Locked"
        b.tvUnlocked.alpha = if (unlocked) 1f else 0.4f
        b.root.alpha = if (unlocked) 1f else 0.65f
    }

    override fun getItemCount() = items.size

    fun update(newList: List<Achievement>) {
        items = newList
        notifyDataSetChanged()
    }
}
