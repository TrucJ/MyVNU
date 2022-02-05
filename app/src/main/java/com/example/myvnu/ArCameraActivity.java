package com.example.myvnu;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.os.HandlerThread;
import android.os.Looper;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class ArCameraActivity extends AppCompatActivity {
    private ModelRenderable modelRenderable;
    private ModelRenderable modelRenderable2;
    private ModelRenderable modelRenderable3;
    private Texture texture;
    //private boolean isAdded = false;
    private final HashMap<AugmentedFace, AugmentedFaceNode> faceNodeMap = new HashMap<>();
    private Frame frame;
    private CustomArFragment customArFragment;
    private String pictureImagePath;
    private double lat, lng;
    private Button btnFF, btnYG, btnBG;
    private AugmentedFaceNode augmentedFaceMode, node;
    private AugmentedFace face;
    private boolean isChange = false;
    private int option = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_camera);
        customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        btnFF = (Button)findViewById(R.id.btnFF);
        btnYG = (Button) findViewById(R.id.btnYG);
        btnBG = (Button)findViewById(R.id.btnBG);
        getData();
        ModelRenderable.builder()
                .setSource(this, R.raw.fox_face)
                .build()
                .thenAccept(rendarable -> {
                    this.modelRenderable = rendarable;
                    this.modelRenderable.setShadowCaster(false);
                    this.modelRenderable.setShadowReceiver(false);

                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "error loading model", Toast.LENGTH_SHORT).show();
                    return null;
                });
        ModelRenderable.builder()
                .setSource(this, R.raw.yellow_sunglasses)
                .build()
                .thenAccept(rendarable -> {
                    this.modelRenderable2 = rendarable;
                    this.modelRenderable2.setShadowCaster(false);
                    this.modelRenderable2.setShadowReceiver(false);

                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "error loading model", Toast.LENGTH_SHORT).show();
                    return null;
                });
        ModelRenderable.builder()
                .setSource(this, R.raw.sunglasses)
                .build()
                .thenAccept(rendarable -> {
                    this.modelRenderable3 = rendarable;
                    this.modelRenderable3.setShadowCaster(false);
                    this.modelRenderable3.setShadowReceiver(false);

                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "error loading model", Toast.LENGTH_SHORT).show();
                    return null;
                });
        Texture.builder()
                .setSource(this, R.drawable.vnu_filter)
                .build()
                .thenAccept(textureModel -> this.texture = textureModel)
                .exceptionally(throwable -> {
                    Toast.makeText(this, "cannot load texture", Toast.LENGTH_SHORT).show();
                    return null;
                });

        assert customArFragment != null;
        customArFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        customArFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if (modelRenderable == null || texture == null) {
                return;
            }
            frame = customArFragment.getArSceneView().getArFrame();
            assert frame != null;
            Collection<AugmentedFace> augmentedFaces = frame.getUpdatedTrackables(AugmentedFace.class);

            for (AugmentedFace augmentedFace : augmentedFaces) {
                //if (isAdded) return;
                if (!faceNodeMap.containsKey(augmentedFace)) {
                    augmentedFaceMode = new AugmentedFaceNode(augmentedFace);
                    augmentedFaceMode.setParent(customArFragment.getArSceneView().getScene());
                    augmentedFaceMode.setFaceRegionsRenderable(modelRenderable);
                    augmentedFaceMode.setFaceMeshTexture(texture);
                    faceNodeMap.put(augmentedFace, augmentedFaceMode);
                }
                else if(isChange){
                    if(option == 1){
                        faceNodeMap.get(augmentedFace).setFaceRegionsRenderable(modelRenderable);
                    }
                    else if(option == 2){
                        faceNodeMap.get(augmentedFace).setFaceRegionsRenderable(modelRenderable2);
                    }
                    else if(option == 3){
                        faceNodeMap.get(augmentedFace).setFaceRegionsRenderable(modelRenderable3);
                    }
                }
                //isAdded = true;
                isChange = false;
                // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
                Iterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> iterator = faceNodeMap.entrySet().iterator();
                Map.Entry<AugmentedFace, AugmentedFaceNode> entry = iterator.next();
                face = entry.getKey();
                while (face.getTrackingState() == TrackingState.STOPPED) {
                    node = entry.getValue();
                    node.setParent(null);
                    iterator.remove();
                }
            }
        });
    }

    private void getData() {
        Bundle bundle = getIntent().getBundleExtra("BUNDLE");

        pictureImagePath = bundle.getString("CNTN19");
        lat = bundle.getDouble("lat");
        lng = bundle.getDouble("lng");
    }

    private String generateFilename() {

        //현재시간을 기준으로 파일 이름 생성
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "IM/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        //사용자의 갤러리에 IM 디렉토리 생성 및 Bitmap 을 JPEG 형식으로 갤러리에 저장
        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }
    private void storeImage(Bitmap image) {
        try {
            //String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
            //pictureImagePath ="MI_"+ timeStamp +".jpg";
            pictureImagePath = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + Const.TMP_IMAGE_FILE;
            FileOutputStream fos = openFileOutput(pictureImagePath, MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Log.d("huheo", "Lưu thành công");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void registeredImage(View view1) {

        //File file = new File(getExternalFilesDir(null) + "/db.imgdb");
        Image image = null;
        try {
            image = frame.acquireCameraImage();  //getting captured Image in YUV_420_888 format
        } catch (NotYetAvailableException notYetAvailableException) {
            notYetAvailableException.printStackTrace();
        }
        //Intent intent = new Intent(ArCameraActivity.this, AddNewPlaceActivity.class);
        final String filename = generateFilename();
        ArSceneView view = customArFragment.getArSceneView();

        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);

        //final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        //handlerThread.start();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PixelCopy.request(view, bitmap, (copyResult) -> {
                if (copyResult == PixelCopy.SUCCESS) {
                    pictureImagePath = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + Const.TMP_IMAGE_FILE;
                    try {
                        saveBitmapToDisk(bitmap, pictureImagePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(ArCameraActivity.this, AddNewPlaceActivity.class);
                    Bundle bundle = new Bundle();
                    Log.d("huheo", "picture: "+pictureImagePath);
                    bundle.putString("CNTN19", pictureImagePath);
                    bundle.putDouble("lat", lat);
                    bundle.putDouble("lng", lng);
                    intent.putExtra("BUNDLE", bundle);
                    Log.d("huheo", "start acti");
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(ArCameraActivity.this, copyResult, Toast.LENGTH_LONG);
                    toast.show();
                }
                //handlerThread.quitSafely();
            }, new Handler(Looper.getMainLooper()));
        }
    }
    public void setFoxFace(View view){
        customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        btnFF.setTextColor(Color.parseColor("#FFFFFF"));
        btnYG.setTextColor(Color.parseColor("#000000"));
        btnBG.setTextColor(Color.parseColor("#000000"));
        isChange = true;
        option = 1;
        //faceNodeMap.clear();
        //AugmentedFaceDisplay(R.raw.fox_face);
    }
    public void setYellowGlasses(View view){
        customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        btnYG.setTextColor(Color.parseColor("#FFFFFF"));
        btnFF.setTextColor(Color.parseColor("#000000"));
        btnBG.setTextColor(Color.parseColor("#000000"));
        isChange = true;
        option=2;
        //faceNodeMap.clear();
        //AugmentedFaceDisplay(R.raw.yellow_sunglasses);
    }
    public void setBlackGlasses(View view){
        customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        btnBG.setTextColor(Color.parseColor("#FFFFFF"));
        btnFF.setTextColor(Color.parseColor("#000000"));
        btnYG.setTextColor(Color.parseColor("#000000"));
        isChange=true;
        option = 3;
        //faceNodeMap.clear();
        //AugmentedFaceDisplay(R.raw.sunglasses);
    }
}