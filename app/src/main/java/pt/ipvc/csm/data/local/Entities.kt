package pt.ipvc.csm.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import pt.ipvc.csm.model.Priority
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.model.Role

/** A registered account (Utilizador). Maps to the "User" entity in the data model. */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: Role,
    val photoUri: String? = null,
    val language: String = "pt",
    val createdAt: Long = System.currentTimeMillis()
)

/** A request category, managed by administrators (Categoria). iconKey maps to a Material icon. */
@Entity(
    tableName = "categories",
    indices = [Index(value = ["name"], unique = true)]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconKey: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * A campus request/occurrence (Pedido). categoryId is nullable so deleting a category
 * (SET NULL) keeps the request rather than cascading it away.
 */
@Entity(
    tableName = "requests",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("userId"), Index("categoryId")]
)
data class RequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val categoryId: Long?,
    val title: String,
    val location: String,
    val description: String,
    val photoUri: String? = null,
    val status: RequestStatus,
    val priority: Priority = Priority.MEDIA,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Photos are stored as newline-separated internal file paths in [RequestEntity.photoUri],
 * which lets a request carry more than one photo without an extra table.
 */
val RequestEntity.photoPaths: List<String>
    get() = photoUri?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()

/**
 * One entry in a request's status timeline (HistoricoEstado): records each state change,
 * when it happened, and which account made it.
 */
@Entity(
    tableName = "status_history",
    foreignKeys = [
        ForeignKey(
            entity = RequestEntity::class,
            parentColumns = ["id"],
            childColumns = ["requestId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["changedByUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("requestId"), Index("changedByUserId")]
)
data class StatusHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requestId: Long,
    val status: RequestStatus,
    val changedAt: Long,
    val changedByUserId: Long,
    val note: String? = null
)

/**
 * A notification (Notificacao). The entity exists to match the data model; the notifications
 * feature itself is planned for a later (bonus) pass.
 */
@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RequestEntity::class,
            parentColumns = ["id"],
            childColumns = ["requestId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("requestId")]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val requestId: Long?,
    val message: String,
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/** Read model: a request joined with its author name and category name/icon. */
data class RequestWithDetails(
    @Embedded val request: RequestEntity,
    val userName: String,
    val categoryName: String?,
    val categoryIcon: String?
)

/** Read model: a category with how many requests reference it. */
data class CategoryWithCount(
    @Embedded val category: CategoryEntity,
    val requestCount: Int
)

/** Read model: a status-history entry joined with the name of who made the change. */
data class StatusHistoryWithAuthor(
    @Embedded val entry: StatusHistoryEntity,
    val changedByName: String
)
