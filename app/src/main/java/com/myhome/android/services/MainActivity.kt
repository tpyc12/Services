package com.myhome.android.services

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.myhome.android.services.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var page = 0

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = (service as? MyForegroundService.LocalBinder) ?: return
            val foregroundService = binder.getService()
            foregroundService.onProgressChanged = { progress ->
                binding.progressBarLoading.progress = progress
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.simpleService.setOnClickListener {
            stopService(MyForegroundService.newIntent(this))
            startService(MyService.newIntent(this, 25))
        }
        binding.foregroundService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyForegroundService.newIntent(this)
            )
        }
        binding.intentService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyIntentService.newIntent(this)
            )
        }
        binding.jobSchedulerService.setOnClickListener {
            val componentName = ComponentName(this, MyJobService::class.java)

            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(true)
                .build()

            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(page++)
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
            } else {
                startService(MyIntentServiceForJobService.newIntent(this, page++))
            }
        }
        binding.jobIntentService.setOnClickListener {
            MyJobIntentService.enqueue(this, page++)
        }
        binding.workManager.setOnClickListener {
            val workerManager = WorkManager.getInstance(applicationContext)
            workerManager.enqueueUniqueWork(
                MyWorker.WORK_NAME,
                ExistingWorkPolicy.APPEND,
                MyWorker.makeRequest(page++)
            )
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(
            MyForegroundService.newIntent(this),
            serviceConnection,
            0
        )
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
}