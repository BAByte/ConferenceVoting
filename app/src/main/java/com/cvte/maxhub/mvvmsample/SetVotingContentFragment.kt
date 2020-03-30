package com.cvte.maxhub.mvvmsample

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class SetVotingContentFragment : Fragment() {
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var binding: FragmentSetVotingContentBinding
    private var adapter: SetVotingContentAdapter? = null
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
        viewModel.votingLive.observe(viewLifecycleOwner) {
            viewModel.initFunctionBean(it)
        }

        viewModel.votingContents.observe(viewLifecycleOwner) {
            if (adapter == null) {
                adapter = SetVotingContentAdapter(it)
                binding.recyclerView.adapter = adapter
            }else{
                adapter!!.notifyDataSetChanged()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

}