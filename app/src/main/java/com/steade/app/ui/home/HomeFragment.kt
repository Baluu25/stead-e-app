package com.steade.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.steade.app.R
import com.steade.app.databinding.FragmentHomeBinding
import com.steade.app.network.RetrofitClient
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnViewAchievements.setOnClickListener {
            findNavController().navigate(R.id.achievementsFragment)
        }

        loadDashboard()
    }

    private fun loadDashboard() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getHome()
                if (response.isSuccessful) {
                    val data = response.body() ?: return@launch

                    binding.tvCurrentStreak.text = "${data.current_streak}"
                    binding.tvLongestStreak.text = "${data.longest_streak}"

                    val habits = data.todays_habits ?: emptyList()
                    val done = habits.count { it.is_done }
                    val total = habits.size

                    binding.tvProgressLabel.text = "$done of $total habits done today"
                    binding.progressToday.max = if (total > 0) total else 1
                    binding.progressToday.progress = done

                    val streakViews = listOf(
                        binding.tvDay1, binding.tvDay2, binding.tvDay3,
                        binding.tvDay4, binding.tvDay5, binding.tvDay6, binding.tvDay7
                    )
                    data.streak_days?.forEachIndexed { i, day ->
                        if (i < streakViews.size) {
                            streakViews[i].text = day.label
                            streakViews[i].alpha = if (day.completed) 1f else 0.3f
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load dashboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
