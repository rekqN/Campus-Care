package pt.ipvc.csm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pt.ipvc.csm.model.Priority
import pt.ipvc.csm.model.RequestStatus

private const val DETAILS_SELECT = """
    SELECT r.*, u.name AS userName, c.name AS categoryName, c.iconKey AS categoryIcon
    FROM requests r
    JOIN users u ON u.id = r.userId
    LEFT JOIN categories c ON c.id = r.categoryId
"""

@Dao
interface UserDao {
    @Insert suspend fun insert(user: UserEntity): Long
    @Update suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}

@Dao
interface CategoryDao {
    @Insert suspend fun insert(category: CategoryEntity): Long
    @Update suspend fun update(category: CategoryEntity)
    @Delete suspend fun delete(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query(
        """
        SELECT c.*, (SELECT COUNT(*) FROM requests r WHERE r.categoryId = c.id) AS requestCount
        FROM categories c
        ORDER BY c.name COLLATE NOCASE
        """
    )
    fun observeAllWithCounts(): Flow<List<CategoryWithCount>>

    @Query("SELECT * FROM categories WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun findByName(name: String): CategoryEntity?
}

@Dao
interface RequestDao {
    @Insert suspend fun insert(request: RequestEntity): Long
    @Update suspend fun update(request: RequestEntity)
    @Delete suspend fun delete(request: RequestEntity)

    @Query("SELECT * FROM requests WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): RequestEntity?

    @Query("$DETAILS_SELECT WHERE r.id = :id LIMIT 1")
    fun observeDetails(id: Long): Flow<RequestWithDetails?>

    @Query("$DETAILS_SELECT WHERE r.userId = :userId ORDER BY r.createdAt DESC")
    fun observeByUser(userId: Long): Flow<List<RequestWithDetails>>

    @Query("$DETAILS_SELECT ORDER BY r.createdAt DESC")
    fun observeAll(): Flow<List<RequestWithDetails>>

    @Query("UPDATE requests SET status = :status, updatedAt = :changedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: RequestStatus, changedAt: Long)

    @Query("UPDATE requests SET priority = :priority, updatedAt = :changedAt WHERE id = :id")
    suspend fun updatePriority(id: Long, priority: Priority, changedAt: Long)
}

@Dao
interface StatusHistoryDao {
    @Insert suspend fun insert(entry: StatusHistoryEntity): Long

    @Query(
        """
        SELECT h.*, u.name AS changedByName
        FROM status_history h
        JOIN users u ON u.id = h.changedByUserId
        WHERE h.requestId = :requestId
        ORDER BY h.changedAt ASC
        """
    )
    fun observeForRequest(requestId: Long): Flow<List<StatusHistoryWithAuthor>>
}

@Dao
interface NotificationDao {
    @Insert suspend fun insert(notification: NotificationEntity): Long

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeForUser(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND read = 0")
    fun observeUnreadCount(userId: Long): Flow<Int>

    @Query("UPDATE notifications SET read = 1 WHERE userId = :userId")
    suspend fun markAllRead(userId: Long)
}
