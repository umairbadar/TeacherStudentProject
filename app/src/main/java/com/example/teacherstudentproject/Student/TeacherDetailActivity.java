package com.example.teacherstudentproject.Student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TeacherDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_name, tv_email, tv_phone, tv_country, tv_city, tv_address, tv_education, tv_specialization,
            tv_experience, tv_courses;

    private String Teacher_ID, lat = "", lng = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //Initializing Views
        initViews();

        Intent intent = getIntent();
        Teacher_ID = intent.getStringExtra("teacher_id");
        //Toast.makeText(getApplicationContext(), intent.getStringExtra("teacher_id"), Toast.LENGTH_LONG).show();
    }

    private void initViews() {

        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_phone = findViewById(R.id.tv_phone);
        tv_country = findViewById(R.id.tv_country);
        tv_city = findViewById(R.id.tv_city);
        tv_address = findViewById(R.id.tv_address);
        tv_education = findViewById(R.id.tv_education);
        tv_specialization = findViewById(R.id.tv_specialization);
        tv_experience = findViewById(R.id.tv_experience);
        tv_courses = findViewById(R.id.tv_courses);

        //Fetching Details of teacher
        getDetails();
    }

    private void getDirections(){
        if (!lat.equals("") && !lng.equals("")) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lng));
            startActivity(intent);
        }
    }

    private void getDetails() {

        StringRequest req = new StringRequest(Request.Method.POST, Api.TeacherDetail_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("success");
                            if (status) {

                                JSONObject innerObj = jsonObject.getJSONObject("teacher");
                                String name = innerObj.getString("firstname") + " " + innerObj.getString("lastname");
                                tv_name.setText(name);
                                tv_email.setText(innerObj.getString("email"));
                                tv_phone.setText(innerObj.getString("telephone"));
                                tv_country.setText(innerObj.getString("country"));
                                tv_city.setText(innerObj.getString("city"));
                                tv_address.setText(innerObj.getString("address"));
                                tv_education.setText(innerObj.getString("education"));
                                tv_specialization.setText(innerObj.getString("specialization"));
                                tv_experience.setText(innerObj.getString("experience"));
                                lat = innerObj.getString("latitude");
                                lng = innerObj.getString("longitude");
                                JSONArray jsonArray = innerObj.getJSONArray("courses");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String course_name = obj.getString("name");
                                    stringBuilder.append(course_name);
                                    stringBuilder.append(", ");
                                }
                                String courses = stringBuilder.toString();
                                if (courses.endsWith(" ")) {
                                    courses = courses.substring(0, courses.length() - 2);
                                }
                                tv_courses.setText(courses);

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
                map.put("teacher_id", Teacher_ID);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(req);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_get_directions) {
            getDirections();
        }
    }
}
