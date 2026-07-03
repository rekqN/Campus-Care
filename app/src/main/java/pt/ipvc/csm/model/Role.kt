package pt.ipvc.csm.model

/** The two application profiles required by the brief. */
enum class Role {
    USER,
    ADMIN;

    val ptLabel: String
        get() = when (this) {
            USER -> "Utilizador"
            ADMIN -> "Administrador"
        }
}
