package com.steade.app.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.steade.app.data.Habit
import com.steade.app.databinding.DialogHabitBinding
import com.steade.app.databinding.FragmentHabitsBinding
import com.steade.app.network.RetrofitClient
import kotlinx.coroutines.launch

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HabitAdapter(
            emptyList(),
            onComplete = { complete(it) },
            onUndo = { undo(it) },
            onEdit = { showDialog(it) },
            onDelete = { confirmDelete(it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { showDialog(null) }

        loadHabits()
    }

    private fun loadHabits() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getHabits()
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    adapter.update(list)
                    binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load habits", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun complete(habit: Habit) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                RetrofitClient.api.completeHabit(mapOf("habit_id" to habit.id))
                loadHabits()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error completing habit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun undo(habit: Habit) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                RetrofitClient.api.undoCompletion(habit.id)
                loadHabits()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error undoing completion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDelete(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Delete \"${habit.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        RetrofitClient.api.deleteHabit(habit.id)
                        loadHabits()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error deleting habit", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDialog(habit: Habit?) {
        val d = DialogHabitBinding.inflate(layoutInflater)

        val frequencies = arrayOf("daily", "weekly", "monthly")
        val freqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, frequencies)
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        d.spinnerFrequency.adapter = freqAdapter

        habit?.let {
            d.etName.setText(it.name)
            d.etDescription.setText(it.description)
            d.etCategory.setText(it.category)
            d.etTargetCount.setText(it.target_count.toString())
            d.etUnit.setText(it.unit)
            val idx = frequencies.indexOf(it.frequency)
            if (idx >= 0) d.spinnerFrequency.setSelection(idx)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (habit == null) "Add Habit" else "Edit Habit")
            .setView(d.root)
            .setPositiveButton("Save") { _, _ ->
                val name = d.etName.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val body = mutableMapOf<String, Any>(
                    "name" to name,
                    "frequency" to frequencies[d.spinnerFrequency.selectedItemPosition]
                )
                d.etDescription.text.toString().takeIf { it.isNotEmpty() }?.let { body["description"] = it }
                d.etCategory.text.toString().takeIf { it.isNotEmpty() }?.let { body["category"] = it }
                d.etTargetCount.text.toString().toIntOrNull()?.let { body["target_count"] = it }
                d.etUnit.text.toString().takeIf { it.isNotEmpty() }?.let { body["unit"] = it }

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        if (habit == null) {
                            RetrofitClient.api.createHabit(body)
                        } else {
                            RetrofitClient.api.updateHabit(habit.id, body)
                        }
                        loadHabits()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error saving habit", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
