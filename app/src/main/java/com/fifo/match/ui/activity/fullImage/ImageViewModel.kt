package com.fifo.match.ui.activity.fullImage

import androidx.databinding.ObservableField
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor() : BaseViewModel() {

    var stImageView  = ObservableField("")
}