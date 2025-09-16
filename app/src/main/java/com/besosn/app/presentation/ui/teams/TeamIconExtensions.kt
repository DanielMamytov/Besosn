package com.besosn.app.presentation.ui.teams

import android.content.res.Resources
import android.net.Uri
import android.widget.ImageView
import java.io.FileNotFoundException
import androidx.annotation.DrawableRes
import com.besosn.app.R

internal fun ImageView.loadTeamImage(team: TeamModel, @DrawableRes fallback: Int = R.drawable.ic_users) {
    val iconUri = team.iconUri
    if (!iconUri.isNullOrBlank()) {
        val parsed = runCatching { Uri.parse(iconUri) }.getOrNull()
        if (parsed != null) {
            try {
                setImageURI(parsed)
                if (drawable != null) {
                    return
                }
            } catch (_: SecurityException) {
                // We have lost URI read permissions (for example when using the Photo Picker).
            } catch (_: FileNotFoundException) {
                // The picked image is no longer available on the device.
            } catch (_: IllegalArgumentException) {
                // Invalid URI value – fall back to the default icon below.
            }
        }
    }

    setImageResource(resolveTeamIconRes(context.resources, team.iconRes, fallback))
}

@DrawableRes
internal fun resolveTeamIconRes(
    resources: Resources,
    @DrawableRes candidate: Int,
    @DrawableRes fallback: Int = R.drawable.ic_users
): Int {
    if (candidate == 0) return fallback

    return try {
        when (resources.getResourceTypeName(candidate)) {
            "drawable", "mipmap" -> candidate
            else -> fallback
        }
    } catch (_: Resources.NotFoundException) {
        fallback
    }
}
