package com.cvte.maxhub.mvvmsample.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.cvte.maxhub.mvvmsample.R
import com.cvte.maxhub.mvvmsample.databinding.ItemVotingContentLayoutBinding
import com.cvte.maxhub.mvvmsample.models.data.VotingContent
import com.iflytek.cloud.*
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.orhanobut.logger.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SetVotingContentAdapter(private val votingContents: MutableList<VotingContent>) :
    RecyclerView.Adapter<SetVotingContentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: ItemVotingContentLayoutBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemVotingContentLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_voting_content_layout,
            parent,
            false
        )

        return ViewHolder(
            binding.root
        ).also { it.binding = binding }
    }

    override fun getItemCount(): Int {
        return votingContents.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = "选项${position + 1}:"
        holder.binding.number = number
        holder.binding.content = votingContents[position]
        holder.binding.voice.setOnClickListener {
            /**
             * 初始化监听器。
             */
            /**
             * 初始化监听器。
             */
            val mInitListener = InitListener { code ->

                if (code != ErrorCode.SUCCESS) {
                    Logger.d("初始化失败，错误码：$code,请点击网址https://www.xfyun.cn/document/error-code查询解决方案")
                }
            }
            val mIatDialog = RecognizerDialog(it.context, mInitListener)
            Logger.d("mIatDialog = $mIatDialog")

            //以下为dialog设置听写参数
            mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
            mIatDialog.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8")
            mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin")


            //开始识别并设置监听器
            mIatDialog.setListener(object : RecognizerDialogListener {
                override fun onResult(p0: RecognizerResult?, p1: Boolean) {
                    if (!p1) {
                        holder.binding.edit.setText(resolvingVoice(p0?.resultString))
                        Logger.d("onResult${p0?.resultString}")
                    }
                }
                override fun onError(p0: SpeechError?) {
                    Logger.d("onError$p0")
                }

            })
            //显示听写对话框
            mIatDialog.show()
        }
    }

    fun resolvingVoice(resultString: String?): String {
        val jsonObject: JSONObject = JSON.parseObject(resultString)
        val jsonArray: JSONArray = jsonObject.getJSONArray("ws")
        val stringBuffer = StringBuffer()
        for (i in 0 until jsonArray.size) {
            val jsonObject1: JSONObject = jsonArray.getJSONObject(i)
            val jsonArray1: JSONArray = jsonObject1.getJSONArray("cw")
            val jsonObject2: JSONObject = jsonArray1.getJSONObject(0)
            val w: String = jsonObject2.getString("w")
            stringBuffer.append(w)
        }
        return stringBuffer.toString()
    }
}