package com.wcs.germain.winstatehack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_connection);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        initTextsandFonts();

        final EditText firstName = findViewById(R.id.connection_firstname);
        final EditText mail = findViewById(R.id.connection_mail);
        final EditText password = findViewById(R.id.connection_password);

        final Button validate = findViewById(R.id.connection_validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String firstNameValue = firstName.getText().toString();
                final String mailValue = mail.getText().toString();
                final String passwordValue = password.getText().toString();

                boolean isMailOK = false;

                Pattern p4 = Pattern.compile("^[\\w.-]+@[\\w.-]{2,}\\.[a-z]{2,5}$");
                Matcher m4 = p4.matcher(mailValue);

                while (m4.find()) {
                    isMailOK = true;
                }

                if (TextUtils.isEmpty(firstNameValue)) {
                    Toast.makeText(ConnectionActivity.this,
                            getString(R.string.invalid_entries),
                            Toast.LENGTH_SHORT).show();
                    firstName.setError(getResources().getString(R.string.invalid_entry));
                }

                if (!isMailOK) {
                    mail.setError(getResources().getString(R.string.invalid_entry));
                    Toast.makeText(ConnectionActivity.this,
                            getString(R.string.invalid_entries),
                            Toast.LENGTH_SHORT).show();
                }

                if (!TextUtils.isEmpty(passwordValue)) {

                    if (TextUtils.isEmpty(firstNameValue)) {
                        Toast.makeText(ConnectionActivity.this,
                                getString(R.string.invalid_entries),
                                Toast.LENGTH_SHORT).show();
                        firstName.setError(getResources().getString(R.string.invalid_entry));

                    } else {

                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                        databaseRef.child("user").orderByChild("firstName").equalTo(firstNameValue)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot!=null && dataSnapshot.getChildren()!=null &&
                                                dataSnapshot.getChildren().iterator().hasNext()){

                                            databaseRef.child("user").orderByChild("password").equalTo(passwordValue)
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            if(dataSnapshot!=null && dataSnapshot.getChildren()!=null &&
                                                                    dataSnapshot.getChildren().iterator().hasNext()){

                                                                LinearLayout progressBar = findViewById(R.id.progressBarLayout);
                                                                progressBar.setVisibility(View.VISIBLE);
                                                                validate.setEnabled(false);
                                                                startActivity(new Intent
                                                                        (ConnectionActivity.this,
                                                                                HomeActivity.class));
                                                                finish();
                                                            } else {
                                                                password.setError(getString(R.string.invalid_entry));
                                                                Toast.makeText(ConnectionActivity.this,
                                                                        getString(R.string.incorrect_password),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            //Error
                                                        }
                                                    });

                                        } else {

                                            User user = new User(firstNameValue, mailValue, passwordValue);
                                            DatabaseReference userRef = database.getReference("user");
                                            String userKey = userRef.push().getKey();
                                            userRef.child(userKey).setValue(user);
                                            Toast.makeText(ConnectionActivity.this,
                                                    getString(R.string.user_created),
                                                    Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ConnectionActivity.this, HomeActivity.class));
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        //Error
                                    }
                                });

                    }

                } else {

                    password.setError(getString(R.string.invalid_entry));
                    Toast.makeText(ConnectionActivity.this,
                            getString(R.string.invalid_entries),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void initTextsandFonts() {

        Typeface regularFont = Typeface.createFromAsset(getAssets(), "fonts/Montserrat_Regular.otf");
        Typeface boldFont = Typeface.createFromAsset(getAssets(), "fonts/Montserrat_Bold.otf");

        TextView title = findViewById(R.id.connection_title);
        TextView subTitle = findViewById(R.id.connection_hint);
        EditText firstName = findViewById(R.id.connection_firstname);
        EditText mail = findViewById(R.id.connection_mail);
        EditText password = findViewById(R.id.connection_password);

        Button validate = findViewById(R.id.connection_validate);

        title.setTypeface(boldFont);
        subTitle.setTypeface(boldFont);
        firstName.setTypeface(regularFont);
        mail.setTypeface(regularFont);
        password.setTypeface(regularFont);

        validate.setTypeface(boldFont);

    }
}
