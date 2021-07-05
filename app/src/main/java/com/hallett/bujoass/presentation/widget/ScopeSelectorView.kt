package com.hallett.bujoass.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.hallett.bujoass.databinding.WidgetScopeSelectorBinding
import com.hallett.bujoass.presentation.gone
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.visible
import timber.log.Timber
import java.util.*

class ScopeSelectorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {


    private val binding = WidgetScopeSelectorBinding.inflate(LayoutInflater.from(context), this)
    private val scopeOptions = PScope.values().map { context.getString(it.displayName) }

    private var calendar: Calendar = Calendar.getInstance()
    private var onScopeSelected: ((PScopeInstance) -> Unit)? = null

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
                    ) = getScopeFromPosition(position)

                    override fun onNothingSelected(parent: AdapterView<*>?) = getScopeFromPosition(0)
                }
            }
            dateSelector.setOnDateSetListener{ _, year, month, dayOfMonth ->
                calendar.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DATE, dayOfMonth)
                }
                val scopeInstance = PScopeInstance(PScope.values()[scopeSpinner.selectedItemPosition], calendar.time)
                onScopeSelected?.invoke(scopeInstance)
                displayScope(scopeInstance)
            }
        }
    }

    private fun WidgetScopeSelectorBinding.getScopeFromPosition(position: Int) {
        val selectedScope = PScope.values()[position]
        val scopeInstance = PScopeInstance(selectedScope, calendar.time)
        onScopeSelected?.invoke(scopeInstance)
        when(selectedScope) {
            PScope.NONE -> {
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

    fun setOnScopeSelectedListener(newListener: ((PScopeInstance) -> Unit)?) {
        this.onScopeSelected = newListener
    }

    fun displayScope(pScopeInstance: PScopeInstance) {
        binding.scopeSpinner.setSelection(PScope.values().indexOf(pScopeInstance.scope))
        binding.dateSelector.setDefaultDate(pScopeInstance.date)
    }

}