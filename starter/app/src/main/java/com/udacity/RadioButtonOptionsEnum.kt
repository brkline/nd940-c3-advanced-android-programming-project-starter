package com.udacity

import androidx.annotation.StringRes

enum class RadioButtonOptionsEnum (
    val option: Int,
        val url: String,
        @StringRes
        val title: Int
) {
    GLIDE(1, GLIDE_DOWNLOAD_URL, R.string.glide_radioButton_text),
    UDACITY(2, LOAD_APP_DOWNLOAD_URL, R.string.loadapp_radioButton_text),
    RETROFIT(3, RETROFIT_DOWNLOAD_URL, R.string.retrofit_radioButton_text)
}