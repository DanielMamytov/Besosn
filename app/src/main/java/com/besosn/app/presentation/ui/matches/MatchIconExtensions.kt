package com.besosn.app.presentation.ui.matches

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.besosn.app.R
import java.io.FileNotFoundException

internal fun ImageView.loadMatchIcon(
    @DrawableRes iconRes: Int,
    iconUri: String?,
    @DrawableRes fallback: Int = R.drawable.jkljfsjfls,
) {
    if (!iconUri.isNullOrBlank()) {
        val parsed = runCatching { Uri.parse(iconUri) }.getOrNull()
        if (parsed != null) {
            try {
                setImageURI(parsed)
                if (drawable != null) {
                    return
                }
            } catch (_: SecurityException) {
                // Ignore and fall back to resource icon
            } catch (_: FileNotFoundException) {
                // Ignore and fall back to resource icon
            } catch (_: IllegalArgumentException) {
                // Ignore and fall back to resource icon
            }
        }
    }

    if (iconRes != 0) {
        try {
            setImageResource(iconRes)
            return
        } catch (_: Exception) {
            // Fall through and use fallback icon
        }
    }

    setImageResource(fallback)
}
