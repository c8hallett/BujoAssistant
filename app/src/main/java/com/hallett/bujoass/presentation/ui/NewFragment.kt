package com.hallett.bujoass.presentation.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.FragmentNewBinding
import com.hallett.bujoass.presentation.viewmodel.NewFragmentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewFragment: BujoAssFragment() {
    private lateinit var binding: FragmentNewBinding

    private val viewModel: NewFragmentViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(NewFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hookUpViewModelObservers()
        hookupClickListeners()
    }

    private fun hookUpViewModelObservers(){
        binding.run{
            lifecycleScope.launch {
                viewModel.observeSelectedDate().collect{ dateValue.text = it }
            }
            lifecycleScope.launch {
                viewModel.observeScopeOptions().collect { resList ->
                    context?.let { ctx ->
                        val spinnerOptions = resList.map { ctx.getString(it) }
                        pickScopeSpn.adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, spinnerOptions)
                    }
                }
            }
            lifecycleScope.launch {
                viewModel.observeSelectedScopeIndex().collect { selectedIndex ->
                    pickScopeSpn.setSelection(selectedIndex)
                }
            }
            lifecycleScope.launch{
                viewModel.observeShouldShowDate().collect {
                    val vis = if(it) View.VISIBLE else View.GONE
                    scopePreLabel.visibility = vis
                    scopePostLabel.visibility = vis
                    dateBlock.visibility = vis
                }
            }
        }
    }

    private fun hookupClickListeners() {
        binding.run {
            pickScopeSpn.apply {
                onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.selectScope(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        viewModel.selectScope(0)
                    }
                }
            }
            pickDateBtn.setOnClickListener {
                DatePickerDialog(requireContext()).apply {
                    setOnDateSetListener { _, year, month, dayOfMonth ->
                        viewModel.selectDate(year, month, dayOfMonth)
                    }
                    datePicker.minDate = System.currentTimeMillis()
                }.show()
            }
            saveBtn.setOnClickListener {
                viewModel.saveTask(taskName.text.toString())
            }
        }

    }
}