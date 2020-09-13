package com.usoof.notesapp.ui.main.notes_recycler

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.usoof.notesapp.data.local.entity.Note
import com.usoof.notesapp.databinding.ItemContainerNoteBinding

class NotesAdapter(
    private val notes: ArrayList<Note>,
    private val clickListener: NoteClickListener
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    companion object {
        const val TAG = "Notes Adapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder =
        NotesViewHolder(ItemContainerNoteBinding.inflate(LayoutInflater.from(parent.context)))

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: notes = ${notes[position]}")
        holder.bind(notes[position])
        holder.itemBinding.layoutContainer.setOnClickListener {
            Log.d(TAG, "onBindViewHolder: clicked note = note @$position")
            clickListener.onNoteClicked(notes[position], position)
        }
    }

    fun addInsertedNote(note: Note) {
        Log.d(TAG, "addInsertedNote: inserting notes")
        notes.add(0, note)
        notifyDataSetChanged()
    }

    fun addUpdatedNote(note: Note, position: Int) {
        Log.d(TAG, "addUpdatedNote: updating notes")
        notes.removeAt(position)
        notes.add(position, note)
        notifyItemChanged(position)
    }

    fun addAllNotes(noteList: List<Note>) {
        notes.addAll(noteList)
        notifyDataSetChanged()
    }

    class NotesViewHolder(val itemBinding: ItemContainerNoteBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(note: Note) {
            itemBinding.containerTitle.text = note.title
            if (note.subtitle.trim().isEmpty()) {
                itemBinding.containerSubtitle.visibility = View.GONE
            } else {
                itemBinding.containerSubtitle.text = note.subtitle
            }
            itemBinding.containerDateTime.text = note.dateTime
            val gradientDrawable = itemBinding.layoutContainer.background as GradientDrawable
            if (note.color.isNotEmpty()) {
                gradientDrawable.setColor(Color.parseColor(note.color))
            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"))
            }

            if (note.imagePath.isNotEmpty()) {
                itemBinding.imageNote.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                itemBinding.imageNote.visibility = View.VISIBLE
            } else {
                itemBinding.imageNote.visibility = View.GONE
            }
        }

    }

}
