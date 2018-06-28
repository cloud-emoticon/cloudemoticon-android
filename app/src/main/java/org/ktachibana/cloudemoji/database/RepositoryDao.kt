package org.ktachibana.cloudemoji.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun add(repository: Repository)

    @Query("SELECT * FROM REPOSITORY")
    fun getAll(): List<Repository>
}