package com.example.myvnu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myvnu.roomdatabase.Place;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecommendActivity extends AppCompatActivity {
    ImageView imageView;
    EditText title;
    EditText desc;
    EditText addr;
    EditText phone;
    EditText link;
    Button btnView;
    ImageView img;
    private DBAction dbAction = new DBAction();
    LinearLayout upLayout;
    LinearLayout downLayout;
    ImageButton btnHome;
    Bundle data;
    ArrayList<Place> items;
    int idx = 0;
    private TextToSpeech siri;
    private ConstraintLayout discoverLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        items = getDiscover();
        initView();
        bindData();
    }
    public void setUpLayout(View view){
        upLayout.setVisibility(View.INVISIBLE);
        downLayout.setVisibility(View.VISIBLE);
    }
    public void setDownLayout(View view){
        downLayout.setVisibility(View.INVISIBLE);
        upLayout.setVisibility(View.VISIBLE);
    }
    private Bitmap loadBitmapFromAsset(String fileName){
        try{
            InputStream inputStream = getAssets().open(Const.ASSETS_BITMAP_FOLDER + fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private void bindData() {
        Place item = null;
        if(items!=null) item = items.get(idx);
        imageView.setImageBitmap(loadBitmapFromAsset(item.getImg()));
        title.setText(item.getTitle());
        desc.setText(item.getDescription());
        addr.setText(item.getAddress());
        phone.setText(item.getPhoneNumber());
        link.setText(item.getLink());
        siri.speak("Tới quán Bò né gần khoa học tự nhiên heoheo", TextToSpeech.QUEUE_FLUSH, null);
    }

    private ArrayList<Place> getDiscover() {
        List<Place> tmp = dbAction.getAllDefaultPlaces(RecommendActivity.this);
        return new ArrayList<Place>(tmp);
    }

    private void initView() {
        siri = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i ==TextToSpeech.SUCCESS){
                    int result = siri.setLanguage(Locale.getDefault());
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(RecommendActivity.this, "Không hỗ trợ ngôn ngữ này!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RecommendActivity.this,"Không thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Place item = items.get(idx);
        imageView = (ImageView) findViewById(R.id.imagePlace2) ;
        btnView = (Button)findViewById(R.id.btnViewPlace2);
        title = (EditText) findViewById(R.id.titlePlace2);
        desc = (EditText) findViewById(R.id.descPlace2);
        addr = (EditText) findViewById(R.id.addrPlace2);
        phone = (EditText) findViewById(R.id.phonePlace2);
        link = (EditText) findViewById(R.id.linkPlace2);
        img = (ImageView)findViewById(R.id.imagePlace2);
        upLayout = (LinearLayout)findViewById(R.id.upLayout2);
        upLayout.setOnTouchListener(new OnSwipeTouchListener(RecommendActivity.this){
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
        downLayout = (LinearLayout) findViewById(R.id.downLayout2);
        downLayout.setOnTouchListener(new OnSwipeTouchListener(RecommendActivity.this){
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
        btnHome = (ImageButton)findViewById(R.id.btnHomeRe);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(RecommendActivity.this, "Về trang chủ", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(RecommendActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        discoverLayout = (ConstraintLayout)findViewById(R.id.discoverLayout);
        discoverLayout.setOnTouchListener(new OnSwipeTouchListener(RecommendActivity.this){
            public void onSwipeTop() {

            }
            public void onSwipeRight() {
                if(idx > 0) {
                    idx--;
                    bindData();
                }
            }
            public void onSwipeLeft() {
                if(idx < items.size() - 1) {
                    idx++;
                    bindData();
                }
            }
            public void onSwipeBottom() {

            }
        });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecommendActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", item.getLat());
                bundle.putDouble("lng", item.getLng());
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
    }
    public void speech2Text(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent, 10);
        }else{
            Toast.makeText(this, "Thiết bị của bạn không hỗ trợ nhập liệu giọng nói!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 10:
                if(resultCode == RESULT_OK && data!=null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    idx++;
                    bindData();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(siri!=null){
            siri.stop();
            siri.shutdown();
        }
        super.onDestroy();
    }
    public void setNext(View view){
        if(idx < items.size() - 1) {
            idx++;
            bindData();
        }
    }
    public void setPrev(View view){
        if(idx > 0) {
            idx--;
            bindData();
        }
    }
}