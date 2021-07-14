package com.hallett.bujoass.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hallett.bujoass.databinding.DialogSelectScopeBinding
import com.hallett.bujoass.domain.Scope
import timber.log.Timber

class SelectScopeDialogFragment: BujoAssDialogFragment() {

    // todo: add confirm buttons
    override val isFullScreen: Boolean = false
    private lateinit var binding: DialogSelectScopeBinding
    private var selectedScope: Scope? = null

    companion object {
        const val RETURN_VALUE_SELECTED_SCOPE = "return_value_selected_scope"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSelectScopeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scopeSelectorView.run {
            displayScope(arguments?.getSerializable(ArgumentConstants.ARGS_SELECTED) as? Scope?)
            setOnScopeSelectedListener {
                selectedScope = it
            }
        }
    }

    override fun notifyOnDismissed() {
        Timber.i("Notifying dismiss")
        findNavController().setNavigationResult(RETURN_VALUE_SELECTED_SCOPE, selectedScope)
        onCompleted(selectedScope != null)
    }
}