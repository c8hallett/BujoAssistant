package com.hallett.bujoass

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.Toast
import com.hallett.bujoass.databinding.ActivityMainBinding
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class BujoAssActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.launchDatePickerBtn.setOnClickListener {
            DatePickerDialog(this).apply {
                setOnDateSetListener { _, year, month, dayOfMonth ->
                    currentlySelectedDateTime.apply {
                        withYear(year)
                        withMonth(month + 1)
                        withDayOfMonth(dayOfMonth)
                    }
                    Toast.makeText(this@BujoAssActivity, "Currently selected: $currentlySelectedDateTime", Toast.LENGTH_LONG).show()
                }
                datePicker.minDate = System.currentTimeMillis()
            }.show()
        }
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