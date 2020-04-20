package com.cvte.maxhub.mvvmsample

import android.content.res.Configuration
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
import kotlinx.android.synthetic.main.fragment_set_people_num.*
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

        binding = FragmentSetPeopleNumBinding.inflate(inflater, container, false).apply {
            closeBtn.setOnClickListener {
                viewModel.delete()
                activity?.finish()
            }
            up.setOnClickListener {
                viewModel.saveData()
                NavHostFragment.findNavController(this@SetPeopleNumFragment).navigateUp()
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
        viewModel.votingLive.observe(viewLifecycleOwner) {
            viewModel.initFunctionBean()
            binding.title.text = resources.getText(R.string.setPeopleTitle)
            binding.numberPicker.minValue = 0
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