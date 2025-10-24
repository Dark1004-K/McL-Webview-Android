package com.bdgen.mcwebview.util

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.integration.android.AndroidKeystoreKmsClient
import kotlinx.coroutines.flow.first

object McPreference {
    private  lateinit var aead : Aead
    const val KEYSET_NAME = "__mcl_key_set__"
    const val PERF_KEY_FILE_NAME = "mcl_shared_pref_keys"
    const val PERF_FILE_NAME = "mcl_shared_prefs"
    const val MASTER_KEY_URL = "${AndroidKeystoreKmsClient.PREFIX}mcl_master_key"

    val Context.dataStore by preferencesDataStore(PERF_FILE_NAME)

    fun init(context: Context) {
        AeadConfig.register()

        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PERF_KEY_FILE_NAME)
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri(MASTER_KEY_URL)
            .build()
            .keysetHandle

        aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    fun encrypt(plainText: String): String {
        val encrypted = aead.encrypt(plainText.toByteArray(), null)
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(cipherText: String): String {
        val decrypted = aead.decrypt(Base64.decode(cipherText, Base64.DEFAULT), null)
        return String(decrypted)
    }

    suspend fun write(context: Context, key:String, value: String) {
        val encrypted = this.encrypt(value)
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = encrypted
        }
    }

    suspend fun read(context: Context, key:String) : String {
        val prefs = context.dataStore.data.first() // 최신 값 (한 key에 하나의 value만 저장 가능하므로, 최신 값을 꺼내와도 무관함.
        return prefs[stringPreferencesKey(key)]?.let { this.decrypt(it)} ?: ""
    }
}