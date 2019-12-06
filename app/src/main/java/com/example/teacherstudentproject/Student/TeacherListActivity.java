package com.example.teacherstudentproject.Student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teacherstudentproject.Constant.Api;
import com.example.teacherstudentproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class TeacherListActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String latitude, longitude;

    private String Course_ID;

    private RecyclerView recyclerView_teachers;
    private Adapter_TeacherList adapter;
    private List<Model_TeacherList> arr_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        sharedPreferences = getSharedPreferences("MyPre", MODE_PRIVATE);
        latitude = sharedPreferences.getString("latitude", "");
        longitude = sharedPreferences.getString("longitude", "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //Initializing Views
        initView();

        Intent intent = getIntent();
        Course_ID = intent.getStringExtra("course_id");
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(getApplicationContext(), SelectCoursesActivity.class));
        finish();
    }

    private void initView() {

        recyclerView_teachers = findViewById(R.id.recyclerView_teachers);
        recyclerView_teachers.setLayoutManager(new GridLayoutManager(this, 1));
        arr_list = new ArrayList<>();
        adapter = new Adapter_TeacherList(arr_list, getApplicationContext());
        recyclerView_teachers.setAdapter(adapter);
        getTeachers();
    }

    private void getTeachers() {

        StringRequest req = new StringRequest(Request.Method.POST, Api.TeacherListing_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("success");
                            if (status) {
                                JSONArray jsonArray = jsonObject.getJSONArray("teachers");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("teacher_id");
                                    String name = object.getString("firstname") + " " + object.getString("lastname");
                                    String distance = object.getString("distance");

                                    Model_TeacherList item = new Model_TeacherList(
                                            id,
                                            name,
                                            distance + " km away"
                                    );
                                    arr_list.add(item);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("error"),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("course_id", Course_ID);
                map.put("latitude", latitude);
                map.put("longitude", longitude);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(req);
    }
}