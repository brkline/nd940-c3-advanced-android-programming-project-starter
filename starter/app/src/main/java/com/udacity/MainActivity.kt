package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.utils.cancelNotifications
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var selectedOption: RadioButtonOptionsEnum? = null

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (download_radio_group.checkedRadioButtonId != -1) {
                when (download_radio_group.checkedRadioButtonId) {
                    glide_radioButton.id -> {
                        selectedOption = RadioButtonOptionsEnum.GLIDE
                    }
                    load_app_radioButton.id -> {
                        selectedOption = RadioButtonOptionsEnum.UDACITY
                    }
                    retrofit_radioButton.id -> {
                        selectedOption = RadioButtonOptionsEnum.RETROFIT
                    }
                }
                selectedOption?.let { option -> download(option) }
                custom_button.startLoading()
            } else {
                Toast.makeText(this, getString(R.string.invalid_option_text), Toast.LENGTH_SHORT).show()
            }

            createChannel(
                    getString(R.string.download_notification_channel_id),
                    getString(R.string.notification_channel)
            )
            download(selectedOption)
        }
    }

    private fun createChannel(channelId: String, channel: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channel,
                    NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)

            notificationManager = getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val query = DownloadManager.Query()
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val isSuccess = status == DownloadManager.STATUS_SUCCESSFUL
                    selectedOption?.let { sendNotifications(isSuccess, it) }
                }
                Toast.makeText(this@MainActivity, "Download Completed", Toast.LENGTH_SHORT).show()
                custom_button.setDelayedCompleted()
            }
        }
    }

    private fun sendNotifications(success: Boolean, radioButtonOptions: RadioButtonOptionsEnum) {
        val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(this,
                success, radioButtonOptions)
    }

    private fun download(selectedOption: RadioButtonOptionsEnum?) {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(selectedOption?.let { getString(it.title) })
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
