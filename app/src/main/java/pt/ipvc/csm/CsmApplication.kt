package pt.ipvc.csm

import android.app.Application
import android.content.Context
import pt.ipvc.csm.data.local.CsmDatabase
import pt.ipvc.csm.data.repository.CsmRepository
import pt.ipvc.csm.data.session.SessionManager

/**
 * Simple manual dependency container — a lightweight alternative to a DI framework,
 * appropriate for an app of this size. Built once and held by the Application.
 */
class AppContainer(context: Context) {
    private val database = CsmDatabase.get(context)
    val session = SessionManager(context)
    val repository = CsmRepository(
        userDao = database.userDao(),
        categoryDao = database.categoryDao(),
        requestDao = database.requestDao(),
        statusHistoryDao = database.statusHistoryDao(),
        notificationDao = database.notificationDao(),
        session = session
    )
}

class CsmApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
