package com.example.dlapd.seoulcarmap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Activity";
    private static final int RC_SIGN_IN = 10;

    public FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private TextView email;
    private TextView userid;

    private CallbackManager mCallbackManager;

    private ImageButton join;
    private ImageButton login;
    private EditText email_login;
    private EditText pwd_login;
    private ProgressDialog mProgress;
    private TextView textviewFindPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // [START initialize_auth]
        //다음과 같이 로그인 작업의 onCreate 메소드에서 FirebaseAuth 개체의 공유 인스턴스를 가져옵니다.
        mAuth = FirebaseAuth.getInstance();

        // [END initialize_auth]
        // Configure Google Sign In
        //다음과 같이 GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출합니다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Button 1개
        SignInButton goologinbtn = (SignInButton) findViewById(R.id.google_login_btn);
        goologinbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        //페이스북 영역 시작
        mCallbackManager = CallbackManager.Factory.create();
        // Initialize Facebook Login button
        LoginButton loginButton = findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // 페이스북 영역 끝

        //그냥 이메일 로그인, 회원가입 영역
        join = (ImageButton) findViewById(R.id.main_join_btn);
        login = (ImageButton) findViewById(R.id.main_login_btn);
        email_login = (EditText) findViewById(R.id.main_email);
        pwd_login = (EditText) findViewById(R.id.main_pwd);
        textviewFindPassword = (TextView) findViewById(R.id.textViewFindpassword);
        mProgress = new ProgressDialog(this,R.style.ProgressDialogStyle);
        mProgress.setMessage("로그인 중입니다");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_login.getText().toString().trim();
                String pwd = pwd_login.getText().toString().trim();

                if (email.isEmpty() || pwd.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "email 또는 Password를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "email형식 불일치", Toast.LENGTH_SHORT).show();
                } else if (pwd.length() <= 5) {
                    Toast.makeText(getApplicationContext(), "Password를 6자리 이상 입력해주세요", Toast.LENGTH_SHORT).show();

                } else {
                    mProgress.show();
                    mAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    mProgress.dismiss();
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(LoginActivity.this, mAuth.getCurrentUser().getEmail() + " (으)로 로그인", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }


        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        textviewFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,FindActivity.class);
                startActivity(intent);


            }
        });

    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgress.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, mAuth.getCurrentUser().getDisplayName() + " (으)로 로그인", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(LoginActivity.this, mAuth.getCurrentUser().getDisplayName() + " (으)로 로그인",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else if (!task.isSuccessful()) {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "페이스북 로그인 실패",
                                    Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "User with Email id already exists",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    /*public class SessionCallback implements ISessionCallback {

        // 로그인에 성공한 상태
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        // 로그인에 실패한 상태
        @Override
        public void onSessionOpenFailed(KakaoException exception) { // 세션 연결이 실패했을때
            //Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        // 사용자 정보 요청
        public void requestMe() {
            // 사용자정보 요청 결과에 대한 Callback
            UserManagement.requestMe(new MeResponseCallback() {
                // 세션 오픈 실패. 세션이 삭제된 경우,
                @Override
                public void onSessionClosed(ErrorResult errorResult) { // 세션이 닫혀 실패한 경우로 에러 결과를 받습니다.
                    //Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());
                }

                // 회원이 아닌 경우,
                @Override
                public void onNotSignedUp() {
                    //Log.e("SessionCallback :: ", "onNotSignedUp");
                }

                // 사용자정보 요청에 성공한 경우,
                @Override
                public void onSuccess(UserProfile userProfile) { // Param : Long, 앱연결을 성공한 경우로 앱연결된 사용자 ID를 받습니다.

                    Log.e("UserProfile", userProfile.toString());
                    Log.e("SessionCallback :: ", "onSuccess");
                    String nickname = userProfile.getNickname();

                    //String email = userProfile.getEmail();
                    String profileImagePath = userProfile.getProfileImagePath();

                    String thumnailPath = userProfile.getThumbnailImagePath();

                    String UUID = userProfile.getUUID();

                    long id = userProfile.getId();
                    Log.e("Profile : ", nickname + "");

                    //Log.e("Profile : ", email + "");

                    Log.e("Profile : ", profileImagePath  + "");

                    Log.e("Profile : ", thumnailPath + "");

                    Log.e("Profile : ", UUID + "");

                    Log.e("Profile : ", id + "");

                    Toast.makeText(MainActivity.this,   userProfile.toString() + " 사용자 카카오톡 연동 성공.",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, DrawableMain.class);
                    startActivity(intent);
                    finish();



                }


                // 사용자 정보 요청 실패
                @Override
                public void onFailure(ErrorResult errorResult) { // 앱연결이 실패한 경우로 에러 결과를 받습니다.
                    Log.e("SessionCallback :: ", "onFailure : " + errorResult.getErrorMessage());
                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else { // 앱연결 실패 후 이벤트
                        redirectMainActivity();
                    }
                }
                public void redirectMainActivity(){// 카카오 앱 연결 실패 후 실행되는 메소드

                    Toast.makeText(MainActivity.this, "앱 연결 실패",
                            Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }



            });

        }

    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*카카오
        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);*/

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                mProgress.show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }
    //사용자가 정상적으로 로그인한 후에 GoogleSignInAccount 개체에서 ID 토큰을 가져와서
    // Firebase 사용자 인증 정보로 교환하고 Firebase 사용자 인증 정보를 사용해 Firebase에 인증합니다.
    private long time = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish();
        }
    }

}