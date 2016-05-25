package com.backendless.hk3.login.kitchen;

/**
 * Created by zini on 5/24/16.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.hk3.data.Dish;
import com.backendless.hk3.data.DishItem;
import com.backendless.hk3.login.Defaults;
import com.backendless.hk3.login.R;

import java.util.ArrayList;
import java.util.List;


public class AddDish extends AppCompatActivity {
    ImageView kitPicField;
    TextView kitNameField;
    ImageView addDishButton;
    RecyclerView dishRecyclerView;
    LinearLayoutManager llm;
    DishAdapter dishAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dish);
        Backendless.initApp(this, Defaults.APPLICATION_ID,Defaults.SECRET_KEY,Defaults.VERSION);

        kitPicField=(ImageView)findViewById(R.id.kitchenPic);
        kitNameField=(TextView)findViewById(R.id.kitchenName);
        addDishButton=(ImageView)findViewById(R.id.addButton);
        dishRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);

        llm=new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        dishRecyclerView.setLayoutManager(llm);

        Dish menu=new Dish();  //create a Dish Object
        List<DishItem> dishItemList=new ArrayList<DishItem>();
        dishAdapter= new DishAdapter(getApplicationContext(),dishItemList);
        dishRecyclerView.setAdapter(dishAdapter);

        addDishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent1=new Intent(AddDish.this, CreateDish.class);
                startActivityForResult(intent1,0);

            }
        });



    }
}
