package com.hallett.bujoass.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.annotation.StringRes
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.WidgetScopeSelectorBinding
import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.presentation.gone
import com.hallett.bujoass.presentation.visible
import java.util.*

class ScopeSelectorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private enum class DisplayScope(@StringRes val displayName: Int) {
        NONE(R.string.display_scope_none),
        DAY(R.string.display_scope_day),
        WEEK(R.string.display_scope_week),
        MONTH(R.string.display_scope_month),
        YEAR(R.string.display_scope_year);
        companion object{
            fun fromScope(scope: Scope?): DisplayScope = when(scope) {
                null -> NONE
                is Scope.Day -> DAY
                is Scope.Week -> WEEK
                is Scope.Month -> MONTH
                is Scope.Year -> YEAR
            }

            fun toScope(position: Int, date: Date): Scope? = when(values()[position]){
                NONE -> null
                DAY -> Scope.Day(date)
                WEEK -> Scope.Week(date)
                MONTH -> Scope.Month(date)
                YEAR -> Scope.Year(date)
            }
        }
    }

    private val binding = WidgetScopeSelectorBinding.inflate(LayoutInflater.from(context), this)
    private val scopeOptions = DisplayScope.values().map { context.getString(it.displayName) }

    private var calendar: Calendar = Calendar.getInstance()
    private var onScopeSelected: ((Scope?) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        binding.run {
            scopeSpinner.apply {
                adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, scopeOptions)
                onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) = setScopeToPosition(position)

                    override fun onNothingSelected(parent: AdapterView<*>?) = setScopeToPosition(0)
                }
            }
            dateSelector.setOnDateSetListener{ _, year, month, dayOfMonth ->
                calendar.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DATE, dayOfMonth)
                }
                val scope = DisplayScope.toScope(scopeSpinner.selectedItemPosition, calendar.time)
                onScopeSelected?.invoke(scope)
                dateSelector.setDefaultDate(scope?.value ?: Date())
            }
        }
    }

    private fun setScopeToPosition(position: Int) {
        val scope = DisplayScope.toScope(position, calendar.time)
        onScopeSelected?.invoke(scope)
        binding.run {
            when(scope) {
                null -> {
                    scopePreLabel.gone()
                    scopePostLabel.gone()
                    dateSelector.gone()
                }
                else -> {
                    scopePreLabel.visible()
                    scopePostLabel.visible()
                    dateSelector.visible()
                }
            }
        }
    }

    fun setOnScopeSelectedListener(newListener: ((Scope?) -> Unit)?) {
        this.onScopeSelected = newListener
    }

    fun displayScope(scope: Scope?) = binding.run{
        val selectedPosition = DisplayScope.values().indexOf(DisplayScope.fromScope(scope))
        scopeSpinner.setSelection(selectedPosition)
        dateSelector.setDefaultDate(scope?.value ?: Date())
    }
}