@file:JvmName("Constants")

package de.studienarbeit.invoicescanner

/**
 * Created by andym on 11.04.2018.
 */

@JvmField val REQUEST_CAMERA_PERMISSION = 1
@JvmField val PIC_FILE_NAME = "temp.jpg"

enum class Fragment {
    CAMERA,
    ARCHIVE,
    FAVORITES,
    ABOUT,
    CONFIRM_RETAKE,
    ANALYZE_PICTURE
}
