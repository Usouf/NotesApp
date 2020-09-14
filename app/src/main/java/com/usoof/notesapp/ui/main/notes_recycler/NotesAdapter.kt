package com.usoof.notesapp.ui.main.notes_recycler

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.usoof.notesapp.data.local.entity.Note
import com.usoof.notesapp.databinding.ItemContainerNoteBinding
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class NotesAdapter(
    private var notes: ArrayList<Note>,
    private val clickListener: NoteClickListener
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    companion object {
        const val TAG = "Notes Adapter"
    }

    private val notesSource = notes
    private var timer: Timer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder =
        NotesViewHolder(ItemContainerNoteBinding.inflate(LayoutInflater.from(parent.context)))

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notes[position])
        holder.itemBinding.layoutContainer.setOnClickListener {
            clickListener.onNoteClicked(notes[position], position)
        }
    }

    fun deleteNote(position: Int) {
        notes.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addInsertedNote(note: Note) {
        notes.add(0, note)
        notifyDataSetChanged()
    }

    fun addUpdatedNote(note: Note, position: Int) {
        notes.removeAt(position)
        notes.add(position, note)
        notifyItemChanged(position)
    }

    fun addAllNotes(noteList: List<Note>) {
        notes.addAll(noteList)
        notifyDataSetChanged()
    }

    fun searchNotes(keyword: String) {
        Log.d(TAG, "searchNotes: searching")
        if (notesSource.isEmpty()) {
            return
        }

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (keyword.trim().isEmpty()) {
                    notes = notesSource
                } else {
                    val temp = ArrayList<Note>()
                    Log.d(TAG, "run: searching")
                    for (note in notes) {
                        if (note.title.toLowerCase(Locale.getDefault())
                                .contains(keyword.toLowerCase(Locale.getDefault()))
                            || note.subtitle.toLowerCase(Locale.getDefault())
                                .contains(keyword.toLowerCase(Locale.getDefault()))
                            || note.note.toLowerCase(Locale.getDefault())
                                .contains(keyword.toLowerCase(Locale.getDefault()))
                        ) {
                            temp.add(note)
                        }
                    }
                    notes = temp
                }
                Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
            }
        }, 500)
    }

    fun cancelSearch() {
        timer?.run {
            cancel()
        }
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
