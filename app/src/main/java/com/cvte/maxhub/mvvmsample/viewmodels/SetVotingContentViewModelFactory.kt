package com.cvte.maxhub.mvvmsample.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.cvte.maxhub.mvvmsample.module.VotingRepository

/**
 * 保证viewModel在碎片重新创建时可以保持数据
 */

class SetVotingContentViewModelFactory(
    private val repository: VotingRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>)
            = SetVotingContentViewModel(repository) as T
}