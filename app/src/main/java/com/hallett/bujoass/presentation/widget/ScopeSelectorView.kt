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
import com.hallett.bujoass.domain.ScopeType
import com.hallett.bujoass.presentation.gone
import com.hallett.bujoass.presentation.visible
import java.util.*

class ScopeSelectorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val labelMap: List<Pair<String, ScopeType?>> = listOf(
        Pair(context.getString(R.string.display_scope_none), null),
        Pair(context.getString(R.string.display_scope_day), ScopeType.DAY),
        Pair(context.getString(R.string.display_scope_week), ScopeType.WEEK),
        Pair(context.getString(R.string.display_scope_month), ScopeType.MONTH),
        Pair(context.getString(R.string.display_scope_year), ScopeType.YEAR),

    )
    private val binding = WidgetScopeSelectorBinding.inflate(LayoutInflater.from(context), this)

    private var calendar: Calendar = Calendar.getInstance()
    private var onScopeSelected: ((Scope?) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        binding.run {
            scopeSpinner.apply {
                adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, labelMap.map { it.first })
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
                val scope = when(val scopeType = labelMap[scopeSpinner.selectedItemPosition].second) {
                    null -> null
                    else -> Scope.newInstance(scopeType, calendar.time)
                }

                onScopeSelected?.invoke(scope)
                dateSelector.setDefaultDate(scope?.value ?: Date())
            }
        }
    }

    private fun setScopeToPosition(position: Int) {
        binding.run {
            val scope = when(val scopeType = labelMap[position].second) {
                null -> null
                else -> Scope.newInstance(scopeType, calendar.time)
            }
            onScopeSelected?.invoke(scope)
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
        val selectedPosition = labelMap.indexOfFirst { it.second == scope?.type }
        scopeSpinner.setSelection(selectedPosition)
        dateSelector.setDefaultDate(scope?.value ?: Date())
    }
}