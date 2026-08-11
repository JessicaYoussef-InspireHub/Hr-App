package net.inspirehub.hr.check_in_out.data

import android.content.Context
import androidx.room.Room
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity(tableName = "location_logs")
data class LocationLogEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val battery: Int,
    val createdAt: Long
)

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationLogEntity)

    @Query("SELECT * FROM location_logs ORDER BY id ASC")
    suspend fun getAll(): List<LocationLogEntity>

    @Delete
    suspend fun delete(
        location: LocationLogEntity
    )

    @Query("DELETE FROM location_logs")
    suspend fun deleteAll()
}


@Database(
    entities = [
        LocationLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}


object LocationDatabaseProvider {

    @Volatile
    private var INSTANCE: LocationDatabase? = null

    fun getDatabase(
        context: Context
    ): LocationDatabase {

        return INSTANCE ?: synchronized(this) {

            Room.databaseBuilder(
                context,
                LocationDatabase::class.java,
                "location_database"
            ).build().also {

                INSTANCE = it
            }
        }
    }
}