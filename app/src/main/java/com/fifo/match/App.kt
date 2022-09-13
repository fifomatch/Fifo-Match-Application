package com.fifo.match

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.SignupModel
import com.fifo.match.ui.activity.chat.Constant
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@DelicateCoroutinesApi
@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var appDataStore: MyDataStore

    companion object {
        var isChatOpen= false
        var mUserDatabase: DatabaseReference? = null
        var mFirebaseAuth: FirebaseAuth? = null
        var userBean: SignupModel? = null
    }


    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        FirebaseAnalytics.getInstance(this)
        FirebaseApp.initializeApp(this)
//        initFirebase()
        initFirebase()
        flowUserDataDemo()

        val INTERVAL = 30000 //2 minutes
        val mHandler = Handler()
        val mHandlerTask: Runnable = object : Runnable {
            override fun run() {
                updateLiveTime()
                mHandler.postDelayed(this, INTERVAL.toLong())
            }
        }
        mHandlerTask.run()
    }


    private fun flowUserDataDemo() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                userBean = appDataStore.getUser()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateLiveTime(){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val isLoggedIn = appDataStore.isSessionStart()
                val signupModel = appDataStore.getUser()
                if (isLoggedIn) {
                    mUserDatabase = FirebaseDatabase.getInstance().reference.child(Constant.FIREBASE_USER_TABLE).child(signupModel?.firebaseId.toString())
                    mUserDatabase!!.child("timestamp").setValue(ServerValue.TIMESTAMP)
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun initFirebase() {
        try {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val isLoggedIn = appDataStore.isSessionStart()
                    val signupModel = appDataStore.getUser()
                    if (isLoggedIn) {
                        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
                        mFirebaseAuth = FirebaseAuth.getInstance()
                        mUserDatabase = FirebaseDatabase.getInstance().reference.child(Constant.FIREBASE_USER_TABLE).child(signupModel?.firebaseId.toString())
                        mUserDatabase!!.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot != null) {
                                    mUserDatabase!!.child("isOnline").setValue(Constant.USER_ONLINE_STATUS);
                                    mUserDatabase!!.child("timestamp").onDisconnect().setValue(ServerValue.TIMESTAMP);
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        })
                    }
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
        }
    }

}

