package com.hallett.bujoass.presentation.widget

import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.hallett.bujoass.databinding.WidgetDateSelectorBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class DateSelectorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null): FrameLayout(context, attrs) {
    private val binding = WidgetDateSelectorBinding.inflate(LayoutInflater.from(context), this)
    private var defaultDate: Date = Date()

    init {
        setOnClickListener {
            DatePickerDialog(it.context).apply {
                setOnDateSetListener { v, year, month, dayOfMonth ->
                    Timber.i("Updating view model with $year, $month, $dayOfMonth")
                    dialogListener?.onDateSet(v, year, month, dayOfMonth)
                }
                with(Calendar.getInstance()) {
                    time = defaultDate
                    updateDate(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))
                }
                datePicker.minDate = System.currentTimeMillis()
            }.show()
        }
    }

    var text: String
        set(value) {
            binding.dateValue.text = value
        }
        get() = binding.dateValue.text.toString()

    private var dialogListener: DatePickerDialog.OnDateSetListener? = null


    fun setOnDateSetListener(listener: DatePickerDialog.OnDateSetListener){
        dialogListener = listener
    }

    fun setDefaultDate(date: Date) {
        defaultDate = date
        binding.dateValue.text = SimpleDateFormat("MMM dd, YYYY").format(date)
    }
}