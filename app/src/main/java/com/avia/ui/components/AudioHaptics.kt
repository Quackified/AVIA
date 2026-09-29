package com.avia.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.avia.data.AppSettings

/**
 * Unified tactile and audio feedback engine for interactive buttons,
 * quiz option selections, and simulation decision gates.
 *
 * Honors user preferences via [AppSettings.hapticsEnabled] and [AppSettings.soundEnabled].
 */
object AudioHaptics {

    /** Subtle tactile tick and click sound for primary taps and mode selections. */
    fun performClick(view: View?, haptic: HapticFeedback?) {
        if (AppSettings.hapticsEnabled) {
            haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        if (AppSettings.soundEnabled) {
            view?.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    /** Option or decision toggle feedback. */
    fun performSelect(view: View?, haptic: HapticFeedback?) {
        if (AppSettings.hapticsEnabled) {
            haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        if (AppSettings.soundEnabled) {
            view?.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    /** Satisfying confirmation on correct answers or successful goal states. */
    fun performCorrect(view: View?, haptic: HapticFeedback?) {
        if (AppSettings.hapticsEnabled) {
            haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        if (AppSettings.soundEnabled) {
            view?.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    /** Distinct feedback on incorrect choices. */
    fun performIncorrect(view: View?, haptic: HapticFeedback?) {
        if (AppSettings.hapticsEnabled) {
            haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}
