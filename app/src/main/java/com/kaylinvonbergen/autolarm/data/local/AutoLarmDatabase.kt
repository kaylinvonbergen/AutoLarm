
package com.kaylinvonbergen.autolarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.kaylinvonbergen.autolarm.data.local.dao.TaskDao
import com.kaylinvonbergen.autolarm.data.local.entity.TaskEntity

/**
 * Main Room database for AutoLarm.
 *
 * Future versions will include alarm entities
 * and relationships between alarms and tasks.
 */
@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AutoLarmDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: AutoLarmDatabase? = null

        fun getDatabase(context: Context): AutoLarmDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutoLarmDatabase::class.java,
                    "autolarm_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
