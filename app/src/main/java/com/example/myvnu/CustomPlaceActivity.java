package com.example.myvnu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myvnu.roomdatabase.CustomPlace;
import com.example.myvnu.roomdatabase.CustomPlaceDatabase;

import java.io.File;

public class CustomPlaceActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnEdit;
    Button btnDel;
    Button btnView;
    Button btnCancel;
    Button btnSave;
    EditText title;
    EditText desc;
    EditText addr;
    EditText phone;
    EditText date;
    LinearLayout layout3;
    LinearLayout layout2;
    LinearLayout upLayout;
    LinearLayout downLayout;
    Bundle data;
    CustomPlace item;
    ImageButton btnHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_place);
        initView();
        initIntent();
        setEdit();
        setDel();
        setView();
    }

    private void setView() {
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomPlaceActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", item.getLat());
                bundle.putDouble("lng", item.getLng());
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
    }

    private void setDel() {

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // X??a kh???i database
                CustomPlaceDatabase.getDatabase(CustomPlaceActivity.this).customPlaceDao().delete(item);
                //Intent intent = new Intent(CustomPlaceActivity.this, MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });

    }

    private void setEdit() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downLayout.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.INVISIBLE);
                layout2.setVisibility(View.VISIBLE);
                setEditable(title,true);
                setEditable(desc, true);
                setEditable(addr, true);
                setEditable(phone, true);

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditable(title,false);
                setEditable(desc, false);
                setEditable(addr, false);
                setEditable(phone, false);
                layout3.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.INVISIBLE);
                title.setText(item.getTitle());
                desc.setText(item.getDescription());
                addr.setText(item.getAddress());
                phone.setText(item.getPhoneNumber());
                date.setText(item.getCheckInDate());
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // L??u v??o database
                setEditable(title,false);
                setEditable(desc, false);
                setEditable(addr, false);
                setEditable(phone, false);
                layout3.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.INVISIBLE);
                item.setTitle(title.getText().toString());
                item.setDescription(desc.getText().toString());
                item.setAddress(addr.getText().toString());
                item.setPhoneNumber(phone.getText().toString());
                CustomPlaceDatabase.getDatabase(CustomPlaceActivity.this).customPlaceDao().update(item);
            }
        });
    }

    private void setEditable(EditText txt, Boolean status) {
        txt.setClickable(status);
        txt.setFocusable(status);
        txt.setFocusableInTouchMode(status);
        txt.setCursorVisible(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initIntent();
    }

    private void initIntent() {
        data = getIntent().getBundleExtra("data");
        if(data != null){
            item = (CustomPlace) data.getSerializable("item");
            imageView.setImageBitmap(loadImage(item.getImg()));
            title.setText(item.getTitle());
            desc.setText(item.getDescription());
            addr.setText(item.getAddress());
            phone.setText(item.getPhoneNumber());
            date.setText(item.getCheckInDate());
        }

    }
    public void setUpLayout2(View view){
        upLayout.setVisibility(View.INVISIBLE);
        downLayout.setVisibility(View.VISIBLE);
    }
    public void setDownLayout2(View view){
        downLayout.setVisibility(View.INVISIBLE);
        upLayout.setVisibility(View.VISIBLE);
    }
    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageCustomPlace);
        btnEdit = (Button)findViewById(R.id.btnEditCP);
        btnDel = (Button)findViewById(R.id.btnDeleteCP);
        btnView = (Button)findViewById(R.id.btnViewCP);
        btnCancel = (Button)findViewById(R.id.btnCancelCP);
        btnSave = (Button)findViewById(R.id.btnSaveCP);
        title = (EditText) findViewById(R.id.titleCustomPlace);
        desc = (EditText) findViewById(R.id.descCustomPlace);
        addr = (EditText) findViewById(R.id.addrCustomPlace);
        phone = (EditText) findViewById(R.id.phoneCustomPlace);
        date = (EditText) findViewById(R.id.dateCustomPlace);
        layout3 = (LinearLayout) findViewById(R.id.layout3Btn);
        layout2 = (LinearLayout) findViewById(R.id.layout2Btn);
        upLayout = (LinearLayout)findViewById(R.id.upLayout2);
        downLayout = (LinearLayout) findViewById(R.id.downLayout2);
        btnHome = (ImageButton)findViewById(R.id.btnHomeCP);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(CustomPlaceActivity.this, "V??? trang ch???", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(CustomPlaceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        upLayout.setOnTouchListener(new OnSwipeTouchListener(CustomPlaceActivity.this){
            public void onSwipeTop() {
                upLayout.setVisibility(View.INVISIBLE);
                downLayout.setVisibility(View.VISIBLE);
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
            }
            public void onSwipeBottom() {

            }
        });
        downLayout.setOnTouchListener(new OnSwipeTouchListener(CustomPlaceActivity.this){
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
            }
            public void onSwipeBottom() {
                downLayout.setVisibility(View.INVISIBLE);
                upLayout.setVisibility(View.VISIBLE);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.getText().toString()));
                startActivity(intent);
            }
        });

    }

    public Bitmap loadImage(String imgName){
        String fullPath = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + Const.IMAGE_FOLDER + imgName;
        try {
            Bitmap b = BitmapFactory.decodeFile(fullPath);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}