package pt.ipvc.csm.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pt.ipvc.csm.R
import pt.ipvc.csm.model.RequestStatus
import pt.ipvc.csm.model.Role

/** Localized label for a request status (used by chips, filters, timeline). */
@Composable
fun statusLabel(status: RequestStatus): String = stringResource(
    when (status) {
        RequestStatus.SUBMETIDO -> R.string.status_submitted
        RequestStatus.EM_ANALISE -> R.string.status_review
        RequestStatus.CONCLUIDO -> R.string.status_completed
        RequestStatus.REJEITADO -> R.string.status_rejected
        RequestStatus.CANCELADO -> R.string.status_cancelled
    }
)

/** Localized label for an account role. */
@Composable
fun roleLabel(role: Role): String = stringResource(
    when (role) {
        Role.USER -> R.string.role_user
        Role.ADMIN -> R.string.role_admin
    }
)
