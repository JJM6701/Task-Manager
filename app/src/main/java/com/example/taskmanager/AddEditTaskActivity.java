package com.example.taskmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditTaskActivity extends AppCompatActivity {
    private boolean isEditing;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        taskId = getIntent().getIntExtra("TASK_ID", -1);
        isEditing = taskId != -1;

        TaskDbHelper dbHelper = new TaskDbHelper(this);

        EditText editTextDueDate = findViewById(R.id.editTextDueDate);
        editTextDueDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year1, monthOfYear, dayOfMonth);
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        editTextDueDate.setText(dateFormatter.format(newDate.getTime()));
                    }, year, month, day);
            datePickerDialog.show();
        });

        if (isEditing) {
            Task task = dbHelper.getTask(taskId);
            ((EditText) findViewById(R.id.editTextTitle)).setText(task.getTitle());
            ((EditText) findViewById(R.id.editTextDescription)).setText(task.getDescription());
            ((EditText) findViewById(R.id.editTextDueDate)).setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(task.getDueDate()));
            ((CheckBox) findViewById(R.id.checkBoxCompleted)).setChecked(task.isCompleted());
        }

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(view -> {
            String title = ((EditText) findViewById(R.id.editTextTitle)).getText().toString().trim();
            String description = ((EditText) findViewById(R.id.editTextDescription)).getText().toString().trim();
            String dueDate = ((EditText) findViewById(R.id.editTextDueDate)).getText().toString().trim();

            if (title.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Title and due date cannot be empty.", Toast.LENGTH_LONG).show();
                return;
            }

            boolean completed = ((CheckBox) findViewById(R.id.checkBoxCompleted)).isChecked();

            if (isEditing) {
                dbHelper.updateTask(taskId, title, description, dueDate, completed);
            } else {
                taskId = (int) dbHelper.addTask(title, description, dueDate, completed);
            }

            Intent returnIntent = new Intent();
            returnIntent.putExtra("modifiedTaskId", taskId);
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }
}
