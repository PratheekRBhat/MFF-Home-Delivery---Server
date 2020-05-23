package com.example.mffhomedeliveryserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.Model.ServerUser;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private static int APP_REQUEST_CODE = 1663;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private DatabaseReference serverRef;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
         providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());

         serverRef = FirebaseDatabase.getInstance().getReference(Common.SERVER_REF);
         firebaseAuth = FirebaseAuth.getInstance();
         dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
         listener = firebaseAuthLocal -> {
             FirebaseUser firebaseUser = firebaseAuthLocal.getCurrentUser();
             if (firebaseUser != null) {
                 checkServerUserFromFirebase(firebaseUser);
             } else {
                 phoneLogin();
             }
         };
    }

    private void checkServerUserFromFirebase(FirebaseUser user) {
        dialog.show();

        serverRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ServerUser serverUser = dataSnapshot.getValue(ServerUser.class);
                            if (serverUser.isActive())
                                updateUI(serverUser);
                            else
                                Toast.makeText(MainActivity.this, "You must be allowed access by admin to continue", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            showRegisterDialog(user);
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegisterDialog(FirebaseUser user) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage("Please fill out the form.");

        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null);
        EditText nameET = itemView.findViewById(R.id.txt_reg_name);
        EditText phoneET = itemView.findViewById(R.id.txt_reg_phone);

        phoneET.setText(user.getPhoneNumber());

        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("REGISTER", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(nameET.getText().toString())) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            ServerUser serverUser = new ServerUser();
            serverUser.setUid(user.getUid());
            serverUser.setName(nameET.getText().toString());
            serverUser.setPhone(phoneET.getText().toString());
            serverUser.setActive(false);    //Default value is false as we will activate the user manually in Firebase.

            serverRef.child(user.getUid()).setValue(serverUser)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                            if (serverUser.isActive())
                                updateUI(serverUser);
                            else
                                Toast.makeText(this, "You must be allowed access by Admin to proceed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateUI(ServerUser serverUser) {
        dialog.dismiss();

        Common.currentServerUser = serverUser;

        Intent intent = new Intent(MainActivity.this, Home.class);
        intent.putExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER, getIntent().getBooleanExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER, false));
        startActivity(intent);
        finish();
    }

    private void phoneLogin() {
        startActivityForResult(AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build(),APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
