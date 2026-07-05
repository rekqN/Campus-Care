package pt.ipvc.csm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        RequestEntity::class,
        StatusHistoryEntity::class,
        NotificationEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CsmDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun requestDao(): RequestDao
    abstract fun statusHistoryDao(): StatusHistoryDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile private var INSTANCE: CsmDatabase? = null

        fun get(context: Context): CsmDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CsmDatabase::class.java,
                    "csm.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
