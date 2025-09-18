package com.besosn.app.presentation.ui.teams

import android.content.res.Resources
import android.net.Uri
import android.widget.ImageView
import java.io.FileNotFoundException
import androidx.annotation.DrawableRes
import com.besosn.app.R

internal fun ImageView.loadTeamImage(team: TeamModel, @DrawableRes fallback: Int = R.drawable.ball) {
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
                
            } catch (_: FileNotFoundException) {
                
            } catch (_: IllegalArgumentException) {
                
            }
        }
    }

    setImageResource(resolveTeamIconRes(context.resources, team.iconRes, fallback))
}

@DrawableRes
internal fun resolveTeamIconRes(
    resources: Resources,
    @DrawableRes candidate: Int,
    @DrawableRes fallback: Int = R.drawable.ball
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
