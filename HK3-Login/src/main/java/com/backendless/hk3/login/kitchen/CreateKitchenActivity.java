package com.backendless.hk3.login.kitchen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.hk3.data.Dish;
import com.backendless.hk3.login.Defaults;
import com.backendless.hk3.login.R;
import com.backendless.hk3.data.Kitchen;
import com.backendless.persistence.BackendlessDataQuery;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * Created by zini on 5/21/16.
 */
public class CreateKitchenActivity extends Activity {

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
    private Dish dish;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_kitchen);

        galleryPhoto = new GalleryPhoto(getApplicationContext());

        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);
        user = Backendless.UserService.CurrentUser();

        String whereClause = "email = '" + user.getEmail() + "'";
        final BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(Kitchen.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Kitchen>>() {
            @Override
            public void handleResponse(BackendlessCollection<Kitchen> response) {
                if (response.getCurrentPage().size() >0) {
                    Kitchen k = response.getCurrentPage().get(0);
                    Intent i = new Intent(CreateKitchenActivity.this, KitchenHomeActivity.class);
                    i.putExtra("kitchen_objectID", k.getObjectId());
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

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
                        kitchen.setOwner(user);
                        kitchen.setDish(new Dish());
                        kitchen.setEmail(user.getEmail());
                        Backendless.Persistence.save(kitchen, new AsyncCallback<Kitchen>() {
                            @Override
                            public void handleResponse(Kitchen savedKitchen) {
                                Log.i("save", "succeed");
                                progress.dismiss();
                                Intent i=new Intent(CreateKitchenActivity.this, KitchenHomeActivity.class);
                                i.putExtra("kitchen_objectID",savedKitchen.getObjectId());
                                startActivity(i);
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
                progress = ProgressDialog.show(CreateKitchenActivity.this, "upload",
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
