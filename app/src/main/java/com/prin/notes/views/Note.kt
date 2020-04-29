package com.prin.notes.views

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.prin.notes.R
import com.prin.notes.models.NoteData
import com.prin.notes.viewmodels.FilesViewModel
import com.prin.notes.views.NoteArgs
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*


class Note : Fragment() {
    private val filesViewModel: FilesViewModel by activityViewModels()
    private val args: NoteArgs by navArgs()
    private lateinit var note: NoteData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val successEncryptObserver = Observer<Boolean>{
            if(it==true){
                this.findNavController().popBackStack()
                filesViewModel.successEncrypt.value=null
            }
        }

        filesViewModel.successEncrypt.observe(this, successEncryptObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_note, container, false)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                rootView.doneNote.performClick()
            }
        })

        val insets = activity?.window?.decorView?.rootWindowInsets
        val statusBarHeight = insets?.systemWindowInsetTop
        var params = rootView.noteContainer.layoutParams as RelativeLayout.LayoutParams
        params.topMargin = statusBarHeight!!
        rootView.noteContainer.layoutParams = params


        if(args.notePos!=-1){
            note = filesViewModel.eventGetNote(args.notePos)
            rootView.noteTitle.setText(note.title)
            rootView.noteBody.setText(note.body)
        }
        rootView.doneNote.setOnClickListener {
            hideKeyboard(requireActivity())
            val title = noteTitle.text.toString()
            val body = noteBody.text.toString()
            filesViewModel.eventEncryptNote(title, body, args.notePos)
        }
        rootView.noteBody.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                params = rootView.doneNote.layoutParams as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                params.topMargin = params.topMargin + statusBarHeight
                rootView.doneNote.layoutParams = params
            }else{
                params = rootView.doneNote.layoutParams as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.topMargin = params.topMargin - statusBarHeight
                rootView.doneNote.layoutParams = params
            }
        }

        rootView.noteBody.setOnKeyListener { _, keyCode, _ ->
            if(keyCode == KeyEvent.KEYCODE_BACK){
                rootView.noteBody.clearFocus()
            }
            false
        }

        // Inflate the layout for this fragment
        return rootView
    }

}

fun hideKeyboard(activity: Activity) {
    val inputManager: InputMethodManager = activity
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusedView = activity.currentFocus
    if (currentFocusedView != null) {
        inputManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}