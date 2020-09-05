package com.usoof.notesapp.data.local.entity

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "notes")
data class Note(

    @NotNull
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "date_time")
    var dateTime: String,

    @ColumnInfo(name = "subtitle")
    var subtitle: String,

    @ColumnInfo(name = "note")
    var note: String,

    @ColumnInfo(name = "image_path")
    var imagePath: String,

    @ColumnInfo(name = "color")
    var color: String,

    @ColumnInfo(name = "web_link")
    var webLink: String
) {

    constructor() : this(0, "", "", "", "", "", "", "")

    override fun toString(): String = "$title : $dateTime"
}