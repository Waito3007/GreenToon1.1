package com.my.greentoon.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.my.greentoon.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edOldPassword, edNewPassword, edConfirmNewPassword;
    private Button btChangePassword,btBack;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        edOldPassword = findViewById(R.id.edOldPassword);
        edNewPassword = findViewById(R.id.edNewPassword);
        edConfirmNewPassword = findViewById(R.id.edConfirmNewPassword);
        btChangePassword = findViewById(R.id.btChangePassword);
        btBack = findViewById(R.id.btback);

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void changePassword() {
        String oldPassword = edOldPassword.getText().toString().trim();
        final String newPassword = edNewPassword.getText().toString().trim();
        String confirmNewPassword = edConfirmNewPassword.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị vòng loading
        showLoading();

        mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), oldPassword)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Đổi mật khẩu
                            mAuth.getCurrentUser().updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thất bại, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                            }
                                            // Ẩn vòng loading
                                            hideLoading();
                                        }
                                    });
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                            // Ẩn vòng loading
                            hideLoading();
                        }
                    }
                });
    }

    private void showLoading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đổi mật khẩu...");
        progressDialog.setCancelable(false); // Không cho phép hủy bỏ
        progressDialog.show();
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
