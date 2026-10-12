
package com.kaylinvonbergen.autolarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.kaylinvonbergen.autolarm.data.local.dao.AlarmDao
import com.kaylinvonbergen.autolarm.data.local.dao.TaskDao
import com.kaylinvonbergen.autolarm.data.local.entity.AlarmEntity
import com.kaylinvonbergen.autolarm.data.local.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        AlarmEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AutoLarmDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun alarmDao(): AlarmDao

    companion object {

        @Volatile
        private var INSTANCE: AutoLarmDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `alarms` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `enabled` INTEGER NOT NULL,
                        `arrivalDate` TEXT NOT NULL,
                        `arrivalTime` TEXT NOT NULL,
                        `departureLocation` TEXT NOT NULL,
                        `destinationLocation` TEXT NOT NULL,
                        `transportationMode` TEXT NOT NULL,
                        `travelTimeSource` TEXT NOT NULL,
                        `travelDurationMinutes` INTEGER NOT NULL,
                        `selectedTaskIds` TEXT NOT NULL,
                        `bufferMinutes` INTEGER NOT NULL,
                        `preEnabled` INTEGER NOT NULL,
                        `preCount` INTEGER NOT NULL,
                        `preInterval` INTEGER NOT NULL,
                        `postEnabled` INTEGER NOT NULL,
                        `postCount` INTEGER NOT NULL,
                        `postInterval` INTEGER NOT NULL,
                        `position` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AutoLarmDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AutoLarmDatabase::class.java,
                    "autolarm_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
