package com.abg.pyi.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.abg.pyi.MyApp
import com.abg.pyi.adapters.CalendarAdapter
import com.abg.pyi.databinding.ActivityGraphFragmentBinding
import kotlinx.coroutines.launch

class ActivityGraphFragment : Fragment() {

    private var _binding: ActivityGraphFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ActivityGraphViewModel
    private lateinit var adapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityGraphFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity().application as MyApp).repository
        val factory = ActivityGraphViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ActivityGraphViewModel::class.java]

        adapter = CalendarAdapter()
        binding.recyclerCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.recyclerCalendar.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.calendarDays.collect { days ->
                    adapter.submitList(days)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}