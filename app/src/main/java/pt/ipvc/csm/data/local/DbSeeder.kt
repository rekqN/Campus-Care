package pt.ipvc.csm.data.local

import android.content.Context
import pt.ipvc.csm.data.PasswordHasher
import pt.ipvc.csm.model.Priority
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.model.Role
import pt.ipvc.csm.util.PhotoStorage

/**
 * Fills an empty database with coherent demo data — a few accounts, a set of campus categories and
 * a realistic spread of requests across statuses, priorities and dates (plus their status timeline
 * and notifications). This exists so the app looks populated for the report screenshots; it runs
 * once, only when there are no users yet (see [pt.ipvc.csm.data.repository.CsmRepository]).
 *
 * Demo logins (all seeded): admin@ipvc.pt / admin123 · pedro@ipvc.pt · ana@ipvc.pt · joao@ipvc.pt
 * (the three users share the password user123).
 */
object DbSeeder {
    private const val DAY = 24L * 60 * 60 * 1000

    suspend fun seed(
        context: Context,
        userDao: UserDao,
        categoryDao: CategoryDao,
        requestDao: RequestDao,
        statusHistoryDao: StatusHistoryDao,
        notificationDao: NotificationDao
    ) {
        val now = System.currentTimeMillis()
        fun daysAgo(d: Double): Long = now - (d * DAY).toLong()

        // ---- Accounts ----
        val adminId = userDao.insert(
            UserEntity(name = "Administrador", email = "admin@ipvc.pt",
                passwordHash = PasswordHasher.hash("admin123"), role = Role.ADMIN, createdAt = daysAgo(40.0))
        )
        val pedro = userDao.insert(
            UserEntity(name = "Pedro Freitas", email = "pedro@ipvc.pt",
                passwordHash = PasswordHasher.hash("user123"), role = Role.USER, createdAt = daysAgo(38.0))
        )
        val ana = userDao.insert(
            UserEntity(name = "Ana Sousa", email = "ana@ipvc.pt",
                passwordHash = PasswordHasher.hash("user123"), role = Role.USER, createdAt = daysAgo(30.0))
        )
        val joao = userDao.insert(
            UserEntity(name = "João Martins", email = "joao@ipvc.pt",
                passwordHash = PasswordHasher.hash("user123"), role = Role.USER, createdAt = daysAgo(25.0))
        )

        // ---- Categories ----
        suspend fun cat(name: String, icon: String): Long =
            categoryDao.insert(CategoryEntity(name = name, iconKey = icon, createdAt = daysAgo(39.0)))
        val manutencao = cat("Manutenção", "build")
        val limpeza = cat("Limpeza", "cleaning_services")
        val redes = cat("Redes / Wi-Fi", "wifi")
        val informatica = cat("Equipamento informático", "computer")
        val cantina = cat("Cantina", "restaurant")
        val seguranca = cat("Segurança", "security")
        val salas = cat("Salas", "meeting_room")

        // ---- Requests (+ timeline + notifications) ----
        // transitions: each Triple is (novo estado, há quantos dias, nota opcional). The initial
        // SUBMETIDO entry (by the author) is added automatically; later states are logged as the
        // admin, and CANCELADO as the author.
        suspend fun request(
            userId: Long, category: Long?, title: String, location: String, description: String,
            priority: Priority, createdDaysAgo: Double,
            transitions: List<Triple<RequestStatus, Double, String?>> = emptyList(),
            photos: List<String> = emptyList()
        ) {
            val created = daysAgo(createdDaysAgo)
            val finalStatus = transitions.lastOrNull()?.first ?: RequestStatus.SUBMETIDO
            val updatedAt = transitions.lastOrNull()?.let { daysAgo(it.second) } ?: created
            val photoUri = photos
                .mapNotNull { PhotoStorage.savePhotoFromAsset(context, "seed/$it") }
                .joinToString("\n").ifBlank { null }
            val reqId = requestDao.insert(
                RequestEntity(
                    userId = userId, categoryId = category, title = title, location = location,
                    description = description, photoUri = photoUri, status = finalStatus,
                    priority = priority, createdAt = created, updatedAt = updatedAt
                )
            )
            statusHistoryDao.insert(
                StatusHistoryEntity(requestId = reqId, status = RequestStatus.SUBMETIDO,
                    changedAt = created, changedByUserId = userId)
            )
            for ((status, dAgo, note) in transitions) {
                if (status == RequestStatus.SUBMETIDO) continue
                val at = daysAgo(dAgo)
                val by = if (status == RequestStatus.CANCELADO) userId else adminId
                statusHistoryDao.insert(
                    StatusHistoryEntity(requestId = reqId, status = status, changedAt = at,
                        changedByUserId = by, note = note)
                )
                if (by != userId) {
                    val base = "O teu pedido \"$title\" está agora: ${status.ptLabel}."
                    notificationDao.insert(
                        NotificationEntity(userId = userId, requestId = reqId,
                            message = if (note != null) "$base $note" else base,
                            read = dAgo > 3.0, createdAt = at)
                    )
                }
            }
        }

        request(pedro, salas, "Projetor da sala B203 não liga", "Bloco B, Sala 203",
            "O projetor liga mas não dá imagem, mesmo trocando o cabo HDMI.",
            Priority.ALTA, 2.0, listOf(Triple(RequestStatus.EM_ANALISE, 1.0, "Já pedimos a substituição da lâmpada.")),
            photos = listOf("projector.jpg"))

        request(pedro, limpeza, "Derrame no chão da cantina", "Cantina",
            "Alguém entornou sumo perto da entrada e o chão está escorregadio.",
            Priority.URGENTE, 9.0, listOf(
                Triple(RequestStatus.EM_ANALISE, 8.8, null),
                Triple(RequestStatus.CONCLUIDO, 8.5, "Limpo, obrigado pelo aviso.")),
            photos = listOf("spill_1.jpg", "spill_2.jpg"))

        request(pedro, redes, "Wi-Fi instável no Bloco A", "Bloco A, 2º piso",
            "A ligação cai de 10 em 10 minutos durante as aulas.", Priority.ALTA, 1.0)

        request(ana, manutencao, "Cadeira partida na biblioteca", "Biblioteca, piso 1",
            "Uma das cadeiras tem a perna solta e abana.", Priority.BAIXA, 5.0,
            photos = listOf("chair.jpg"))

        request(ana, informatica, "Computador não arranca no Lab 1", "Lab. Informática 1",
            "O PC do lugar 12 não passa do ecrã preto.", Priority.MEDIA, 6.0,
            listOf(Triple(RequestStatus.EM_ANALISE, 4.0, null)))

        request(joao, seguranca, "Porta da sala C101 não tranca", "Bloco C, Sala 101",
            "A fechadura está a falhar e a porta fica sempre aberta.", Priority.MEDIA, 12.0, listOf(
                Triple(RequestStatus.EM_ANALISE, 11.0, null),
                Triple(RequestStatus.CONCLUIDO, 9.0, "Fechadura substituída.")))

        request(pedro, manutencao, "Torneira a pingar no WC", "Bloco A, WC piso 1",
            "A torneira do lavatório não fecha bem.", Priority.BAIXA, 15.0,
            listOf(Triple(RequestStatus.CONCLUIDO, 13.0, "Reparação feita pela equipa de manutenção.")))

        request(joao, manutencao, "Luz fundida no corredor", "Bloco B, corredor piso 2",
            "Duas lâmpadas fundidas, o corredor está às escuras.", Priority.MEDIA, 3.0)

        request(ana, cantina, "Micro-ondas da cantina avariado", "Cantina",
            "O micro-ondas dos alunos deixou de aquecer.", Priority.BAIXA, 20.0,
            listOf(Triple(RequestStatus.REJEITADO, 18.0, "O equipamento é do concessionário, não da instituição.")))

        request(pedro, informatica, "Quadro interativo sem imagem", "Sala B110",
            "O quadro interativo não recebe sinal do computador.", Priority.ALTA, 7.0,
            listOf(Triple(RequestStatus.EM_ANALISE, 2.0, "A aguardar peça de substituição.")),
            photos = listOf("board_1.jpg", "board_2.jpg", "board_3.jpg"))

        request(joao, informatica, "Impressora da biblioteca sem toner", "Biblioteca",
            "A impressora está sem toner há vários dias.", Priority.MEDIA, 16.0,
            listOf(Triple(RequestStatus.CONCLUIDO, 14.0, "Toner reposto.")),
            photos = listOf("printer.jpg"))

        request(ana, manutencao, "Aquecimento não funciona na sala A004", "Bloco A, Sala 004",
            "A sala está muito fria, o aquecimento não liga.", Priority.MEDIA, 4.0)

        request(joao, seguranca, "Cheiro a gás junto ao laboratório", "Lab. Química",
            "Sente-se um cheiro forte a gás no corredor do laboratório.", Priority.URGENTE, 0.5,
            listOf(Triple(RequestStatus.EM_ANALISE, 0.3, "Equipa de segurança a caminho.")))

        request(pedro, salas, "Afinal era só o cabo", "Bloco D, Sala 5",
            "Pedi ajuda para o projetor mas já resolvi, era o cabo mal ligado.", Priority.BAIXA, 10.0,
            listOf(Triple(RequestStatus.CANCELADO, 9.5, null)))
    }
}
