package com.prin.notes.views

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.prin.notes.R
import com.prin.notes.viewmodels.AuthViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class Login : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bg_balls.setBlur(2)
        key_text.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus)
                key_text.hint=""
            else
                key_text.hint=getString(R.string.key_text)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        activity?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
        val errorObserver = Observer<String>{error ->
            keyError.text=error
        }
        authViewModel.errorLogin.observe(this, errorObserver)

        val successObserver = Observer<Boolean>{success ->
            if(success){
                this.findNavController().navigate(R.id.action_fragment_login_to_notes)
            }
        }
        authViewModel.successLogin.observe(this, successObserver)
    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        rootView.key_text.setOnEditorActionListener { v, actionId, _ ->
            if(actionId == IME_ACTION_DONE) {
                authViewModel.eventLogin(v.text.toString())
            }
            false
        }
        rootView.newPadButton.setOnTouchListener { v, event ->
            val rect = Rect(v.left, v.top, v.right, v.bottom)
            if(event.action == MotionEvent.ACTION_DOWN){
                rootView.newPadButton.setTextColor(getColor(requireContext(), R.color.accentColorTouch))
            }else if(event.action == MotionEvent.ACTION_MOVE){
                if (!rect.contains(v.left + event.x.toInt(), v.top + event.y.toInt())) {
                    rootView.newPadButton.setTextColor(getColor(requireContext(), R.color.accentColor))
                    view?.dispatchTouchEvent(MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_CANCEL, 0f, 0f, 0))
                }
            }else if(event.action == MotionEvent.ACTION_UP){
                this.findNavController().navigate(R.id.action_fragment_login_to_create)
            }
            true
        }
        return rootView
    }
}
