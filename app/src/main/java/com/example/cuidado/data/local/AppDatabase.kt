package com.example.cuidado.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ActivityEntity::class, ActivityLogEntity::class, ProfileEntity::class, CaregiverEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `activity_logs` (
                        `id` TEXT NOT NULL, 
                        `activityId` TEXT NOT NULL, 
                        `date` TEXT NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `executionTime` TEXT, 
                        PRIMARY KEY(`id`), 
                        FOREIGN KEY(`activityId`) REFERENCES `activities`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_activity_logs_activityId` ON `activity_logs` (`activityId`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_activity_logs_activityId_date` ON `activity_logs` (`activityId`, `date`)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `profile` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `age` INTEGER NOT NULL, `conditions` TEXT NOT NULL, `frequentMedications` TEXT NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `caregiver` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `phone` TEXT NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cuidado_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
