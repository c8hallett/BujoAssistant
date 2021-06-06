package com.hallett.bujoass.presentation.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.FragmentNewBinding
import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.presentation.viewmodel.NewFragmentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewFragment: BujoAssFragment() {
    private lateinit var binding: FragmentNewBinding;

    private val viewModel: NewFragmentViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(NewFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run{
            lifecycleScope.launch {
                viewModel.observeSelectedDate().collect{ dateValue.text = it }
            }
            pickScopeSpn.apply {
                adapter = ArrayAdapter.createFromResource(context,
                    R.array.scope_array, android.R.layout.simple_spinner_item)
            }
            pickDateBtn.setOnClickListener {
                DatePickerDialog(requireContext()).apply {
                    setOnDateSetListener { _, year, month, dayOfMonth ->
                        viewModel.updateSelectedTime(year, month, dayOfMonth)
                    }
                    datePicker.minDate = System.currentTimeMillis()
                }.show()
            }
        }
    }
}