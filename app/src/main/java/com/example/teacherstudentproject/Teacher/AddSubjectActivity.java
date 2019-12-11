package com.example.teacherstudentproject.Teacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.example.teacherstudentproject.Constant.Api;
import com.example.teacherstudentproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddSubjectActivity extends AppCompatActivity {

    private MultiSpinnerSearch searchSpinner;
    private List<KeyPairBoolData> listArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        searchSpinner = (MultiSpinnerSearch) findViewById(R.id.spinner);
        listArray = new ArrayList<KeyPairBoolData>();
        getCourses();

        searchSpinner.setEmptyTitle("Not Data Found!");
        searchSpinner.setSearchHint("Find Data");

        searchSpinner.setItems(listArray, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i("TAG", items.get(i).getId() + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });
    }

    private void getCourses(){
        StringRequest req = new StringRequest(Request.Method.GET, Api.Courses_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("success");
                            if (status){
                                JSONArray jsonArray = jsonObject.getJSONArray("courses");
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    KeyPairBoolData h = new KeyPairBoolData();
                                    h.setId(Long.parseLong(object.getString("id")));
                                    h.setName(object.getString("name"));
                                    h.setSelected(false);
                                    listArray.add(h);
                                }
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
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(req);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
        finish();
    }
}
