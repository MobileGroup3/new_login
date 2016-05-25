package com.backendless.hk3.login.kitchen;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.hk3.data.DishItem;
import com.backendless.hk3.login.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by zini on 5/24/16.
 */
public class DishAdapter extends RecyclerView.Adapter<DishAdapter.MyViewHolder>{

    private List<DishItem> dishItemList;
    private Activity activity;

    public DishAdapter (Activity a, List<DishItem>dishItemList){
        this.activity=a;
        this.dishItemList=dishItemList;
    }

     class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView dishImageView;
        TextView dishNameTextView;
         TextView dishDescriptionTextView;
         TextView dishPriceTextView;
         TextView dishRemainingNumberTextView;
         ImageView editButton;

         public MyViewHolder(final View itemView){
             super(itemView);
             dishImageView = (ImageView) itemView.findViewById(R.id.image_view_dish);
             dishNameTextView = (TextView) itemView.findViewById(R.id.text_view_dish_name);
             dishDescriptionTextView = (TextView) itemView.findViewById(R.id.text_view_dish_description);
             dishPriceTextView = (TextView) itemView.findViewById(R.id.text_view_dish_price);
             dishRemainingNumberTextView = (TextView) itemView.findViewById(R.id.text_view_remaining_number);
             editButton = (ImageView) itemView.findViewById(R.id.editDish);

         }

    }

    @Override
    public int getItemCount(){
        return dishItemList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View viewItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row,parent,false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        final DishItem dish=dishItemList.get(position);
        Picasso.with(activity).load(dish.getPicture()).into(holder.dishImageView);
        holder.dishNameTextView.setText(dish.getName());
        holder.dishDescriptionTextView.setText(dish.getDescription());
        holder.dishPriceTextView.setText(String.valueOf(dish.getPrice()));
        holder.dishRemainingNumberTextView.setText(String.valueOf(dish.getMax_num()));
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, CreateOrEditDishActivity.class);
                i.putExtra("dishItemId", dish.getObjectId());
                activity.startActivityForResult(i, KitchenHomeActivity.EDIT_DISH_ITEM);
            }
        });
    }

    public void setData(List<DishItem> list){
        dishItemList=list;
        notifyDataSetChanged();
    }

}
