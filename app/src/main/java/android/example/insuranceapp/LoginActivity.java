package android.example.insuranceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import android.example.models.User;
import android.example.utils.Check;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static final int REQUEST_SIGNUP = 0;
    private Context mContext;
    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;
    private int finalResponseCode;
    private User user;
    private Bundle bundle;
    private String hosted_url;
    private String login_route;
    private String signup_route;
    private Check genericCheck;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);

        genericCheck = new Check(this);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(genericCheck.checkInternetConnection())
                    login();
            }
        });

        //Pull the Bluemix url and other API routes.
        mContext = getApplicationContext();
        hosted_url = mContext.getString(R.string.hosted_url);
        signup_route = mContext.getString(R.string.signup);
        login_route =  mContext.getString(R.string.login);

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Navigate to Signup page
                if(genericCheck.checkInternetConnection()) {
                    Uri uri = Uri.parse(hosted_url + signup_route);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        //progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.show();

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        bundle = new Bundle();
        bundle.putString("email",email);

        Thread thread = new Thread(new Runnable(){
            public void run() {
                HttpURLConnection client = null;

                try {


                    //Http POST call for login
                    String postUrl=hosted_url + login_route +"?username="+ email + "&password=" + password;
                    URL url = new URL(postUrl);
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("POST");
                    client.setInstanceFollowRedirects(false);
                    client.setDoOutput(true);

                    Log.d(TAG,"POSTResponseCode" + client.getResponseCode());

                    if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        StringBuilder result = new StringBuilder();
                        InputStream in = new BufferedInputStream(client.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        Log.d(TAG,"RESULT"+result.toString());

                        // JSon to Java Object mapping
                        Gson gson = new Gson();
                        user = gson.fromJson(result.toString(),User.class);
                        Log.d(TAG,"USER:" + user.username);

                        // capture the session cookie for later use
                        String cookieValue =  client.getHeaderField("Set-Cookie");
                        Log.d(TAG,"CookieValue" + cookieValue);
                        bundle.putString("cookie",cookieValue);
                    }

                    finalResponseCode = client.getResponseCode();
                    Log.d(TAG,"ResponseCode of GET" + finalResponseCode);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    client.disconnect();

                }
            }
        });

        thread.start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if(user !=null && finalResponseCode == 200) {
                            bundle.putString("fname",user.fname);
                            bundle.putString("lname",user.lname);
                            onLoginSuccess();
                            progressDialog.dismiss();
                        }
                        else
                        {
                            onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(this, LandingActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Invalid username or password", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Enter password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}
