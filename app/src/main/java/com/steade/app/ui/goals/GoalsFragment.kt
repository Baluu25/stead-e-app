package com.steade.app.ui.goals

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.steade.app.data.Goal
import com.steade.app.databinding.DialogGoalBinding
import com.steade.app.databinding.FragmentGoalsBinding
import com.steade.app.network.RetrofitClient
import kotlinx.coroutines.launch

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GoalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GoalAdapter(
            emptyList(),
            onProgress = { showProgressDialog(it) },
            onEdit = { showGoalDialog(it) },
            onDelete = { confirmDelete(it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { showGoalDialog(null) }

        loadGoals()
    }

    private fun loadGoals() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getGoals()
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    adapter.update(list)
                    binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load goals", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDelete(goal: Goal) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Goal")
            .setMessage("Delete \"${goal.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        RetrofitClient.api.deleteGoal(goal.id)
                        loadGoals()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error deleting goal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showProgressDialog(goal: Goal) {
        val input = EditText(requireContext()).apply {
            hint = "Amount to add"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Log Progress — ${goal.title}")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val amount = input.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        RetrofitClient.api.logProgress(goal.id, mapOf("amount" to amount))
                        loadGoals()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error logging progress", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showGoalDialog(goal: Goal?) {
        val d = DialogGoalBinding.inflate(layoutInflater)

        goal?.let {
            d.etTitle.setText(it.title)
            d.etDescription.setText(it.description)
            d.etTarget.setText(it.target_value.toInt().toString())
            d.etUnit.setText(it.unit)
            d.etDeadline.setText(it.deadline)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (goal == null) "Add Goal" else "Edit Goal")
            .setView(d.root)
            .setPositiveButton("Save") { _, _ ->
                val title = d.etTitle.text.toString().trim()
                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val body = mutableMapOf<String, Any>("title" to title)
                d.etDescription.text.toString().takeIf { it.isNotEmpty() }?.let { body["description"] = it }
                d.etTarget.text.toString().toDoubleOrNull()?.let { body["target_value"] = it }
                d.etUnit.text.toString().takeIf { it.isNotEmpty() }?.let { body["unit"] = it }
                d.etDeadline.text.toString().takeIf { it.isNotEmpty() }?.let { body["deadline"] = it }

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        if (goal == null) {
                            RetrofitClient.api.createGoal(body)
                        } else {
                            RetrofitClient.api.updateGoal(goal.id, body)
                        }
                        loadGoals()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error saving goal", Toast.LENGTH_SHORT).show()
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
