package com.backendless.hk3.login.kitchen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.Files;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.hk3.data.Dish;
import com.backendless.hk3.login.DefaultCallback;
import com.backendless.hk3.login.Defaults;
import com.backendless.hk3.login.HK3User;
import com.backendless.hk3.login.LoginActivity;
import com.backendless.hk3.login.R;
import com.backendless.hk3.data.Kitchen;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by zini on 5/21/16.
 */
public class CreateKitchen extends Activity {

    private final String TAG=this.getClass().getName();

    private EditText nameField;
    private EditText phoneField;
    private EditText cityField;
    private EditText streetField;
    private EditText zipcodeField;
    private Spinner foodCategory;
    private Button createButton;
    private ProgressDialog progress;

    private ImageView ivGallery;
    private ImageView ivImage;
    GalleryPhoto galleryPhoto;
    final int GALLERY_REQUEST=0;
    String selectedPhoto;

    private String name;
    private String phone;
    private String city;
    private String street;
    private String zipCode;
    private String category;

    private BackendlessUser user;
    private Kitchen kitchen;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_kitchen);

        galleryPhoto = new GalleryPhoto(getApplicationContext());

        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);
        user = Backendless.UserService.CurrentUser();

        nameField = (EditText) findViewById(R.id.kName);
        phoneField = (EditText) findViewById(R.id.phone);
        cityField = (EditText) findViewById(R.id.city);
        streetField = (EditText) findViewById(R.id.street);
        zipcodeField = (EditText) findViewById(R.id.zipcode);
        foodCategory = (Spinner) findViewById(R.id.category);
        createButton = (Button) findViewById(R.id.createKitchen);
        ivGallery = (ImageView) findViewById(R.id.imageView);
        ivImage = (ImageView) findViewById(R.id.homepage);

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = nameField.getText().toString().trim();
                String phoneText = phoneField.getText().toString().trim();
                String cityText = cityField.getText().toString().trim();
                String streetText = streetField.getText().toString().trim();
                String zipText = zipcodeField.getText().toString().trim();
                String categoryText = foodCategory.getSelectedItem().toString();

                if (nameText.isEmpty()) {
                    showToast("Field 'Name' cannot be empty ");
                    return;
                }

                if (phoneText.isEmpty()) {
                    showToast("Field 'Phone' cannot be empty");
                    return;
                }

                if (cityText.isEmpty()) {
                    showToast("Field 'City' cannot be empty");
                    return;
                }

                if (streetText.isEmpty()) {
                    showToast("Field 'Street' cannot be empty");
                    return;
                }

                if (zipText.isEmpty()) {
                    showToast("Field 'Zipcode' cannot be empty");
                    return;
                }

                if (categoryText.isEmpty()) {
                    showToast("Field 'Category' cannot be empty");
                    return;
                }

                if (selectedPhoto == null || selectedPhoto.equals("")) {
                    showToast("No image selected");
                    return;
                }

                kitchen = new Kitchen();
                if (!nameText.isEmpty()) {
                    name = nameText;
                    kitchen.setKitchenName(name);

                }

                if (!phoneText.isEmpty()) {
                    phone = phoneText;
                    kitchen.setPhoneNumber(phone);
                }

                if (!cityText.isEmpty()) {
                    city = cityText;
                    kitchen.setCity(city);
                }

                if (!streetText.isEmpty()) {
                    street = streetText;
                    kitchen.setStreet(street);
                }

                if (!zipText.isEmpty()) {
                    zipCode = zipText;
                    kitchen.setZipCode(zipCode);
                }

                if (!categoryText.isEmpty()) {
                    category = categoryText;
                    kitchen.setCategory(category);
                }
                Log.i("Upload", "selectedPhoto: " + selectedPhoto);
                File f= new File(selectedPhoto);
                Backendless.Files.upload(f, "kitchen_pic", true, new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(BackendlessFile backendlessFile) {
                        Log.i("Upload", "upload success: " + backendlessFile.getFileURL());
                        kitchen.setKitchenPic(backendlessFile.getFileURL());
                        Dish dish=new Dish();
                        kitchen.setDish(dish);
                        kitchen.setOwner(user);
                        kitchen.setEmail(user.getEmail());
                        Backendless.Persistence.save(kitchen, new AsyncCallback<Kitchen>() {
                            @Override
                            public void handleResponse(Kitchen kitchen) {
                                Log.i("save", "succeed");
                                progress.dismiss();
                                startActivity( new Intent( CreateKitchen.this, AddDish.class ) );
                                finish();



                            }
                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {

                                Log.e("save", "fail: " + backendlessFault.getMessage() + " " + backendlessFault.getDetail());
                                progress.dismiss();
                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.i("Upload", "upload failed: " + backendlessFault.getMessage());
                        progress.dismiss();

                    }


                });
                progress = ProgressDialog.show(CreateKitchen.this, "upload",
                        "uploading", true);

            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode ==  RESULT_OK){
            if(requestCode == GALLERY_REQUEST){
                Uri uri=data.getData();

                galleryPhoto.setPhotoUri(uri);
                String photoPath=galleryPhoto.getPath();
                selectedPhoto=photoPath;
                try{
                    Bitmap bitmap= ImageLoader.init().from(photoPath).requestSize(512,512).getBitmap();
                    ivImage.setImageBitmap(bitmap);
                }catch (FileNotFoundException e){
                    Toast.makeText(getApplicationContext(),"Something Wrong with Selecting Photos", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}


