package pt.ipvc.csm.model

/**
 * How urgent a request is. Chosen by the author when creating a request and adjustable by an
 * administrator during triage. Declaration order is ascending severity (BAIXA < URGENTE).
 */
enum class Priority(val ptLabel: String) {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta"),
    URGENTE("Urgente");

    /** ALTA/URGENTE are surfaced with extra emphasis (card flag, dashboard count). */
    val isElevated: Boolean
        get() = this == ALTA || this == URGENTE

    companion object {
        /** The priority a new request starts at when the author doesn't change it. */
        val DEFAULT = MEDIA
    }
}
