package com.cvte.maxhub.mvvmsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import com.cvte.maxhub.mvvmsample.databinding.FragmentSetPeopleNumBinding
import com.cvte.maxhub.mvvmsample.models.database.AppDatabase
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.cvte.maxhub.mvvmsample.viewmodels.SetPeopleNumViewModelFactory
import com.cvte.maxhub.mvvmsample.viewmodels.SetPeopleNumViewModel
import kotlinx.coroutines.*

/**
 * 水果列表和水果详情的展示
 */
class SetPeopleNumFragment : Fragment() {
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var binding: FragmentSetPeopleNumBinding
    private val viewModel: SetPeopleNumViewModel by viewModels {
        SetPeopleNumViewModelFactory(
            VotingRepository.getInstance(
                AppDatabase.getInstance(requireContext()).votingDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        GlobalScope.launch {
            viewModel.initFunctionBean(requireContext())
        }
        binding = FragmentSetPeopleNumBinding.inflate(inflater, container, false).apply {
            closeBtn.setOnClickListener {
                activity?.finish()
            }
            up.setOnClickListener {
                viewModel.saveData()
                NavHostFragment.findNavController(this@SetPeopleNumFragment).popBackStack()
            }
            next.setOnClickListener {
                viewModel.saveData()
                NavHostFragment.findNavController(this@SetPeopleNumFragment)
                    .navigate(R.id.action_setPeopleNumFragment_to_setVotingNumFragment)
            }

            numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                viewModel.num.postValue(
                    newVal
                )
            }
        }

        subUI()

        return binding.root
    }

    private fun subUI() {
        viewModel.num.observe(viewLifecycleOwner) { value ->
            binding.numberPicker.value = value
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

}