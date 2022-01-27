package com.example.myvnu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myvnu.roomdatabase.CustomPlace;

import java.io.File;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private List<CustomPlace> items;

    public MyAdapter(Context context, List<CustomPlace> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_places, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final CustomPlace item = items.get(position);
        holder.txtViewTitle.setText(item.getTitle());
        holder.txtViewDate.setText(item.getCheckInDate());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchContext(item);
            }
        });
        holder.imageView.setImageBitmap(loadImage(item.getImg()));
    }

    private void switchContext(CustomPlace item) {
        Intent intent = new Intent(context, CustomPlaceActivity.class);
        Bundle data = new Bundle();
        data.putSerializable("item", item);
        intent.putExtra("data", data);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtViewTitle;
        private TextView txtViewDate;
        private ConstraintLayout layout;
        private ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (ConstraintLayout) itemView.findViewById(R.id.layoutItem);
            txtViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            txtViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            imageView = (ImageView) itemView.findViewById(R.id.imageView4);
        }
    }

    public Bitmap loadImage(String imgName){
        String fullPath = MainActivity.imgPath + imgName;
        Bitmap b = BitmapFactory.decodeFile(fullPath);
        return b;
    }
}
