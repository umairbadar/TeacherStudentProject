package com.example.teacherstudentproject.teacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teacherstudentproject.endpoints.Api;
import com.example.teacherstudentproject.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class AddNewCourseActivity extends AppCompatActivity implements View.OnClickListener {

    private SearchableSpinner spn_course_cat;
    private ArrayList<String> arr_course_cat;
    private ArrayList<String> arr_course_cat_id;
    private String course_Cat_ID;

    private EditText et_course_name, et_subject_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_course);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //Initializing Views
        inieViews();
    }

    private void inieViews(){

        et_course_name = findViewById(R.id.et_course_name);
        et_subject_name = findViewById(R.id.et_subject_name);

        spn_course_cat = findViewById(R.id.spn_courses_cat);
        arr_course_cat = new ArrayList<>();
        arr_course_cat_id = new ArrayList<>();
        getCourseCat();

        spn_course_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                course_Cat_ID = arr_course_cat_id.get(position);
                if (course_Cat_ID.equals("1")){
                    et_subject_name.setVisibility(View.VISIBLE);
                } else {
                    et_subject_name.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getCourseCat(){

        StringRequest req = new StringRequest(Request.Method.GET, Api.CoursesCategory_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            spn_course_cat.setTitle("Select Courses");

                            arr_course_cat.add("Select Course Catgory");
                            arr_course_cat_id.add("0");

                            arr_course_cat.add("Add new Course Category");
                            arr_course_cat_id.add("1");

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("categories");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                arr_course_cat_id.add(object.getString("category_id"));
                                arr_course_cat.add(object.getString("name"));
                            }
                            spn_course_cat.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                                    android.R.layout.simple_spinner_dropdown_item, arr_course_cat));

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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), CoursesActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

    }
}
