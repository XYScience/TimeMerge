package com.science.timemerge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        PeriodTimeDialog dialog = new PeriodTimeDialog(this, "营业时间", "取消");
        dialog.initTime("12:00-18:00");
        dialog.setCloseListener(new PeriodTimeDialog.DialogCloseListener() {
            @Override
            public void confirm(String timeData) {
                Toast.makeText(MainActivity.this, timeData, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void delete() {
                Toast.makeText(MainActivity.this, "delete", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
