package pt.ipvc.csm.data.repository

import kotlinx.coroutines.flow.Flow
import pt.ipvc.csm.data.PasswordHasher
import pt.ipvc.csm.data.local.CategoryDao
import pt.ipvc.csm.data.local.CategoryEntity
import pt.ipvc.csm.data.local.CategoryWithCount
import pt.ipvc.csm.data.local.NotificationDao
import pt.ipvc.csm.data.local.NotificationEntity
import pt.ipvc.csm.data.local.RequestDao
import pt.ipvc.csm.data.local.RequestEntity
import pt.ipvc.csm.data.local.RequestWithDetails
import pt.ipvc.csm.data.local.StatusHistoryDao
import pt.ipvc.csm.data.local.StatusHistoryEntity
import pt.ipvc.csm.data.local.StatusHistoryWithAuthor
import pt.ipvc.csm.data.local.UserDao
import pt.ipvc.csm.data.local.UserEntity
import pt.ipvc.csm.data.session.SessionManager
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.model.Role

/** Result of a form/mutation action, carrying a Portuguese error message on failure. */
sealed interface OpResult {
    data object Success : OpResult
    data class Error(val message: String) : OpResult
}

/**
 * Single point of access to the data layer: authentication, session, and CRUD for
 * requests/categories, plus writing the status-history timeline. Lists are exposed as
 * Flows so the UI updates reactively; mutations return [OpResult].
 */
