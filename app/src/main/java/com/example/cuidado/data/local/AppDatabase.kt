package com.example.cuidado.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ActivityEntity::class, ActivityLogEntity::class, ProfileEntity::class, CaregiverEntity::class, MedicionEntity::class, RangoMedicionEntity::class, CitaEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun profileDao(): ProfileDao
    abstract fun medicionDao(): MedicionDao
    abstract fun citaDao(): CitaDao

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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE activity_logs ADD COLUMN notes TEXT NOT NULL DEFAULT ''")
                db.execSQL("CREATE TABLE IF NOT EXISTS `mediciones` (`id` TEXT NOT NULL, `tipo` TEXT NOT NULL, `valor` REAL NOT NULL, `fecha` TEXT NOT NULL, `hora` TEXT NOT NULL, `notas` TEXT NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `rangos_medicion` (`tipo` TEXT NOT NULL, `valorMin` REAL NOT NULL, `valorMax` REAL NOT NULL, `umbralConsecutivo` INTEGER NOT NULL, PRIMARY KEY(`tipo`))")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `CITA` (`lIdCita` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lIdPerfil` INTEGER NOT NULL, `dtFechaHora` INTEGER NOT NULL, `strEspecialista` TEXT NOT NULL, `strUbicacion` TEXT NOT NULL, `strNotas` TEXT NOT NULL)")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE CITA ADD COLUMN strCategoria TEXT NOT NULL DEFAULT 'General'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cuidado_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
