package com.cvte.maxhub.mvvmsample

import android.graphics.Color
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
import com.cvte.maxhub.mvvmsample.databinding.FragmentPieResultBinding
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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*


/**
 * 水果列表和水果详情的展示
 */
class PieResultFragment : Fragment() {
    companion object {
        var tfLight: Typeface? = null
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var binding: FragmentPieResultBinding
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

        binding = FragmentPieResultBinding.inflate(inflater, container, false)
        tfLight = Typeface.createFromAsset(context?.assets, "OpenSans-Light.ttf")
        subUI()

        binding.apply {
            closeBtn.setOnClickListener {
                context?.let { it1 -> viewModel.deleteVoteFromRemote(it1) }
                activity?.finish()
            }

            cancel.setOnClickListener {
                context?.let { it1 -> viewModel.deleteVoteFromRemote(it1) }
                NavHostFragment.findNavController(this@PieResultFragment)
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
                    viewModel.upLoadResultBitmap(it1)
                }
            }
        }

        viewModel.isOpenVoteSuccess.observe(viewLifecycleOwner) {
            if (it == viewModel.fail) {
                Toast.makeText(context, getString(R.string.startVoteFail), Toast.LENGTH_SHORT).show()
                NavHostFragment.findNavController(this@PieResultFragment)
                    .popBackStack(R.id.setVotingContentFragment, false)
            }
        }
    }


    private fun setData(votingContents: List<VotingContent>) {
        val entries: ArrayList<PieEntry> = ArrayList()
        val stringBuffer = StringBuffer()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (i in votingContents.indices) {
            if (votingContents[i].supportNum != 0) {
                entries.add(
                    PieEntry(
                        (votingContents[i].supportNum.toFloat()),
                        Parties.parties[i % Parties.parties.size],
                        resources.getDrawable(R.drawable.star, null)
                    )
                )
            }

            stringBuffer.append("${Parties.parties[i % Parties.parties.size]}: ${votingContents[i].content}  支持人数: ${votingContents[i].peoples.size}")

            if (!isAnonymous(viewModel.votingLive)) {
                val peoplesName = votingContents[i].peoples.map { it.name }
                stringBuffer.append("${getString(R.string.support) }$peoplesName")
            }

            stringBuffer.append("\n\n")
        }

        binding.result.text = stringBuffer.toString()

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 10f

        // add a lot of colors
        val colors: ArrayList<Int> = ArrayList()
        for (c in MyColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in MyColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in MyColorTemplate.PASTEL_COLORS) colors.add(c)
        for (c in MyColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in MyColorTemplate.JOYFUL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.chart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setValueTypeface(tfLight)
        binding.chart.data = data

        // undo all highlights
        binding.chart.highlightValues(null)
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
                NavHostFragment.findNavController(this@PieResultFragment)
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