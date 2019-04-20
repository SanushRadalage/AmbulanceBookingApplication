package com.example.asus.ambi;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class patprofile extends AppCompatActivity {

    EditText nm, cn, em, bld;
    Button bak, sv;

    FirebaseAuth aAuth;
    DatabaseReference pdatab;
    private String paId;
    private String nam;
    private String contact;
    private String blood;
    private String emergency;
    private ImageView mprofileiv;
    private String profileimurl;


    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patprofile);


        nm = findViewById(R.id.enm);
        cn = findViewById(R.id.cont);
        em = findViewById(R.id.emg);
        bak = findViewById(R.id.back);
        sv = findViewById(R.id.save);
        bld = findViewById(R.id.bld);
        mprofileiv = findViewById(R.id.pro);



        aAuth = FirebaseAuth.getInstance();

        paId = aAuth.getCurrentUser().getUid();
        pdatab = FirebaseDatabase.getInstance().getReference().child("Users").child("Patient").child(paId);
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

                    if(map.get("ContactNumber") != null)
                    {
                        contact = map.get("ContactNumber").toString();
                        cn.setText(contact);
                    }

                    if(map.get("EmergencyContact") != null)
                    {
                        emergency = map.get("EmergencyContact").toString();
                        em.setText(emergency);
                    }
                    if(map.get("BloodGroup") != null)
                    {
                        blood = map.get("BloodGroup").toString();
                        bld.setText(blood);
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
        contact = cn.getText().toString();
        nam = nm.getText().toString();
        blood = bld.getText().toString();

        Map info = new HashMap();
        info.put("Name", nam);
        pdatab.updateChildren(info);

        Map inf = new HashMap();
        info.put("ContactNumber", contact);
        pdatab.updateChildren(inf);

        Map in = new HashMap();
        info.put("EmergencyContact", emergency);
        pdatab.updateChildren(in);

        Map i = new HashMap();
        info.put("BloodGroup", blood);
        pdatab.updateChildren(i);


        if(resultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(paId);
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

