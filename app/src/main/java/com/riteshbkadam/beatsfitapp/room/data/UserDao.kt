package com.riteshbkadam.beatsfitapp.room.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM `user-table`")
    suspend fun clearDatabase()

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM `user-table` LIMIT 1")
    suspend fun getUser(): User?
}
