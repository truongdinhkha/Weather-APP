package com.uit.myairquality;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.uit.myairquality.Interfaces.APIInterface;
import com.uit.myairquality.Model.APIClient;
import com.uit.myairquality.Model.Token;
import com.uit.myairquality.LoadingAlert;
import com.uit.myairquality.Model.TokenResponse;
import com.uit.myairquality.Model.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LogIn extends AppCompatActivity {

    EditText username, password;
    Button btnLogin, btnBackLogin;

    TextView btnForgotPassword;
    String User, Pass;
    APIInterface apiInterface;
    String client_id = "openremote";
    String grantType = "password";

    CardView cardView;
    LoadingAlert loadingAlert = new LoadingAlert(LogIn.this);
    String loginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChangeLanguages.loadLocaleChanged(LogIn.this);
        setContentView(R.layout.activity_login);
        apiInterface = APIClient.getClient(URL.mainURL).create(APIInterface.class);
        btnLogin = findViewById(R.id.btnLogin);
        btnBackLogin = findViewById(R.id.btnBackLogin);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cardView = findViewById(R.id.cardView);
        loginSuccess = getResources().getString(R.string.loginSuccess);
        TextView loginSuccess = new TextView(this);
        loginSuccess.setText(R.string.loginSuccess);

        // Hard code for test
        username.setText("user");
        password.setText("123");

        //Quay lại màn hình Homepage
        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, Homepage.class);
                startActivity(intent);
            }
        });
        //Quên mật khẩu
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, ResetPassword.class);
                startActivity(intent);
            }
        });
        //Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadingAlert.startAlertDialog();
                User = String.valueOf(username.getText());
                Pass = String.valueOf(password.getText());
                getToken(User, Pass);
            }
        });
    }

    public void getToken(String usr, String pwd) {
        Call<TokenResponse> call = apiInterface.Login(client_id, usr, pwd, grantType);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LogIn.this,loginSuccess, Toast.LENGTH_SHORT).show();
                    assert response.body() != null;
                    TokenResponse token = response.body();
//                    APIClient.token = com.uit.myairquality.Model.Token.access_token;
                    // Hard-code to Map Activity
                    Intent intent = new Intent(LogIn.this, Map.class);
                    intent.putExtra("access_token", token.getAccessToken());
//                    Intent intent = new Intent(LogIn.this, Map.class);
                    startActivity(intent);
                } else {
                    Log.d("Login", "fail" + response.message());
                    Toast.makeText(LogIn.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
                loadingAlert.closeAlertDialog();
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                loadingAlert.closeAlertDialog();
                Toast.makeText(LogIn.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
            }

        });
//        Intent intent = new Intent(LogIn.this, HomeFragment.class);
//        intent.putExtra("key", User); // "key" là tên để nhận dữ liệu ở Activity đích
//        startActivity(intent);
    }
}
