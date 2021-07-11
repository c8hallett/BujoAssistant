package com.hallett.bujoass.presentation.ui.new_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hallett.bujoass.databinding.FragmentAddNewTaskBinding
import com.hallett.bujoass.presentation.model.PresentationResult
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class AddNewTaskFragment: BujoAssFragment() {
    private lateinit var binding: FragmentAddNewTaskBinding

    private val viewModel: AddNewTaskFragmentViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(AddNewTaskFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hookUpViewModelObservers()
        hookupClickListeners()
    }

    private fun hookUpViewModelObservers(){
        lifecycleScope.launch {
            viewModel.observeNewTaskSaved().collect {
                when(it){
                    is PresentationResult.Loading -> {}
                    is PresentationResult.Error -> {
                        Timber.w(it.error, "Couldn't save task for some reason")
                    }
                    is PresentationResult.Success -> findNavController().popBackStack()
                }
            }
        }
        lifecycleScope.launch{
            viewModel.observeNewScopeSelected().collect {
                binding.scopeSelector.displayScope(it)
            }
        }
    }

    private fun hookupClickListeners() {
        binding.run {
            scopeSelector.setOnScopeSelectedListener{ viewModel.onNewScopeSelected(it) }
            saveBtn.setOnClickListener {
                viewModel.saveTask(taskName.text.toString())
            }
            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }
}