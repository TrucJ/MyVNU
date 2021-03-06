package com.example.myvnu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
    private Recommendation rec = new Recommendation();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        items = getDiscover("");
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
        imageView.setImageBitmap(loadBitmapFromCache(item.getImg()));
        title.setText(item.getTitle());
        desc.setText(item.getDescription());
        addr.setText(item.getAddress());
        phone.setText(item.getPhoneNumber());
        link.setText(item.getLink());
        siri.speak(rec.makeIntro(RecommendActivity.this, item), TextToSpeech.QUEUE_FLUSH, null);
    }

    private ArrayList<Place> getDiscover(String s) {
        double clat = ((GlobalVariable) this.getApplication()).getChosenLat();
        double clng = ((GlobalVariable) this.getApplication()).getChosenLng();
        Log.d("huheo", Double.toString(clat));
        Log.d("huheo", Double.toString(clng));
        List<Place> tmp = rec.findPlaceWithQuery(RecommendActivity.this,s);
        idx = 0;
        ArrayList<Place> p = new ArrayList<Place>(tmp);
        int size = p.size();
        for(int i = 0; i < size - 1; ++i){
            final int j = new Random().nextInt(size);
            final int k = new Random().nextInt(size);
            Place a = p.get(j);
            Place b = p.get(k);
            p.set(j, b);
            p.set(k, a);
        }
        return p;
    }

    private void initView() {
        siri = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i ==TextToSpeech.SUCCESS){
                    int result = siri.setLanguage(Locale.getDefault());
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(RecommendActivity.this, "Kh??ng h??? tr??? ng??n ng??? n??y!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RecommendActivity.this,"Kh??ng th??nh c??ng!", Toast.LENGTH_SHORT).show();
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
                Toast toast = Toast.makeText(RecommendActivity.this, "V??? trang ch???", Toast.LENGTH_SHORT);
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

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.getText().toString()));
                startActivity(intent);
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getText().toString()));
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
            Toast.makeText(this, "Thi???t b??? c???a b???n kh??ng h??? tr??? nh???p li???u gi???ng n??i!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 10:
                if(resultCode == RESULT_OK && data!=null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    items = getDiscover(result.get(0));
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
    public Bitmap loadBitmapFromCache(String fileName){
        try{
            Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getCacheDir().getAbsolutePath() + "/places/" + fileName);
            return bitmap;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}