package com.dwikiriyadi.ibli;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername,editTextPassword;
    private Button buttonLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonLogin = (Button) findViewById(R.id.login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        buttonLogin.setOnClickListener(this);
    }

    private void userLogin(){
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean("error")){
                                if (object.getInt("admin") == 1){
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Silahkan masuk melalui aplikasi desktop",
                                            Toast.LENGTH_LONG
                                    ).show();
                                } else if (object.getInt("admin") == 0) {
                                    if (object.get("role").equals("Mahasiswa")) {
                                        SharedPrefManager.getInstance(getApplicationContext())
                                                .userLogin(
                                                        object.getString("api_key")
                                                );
                                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Silahkan masuk melalui aplikasi desktop",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        object.getString("status"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonLogin){
            userLogin();
        }
    }
}
