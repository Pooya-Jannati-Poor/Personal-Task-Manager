package ir.pooyadev.presentation.view.fragments;

import static androidx.navigation.Navigation.findNavController;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import ir.pooyadev.domain.model.local.Task;
import ir.pooyadev.presentation.R;
import ir.pooyadev.presentation.databinding.FragmentTaskFormBinding;
import ir.pooyadev.presentation.view.base.BaseFragment;
import ir.pooyadev.presentation.viewmodel.TaskFormFragmentViewModel;

@AndroidEntryPoint
public class TaskFormFragment extends BaseFragment<FragmentTaskFormBinding> {

    public TaskFormFragment() {
        super(R.layout.fragment_task_form);
    }

    private TaskFormFragmentViewModel viewModel;

    private Long taskId;

    private Task modelTask;
    private Long reminderDateTime = null;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkRequestPermissionResult();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();

        fetchTaskIdFromArgs();

        checkTaskIdIsNewOrEdit();

        collectTask();

        setupSaveTaskButton();

        checkTaskOperation();

    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(TaskFormFragmentViewModel.class);
    }

    private void fetchTaskIdFromArgs() {
        TaskFormFragmentArgs taskFormFragmentArgs = TaskFormFragmentArgs.fromBundle(getArguments());
        taskId = taskFormFragmentArgs.getTaskId();
    }

    private void checkTaskIdIsNewOrEdit() {
        if (taskId == -1L) {
            changeTaskFormForNew();
            setupReminderSwitch();
        } else {
            changeTaskFormForEdit();
            fetchTaskByTaskIdFromDb();
        }
    }

    private void fetchTaskByTaskIdFromDb() {
        viewModel.fetchTaskByTaskId(taskId);
    }

    private void collectTask() {
        viewModel.getTaskLiveData().observe(getViewLifecycleOwner(), task -> {
            modelTask = task;
            setTaskDataInView();
        });
    }

    private void setTaskDataInView() {
        bindingFragment.edTaskTitle.setText(modelTask.getTaskTitle());
        bindingFragment.edTaskDescription.setText(modelTask.getTaskDescription());
        if (modelTask.getTaskAlarmDate() != null) {
            bindingFragment.switchReminder.setChecked(true);
        }
        setupReminderSwitch();
    }

    private void setupReminderSwitch() {
        bindingFragment.switchReminder.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                showDatePicker();
            } else {
                clearReminderDate();
            }
        });
    }

    private void clearReminderDate() {
        bindingFragment.switchReminder.setText("");
        bindingFragment.switchReminder.setChecked(false);
        reminderDateTime = null;
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Choose Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(this::showTimePicker);

        datePicker.addOnNegativeButtonClickListener(view -> {
            clearReminderDate();
        });

        datePicker.addOnCancelListener(dialogInterface -> {
            clearReminderDate();
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker(Long selectedDate) {

        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTitleText("Choose Time")
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(currentHour)
                .setMinute(currentMinute)
                .build();

        timePicker.addOnPositiveButtonClickListener(view -> {
            int selectedHour = timePicker.getHour();
            int selectedMinute = timePicker.getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selectedDate);
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date finalDate = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            String formattedDateTime = sdf.format(finalDate);

            bindingFragment.switchReminder.setText(formattedDateTime);
            reminderDateTime = calendar.getTimeInMillis();
            modelTask = new Task(0, "", "", calendar.getTimeInMillis(), 1L);

        });

        timePicker.addOnNegativeButtonClickListener(view -> {
            clearReminderDate();
        });

        timePicker.addOnCancelListener(dialogInterface -> {
            clearReminderDate();
        });

        timePicker.show(getParentFragmentManager(), "TIME_PICKER");
    }

    private void setupSaveTaskButton() {
        bindingFragment.btnSaveTask.setOnClickListener(view -> {
            onSaveTaskClicked();
        });
    }

    private boolean taskDataValidation() {
        boolean result = true;
        if (Objects.requireNonNull(bindingFragment.edTaskTitle.getText()).toString().isEmpty()) {
            bindingFragment.edLayoutTaskTitle.setError(getString(R.string.required));
            result = false;
        }
        if (Objects.requireNonNull(bindingFragment.edTaskDescription.getText()).toString().isEmpty()) {
            bindingFragment.edLayoutTaskDescription.setError(getString(R.string.required));
            result = false;
        }
        return result;
    }

    private boolean requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return false;
            }
        }
        return true;
    }

    private void checkRequestPermissionResult() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(requireContext(), "Notification Permission Denied.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void onSaveTaskClicked() {
        if (taskDataValidation()) {

            if (requestNotificationPermission()) {

                if (reminderDateTime != null) {
                    if (!checkAndRequestAlarmPermission()) {
                        return;
                    }
                }

                Task task = getTaskDataFromView();
                handleSaveTask(task);
            } else {
                Toast.makeText(requireContext(), "Need Notification Permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean checkAndRequestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(requireContext(), "Please enable precise alarms permission.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                startActivity(intent);
                return false;
            }
        }
        return true;
    }

    private Task getTaskDataFromView() {
        String taskTitle = bindingFragment.edTaskTitle.getText().toString();
        String taskDescription = bindingFragment.edTaskDescription.getText().toString();
        long currentTimeMillis = System.currentTimeMillis();
        long id = (taskId == -1L) ? 0 : taskId;
        long taskCreatedAt = (taskId == -1L) ? currentTimeMillis : modelTask.getTaskCreatedAt();

        return new Task(id, taskTitle, taskDescription, reminderDateTime, taskCreatedAt);
    }

    private void handleSaveTask(Task task) {
        if (taskId == -1L) {
            insertTask(task);
        } else {
            updateTask(task);
        }
    }

    private void insertTask(Task task) {
        viewModel.insertTask(task);
    }

    private void updateTask(Task task) {
        viewModel.updateTask(task);
    }

    private void checkTaskOperation() {
        viewModel.isTaskSaved().observe(getViewLifecycleOwner(), isTaskSaved -> {
            if (isTaskSaved) {
                findNavController(bindingFragment.getRoot()).popBackStack();
            }
        });
    }


    private void changeTaskFormForNew() {
        bindingFragment.tvTaskFormTitle.setText(R.string.add_new_task);
    }

    private void changeTaskFormForEdit() {
        bindingFragment.tvTaskFormTitle.setText(R.string.edit_task);
    }
}