package com.prin.notes.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prin.notes.models.FilesModel
import com.prin.notes.models.NoteData

class FilesViewModel : ViewModel() {
    private val filesModel = FilesModel()

    val notes: MutableLiveData<MutableList<NoteData>> by lazy {
        MutableLiveData<MutableList<NoteData>>(mutableListOf())
    }

    val successEncrypt: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    //Events
    fun eventDeleteNote(notePos: Int){
        filesModel.deleteNote(notes.value!![notePos].noteId)
        notesRemoveAt(notePos)
    }

    fun eventGetNote(notePos : Int) : NoteData {
        return notes.value!![notePos]
    }

    fun eventDecryptNotes() {
        notes.value = filesModel.decryptNotes()
        notes.value = notes.value
    }

    fun eventEncryptNote(title: String, body:String, notePos: Int){
        val titleTrimmed = title.trim()
        val bodyTrimmed = body.trim()

        if(notePos!=-1 &&
            titleTrimmed == notes.value!![notePos].title &&
            bodyTrimmed == notes.value!![notePos].body){
            successEncrypt.value = true
            return
        }

        if(titleTrimmed.isBlank() && bodyTrimmed.isBlank()){
            if(notePos!=-1){
                notesRemoveAt(notePos)
                filesModel.deleteNote(notes.value!![notePos].noteId)
            }
            successEncrypt.value = true
            return
        }

        val newNote = filesModel.encryptNote(titleTrimmed, bodyTrimmed, notePos)
        if(notePos!=-1){
            notesRemoveAt(notePos)
        }
        notes.value?.add(0, newNote)
        notes.value = notes.value

        successEncrypt.value = true
    }

    private fun notesRemoveAt(notePos: Int){
        notes.value?.removeAt(notePos)
        notes.value = notes.value
    }
}