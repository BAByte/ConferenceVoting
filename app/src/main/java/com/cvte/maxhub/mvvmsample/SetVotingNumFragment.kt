package com.cvte.maxhub.mvvmsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import com.cvte.maxhub.mvvmsample.databinding.FragmentHomeBinding
import com.cvte.maxhub.mvvmsample.databinding.FragmentSetPeopleNumBinding
import com.cvte.maxhub.mvvmsample.databinding.FragmentSetVotingNumBinding
import com.cvte.maxhub.mvvmsample.models.database.AppDatabase
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.cvte.maxhub.mvvmsample.viewmodels.*
import kotlinx.coroutines.*

/**
 * 水果列表和水果详情的展示
 */
class SetVotingNumFragment : Fragment() {
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var binding: FragmentSetVotingNumBinding
    private val viewModel: SetVotingNumViewModel by viewModels {
        SetVotingNumViewModelFactory(
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
        binding = FragmentSetVotingNumBinding.inflate(inflater, container, false).apply {
            closeBtn.setOnClickListener {
                viewModel.delete()
                activity?.finish()
            }
            up.setOnClickListener {
                viewModel.saveData()
                NavHostFragment.findNavController(this@SetVotingNumFragment).popBackStack()
            }
            numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                viewModel.num.postValue(
                    newVal
                )
            }
            next.setOnClickListener {
                viewModel.saveData()
                NavHostFragment.findNavController(this@SetVotingNumFragment)
                    .navigate(R.id.action_setVotingNumFragment_to_setVotingContentFragment)
            }

        }

        subUI()

        return binding.root
    }


    private fun subUI() {
        viewModel.votingLive.observe(viewLifecycleOwner) {
            viewModel.initFunctionBean()
        }
        viewModel.num.observe(viewLifecycleOwner) { value ->
            binding.numberPicker.value = value
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

}