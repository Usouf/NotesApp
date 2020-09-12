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
) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder =
        NotesViewHolder(ItemContainerNoteBinding.inflate(LayoutInflater.from(parent.context)))

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notes[position])
        holder.itemBinding.layoutContainer.setOnClickListener {
            clickListener.onNoteClicked(notes[position], position)
        }
    }

    fun addInsertedNote(note: Note, position: Int) {

        if (position == 0) {
            notes.add(0, note)
            notifyItemInserted(0)
        } else {
            notes.removeAt(position)
            notes.add(position, note)
            notifyItemChanged(position)
        }

    }

    fun addAllNotes(noteList: List<Note>) {
        notes.addAll(noteList)
        notifyDataSetChanged()
    }

    class NotesViewHolder(val itemBinding: ItemContainerNoteBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        companion object {
            const val TAG = "Notes ViewHolder"
        }

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
                Log.d(TAG, "bind: available ${note.imagePath}")
                itemBinding.imageNote.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                itemBinding.imageNote.visibility = View.VISIBLE
            } else {
                Log.d(TAG, "bind: empty ${note.imagePath}")
                itemBinding.imageNote.visibility = View.GONE
            }
        }

    }

}
