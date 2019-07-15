package com.example.baizhi_xu.signin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baizhi_xu.signin.model.People;
import com.example.baizhi_xu.signin.model.RecordSignRate;
import com.example.baizhi_xu.signin.unit.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeacherActivity extends AppCompatActivity {
    private People people;
    private TextView name;
    private Button logout,inputPlan,search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        people = (People) getIntent().getSerializableExtra("People");
        setContentView(R.layout.activity_teacher);
        name = findViewById(R.id.tname);
        name.setText(people.getName());
        logout = findViewById(R.id.logout);
        inputPlan = findViewById(R.id.inputPlan);
        search = findViewById(R.id.search);
        inputPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InputPlanActivity.class);
                intent.putExtra("People", people);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("People", people);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
