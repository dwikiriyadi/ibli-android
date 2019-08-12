package com.dwikiriyadi.ibli;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView qrcode;
    private TextView textViewNama, textViewRole;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        qrcode = (ImageView) findViewById(R.id.qrcode);
        textViewNama = (TextView) findViewById(R.id.nama_user);
        textViewRole = (TextView) findViewById(R.id.role_user);
        buttonLogout = (Button) findViewById(R.id.logoutButton);

        getProfile();

        buttonLogout.setOnClickListener(this);
        qrcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == qrcode) {
            startActivity(new Intent(this, QrcodeActivity.class));
        }
        if (v == buttonLogout){
            SharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public void getProfile() {
        final String token = SharedPrefManager.getInstance(this).getToken();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean("error")){
                                JSONArray result = object.getJSONArray("result");
                                JSONObject profil = result.getJSONObject(0);
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .profile(
                                                profil.getString("nama_lengkap"),
                                                profil.getString("no_induk"),
                                                profil.getString("role"),
                                                profil.getInt("admin")
                                        );
                                textViewNama.setText(profil.getString("nama_lengkap"));
                                textViewRole.setText(profil.getString("role"));
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                params.put("api_key", token);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
