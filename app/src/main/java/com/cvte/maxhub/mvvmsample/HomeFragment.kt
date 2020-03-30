package com.cvte.maxhub.mvvmsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.cvte.maxhub.mvvmsample.databinding.FragmentHomeBinding
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.cvte.maxhub.mvvmsample.models.database.AppDatabase
import com.cvte.maxhub.mvvmsample.viewmodels.HomeViewModel
import com.cvte.maxhub.mvvmsample.viewmodels.HomeViewModelFactory
import androidx.lifecycle.observe
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*

/**
 * 水果列表和水果详情的展示
 */
class HomeFragment : Fragment() {
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
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

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        subUI()

        binding.apply {
            closeBtn.setOnClickListener {
                viewModel.delete()
                activity?.finish()
            }

            next.setOnClickListener {
                viewModel.saveData()
                NavHostFragment.findNavController(this@HomeFragment)
                    .navigate(R.id.action_homeFragment_to_setPeopleNumFragment)
            }
        }


        return binding.root
    }

    private fun subUI() {
        viewModel.votingLive.observe(viewLifecycleOwner) {
            viewModel.initFunctionBean(requireContext())
        }
        viewModel.realtime.observe(viewLifecycleOwner) {
            binding.realtime.run {
                data = viewModel.realtime.value
                setSelectFunctionClick(root, this.checkBox)
            }
        }
        viewModel.anonymous.observe(viewLifecycleOwner) {
            binding.anonymous.run {
                data = viewModel.anonymous.value
                setSelectFunctionClick(root, this.checkBox)
            }
        }
        viewModel.location.observe(viewLifecycleOwner) {
            binding.location.run {
                data = viewModel.location.value
                setSelectFunctionClick(root, this.checkBox)
            }
        }
    }


    private fun setSelectFunctionClick(root: View, checkBox: CheckBox) {
        root.setOnClickListener {
            checkBox.run {
                isChecked = !isChecked
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

}