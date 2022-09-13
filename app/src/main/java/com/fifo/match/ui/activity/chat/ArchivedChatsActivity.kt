package com.fifo.match.ui.activity.chat

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.fifo.match.R
import com.fifo.match.databinding.LayoutChatFragment1Binding
import com.fifo.match.ui.activity.home.fragment.chat.GroupMetaData


/**
 *
 * @author Tarun Takyar
 * @version 1.0
 * @since 1.0
 */
class ArchivedChatsActivity : BaseBindingActivity() {
    lateinit var mAdapter: ArchivedChatListAdapter
    var mGroupMetaDataList: ArrayList<GroupMetaData>? = null
    var isUpdated = false
    lateinit var mBinding: LayoutChatFragment1Binding

    companion object {
        lateinit var msContext: ArchivedChatsActivity
        fun newInstance() = ArchivedChatsActivity()

    }

    override fun createContentView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_chat_fragment1)
        mBinding.lifecycleOwner = this

        msContext = this

        mGroupMetaDataList = intent.getSerializableExtra("listData") as ArrayList<GroupMetaData>?
        try {
            isUpdated = true
            var mDataList = mGroupMetaDataList!!.filter { it.is_archived == "1"} as ArrayList<GroupMetaData>
            if (mDataList!!.size==0){
                mBinding.recyclerGroupChat1.visibility = View.GONE
                mBinding.txtNoData.visibility = View.VISIBLE
            }else{
                mBinding.recyclerGroupChat1.visibility = View.VISIBLE
                mBinding.txtNoData.visibility = View.GONE
            }

            mGroupMetaDataList = mDataList

//            if (mDataList.isNotEmpty()) {
//                mAdapter.updateDataModel(mDataList!!)
//            }

            updateAdapterData()
        } catch (e: java.lang.Exception) {
        }

        mBinding.imgNotification.setOnClickListener{
            finish()
        }
    }

    override fun createActivityObject() {

    }

    override fun initializeObject() {
    }

    override fun setOnClickListener() {
    }

    fun updateAdapterData() {

        mAdapter = ArchivedChatListAdapter(mGroupMetaDataList, this)
        mBinding.recyclerGroupChat1.layoutManager = LinearLayoutManager(this)
        mBinding.homeAdapter1 = mAdapter
        // binding.recyclerGroupChat.itemAnimator=null
        mAdapter.notifyDataSetChanged()


    }
    override fun onDestroy() {
        super.onDestroy()
    }
}