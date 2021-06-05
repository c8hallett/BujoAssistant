package com.hallett.bujoass

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hallett.bujoass.databinding.FragmentNewBinding
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class NewFragment: Fragment() {
    private lateinit var binding: FragmentNewBinding
    private var currentlySelectedDateTime: ZonedDateTime = ZonedDateTime.now()
    private var scope: Scope = Scope.DAY


    enum class Scope {
        DAY,
        WEEK,
        MONTH,
        YEAR,
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
            dateValue.text = currentlySelectedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
            pickScopeSpn.apply {
                adapter = ArrayAdapter.createFromResource(context, R.array.scope_array, android.R.layout.simple_spinner_item)
            }
            pickDateBtn.setOnClickListener {
                DatePickerDialog(requireContext()).apply {
                    setOnDateSetListener { _, year, month, dayOfMonth ->
                        currentlySelectedDateTime = currentlySelectedDateTime.withYear(year)
                        currentlySelectedDateTime = currentlySelectedDateTime.withMonth(month + 1)
                        currentlySelectedDateTime = currentlySelectedDateTime.withDayOfMonth(dayOfMonth)
                        dateValue.text = currentlySelectedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    datePicker.minDate = System.currentTimeMillis()
                }.show()
            }
        }
    }
}