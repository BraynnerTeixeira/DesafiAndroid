package com.desafio.desafioapp.Facebook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.desafio.desafioapp.MainActivity;
import com.desafio.desafioapp.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FBLoginActivity extends AppCompatActivity {


    LoginButton loginButton;
    private CallbackManager callbackManager;

    //TextView nome;
    //ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblogin);

        loginButton = findViewById(R.id.login_button);


        callbackManager = CallbackManager.Factory.create();
        loginButton.setPermissions(Arrays.asList("email ","public_profile"));



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                startActivity(new Intent(FBLoginActivity.this, MainActivity.class));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken == null){

                Toast.makeText(FBLoginActivity.this, "Usuario Deslogado", Toast.LENGTH_SHORT).show();

            }
            else {
              UserProfile(currentAccessToken);

            }
        }
    };


    public void UserProfile(AccessToken newAccessToken){

        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @SuppressLint("CheckResult")
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {


                 /*   nome = findViewById(R.id.nome_perfil);
                    profile = findViewById(R.id.profile);

                    String first_name = object.getString("name");
                    String id = object.getString("id");

                    String image = "https://graph.facebook.com/"+id+"/picture?type=normal";

                    nome.setText(first_name);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(FBLoginActivity.this).load(image).into(profile);*/


            }
        });
        Bundle parametros = new Bundle();
        parametros.putString("fields", "id,name,email,picture.width(120).height(120)");
        request.setParameters(parametros);
        request.executeAsync();


    }

}
