package com.prin.notes.models

import android.security.keystore.KeyProperties
import android.util.Base64
import com.prin.notes.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class FilesModel {
    private val context = MainActivity.getContext()
    private val keyHashedString = MainActivity.keyHashedString
    private val rootDir = File(context.filesDir, "notes")
    private val userDir = File(rootDir, keyHashedString)
    private val separator: String = "|{separator}|"
    private var appKey: Key?
    private val keyStore: KeyStore? = KeyStore.getInstance("AndroidKeyStore")

    init{
        keyStore?.load(null)
        appKey = keyStore?.getKey("AppKey", null)
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
        if (!userDir.exists()) {
            userDir.mkdirs()
        }
    }

    fun decryptNotes() : MutableList<NoteData> {
        val userFiles = userDir.listFiles()
        lateinit var noteDecrypted: NoteData
        val notesDecrypted: MutableList<NoteData> = mutableListOf()
        if(userFiles.isNullOrEmpty()){
            return mutableListOf()
        }


        for (f in userFiles) {
            val name = f.name
            val nameSplit = name.split(" ")
            val dateEditString = nameSplit[2].split(".")[0]

            var ivString = nameSplit[1]
            ivString = ivString.replace("___", "/").replace("+_+_", "\\").replace("-_-_", ".")
            val iv = GCMParameterSpec(128, Base64.decode(ivString.toByteArray(Charsets.UTF_8), Base64.NO_PADDING))
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(KeyProperties.PURPOSE_DECRYPT, appKey, iv)
            val noteBytes = cipher.doFinal(f.readBytes())
            val note = Base64.decode(noteBytes, Base64.NO_PADDING).toString(Charsets.UTF_8)

            noteDecrypted =
                NoteData(name, note, dateEditString)
            notesDecrypted.add(noteDecrypted)
        }
        notesDecrypted.sortByDescending {
            it.dateEdit
        }

        return notesDecrypted
    }


    fun deleteNote(noteId: Int){
        for(f in userDir.listFiles()){
            if(f.name.startsWith(noteId.toString())){
                f.delete()
                break
            }
        }
    }

    fun encryptNote(title: String, body: String, noteId: Int) : NoteData {
        val numFile =
            if(noteId==-1){
                val userFiles = userDir.listFiles()
                val numFiles = userFiles.size
                numFiles
            }else{
                deleteNote(noteId)
                noteId
            }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(KeyProperties.PURPOSE_ENCRYPT, appKey)
        val textToEncrypt = "$title$separator$body"
        val textToEncrptyBytes = Base64.encode(textToEncrypt.toByteArray(), Base64.NO_PADDING)
        val cipherText = cipher.doFinal(textToEncrptyBytes)
        var ivString = Base64.encode(cipher.iv, Base64.NO_PADDING).toString(Charsets.UTF_8)
        ivString = ivString.replace("/", "___").replace("\\", "+_+_").replace(".", "-_-_")
        val dateEditString =
            DateUtils.getCurrentDateTimeAsString()
        val saveFile = File(userDir, "$numFile $ivString $dateEditString.txt")
        val outputStream = FileOutputStream(saveFile)
        outputStream.write(cipherText)
        outputStream.close()

        return NoteData(
            numFile,
            title,
            body,
            dateEditString
        )
    }


}