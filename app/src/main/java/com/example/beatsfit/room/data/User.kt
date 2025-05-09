package com.example.beatsfit.room.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user-table")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "first-name")
    val firstName: String = "",

    @ColumnInfo(name = "last-name")
    val lastName: String = "",

    @ColumnInfo(name = "image-uri")
    val imageUri: String?,

    @ColumnInfo(name = "e-mail")
    val email: String?,

    @ColumnInfo(name = "height")
    val height: Int,

    @ColumnInfo(name = "weight")
    val weight: String?,

    @ColumnInfo(name = "gender")
    val gender: String?,

    @ColumnInfo(name = "stepGoal")
    val stepGoal: Int?,

    @ColumnInfo(name = "cyclingGoal")
    val cyclingGoal: Int?

    )