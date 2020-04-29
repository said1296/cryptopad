package com.prin.notes.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prin.notes.models.CryptoModel
import java.lang.Exception

class AuthViewModel : ViewModel() {
    private val cryptoModel = CryptoModel()
    private var firstPass: Boolean = true

    val successCreate: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val successLogin: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val errorLogin: MutableLiveData<String> by lazy {
        MutableLiveData<String>(null)
    }

    val errorNewKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>(null)
    }


    //Events
    @ExperimentalStdlibApi
    fun eventLogin(key: String) {
        if(key.length < 5 || cryptoModel.auth(key)==null){
            errorLogin.value = "Incorrect key"
        }else{
            successLogin.value = true
        }
    }

    fun eventInputChange(key: String){
        if(key.length >= 5 && firstPass)
            firstPass = false

        if(key.length<5 && !firstPass)
            errorNewKey.value="At least 5 characters"
        else if (!firstPass)
            errorNewKey.value=""
    }

    @ExperimentalStdlibApi
    fun eventInputSubmit(key: String){
        firstPass=false
        eventInputChange(key)
        if(errorNewKey.value==""){
            try{
                cryptoModel.createKey(key)
                successCreate.value=true
            }catch (e: Exception){
                e.printStackTrace()
                errorNewKey.value=e.message
            }
        }
    }
}