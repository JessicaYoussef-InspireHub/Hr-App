package net.inspirehub.hr.lunch.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteLunch(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val supplierName: String,
    val price: String,
    val imageBase64: String?
)

@Dao
interface FavoriteLunchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteLunch)

    @Query("SELECT * FROM favorites")
    fun getAllFavoritesFlow(): Flow<List<FavoriteLunch>>

    @Query("SELECT * FROM favorites WHERE id = :id LIMIT 1")
    fun getFavoriteByIdFlow(id: Int): Flow<FavoriteLunch?>



    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavorite(id: Int)

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()

}