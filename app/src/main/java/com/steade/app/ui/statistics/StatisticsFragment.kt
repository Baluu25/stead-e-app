package com.steade.app.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.steade.app.databinding.FragmentStatisticsBinding
import com.steade.app.network.RetrofitClient
import kotlinx.coroutines.launch

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStats()
    }

    private fun loadStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getStatistics()
                if (response.isSuccessful) {
                    val s = response.body() ?: return@launch

                    binding.tvTotalHabits.text = s.total_habits.toString()
                    binding.tvActiveHabits.text = s.active_habits.toString()
                    binding.tvCurrentStreak.text = "${s.current_streak} days"
                    binding.tvLongestStreak.text = "${s.longest_streak} days"
                    binding.tvWeekCompletions.text = s.completions_this_week.toString()

                    binding.tvCategoryBreakdown.text = if (s.category_breakdown.isEmpty()) {
                        "No data yet"
                    } else {
                        s.category_breakdown.entries.joinToString("\n") {
                            "${it.key.replaceFirstChar { c -> c.uppercase() }}: ${it.value}"
                        }
                    }

                    binding.tvWeeklyBreakdown.text = if (s.daily_completions.isEmpty()) {
                        "No data yet"
                    } else {
                        s.daily_completions.entries.joinToString("\n") {
                            "${it.key}: ${it.value} completions"
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load statistics", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
