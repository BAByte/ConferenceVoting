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
import com.cvte.maxhub.mvvmsample.adapters.SetVotingContentAdapter
import com.cvte.maxhub.mvvmsample.databinding.FragmentSetVotingContentBinding
import com.cvte.maxhub.mvvmsample.models.database.AppDatabase
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.cvte.maxhub.mvvmsample.viewmodels.SetVotingContentViewModel
import com.cvte.maxhub.mvvmsample.viewmodels.SetVotingContentViewModelFactory
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class SetVotingContentFragment : Fragment() {
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var binding: FragmentSetVotingContentBinding
    private var adapter: SetVotingContentAdapter? = null
    private val maxVotingNum = 6
    private val viewModel: SetVotingContentViewModel by viewModels {
        SetVotingContentViewModelFactory(
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

        binding = FragmentSetVotingContentBinding.inflate(inflater, container, false).apply {
            closeBtn.setOnClickListener {
                viewModel.delete()
                activity?.finish()
            }
            up.setOnClickListener {
                viewModel.saveData()
                NavHostFragment.findNavController(this@SetVotingContentFragment).popBackStack()
            }
        }

        subUI()

        return binding.root
    }


    private fun subUI() {
        viewModel.votingLive.observe(viewLifecycleOwner) { voting ->
            viewModel.initFunctionBean(voting)
            binding.next.setOnClickListener {
                viewModel.saveData()
                if (voting.votingContents.size < maxVotingNum) {
                    NavHostFragment
                        .findNavController(this@SetVotingContentFragment)
                        .navigate(R.id.action_setVotingContentFragment_to_PieResultFragment)
                }else {
                    NavHostFragment
                        .findNavController(this@SetVotingContentFragment)
                        .navigate(R.id.action_setVotingContentFragment_to_barResultFragment)
                }
            }
        }

        viewModel.votingContents.observe(viewLifecycleOwner) {
            Logger.d("subUI $it")
            if (adapter == null) {
                adapter = SetVotingContentAdapter(it)
                binding.recyclerView.adapter = adapter
            } else {
                binding.recyclerView.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.saveData()
    }
    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

}