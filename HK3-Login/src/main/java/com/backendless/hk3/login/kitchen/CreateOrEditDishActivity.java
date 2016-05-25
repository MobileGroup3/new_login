package com.backendless.hk3.login.kitchen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.hk3.data.DishItem;
import com.backendless.hk3.data.Kitchen;
import com.backendless.hk3.login.Defaults;
import com.backendless.hk3.login.R;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by zini on 5/24/16.
 */
public class CreateOrEditDishActivity extends AppCompatActivity {

    final int  DISHPIC_REQUEST=1;

    private EditText nameField;
    private EditText priceField;
    private EditText quantityField;
    private EditText descriptionField;
    private Button imageButton;
    private ImageView ivDishPic;
    private Button sumbitButton;

    private BackendlessUser user;
    GalleryPhoto galleryPhoto;
    String selectedPhoto;
    private ProgressDialog progress;
    private Kitchen kitchen;
    private DishItem dishItem;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_dish);
        galleryPhoto = new GalleryPhoto(this);

        nameField=(EditText)findViewById(R.id.nameText);
        priceField=(EditText)findViewById(R.id.priceText);
        quantityField=(EditText)findViewById(R.id.quantityText);
        descriptionField=(EditText)findViewById(R.id.editText2);
        imageButton=(Button)findViewById(R.id.dishUpload);
        ivDishPic=(ImageView)findViewById(R.id.dishImg);
        sumbitButton=(Button)findViewById(R.id.submitButton);

        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);
        user = Backendless.UserService.CurrentUser();

        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(galleryPhoto.openGalleryIntent(),DISHPIC_REQUEST);
            }
        });

        String dishItemId = getIntent().getStringExtra("dishItemId");
        if (dishItemId != null) {
            // edit
            Backendless.Persistence.of(DishItem.class).findById(dishItemId, new AsyncCallback<DishItem>() {
                @Override
                public void handleResponse(DishItem di) {
                    nameField.setText(di.getName());
                    priceField.setText(di.getPrice()+"");
                    quantityField.setText(di.getMax_num()+"");
                    descriptionField.setText(di.getDescription());
                    Picasso.with(CreateOrEditDishActivity.this).load(di.getPicture()).into(ivDishPic);
                    dishItem = di;
                }

                @Override
                public void handleFault(BackendlessFault fault) {

                }
            });
        }

        sumbitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (dishItem == null) {
                    dishItem=new DishItem();
                }

                String name=nameField.getText().toString().trim();
                int price=Integer.parseInt(priceField.getText().toString().trim());
                int quantity= Integer.parseInt(quantityField.getText().toString());
                String description=descriptionField.getText().toString().trim();

                if(nameField.length()==0){
                    showToast("Please Enter a Dish Name");
                    return;
                }
                if(price<0||priceField.length()==0){
                    showToast("Please Enter a Positive Number");
                    return;
                }
                if(quantity<0||quantityField.length()==0){
                    showToast("Please Enter a Positive Number");
                    return;
                }
                if(descriptionField.length()==0){
                    showToast("Please Enter Dish Description");
                    return;
                }

                if (selectedPhoto == null && dishItem.getPicture() == null) {
                    showToast("Please Select Photo");
                    return;
                }

                if(!name.isEmpty()){
                    dishItem.setName(name);

                }
                if(!description.isEmpty()){
                    dishItem.setDescription(description);
                }

                dishItem.setPrice(price);
                dishItem.setMax_num(quantity);

                if (selectedPhoto == null) {
                    // didn't change pic
                    saveDishItem(null);
                } else {
                    Log.i("upload","selectedPhoto: "+ selectedPhoto);
                    File f=new File(selectedPhoto);
                    Backendless.Files.upload(f, "dish_pic", true, new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(BackendlessFile response) {
                            Log.i("Upload", "upload success: " + response.getFileURL());
                            saveDishItem(response.getFileURL());
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e("Upload", "upload failed: " + fault.getMessage());
                            progress.dismiss();
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });
                }


                progress=ProgressDialog.show(CreateOrEditDishActivity.this,"Create Dish","A new dish is created",true);
            }
        });
    }

    private void saveDishItem(String picURL) {
        if (picURL != null) {
            dishItem.setPicture(picURL);
        }
        Backendless.Persistence.save(dishItem, new AsyncCallback<DishItem>() {
            @Override
            public void handleResponse(DishItem response) {
                Log.i("dishItem saved: ","success");
                progress.dismiss();
                Intent result = new Intent();
                result.putExtra("dishItemId", response.getObjectId());
                setResult(RESULT_OK, result);
                finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.e("save", "fail: " + fault.getMessage() + " " + fault.getDetail());
                progress.dismiss();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            if(requestCode==DISHPIC_REQUEST){
                Uri uri=data.getData();
                galleryPhoto.setPhotoUri(uri);
                String photoPath=galleryPhoto.getPath();
                selectedPhoto=photoPath;
                try{
                    Bitmap bitmap= ImageLoader.init().from(photoPath).requestSize(256,256).getBitmap();
                }catch (FileNotFoundException e){
                    showToast("Something Wrong with Selecting Photos");
                }
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
