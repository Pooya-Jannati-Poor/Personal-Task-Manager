package ir.pooyadev.data.receiver.reminder

import android.R
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import ir.pooyadev.domain.usecases.local.FetchTasksByTaskIdUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var fetchTasksByTaskIdUseCase: FetchTasksByTaskIdUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {

                val taskId = intent?.getLongExtra("TASK_ID", -1L)
                if (taskId != -1L) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val task = fetchTasksByTaskIdUseCase.invoke(taskId!!)
                        if (task != null) {
                            showNotification(it, task.id.toInt(), task.taskTitle)
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, notificationId: Int, taskTitle: String) {
        val channelId = "TASK_REMINDER_CHANNEL_ID"

        val notificationTitle = "Task Reminder"
        val notificationText = "Task Title: $taskTitle"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_lock_idle_alarm)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}