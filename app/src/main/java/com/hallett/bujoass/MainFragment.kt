package com.hallett.bujoass

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hallett.bujoass.databinding.FragmentMainBinding
import java.time.ZonedDateTime

class MainFragment: Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.launchDatePickerBtn.setOnClickListener {
            context?.let { ctx ->
                DatePickerDialog(ctx).apply {
                    setOnDateSetListener { _, year, month, dayOfMonth ->
                        currentlySelectedDateTime.apply {
                            withYear(year)
                            withMonth(month + 1)
                            withDayOfMonth(dayOfMonth)
                        }

                        Toast.makeText(ctx, "Currently selected: $currentlySelectedDateTime", Toast.LENGTH_LONG).show()
                    }
                    datePicker.minDate = System.currentTimeMillis()
                }.show()

            }
        }
        return binding.root
    }


    enum class Scope {
        DAY,
        WEEK,
        MONTH,
        YEAR,
    }

    private var currentlySelectedDateTime: ZonedDateTime = ZonedDateTime.now()

    private var scope: Scope = Scope.DAY
}