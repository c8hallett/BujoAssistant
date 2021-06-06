package com.hallett.bujoass.presentation.widget

import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.hallett.bujoass.databinding.WidgetDateSelectorBinding
import timber.log.Timber

class DateSelectorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null): FrameLayout(context, attrs) {
    private val binding = WidgetDateSelectorBinding.inflate(LayoutInflater.from(context), this)

    init {
        setOnClickListener {
            DatePickerDialog(it.context).apply {
                setOnDateSetListener { v, year, month, dayOfMonth ->
                    Timber.i("Updating view model with $year, $month, $dayOfMonth")
                    dialogListener?.onDateSet(v, year, month, dayOfMonth)
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
}