package com.fifo.match.ui.activity.completeProfile.fragment

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fifo.match.R
import com.fifo.match.databinding.FragmentProfileFirstBinding
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.CompleteProfileListBean
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.completeProfile.CompleteProfileActivity
import com.fifo.match.ui.activity.completeProfile.CompleteProfileViewModel
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFirstFragment : BaseFragment<CompleteProfileViewModel, FragmentProfileFirstBinding>(
    CompleteProfileViewModel::class.java), OnRangeChangedListener {

    private var isFifo = false

    @Inject
    lateinit var myDataStore: MyDataStore
    private val viewModel: CompleteProfileViewModel by viewModels()
    private var profileListBean: CompleteProfileListBean?=null

    var minAge: Int = 18
    var maxAge: Int = 70

    companion object {
        fun newInstance() = ProfileFirstFragment()
    }

    override fun getLayoutRes() = R.layout.fragment_profile_first

    @SuppressLint("SimpleDateFormat")
    override fun onRender() {
        super.onRender()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.fragment = this
        initRound()
        initListeners()
        binding.seekBarAge.setRange(18F, 70F)
        binding.seekBarAge.setOnRangeChangedListener(this)
        binding.seekBarAge.setProgress(18.0F, 70.0F)

        viewModel.getCompleteDataList()
        registerObservers()

        binding.etDOB.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.add(Calendar.YEAR, -18)
            val dateToday =cal.time
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            val  dateforrow = sdf.format(dateToday)
           datePicker(requireContext(),binding.etDOB,dateforrow, isMinDate = false, isDOB = true)
        }

    }

    private fun initListeners() {
        dataObservers()
        binding.etDesc.doOnTextChanged { text, start, before, count ->
//            if (count <= 22) {
////                toast("working")
//            }else{
//                toast("stop")
//            }
        }

        binding.etWrit.doAfterTextChanged { text ->
            isFifo = text!!.isEmpty()
        }
    }

    fun onFifo(int: Int){
        when(int){
            1->{
                binding.btnYes.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                binding.btnNo.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnYes.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange))
                binding.btnNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
                viewModel.stWorkingFifo.set("yes")
                binding.tvswing.visible()
                binding.etWrit.visible()
                isFifo = true
            }
            2->{
                binding.btnNo.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                binding.btnYes.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange));
                binding.btnYes.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
                viewModel.stWorkingFifo.set("no")
                binding.tvswing.gone()
                binding.etWrit.gone()
                isFifo = false
                viewModel.stSwing.set("")
            }
        }
    }

    private fun initRound() {
        binding.etDOB.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.etStatus.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.etOccupation.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.etEducation.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.etWrit.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.etDesc.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.etFirstName.roundBorderedViewFromResId(15, R.color.light_gray, R.color.light_gray, 1)
        binding.etEmail.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)

    }

    override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
        minAge = leftValue.toInt()
        maxAge = rightValue.toInt()
        if (maxAge < 70)
            binding.hintAgeRange.text = "".plus(leftValue.toInt()).plus(" - ").plus(rightValue.toInt())
        else
            binding.hintAgeRange.text = "".plus(leftValue.toInt()).plus(" - ").plus("70+")

        viewModel.stMinAge.set(leftValue.toString())
        viewModel.stMaxAge.set(rightValue.toString())
    }

    override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}

    override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}

    private fun registerObservers() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    tryCast<CompleteProfileListBean>(response.data?.result) {
                        profileListBean=this
                        profileListBean.let {
                            setSpinnerData(profileListBean!!)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    //requireActivity().toastError(response.exception.localizedMessage!!.toString())
                }

            }
        }

        viewModel.userData.observe(this){
            binding.etFirstName.setText(it?.name?:"")
            Log.d("TAGname", it?.name?:"")
        }
    }

    private fun setSpinnerData(profileListBean: CompleteProfileListBean) {
        val listOfRelation = mutableListOf<String>()
        val listOfOccupationsData = mutableListOf<String>()
        val listOfEducation = mutableListOf<String>()
        profileListBean.relationshipStatusData?.forEach{
            listOfRelation.add(it?.name.toString())
        }

        profileListBean.occupationsData?.forEach{
            listOfOccupationsData.add(it?.name.toString())
        }
        profileListBean.educationData?.forEach{
            listOfEducation.add(it?.name.toString())
        }
        binding.etStatus.item=listOfRelation as List<String>
        binding.etOccupation.item=listOfOccupationsData as List<String>
        binding.etEducation.item=listOfEducation as List<String>

        binding.etStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.stRelationshipStatus.set(profileListBean.relationshipStatusData?.get(position)?.id.toString())
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        binding.etOccupation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.stOccupations.set(profileListBean.occupationsData?.get(position)?.id.toString())
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        binding.etEducation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.stEducation.set(profileListBean.educationData?.get(position)?.id.toString())
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

    }

    fun getProfileFirstData(){
        when {
            viewModel.stEmail.get().isNullOrEmpty() -> {
                requireContext().toastError(getString(R.string.enter_Emal_id))
            }
            binding.etDOB.text.isNullOrEmpty() -> {
                requireContext().toastError(getString(R.string.enter_Date_Of_Birth))
            }
            binding.etStatus.selectedItemPosition < 0 -> {
                requireContext().toastError(getString(R.string.enter_relaction_status))
            }
            binding.etOccupation.selectedItemPosition < 0 -> {
                requireContext().toastError(getString(R.string.enter_occoucton_status))
            }
            binding.etEducation.selectedItemPosition < 0 -> {
                requireContext().toastError(getString(R.string.enter_Education_status))
            }
            viewModel.stWorkingFifo.get().isNullOrEmpty() -> {
                requireContext().toastError(getString(R.string.enter_workingFio))
            }
            isFifo -> {
                if (viewModel.stSwing.get().isNullOrEmpty()) {
                    requireContext().toastError(getString(R.string.enter_swing))
                }
            }
            viewModel.stBio.get().isNullOrEmpty() -> {
                requireContext().toastError(getString(R.string.enter_bioa))
            }
            else -> {
                viewModel.getProfileFirstData(binding.etDOB.text.toString())
            }
        }
    }

    private fun dataObservers() {
        viewModel.responseFirst.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    if (response.data?.status == 200){

                        (activity as CompleteProfileActivity).redirectToSecondFragment()
                    }else{
                    toast(response.data?.message.toString())
                    }
                   lifecycleScope.launch(Dispatchers.IO) {
                     viewModel.userData.value?.profileComplete = 1
                       myDataStore.updateUser(viewModel.userData.value!!)
                   }

                }
                is NetworkResult.Error -> {
                   ShowProgressDialog.hideProgress()
                    requireActivity().toastError(response.exception.localizedMessage!!.toString())
                }

            }
        }
    }

}





