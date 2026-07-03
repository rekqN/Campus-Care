package pt.ipvc.csm.model

/**
 * The lifecycle states of a request. The first four are the minimum required by
 * the brief; CANCELADO is added because users may cancel a request not yet completed.
 */
enum class RequestStatus(val ptLabel: String) {
    SUBMETIDO("Submetido"),
    EM_ANALISE("Em análise"),
    CONCLUIDO("Concluído"),
    REJEITADO("Rejeitado"),
    CANCELADO("Cancelado");

    /** Statuses shown in the active "Os meus pedidos" list. */
    val isActive: Boolean
        get() = this == SUBMETIDO || this == EM_ANALISE

    /** Statuses shown in the "Histórico" list. */
    val isHistory: Boolean
        get() = this == CONCLUIDO || this == REJEITADO || this == CANCELADO

    companion object {
        /** The four states an administrator can assign. */
        val adminAssignable = listOf(SUBMETIDO, EM_ANALISE, CONCLUIDO, REJEITADO)
    }
}
