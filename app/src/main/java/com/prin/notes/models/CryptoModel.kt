package com.prin.notes.models

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.prin.notes.MainActivity
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.random.Random.Default.nextBytes

class CryptoModel {
    private val context = MainActivity.getContext()
    private val keyStore: KeyStore? = KeyStore.getInstance("AndroidKeyStore")
    private var appKey: Key?
    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKey = MasterKeys.getOrCreate(keyGenParameterSpec)
    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        "encryptedPreferences",
        masterKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init{
        keyStore?.load(null)
        appKey = keyStore?.getKey("AppKey", null)
        val previouslyStarted = sharedPreferences.getBoolean("previouslyStarted", false)
        if (!previouslyStarted) {
            try{
                createAppKey()
                sharedPreferences.edit().putBoolean("previouslyStarted", true).apply()
            }catch (e: Exception){
                throw e
            }
        }
    }

    @ExperimentalStdlibApi
    fun auth(key: String) : Key? {
        MainActivity.keyHashedString = hashKey(key)
        return keyStore?.getKey(MainActivity.keyHashedString, null)
    }

    @ExperimentalStdlibApi
    fun createKey(key: String) : Key? {
        val keyEncryptedString = hashKey(key)

        val secretKey = auth(key)
        if(secretKey!=null)
            throw Exception("Key not available")

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            keyEncryptedString,
            KeyProperties.PURPOSE_ENCRYPT or  KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(256)
            setUserAuthenticationRequired(true)
        }.build()

        keyGenerator.init(spec)

        try{
            return keyGenerator.generateKey()
        }catch (e: Exception){
            throw e
        }
    }

    @ExperimentalStdlibApi
    private fun hashKey(key: String) : String {
        val saltString = encryptedSharedPreferences.getString("salt", null)
        val saltBytes = Base64.decode(saltString?.toByteArray(Charsets.UTF_8), Base64.NO_PADDING)
        val pbKeySpec = PBEKeySpec(key.toCharArray(), saltBytes, 1324, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyHashBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
        MainActivity.keyHashedString = Base64.encode(keyHashBytes, Base64.NO_PADDING).toString(Charsets.UTF_8)
        return MainActivity.keyHashedString!!
    }

    private fun createAppKey(){
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            "AppKey",
            KeyProperties.PURPOSE_ENCRYPT or  KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(256)
            setUserAuthenticationRequired(false)
        }.build()

        keyGenerator.init(spec)

        try{
            appKey = keyGenerator.generateKey()
            val saltString = Base64.encode(nextBytes(ByteArray(8)), Base64.NO_PADDING).toString(Charsets.UTF_8)
            encryptedSharedPreferences.edit().putString("salt",saltString).apply()

        }catch (e: Exception){
            e.printStackTrace()
            throw e
        }
    }
}