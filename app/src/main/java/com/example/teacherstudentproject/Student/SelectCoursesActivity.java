package com.example.teacherstudentproject.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.example.teacherstudentproject.Welcome.WelcomeActivity;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SelectCoursesActivity extends AppCompatActivity implements View.OnClickListener {


    private SharedPreferences sharedPreferences;

    //Course Category
    private Spinner spn_course_cat;
    private ArrayList<String> arr_courses_cat;
    private ArrayList<String> arr_courses_cat_id;
    private String Course_Cat_ID;

    //Courses
    private SearchableSpinner spn_courses;
    private ArrayList<String> arr_courses;
    private ArrayList<String> arr_courses_id;
    private String Course_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_courses);

        sharedPreferences = getSharedPreferences("MyPre", MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //Initializing Views
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout){

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
            finish();
        }

        return true;
    }

    private void initViews(){

        //Course Category
        spn_course_cat = findViewById(R.id.spn_course_cat);
        arr_courses_cat = new ArrayList<>();
        arr_courses_cat_id = new ArrayList<>();
        getCoursesCategory();

        spn_course_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Course_Cat_ID = arr_courses_cat_id.get(position);
                if (!Course_Cat_ID.equals("0")){
                    getCourses(Course_Cat_ID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Courses
        spn_courses = findViewById(R.id.spn_courses);
        arr_courses = new ArrayList<>();
        arr_courses_id = new ArrayList<>();

        spn_courses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Course_ID = arr_courses_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCoursesCategory(){

        StringRequest req = new StringRequest(Request.Method.GET, Api.CoursesCategory_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            arr_courses_cat_id.add("0");
                            arr_courses_cat.add("Select Course Category");
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("categories");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                arr_courses_cat_id.add(object.getString("category_id"));
                                arr_courses_cat.add(object.getString("name"));
                            }
                            spn_course_cat.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                                    android.R.layout.simple_spinner_dropdown_item, arr_courses_cat));
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
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(req);
    }

    private void getCourses(final String CatID){

        arr_courses.clear();
        arr_courses_id.clear();

        StringRequest req = new StringRequest(Request.Method.POST, Api.Courses_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            spn_courses.setTitle("Select Courses");
                            arr_courses_id.add("0");
                            arr_courses.add("Select Course");
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("success");
                            if (status){
                                spn_courses.setVisibility(View.VISIBLE);
                                JSONArray jsonArray = jsonObject.getJSONArray("courses");
                                for (int i = 0;i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    arr_courses_id.add(object.getString("id"));
                                    arr_courses.add(object.getString("name"));
                                }

                                spn_courses.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                                        android.R.layout.simple_spinner_dropdown_item, arr_courses));
                            } else {
                                spn_courses.setVisibility(View.GONE);
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
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("category_id", CatID);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(req);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_find_teacher){

            if (spn_course_cat.getSelectedItemPosition() == 0){
                Toast.makeText(getApplicationContext(), "Select Course Category",
                        Toast.LENGTH_LONG).show();
            } else if (spn_courses.getSelectedItemPosition() == 0) {
                Toast.makeText(getApplicationContext(), "Select Course",
                        Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), TeacherListActivity.class);
                intent.putExtra("course_id", Course_ID);
                startActivity(intent);
                finish();
            }
        }
    }
}
