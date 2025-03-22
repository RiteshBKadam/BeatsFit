package com.riteshbkadam.beatsfitapp.room.data

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }


    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun clearDatabase() {
        userDao.clearDatabase()
    }

    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }

    suspend fun getUser(): User? {
        return userDao.getUser()
    }

}
