package com.prin.notes.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.LAYER_TYPE_HARDWARE
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prin.notes.MainActivity
import com.prin.notes.R
import com.prin.notes.views.NotesDirections
import com.prin.notes.models.NoteData
import com.prin.notes.viewhandlers.RecyclerViewSwiperCallback
import com.prin.notes.viewmodels.FilesViewModel
import kotlinx.android.synthetic.main.card_note.view.*
import kotlinx.android.synthetic.main.fragment_notes.view.*


class Notes : Fragment() {
    private val filesViewModel: FilesViewModel by activityViewModels()
    private var recyclerViewAdapter =
        RecyclerViewAdapter(mutableListOf())
    private var isFirstEntry = true
    private lateinit var recyclerViewSwiperCallback: RecyclerViewSwiperCallback
    private lateinit var recyclerViewSwiper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerViewSwiperCallback =
            RecyclerViewSwiperCallback(
                requireActivity()
            ) {
                filesViewModel.eventDeleteNote(it)
            }
        recyclerViewSwiper = ItemTouchHelper(recyclerViewSwiperCallback)
        val notesObserver = Observer<List<NoteData>>{ notesUpdated ->
            recyclerViewAdapter.setData(notesUpdated)
            if(notesUpdated.isNullOrEmpty() && isFirstEntry){
                this.findNavController().navigate(R.id.action_notes_to_note)
            }
            isFirstEntry=false
        }
        filesViewModel.notes.observe(this, notesObserver)

        activity?.window?.apply {
            navigationBarColor = Color.BLACK
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }

        filesViewModel.eventDecryptNotes()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_notes, container, false)
        val insets = activity?.window?.decorView?.rootWindowInsets
        val statusBarHeight = insets?.systemWindowInsetTop
        val params = rootView.notesContainer.layoutParams as FrameLayout.LayoutParams
        params.topMargin = statusBarHeight!!
        rootView.notesContainer.layoutParams = params

        rootView.notesRecycler.setLayerType(LAYER_TYPE_HARDWARE, null)

        rootView.notesRecycler.adapter = recyclerViewAdapter
        rootView.notesRecycler.layoutManager = LinearLayoutManager(this.context)
        rootView.notesRecycler.setHasFixedSize(true)
        recyclerViewSwiper.attachToRecyclerView(rootView.notesRecycler)

        rootView.addNote.setOnClickListener {
            this.findNavController().navigate(R.id.action_notes_to_note)
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity!!.finish()
            }
        })
        // Inflate the layout for this fragment
        return rootView
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            0 -> this.findNavController().navigate(
                NotesDirections.actionNotesToNote(
                    item.groupId
                )
            )
            1 -> filesViewModel.eventDeleteNote(item.groupId)
        }
        return super.onContextItemSelected(item)
    }

}

class RecyclerViewAdapter (var notes: List<NoteData>) : RecyclerView.Adapter<RecyclerViewAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
        val titleView: TextView = itemView.note_title
        val bodyView: TextView = itemView.note_body
        val dateEditView: TextView = itemView.note_date_edit

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo?) {
                menu?.add(adapterPosition, 0, 0, "Edit")
                menu?.add(adapterPosition, 1, 0, "Delete")
        }

    }

    fun setData(notesUpdated: List<NoteData>) {
        notes = notesUpdated
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notes[position]
        if(currentNote.title.isBlank()){
            holder.titleView.visibility = View.GONE
        }else holder.titleView.text = currentNote.title

        holder.bodyView.text = currentNote.body
        holder.dateEditView.text = currentNote.dateForView
        holder.itemView.setOnClickListener {
            val action =
                NotesDirections.actionNotesToNote(
                    position
                )
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int = notes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_note,
            parent, false)
        return NoteViewHolder(
            itemView
        )
    }
}
