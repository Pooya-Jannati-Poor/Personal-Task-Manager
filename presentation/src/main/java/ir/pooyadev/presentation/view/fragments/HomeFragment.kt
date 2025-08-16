package ir.pooyadev.presentation.view.fragments

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ir.pooyadev.data.worker.SyncWorker
import ir.pooyadev.domain.model.local.SortOrder
import ir.pooyadev.domain.model.local.Task
import ir.pooyadev.presentation.R
import ir.pooyadev.presentation.adapters.AdapterRecTask
import ir.pooyadev.presentation.adapters.TaskLastItemMarginDecoration
import ir.pooyadev.presentation.databinding.FragmentHomeBinding
import ir.pooyadev.presentation.util.TaskUiState
import ir.pooyadev.presentation.view.base.BaseFragment
import ir.pooyadev.presentation.viewmodel.HomeFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.getValue

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeFragmentViewModel by viewModels()
    private lateinit var adapterRecTask: AdapterRecTask

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    fragmentContext,
                    "Need notification permission to show reminder.",
                    Toast.LENGTH_LONG
                ).show()
            }
            checkExactAlarmPermission()
        }

    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            startDataFlow()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecTaskAdapter()

        setupSortMenu()

        setupAddTaskFab()

        askForNotificationPermission()

    }

    private fun setupSortMenu() {
        bindingFragment.imgSortOptions.setOnClickListener { view ->
            showSortPopupMenu(view)
        }
    }

    private fun showSortPopupMenu(anchorView: View) {
        val popup = PopupMenu(fragmentContext, anchorView)
        val inflater: MenuInflater = popup.menuInflater

        inflater.inflate(R.menu.home_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_sort_by_newest -> {
                    viewModel.changeSortOrder(SortOrder.BY_NEWEST)
                    true
                }

                R.id.action_sort_by_oldest -> {
                    viewModel.changeSortOrder(SortOrder.BY_OLDEST)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startDataFlow()
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager =
                requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                startDataFlow()
            } else {
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also { intent ->
                    settingsLauncher.launch(intent)
                }
            }
        } else {
            startDataFlow()
        }
    }

    private fun startDataFlow() {
        viewModel.startLoadingData()

        collectUiState()
        schedulePeriodicSync()
    }

    private fun initRecTaskAdapter() {
        adapterRecTask = AdapterRecTask({ task ->
            navigateToAddUpdateFragment(task.id)
        }, { task ->
            showDeleteConfirmationDialog(task)
        })
        val linearLayoutManager =
            LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
        bindingFragment.recTasks.layoutManager = linearLayoutManager
        bindingFragment.recTasks.adapter = adapterRecTask

        val marginPx = (8 * resources.displayMetrics.density).toInt()
        bindingFragment.recTasks.addItemDecoration(
            TaskLastItemMarginDecoration(marginPx)
        )

    }

    private fun showDeleteConfirmationDialog(task: Task) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure to delete ${task.taskTitle}?") // پیام دیالوگ
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.deleteTask(task)
                dialog.dismiss()
            }
            .show()
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                bindingFragment.progressBarLoading.isVisible = state is TaskUiState.Loading

                when (state) {
                    is TaskUiState.Success -> {
                        bindingFragment.recTasks.isVisible = true
                        bindingFragment.tvEmptyTaskList.isVisible = false
                        adapterRecTask.setInitialTasks(ArrayList(state.tasks))
                    }

                    is TaskUiState.Empty -> {
                        bindingFragment.recTasks.isVisible = false
                        bindingFragment.tvEmptyTaskList.isVisible = true
                        adapterRecTask.setInitialTasks(arrayListOf())
                    }

                    is TaskUiState.Error -> {
                        bindingFragment.recTasks.isVisible = false
                        bindingFragment.tvEmptyTaskList.isVisible = false
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }

                    is TaskUiState.Loading -> {
                        bindingFragment.recTasks.isVisible = false
                        bindingFragment.tvEmptyTaskList.isVisible = false
                    }
                }
            }
        }
    }

    private fun navigateToAddUpdateFragment(taskId: Long = -1L) {

        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToTaskFormFragment(
                taskId
            )
        )

    }

    private fun setupAddTaskFab() {
        bindingFragment.fabAddTask.setOnClickListener {
            navigateToAddUpdateFragment()
        }
    }

    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 16,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext().applicationContext).enqueueUniquePeriodicWork(
            "TaskSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }

}