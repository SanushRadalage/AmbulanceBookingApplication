package com.example.asus.ambi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class driverprofile extends AppCompatActivity {

    EditText nm, cn;
    RadioGroup mradiogroup;
    Button bak, sv;

    FirebaseAuth aAuth;
    DatabaseReference pdatab;
    private String drId;
    private String nam;
    private String con;


    private String mservice;
    private ImageView mprofileiv;
    private String profileimurl;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverprofile);

        nm = findViewById(R.id.enm);
        cn = findViewById(R.id.cn);
        bak = findViewById(R.id.back);
        sv = findViewById(R.id.save);
        mprofileiv = findViewById(R.id.pro);
        mradiogroup = findViewById(R.id.radioGroup);


        aAuth = FirebaseAuth.getInstance();
        drId = aAuth.getCurrentUser().getUid();

        pdatab = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(drId);
        getuserinfo();

        mprofileiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);


            }
        });


        bak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserInf();

            }
        });
    }

    private void getuserinfo()
    {
        pdatab.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Name") != null)
                    {
                        nam = map.get("Name").toString();
                        nm.setText(nam);
                    }

                    if(map.get("Contact_Number") != null)
                    {
                        con = map.get("Contact_Number").toString();
                        cn.setText(con);
                    }
                    if(map.get("Service") != null)
                    {
                        mservice = map.get("Service").toString();
                        switch (mservice)
                        {
                            case"Hemas":
                                mradiogroup.check(R.id.ambiX);
                                break;

                            case"Nawaloka":
                                mradiogroup.check(R.id.ambiY);
                                break;

                            case"Durdans":
                                mradiogroup.check(R.id.ambiZ);
                                break;

                            case"1990":
                                mradiogroup.check(R.id.ambiA);
                                break;

                        }
                    }
                    if(map.get("profileImageUrl") != null)
                    {
                        profileimurl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(profileimurl).into(mprofileiv);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInf()
    {
        nam = nm.getText().toString();
        con = cn.getText().toString();

        int selectId = mradiogroup.getCheckedRadioButtonId();

        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if(radioButton.getText() == null)
        {
            return;
        }

        mservice = radioButton.getText().toString();


        Map info = new HashMap();
        info.put("Name", nam);
        info.put("Contact_Number", con);
        info.put("Service", mservice);
        pdatab.updateChildren(info);

        if(resultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(drId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);

            } catch (IOException e) {
                e.printStackTrace();
            }


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            pdatab.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mprofileiv.setImageURI(resultUri);
        }
    }
}
