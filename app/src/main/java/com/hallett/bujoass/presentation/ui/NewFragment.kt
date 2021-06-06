package com.hallett.bujoass.presentation.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.FragmentNewBinding
import com.hallett.bujoass.domain.Scope
import java.text.SimpleDateFormat
import java.util.Calendar

class NewFragment: Fragment() {
    private lateinit var binding: FragmentNewBinding
    private var currentlySelectedDateTime: Calendar = Calendar.getInstance()
    private var scope: Scope = Scope.DAY


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
            dateValue.text = SimpleDateFormat("MMM DD, YYYY").format(currentlySelectedDateTime.time)
            pickScopeSpn.apply {
                adapter = ArrayAdapter.createFromResource(context,
                    R.array.scope_array, android.R.layout.simple_spinner_item)
            }
            pickDateBtn.setOnClickListener {
                DatePickerDialog(requireContext()).apply {
                    setOnDateSetListener { _, year, month, dayOfMonth ->
                        currentlySelectedDateTime.apply{
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DATE, dayOfMonth)
                            dateValue.text = SimpleDateFormat("MMM DD, YYYY").format(currentlySelectedDateTime.time)
                        }
                    }
                    datePicker.minDate = System.currentTimeMillis()
                }.show()
            }
        }
    }
}