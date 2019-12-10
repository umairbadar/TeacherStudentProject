package com.example.teacherstudentproject.Teacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.teacherstudentproject.R;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_submit){
            finish();
        }
    }

    /*@Override
    public void onBackPressed() {

        startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
        finish();
    }*/
}
