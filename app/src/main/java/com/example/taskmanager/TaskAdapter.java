package com.example.taskmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private LayoutInflater inflater;
    private Context context;
    private TaskEvents taskEvents;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private TaskDbHelper dbHelper;
    public interface TaskEvents {
        void onDeleteTask(int taskId);
        void onEditTask(int taskId);
    }
    public TaskAdapter(Context context, List<Task> tasks, TaskEvents taskEvents, TaskDbHelper dbHelper) {
        this.context = context;
        this.tasks = tasks;
        this.inflater = LayoutInflater.from(context);
        this.taskEvents = taskEvents;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }
    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView title, description, dueDate;
        private CheckBox checkBox;
        private Button btnEdit, btnDelete;
        private View detailsLayout;
        private boolean isExpanded = false;
        public TaskViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            description = itemView.findViewById(R.id.textViewDescription);
            dueDate = itemView.findViewById(R.id.textViewDueDate);
            checkBox = itemView.findViewById(R.id.checkBox);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            btnEdit = itemView.findViewById(R.id.buttonEdit);
            btnDelete = itemView.findViewById(R.id.buttonDelete);

            btnEdit.setOnClickListener(v -> taskEvents.onEditTask(tasks.get(getAdapterPosition()).getId()));
            btnDelete.setOnClickListener(v -> taskEvents.onDeleteTask(tasks.get(getAdapterPosition()).getId()));

            itemView.setOnClickListener(v -> toggleDetails());

            checkBox.setOnClickListener(v -> {
                Task task = tasks.get(getAdapterPosition());
                boolean newStatus = checkBox.isChecked();
                task.setCompleted(newStatus);
                int result = dbHelper.updateTaskCompletion(task.getId(), newStatus);
                if (result > 0) {
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
        public void bind(Task task) {
            title.setText(task.getTitle());
            description.setText(task.getDescription());
            dueDate.setText(sdf.format(task.getDueDate()));
            checkBox.setChecked(task.isCompleted());

            resetVisibility();
        }
        private void resetVisibility() {
            description.setVisibility(View.GONE);
            dueDate.setVisibility(View.GONE);
            detailsLayout.setVisibility(View.GONE);
        }
        public void toggleDetails() {
            isExpanded = !isExpanded;
            description.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            dueDate.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            detailsLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }
    }
}