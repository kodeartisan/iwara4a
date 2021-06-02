package com.rerere.iwara4a.ui.page.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.event.LoginEvent
import com.rerere.iwara4a.util.registerListener
import com.rerere.iwara4a.util.unregisterListener
import dagger.hilt.android.lifecycle.HiltViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(

) : ViewModel() {

}