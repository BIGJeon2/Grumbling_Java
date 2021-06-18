package com.bigjeon.grumbling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.grumbling.R;
import com.example.grumbling.databinding.Login_Binding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Google_Login_Activity extends AppCompatActivity {
    //구글 로그인 id값 가져와 firestore의 값에 해당하는 값이 있는지 확인, 있으면 SharedPreference에 저장, 없으면 프로필 설정창으로 이동
    Login_Binding binding;
    private static final String TAG = "GoogleActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private static int Sign_In_Code = 1001;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_google_login);
        binding.setLoginActivity(this);

        //구글 로그인 옵션 설정
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //구글 로그인 버튼 클릭 이벤트
        binding.GoogleSignInBtn.setOnClickListener(v -> SignIn());
    }

    private void SignIn() {
        Intent Sign_In_Intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(Sign_In_Intent, Sign_In_Code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1001:
                Log.e("RESULT", Auth.GoogleSignInApi.getSignInResultFromIntent(data).getStatus().toString());
                if(resultCode == RESULT_OK){
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Toast.makeText(Google_Login_Activity.this, "구글 로그인 성공!", Toast.LENGTH_SHORT).show();
                        firebasseAuthWithGoogle(account.getIdToken());
                    }catch (ApiException e){
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(Google_Login_Activity.this, "구글 로그인 실패!" + resultCode, Toast.LENGTH_SHORT).show();
                }break;
        }
    }

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

    //FireStore에 UID가 없다면 프로필 설정 창으로, 기존 유저라면 바로 메인뷰로 이동
    private void updateUI(FirebaseUser user) {
        if(user != null){
            String My_Uid = user.getUid();
            Intent_To_Set_Profile(My_Uid);
            Toast.makeText(this, user + "로그인 성공", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
    }
    //현재 로그인 된 User의 UID를 가지고 프로필 설정으로 이동(신규 유저에게만 해당)
    private void Intent_To_Set_Profile(String My_Uid){
        Intent Set_Profile_Intent = new Intent(this, Set_User_Profile_Activity.class);
        Set_Profile_Intent.putExtra("UID", My_Uid);
        Set_Profile_Intent.putExtra("CODE", "FIRST_SET");
        startActivity(Set_Profile_Intent);
    }
}