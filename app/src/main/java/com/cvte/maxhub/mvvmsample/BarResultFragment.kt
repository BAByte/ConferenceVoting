package com.cvte.maxhub.mvvmsample

import android.graphics.Typeface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import com.cvte.maxhub.mvvmsample.databinding.FragmentBarResultBinding
import com.cvte.maxhub.mvvmsample.models.data.FunctionType
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.models.data.VotingContent
import com.cvte.maxhub.mvvmsample.models.database.AppDatabase
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.cvte.maxhub.mvvmsample.utils.MyColorTemplate
import com.cvte.maxhub.mvvmsample.utils.Parties
import com.cvte.maxhub.mvvmsample.utils.SimpleUtils
import com.cvte.maxhub.mvvmsample.viewmodels.ResultViewModel
import com.cvte.maxhub.mvvmsample.viewmodels.ResultViewModelFactory
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import java.util.*


/**
 * 水果列表和水果详情的展示
 */
class BarResultFragment : Fragment() {
    companion object {
        var tfLight: Typeface? = null
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var binding: FragmentBarResultBinding
    private val viewModel: ResultViewModel by viewModels {
        ResultViewModelFactory(
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

        binding = FragmentBarResultBinding.inflate(inflater, container, false)
        tfLight = Typeface.createFromAsset(context?.assets, "OpenSans-Light.ttf")
        subUI()

        binding.apply {
            closeBtn.setOnClickListener {
                context?.let { it1 -> viewModel.deleteVoteFromRemote(it1) }
                activity?.finish()
            }

            cancel.setOnClickListener {
                context?.let { it1 -> viewModel.deleteVoteFromRemote(it1) }
                NavHostFragment.findNavController(this@BarResultFragment)
                    .popBackStack(R.id.homeFragment, false)
            }
        }


        return binding.root
    }

    private fun subUI() {
        viewModel.votingLive.observe(viewLifecycleOwner) {
            if (!viewModel.isStart) {
                Logger.d("subUI : $it")
                context?.let { it1 -> viewModel.InitiateVote(it1,it) }
            }
            binding.join = it.joinQRCode

            //投票不是实时结果并且正在进行投票
            if (!isRealTime(it) && it.status) {
                binding.realtimeTipsLayout.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                val title: String = if (it.peopleNum == 0) {
                    getString(R.string.joined) + it.voted
                } else {
                    getString(R.string.joined)+it.voted+ getString(R.string.not_join)+ (it.peopleNum - it.voted)
                }
                binding.title.text = title
                binding.endVoting.setOnClickListener {
                    context?.let { it1 -> viewModel.endVoteFromRemote(it1) }
                }
                binding.endVoting.setOnClickListener {
                    context?.let { it1 -> viewModel.endVoteFromRemote(it1) }
                }
                return@observe
            }

            binding.realtimeTipsLayout.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
            setData(it.votingContents)
            binding.takeResult = it.downloadQRCode

            uiScope.launch {
                delay(1000)
                val bitmap = SimpleUtils.shotScrollView(binding.scrollView)
                context?.let { it1 ->
                    viewModel.saveResultToLocal(it1, bitmap)
                    viewModel.upLoadResultBitmap(it1)}
            }
        }
        viewModel.isOpenVoteSuccess.observe(viewLifecycleOwner) {
            if (it == viewModel.fail) {
                Toast.makeText(context, getString(R.string.startVoteFail), Toast.LENGTH_SHORT).show()
                NavHostFragment.findNavController(this)
                    .popBackStack(R.id.setVotingContentFragment, false)
            }
        }
    }

    private fun setData(votingContents: List<VotingContent>) {
        val stringBuffer = StringBuffer()
        val start = 1f
        val values = ArrayList<BarEntry>()
        var i = start.toInt()
        while (i < start + votingContents.size) {
            val `val` = votingContents[i - 1].supportNum.toFloat()
            if (Math.random() * 100 < 25) {
                values.add(BarEntry(i.toFloat(), `val`, resources.getDrawable(R.drawable.star)))
            } else {
                values.add(BarEntry(i.toFloat(), `val`))
            }
            stringBuffer.append("${Parties.parties[i - 1 % Parties.parties.size]}: ${votingContents[i - 1].content}  支持人数: ${votingContents[i - 1].peoples.size}")

            if (!isAnonymous(viewModel.votingLive)) {
                val peoplesName = votingContents[i - 1].peoples.map { it.name }
                stringBuffer.append("${getString(R.string.support) }$peoplesName")
            }

            stringBuffer.append("\n\n")

            i++
        }

        binding.result.text = stringBuffer.toString()

        val set1: BarDataSet
        if (binding.chart.data != null &&
            binding.chart.data.dataSetCount > 0
        ) {
            set1 = binding.chart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "The year 2017")
            set1.setDrawIcons(false)
            // add a lot of colors
            val colors: ArrayList<Int> = ArrayList()
            for (c in MyColorTemplate.VORDIPLOM_COLORS) colors.add(c)
            for (c in MyColorTemplate.LIBERTY_COLORS) colors.add(c)
            for (c in MyColorTemplate.PASTEL_COLORS) colors.add(c)
            for (c in MyColorTemplate.COLORFUL_COLORS) colors.add(c)
            for (c in MyColorTemplate.JOYFUL_COLORS) colors.add(c)
            colors.add(ColorTemplate.getHoloBlue())
            set1.colors = colors
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.setValueTypeface(tfLight)
            data.barWidth = 0.9f
            binding.chart.data = data

        }
        binding.chart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        monitor()
    }

    private fun monitor() {
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                // 监听到返回按钮点击事件
                context?.let { viewModel.deleteVoteFromRemote(it) }
                NavHostFragment.findNavController(this)
                    .popBackStack(R.id.homeFragment, false)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false

        }
    }


    private fun isAnonymous(voting: LiveData<Voting>): Boolean {
        voting.value?.let {
            if ((it.type and FunctionType.anonymous.type) != 0)
                return true
        }
        return false
    }

    private fun isRealTime(voting: Voting): Boolean {
        voting.let {
            if ((it.type and FunctionType.realtime.type) != 0)
                return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}