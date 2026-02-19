package net.inspirehub.hr.check_in_out.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
@Entity(tableName = "offline_logs")
data class OfflineLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String,
    val lat: Double,
    val lng: Double,
    val action_time: String,
    val action_tz: String
)


@Dao
interface OfflineLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: OfflineLog)

    @Query("SELECT * FROM offline_logs")
    suspend fun getAllLogs(): List<OfflineLog>

    @Query("DELETE FROM offline_logs WHERE id = :id")
    suspend fun deleteLogById(id: Int)

    @Query("DELETE FROM offline_logs")
    suspend fun deleteAllLogs()

    @Query("SELECT * FROM offline_logs ORDER BY id DESC LIMIT 1")
    suspend fun getLastLog(): OfflineLog?

}



@Database(entities = [OfflineLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun offlineLogDao(): OfflineLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "offline_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


