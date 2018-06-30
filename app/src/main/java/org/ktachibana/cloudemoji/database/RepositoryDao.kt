package org.ktachibana.cloudemoji.database

import android.arch.persistence.room.*

@Dao
abstract class RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun add(repository: Repository)

    @Query("SELECT * FROM REPOSITORY")
    abstract fun getAll(): List<Repository>

    @Query("SELECT * FROM REPOSITORY WHERE URL = :url")
    abstract fun get(url: String): Repository?

    fun exists(url: String): Boolean {
        return get(url) == null
    }

    @Update
    abstract fun update(repository: Repository)

    @Delete
    abstract fun delete(repository: Repository)
}