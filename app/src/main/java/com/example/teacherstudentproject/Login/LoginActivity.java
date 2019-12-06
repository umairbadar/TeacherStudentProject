package com.example.teacherstudentproject.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.example.teacherstudentproject.Student.SelectCoursesActivity;
import com.example.teacherstudentproject.Teacher.TeacherActivity;
import com.example.teacherstudentproject.Welcome.WelcomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_email, et_password;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        sharedPreferences = getSharedPreferences("MyPre", MODE_PRIVATE);

        //Initializing Views
        initViews();
    }

    private void initViews() {

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
    }

    private void loginUser() {

        StringRequest req = new StringRequest(Request.Method.POST, Api.Login_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("success");
                            if (status) {
                                JSONObject innerObj = jsonObject.getJSONObject("data");

                                String customerGroup = innerObj.getString("customer_group_id");

                                editor = sharedPreferences.edit();
                                editor.putString("customer_id", innerObj.getString("customer_id"));
                                editor.putString("customer_group", customerGroup);
                                editor.putString("firstname", innerObj.getString("firstname"));
                                editor.putString("lastname", innerObj.getString("lastname"));
                                editor.putString("email", innerObj.getString("email"));
                                editor.putString("telephone", innerObj.getString("telephone"));

                                JSONObject obj = innerObj.getJSONObject("address");

                                editor.putString("address", obj.getString("address_1"));
                                editor.putString("latitude", obj.getString("latitude"));
                                editor.putString("longitude", obj.getString("longitude"));
                                editor.putString("city", obj.getString("city"));
                                editor.putString("zone_id", obj.getString("zone_id"));
                                editor.putString("zone", obj.getString("zone"));
                                editor.putString("country_id", obj.getString("country_id"));
                                editor.putString("country", obj.getString("country"));
                                editor.putBoolean("saveLogin", true);
                                editor.apply();

                                if (customerGroup.equals("1")) {
                                    startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), SelectCoursesActivity.class));
                                    finish();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Email and Password",
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
                map.put("email", et_email.getText().toString());
                map.put("password", et_password.getText().toString());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(req);
    }

    private void validation() {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(et_email.getText())) {
            et_email.setError("Please enter email address");
            et_email.requestFocus();
        } else if (!et_email.getText().toString().matches(emailPattern)) {
            et_email.setError("Please enter valid email address");
            et_email.requestFocus();
        } else if (TextUtils.isEmpty(et_password.getText())) {
            et_password.setError("Please enter password");
            et_password.requestFocus();
        } else {
            loginUser();
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_login) {
            validation();
            /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=24.9180,67.0971"));
            startActivity(intent);*/
        }
    }
}
