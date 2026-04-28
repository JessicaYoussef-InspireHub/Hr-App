package net.inspirehub.hr.lunch.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.content.Context
import androidx.room.Delete
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

object DatabaseProvider {
    private var db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration(false)
                .build()
        }
        return db!!
    }
}

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int

)

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItem)

    @Query("SELECT * FROM cart_items")
    suspend fun getAllItems(): List<CartItem>

    @Query("SELECT * FROM cart_items")
    fun getAllItemsFlow(): Flow<List<CartItem>>

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()


    @Delete
    suspend fun deleteItem(item: CartItem)
}

@Database(
    entities = [
        CartItem::class,
        OrderEntity::class,
        OrderItemEntity::class,
        FavoriteLunch::class
    ], version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun favoriteLunchDao(): FavoriteLunchDao
}