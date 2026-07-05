package pt.ipvc.csm.ui

import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Applies the chosen language (pt/en) at runtime so every `stringResource(...)` inside [content]
 * resolves in that language, without recreating the Activity.
 *
 * Important: we override the resources via a [ContextWrapper] that still keeps the original
 * Activity as its base context. A plain `createConfigurationContext(...)` would be detached from
 * the Activity and would break APIs that look up the Activity from LocalContext (e.g. the photo
 * picker's ActivityResultRegistry).
 */
@Composable
fun LocalizedApp(language: String, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val config = remember(language, context) {
        Configuration(context.resources.configuration).apply { setLocale(Locale(language)) }
    }
    val localizedContext = remember(config, context) {
        val localizedResources: Resources = context.createConfigurationContext(config).resources
        object : ContextWrapper(context) {
            override fun getResources(): Resources = localizedResources
        }
    }
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides config
    ) {
        content()
    }
}
