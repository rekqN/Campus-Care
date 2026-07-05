package pt.ipvc.csm.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Applies the chosen language (pt/en) at runtime by overriding the Context/Configuration locale,
 * so every `stringResource(...)` inside [content] resolves in that language. Switching the
 * preference recomposes and re-localizes the whole app without recreating the Activity.
 */
@Composable
fun LocalizedApp(language: String, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val localizedContext = remember(language, context) {
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale(language))
        context.createConfigurationContext(config)
    }
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedContext.resources.configuration
    ) {
        content()
    }
}
