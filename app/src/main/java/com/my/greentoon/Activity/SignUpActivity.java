package com.my.greentoon.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.my.greentoon.Model.User;
import com.my.greentoon.R;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private EditText edEmail, edPassword, edConfirmPassword;
    private Button btSignup, btBack;
    private TextView tvGreentoon;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private boolean isAdminConfirmationEnabled = false;
    private int clickCount = 0;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);
        btSignup = findViewById(R.id.btSignup);

        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminConfirmationEnabled) {
                    // Nếu đã bật xác nhận admin, gửi email xác nhận cho admin
                    sendAdminConfirmationEmail(edEmail.getText().toString().trim());
                } else {
                    // Nếu chưa bật xác nhận admin, đăng ký tài khoản người dùng
                    signUpUser();
                }
            }
        });

        //code back ve trang truoc
        btBack = findViewById(R.id.btback);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng SignInActivity và trở về fragment_profile
            }
        });

        // Lắng nghe sự kiện khi TextView được nhấn
        TextView tvGreentoon = findViewById(R.id.tvGreentoon);
        tvGreentoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tăng giá trị của biến đếm
                clickCount++;

                // Kiểm tra nếu đã nhấn 5 lần
                if (clickCount == 5) {
                    // Hiển thị ô đánh dấu
                    findViewById(R.id.cbProtocol).setVisibility(View.VISIBLE);
                    // Bật chức năng xác nhận admin
                    isAdminConfirmationEnabled = true;
                }
            }
        });

        // Khởi tạo mCallbacks cho xác nhận số điện thoại
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // Xác nhận số điện thoại tự động thành công
                // Chuyển hướng đến xử lý đăng ký tài khoản
                signUpWithPhoneCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // Xác nhận số điện thoại thất bại
                Toast.makeText(SignUpActivity.this, "Xác nhận số điện thoại thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // Mã xác nhận đã được gửi thành công đến số điện thoại, bạn cần lưu lại để xác nhận sau đó
                // Ở đây, bạn có thể yêu cầu người dùng nhập mã xác nhận để hoàn tất đăng ký
                // Trong ví dụ này, chúng ta sẽ hiển thị một dialog để người dùng nhập mã xác nhận

                // Lưu mã xác nhận để sử dụng sau này
                mVerificationId = verificationId;

                // Tạo dialog nhập mã xác nhận
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("Nhập mã xác nhận");

                // Tạo EditText để người dùng nhập mã xác nhận
                final EditText input = new EditText(SignUpActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Thiết lập nút Xác nhận trong dialog
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Lấy mã xác nhận từ EditText
                        String verificationCode = input.getText().toString().trim();

                        // Nếu mã xác nhận không rỗng
                        if (!verificationCode.isEmpty()) {
                            // Xác nhận mã xác nhận với Firebase
                            verifyPhoneNumberWithCode(verificationCode);
                        } else {
                            // Nếu mã xác nhận rỗng, hiển thị thông báo
                            Toast.makeText(SignUpActivity.this, "Vui lòng nhập mã xác nhận", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Hiển thị dialog
                builder.show();
            }
        };
    }

    private void signUpUser() {
        // Hiển thị vòng loading
        showLoading();

        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        String confirmPassword = edConfirmPassword.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            // Ẩn vòng loading nếu có lỗi xảy ra
            hideLoading();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                        } else {
                            try {
                                throw task.getException();
//                            } catch (FirebaseAuthInvalidCredentialsException e) {
//                                Toast.makeText(SignUpActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(SignUpActivity.this, "Email đã được đăng ký", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(SignUpActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                            }
                            // Ẩn vòng loading nếu đăng ký thất bại
                            hideLoading();
                        }
                    }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String userEmail = user.getEmail();
            // Lưu thông tin người dùng vào Realtime Database
            User userData = new User(userId, userEmail);
            userData.setAvatarUser("https://firebasestorage.googleapis.com/v0/b/greentoon-937f6.appspot.com/o/avatars%2FGwdz6c6FAjYLKfDC3LlwaWyGWg12.jpg?alt=media&token=29a002c7-f96e-4b4c-8d96-6108ccfa6f68");
            userData.setNameUser("User");
            databaseReference.child(userId).setValue(userData);
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Gửi email xác nhận thành công", Toast.LENGTH_SHORT).show();
                                // Ẩn vòng loading khi gửi email thành công
                                hideLoading();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Gửi email xác nhận thất bại", Toast.LENGTH_SHORT).show();
                                // Ẩn vòng loading nếu gửi email thất bại
                                hideLoading();
                            }
                        }
                    });
        }
    }

    private void showLoading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký...");
        progressDialog.setCancelable(false); // Không cho phép hủy bỏ
        progressDialog.show();
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void sendAdminConfirmationEmail(String userEmail) {
        // Kiểm tra xem người dùng đã nhập đúng mật khẩu chưa
        String password = edPassword.getText().toString().trim();
        String confirmPassword = edConfirmPassword.getText().toString().trim();
        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi mã xác nhận cho số điện thoại +84797841166
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84569491785",        // Số điện thoại cần xác nhận
                60,                  // Thời gian chờ xác nhận (giây)
                TimeUnit.SECONDS,    // Đơn vị thời gian
                this,                // Activity hiện tại
                mCallbacks);         // Callbacks xác nhận số điện thoại
    }

    private void verifyPhoneNumberWithCode(String verificationCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
        signUpWithPhoneCredential(credential);
    }

    private void signUpWithPhoneCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Xác thực số điện thoại thành công
                            FirebaseUser user = task.getResult().getUser();
                            // Lưu thông tin người dùng vào Realtime Database
                            String userId = user.getUid();
                            String userEmail = user.getPhoneNumber(); // Sử dụng số điện thoại làm email
                            User userData = new User(userId, userEmail);
                            userData.setAvatarUser("https://firebasestorage.googleapis.com/v0/b/greentoon-937f6.appspot.com/o/avatars%2FGwdz6c6FAjYLKfDC3LlwaWyGWg12.jpg?alt=media&token=29a002c7-f96e-4b4c-8d96-6108ccfa6f68");
                            userData.setNameUser("User");
                            databaseReference.child(userId).setValue(userData);
                            // Hiển thị thông báo và ẩn loading
                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            hideLoading();
                        } else {
                            // Xác thực số điện thoại thất bại
                            Toast.makeText(SignUpActivity.this, "Xác nhận số điện thoại thất bại", Toast.LENGTH_SHORT).show();
                            hideLoading();
                        }
                    }
                });
    }
}
