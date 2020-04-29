package com.prin.notes.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.prin.notes.R
import com.prin.notes.viewmodels.AuthViewModel
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.fragment_create.view.*

class Create : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val errorObserver = Observer<String>{error ->
            newKeyError.text=error
        }
        val successCreateObserver = Observer<Boolean> {
            if(it)
                findNavController().navigate(R.id.action_create_to_notes)
        }

        authViewModel.successCreate.observe(this, successCreateObserver)

        authViewModel.errorNewKey.observe(this, errorObserver)
    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_create, container, false)

        rootView.new_key_text.setOnFocusChangeListener {_, hasFocus ->
            if(hasFocus)
                rootView.new_key_text.hint=""
            else
                rootView.new_key_text.hint="NEW KEY"
        }

        rootView.new_key_text.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                authViewModel.eventInputChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        rootView.new_key_text.setOnEditorActionListener { v, actionId, _ ->
            if(actionId==EditorInfo.IME_ACTION_DONE){
                authViewModel.eventInputSubmit(v.text.toString())
            }
            false
        }

        return rootView
    }

}
