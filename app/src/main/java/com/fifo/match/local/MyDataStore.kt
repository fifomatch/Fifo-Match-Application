package com.fifo.match.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.clear
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.fifo.match.model.SignupModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDataStore @Inject constructor(private val context: Context) {

    private var dataStore: DataStore<Preferences>?=null

    companion object{
        private const val PREF_NAME="fifo"
        const val USER_DATA="user_data"
        const val isLoggedin="isLogin"
    }

    init {
        dataStore= context.createDataStore(name= PREF_NAME)
    }

   suspend fun putString(key:String,value:String) {
        val dataStoreKey= preferencesKey<String>(key)
        dataStore?.edit {
            settings->
            settings[dataStoreKey]=value
        }
    }

    suspend fun getString(key: String):String {
        val dataStoreKey= preferencesKey<String>(key)
        val preferences=dataStore?.data?.first()
        return preferences?.get(dataStoreKey)?:""
    }

    suspend fun putBoolean(key: String,value: Boolean) {
        val dataStoreKey= preferencesKey<Boolean>(key)
        dataStore?.edit { settings->
            settings[dataStoreKey]=value
        }
    }

    suspend fun getBoolean(key: String):Boolean {
        val dataStoreKey= preferencesKey<Boolean>(key)
        val preferences=dataStore?.data?.first()
        return preferences?.get(dataStoreKey)?:false
    }

    suspend fun putInt(key:String,value:Int) {
        val dataStoreKey= preferencesKey<Int>(key)
        dataStore?.edit {
                settings->
            settings[dataStoreKey]=value
        }
    }

    suspend fun getInt(key: String):Int {
        val dataStoreKey= preferencesKey<Int>(key)
        val preferences=dataStore?.data?.first()
        return preferences?.get(dataStoreKey)?:0
    }

    suspend fun saveUser(signupBean: SignupModel) {
        val jsonString=Gson().toJson(signupBean)
        val preferenceKey= preferencesKey<String>(USER_DATA)
        dataStore?.edit {
            it[preferenceKey]=jsonString

        }
    }

    suspend fun updateUser(signupBean: SignupModel) {
        val jsonString= Gson().toJson(signupBean)
        val preferenceKey= preferencesKey<String>(USER_DATA)
        dataStore?.edit {
            it[preferenceKey]=jsonString

        }
    }

    /*suspend fun getUser():SignupModel? {
        val dataStoreKey= preferencesKey<String>(USER_DATA)
        val preferences=dataStore?.data?.first()
        var signUpModel:SignupModel?=null
        preferences?.get(dataStoreKey)?.let {
            signUpModel= Gson().fromJson<SignupModel>(it,SignupModel::class.java)
        }
        return signUpModel
    }*/

    suspend fun getUser():SignupModel? {
        val dataStoreKey= preferencesKey<String>(USER_DATA)
        val preferences=dataStore?.data?.first()
        var signUpModel:SignupModel?=null
        preferences?.get(dataStoreKey)?.let {
            signUpModel= Gson().fromJson<SignupModel>(it,SignupModel::class.java)
        }
        return signUpModel
    }

    suspend fun startSession() {
        val dataStoreKey= preferencesKey<Boolean>(isLoggedin)
        dataStore?.edit { settings->
            settings[dataStoreKey]=true
        }
    }

    suspend fun isSessionStart():Boolean {
        val dataStoreKey= preferencesKey<Boolean>(isLoggedin)
        val preferences=dataStore?.data?.first()

        return preferences?.get(dataStoreKey)?:false
    }

    suspend fun clearSession() {
        val dataStoreKey= preferencesKey<Boolean>(isLoggedin)
        dataStore?.edit { settings->
//            settings[dataStoreKey]=true
           settings.clear()
        }
    }

}