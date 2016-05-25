package com.backendless.hk3.login.kitchen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.backendless.hk3.login.R;

/**
 * Created by zini on 5/24/16.
 */
public class CreateDish extends AppCompatActivity {
    private EditText nameField;
    private EditText priceField;
    private EditText quantityField;
    private EditText descriptionField;
    private Button imageButton;
    private ImageView ivDishPic;
    private Button sumbitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_dish);

        nameField=(EditText)findViewById(R.id.nameText);
        priceField=(EditText)findViewById(R.id.priceText);
        quantityField=(EditText)findViewById(R.id.quantityText);
        descriptionField=(EditText)findViewById(R.id.editText2);
        imageButton=(Button)findViewById(R.id.dishUpload);
        
        }

    }
}
