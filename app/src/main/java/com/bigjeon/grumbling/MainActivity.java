package com.bigjeon.grumbling;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.grumbling.R;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private SignInButton Sign_In_Btn;
    private Uri imgUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Sign_In_Btn = findViewById(R.id.Main_Activity_Sign_In_Btn);

        //구글 로그인 옵션 설정
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //구글 로그인 버튼 클릭 이벤트
        Sign_In_Btn.setOnClickListener(v -> SignIn());
    }

    private void SignIn() {
        Intent Sign_In_Intent = mGoogleSignInClient.getSignInIntent();
        startActivityResult.launch(Sign_In_Intent);
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            //구글 로그인 성공시
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Toast.makeText(MainActivity.this, "구글 로그인 성공!", Toast.LENGTH_SHORT).show();
                            firebasseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void firebasseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "sign in with credetial :: Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            //Start_SetProfile();
                        }else {
                            Log.w(TAG, "signin with google failed", task.getException());
                            updateUI(null);
                        }
                    }
                });
        }
    //만약 처음 사용하는 유저라면 Set_Profile_Dialog띄워줌  기존 유저라면 바로 메인뷰로 이동
    private void updateUI(FirebaseUser user) {
        if(user != null){
            Toast.makeText(this, user + "로그인 성공", Toast.LENGTH_SHORT).show();
            Alert_SetProfile_Dialog();
        }else{
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
    }
    private void Alert_SetProfile_Dialog(){
        Set_User_Profile_Dialog dialog = new Set_User_Profile_Dialog();
    }
}