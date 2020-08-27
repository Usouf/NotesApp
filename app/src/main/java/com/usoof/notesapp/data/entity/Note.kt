package com.usoof.notesapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "date_time")
    val dateTime: String,

    @ColumnInfo(name = "subtitle")
    val subtitle: String,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name = "image_path")
    val imagePath: String,

    @ColumnInfo(name = "color")
    val color: String,

    @ColumnInfo(name = "web_link")
    val webLink: String
) {
    override fun toString(): String = "$title : $dateTime"
}