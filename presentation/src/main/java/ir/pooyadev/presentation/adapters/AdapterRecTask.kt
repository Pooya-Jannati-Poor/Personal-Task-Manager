package ir.pooyadev.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ir.pooyadev.domain.model.local.Task
import ir.pooyadev.presentation.R
import ir.pooyadev.presentation.databinding.LayoutRecTaskBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdapterRecTask(
    private val onTaskClicked: (Task) -> Unit,
    private val onTaskRemovedClicked: (Task) -> Unit,
) :
    RecyclerView.Adapter<AdapterRecTask.ItemAdapter>() {

    private lateinit var bindingAdapter: LayoutRecTaskBinding

    private var lsModelTasks: ArrayList<Task> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setInitialTasks(tasks: ArrayList<Task>) {
        lsModelTasks.clear()
        lsModelTasks.addAll(tasks)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter {
        val inflater = LayoutInflater.from(parent.context)
        bindingAdapter = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_rec_task,
            parent,
            false
        )
        return ItemAdapter(bindingAdapter)
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {

        val modelTask = lsModelTasks[position]
        holder.tvTitle.text = modelTask.taskTitle
        holder.tvDescription.text = modelTask.taskDescription


        val timeInMillis: Long =
            if (modelTask.taskAlarmDate != null) modelTask.taskAlarmDate!! else modelTask.taskCreatedAt
        val date = Date(timeInMillis)
        val format = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        val formattedDate = format.format(date)
        holder.tvAlarmDate.text = formattedDate

        if (modelTask.taskAlarmDate != null) {
            holder.imgReminder.visibility = View.VISIBLE
        } else {
            holder.imgReminder.visibility = View.GONE
        }

        holder.imgMoreOptions.setOnClickListener {
            onTaskRemovedClicked.invoke(modelTask)
        }


        holder.itemView.setOnClickListener {
            onTaskClicked.invoke(modelTask)
        }


    }

    override fun getItemCount(): Int = lsModelTasks.size

    inner class ItemAdapter(bindingAdapter: LayoutRecTaskBinding) :
        RecyclerView.ViewHolder(bindingAdapter.root) {

        val tvTitle = bindingAdapter.tvTitle
        val tvDescription = bindingAdapter.tvDescription
        val imgMoreOptions = bindingAdapter.imgMoreOptions
        val tvAlarmDate = bindingAdapter.tvAlarmDate
        val imgReminder = bindingAdapter.imgReminder

    }

}