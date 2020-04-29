package com.prin.notes.models

import java.util.*

class NoteData {
    private val separator: String = "|{separator}|"
    var noteId: Int
    var title: String
    var body: String
    var dateEdit: Date?
    var dateForView: String

    constructor(noteName: String,
                noteRaw: String,
                dateString: String) {
        val noteNameSplit = noteName.split(" ")
        val noteSplit = noteRaw.split(separator)
        noteId  = noteNameSplit[0].toInt()
        title  = noteSplit[0]
        body = noteSplit[1]
        dateEdit = DateUtils.stringToDate(dateString)
        dateForView = dateString.split("_")[0]
    }

    constructor(noteId: Int,
                title: String,
                body: String,
                dateString: String) {
        this.noteId = noteId
        this.title = title
        this.body = body
        dateEdit = DateUtils.stringToDate(dateString)
        dateForView = dateString.split("_")[0]
    }
}