class CsmRepository(
    private val userDao: UserDao,
    private val categoryDao: CategoryDao,
    private val requestDao: RequestDao,
    private val statusHistoryDao: StatusHistoryDao,
    private val notificationDao: NotificationDao,
    private val session: SessionManager
) {

    // ---- Session / current user ----

    val currentUserId: Flow<Long?> = session.currentUserId

    val darkMode: Flow<Boolean> = session.darkMode

    suspend fun setDarkMode(enabled: Boolean) = session.setDarkMode(enabled)

    val language: Flow<String> = session.language

    suspend fun setLanguage(code: String) = session.setLanguage(code)

    // ---- Notifications ----

    fun notificationsForUser(userId: Long): Flow<List<NotificationEntity>> =
        notificationDao.observeForUser(userId)

    fun unreadCountForUser(userId: Long): Flow<Int> =
        notificationDao.observeUnreadCount(userId)

    suspend fun markNotificationsRead(userId: Long) = notificationDao.markAllRead(userId)

    fun observeUser(id: Long): Flow<UserEntity?> = userDao.observeById(id)

    suspend fun getUser(id: Long): UserEntity? = userDao.getById(id)

    // ---- Authentication ----

    suspend fun login(email: String, password: String): OpResult {
        val user = userDao.findByEmail(email.trim().lowercase())
            ?: return OpResult.Error("Email ou password incorretos.")
        if (!PasswordHasher.verify(password, user.passwordHash)) {
            return OpResult.Error("Email ou password incorretos.")
        }
        session.setLoggedInUser(user.id)
        return OpResult.Success
    }

    suspend fun register(name: String, email: String, password: String, role: Role): OpResult {
        val cleanEmail = email.trim().lowercase()
        if (userDao.findByEmail(cleanEmail) != null) {
            return OpResult.Error("Já existe uma conta com este email.")
        }
        val id = userDao.insert(
            UserEntity(
                name = name.trim(),
                email = cleanEmail,
                passwordHash = PasswordHasher.hash(password),
                role = role
            )
        )
        session.setLoggedInUser(id)
        return OpResult.Success
    }

    /** Updates the logged-in profile. A null [newPassword] leaves the password unchanged. */
    suspend fun updateProfile(
        userId: Long,
        name: String,
        email: String,
        newPassword: String?,
        role: Role,
        photoUri: String?
    ): OpResult {
        val current = userDao.getById(userId) ?: return OpResult.Error("Utilizador não encontrado.")
        val cleanEmail = email.trim().lowercase()
        val owner = userDao.findByEmail(cleanEmail)
        if (owner != null && owner.id != userId) {
            return OpResult.Error("Esse email já está a ser usado por outra conta.")
        }
        userDao.update(
            current.copy(
                name = name.trim(),
                email = cleanEmail,
                role = role,
                photoUri = photoUri,
                passwordHash = if (newPassword.isNullOrBlank()) current.passwordHash
                else PasswordHasher.hash(newPassword)
            )
        )
        return OpResult.Success
    }

    suspend fun logout() = session.clear()

    // ---- Categories ----

    fun categories(): Flow<List<CategoryEntity>> = categoryDao.observeAll()

    fun categoriesWithCounts(): Flow<List<CategoryWithCount>> = categoryDao.observeAllWithCounts()

    suspend fun addCategory(name: String, iconKey: String?): OpResult {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return OpResult.Error("O nome da categoria é obrigatório.")
        if (categoryDao.findByName(cleanName) != null) {
            return OpResult.Error("Já existe uma categoria com este nome.")
        }
        categoryDao.insert(CategoryEntity(name = cleanName, iconKey = iconKey))
        return OpResult.Success
    }

    suspend fun updateCategory(category: CategoryEntity, name: String, iconKey: String?): OpResult {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return OpResult.Error("O nome da categoria é obrigatório.")
        val existing = categoryDao.findByName(cleanName)
        if (existing != null && existing.id != category.id) {
            return OpResult.Error("Já existe uma categoria com este nome.")
        }
        categoryDao.update(category.copy(name = cleanName, iconKey = iconKey))
        return OpResult.Success
    }

    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.delete(category)

    // ---- Requests ----

    fun requestsByUser(userId: Long): Flow<List<RequestWithDetails>> =
        requestDao.observeByUser(userId)

    fun allRequests(): Flow<List<RequestWithDetails>> = requestDao.observeAll()

    fun requestDetails(id: Long): Flow<RequestWithDetails?> = requestDao.observeDetails(id)

    fun statusHistory(requestId: Long): Flow<List<StatusHistoryWithAuthor>> =
        statusHistoryDao.observeForRequest(requestId)

    suspend fun createRequest(
        userId: Long,
        categoryId: Long?,
        title: String,
        location: String,
        description: String,
        photoUri: String?
    ): OpResult {
        val now = System.currentTimeMillis()
        val requestId = requestDao.insert(
            RequestEntity(
                userId = userId,
                categoryId = categoryId,
                title = title.trim(),
                location = location.trim(),
                description = description.trim(),
                photoUri = photoUri,
                status = RequestStatus.SUBMETIDO,
                createdAt = now,
                updatedAt = now
            )
        )
        // First timeline entry: submitted by its author.
        statusHistoryDao.insert(
            StatusHistoryEntity(
                requestId = requestId,
                status = RequestStatus.SUBMETIDO,
                changedAt = now,
                changedByUserId = userId
            )
        )
        return OpResult.Success
    }

    /** Admin action: set a new state and log it in the timeline. */
    suspend fun changeStatus(requestId: Long, status: RequestStatus, byUserId: Long): OpResult {
        val request = requestDao.getById(requestId) ?: return OpResult.Error("Pedido não encontrado.")
        if (request.status == status) return OpResult.Success
        val now = System.currentTimeMillis()
        requestDao.updateStatus(requestId, status, now)
        statusHistoryDao.insert(
            StatusHistoryEntity(
                requestId = requestId,
                status = status,
                changedAt = now,
                changedByUserId = byUserId
            )
        )
        // Notify the request's owner when someone else (an admin) changed the state.
        if (request.userId != byUserId) {
            notificationDao.insert(
                NotificationEntity(
                    userId = request.userId,
                    requestId = requestId,
                    message = "O teu pedido \"${request.title}\" está agora: ${status.ptLabel}."
                )
            )
        }
        return OpResult.Success
    }

    /** User action: cancel a request that is not yet completed. */
    suspend fun cancelRequest(requestId: Long, byUserId: Long): OpResult {
        val request = requestDao.getById(requestId) ?: return OpResult.Error("Pedido não encontrado.")
        if (!request.status.isActive) {
            return OpResult.Error("Este pedido já não pode ser cancelado.")
        }
        return changeStatus(requestId, RequestStatus.CANCELADO, byUserId)
    }

    suspend fun deleteRequest(requestId: Long): OpResult {
        val request = requestDao.getById(requestId) ?: return OpResult.Error("Pedido não encontrado.")
        requestDao.delete(request)
        return OpResult.Success
    }
}
