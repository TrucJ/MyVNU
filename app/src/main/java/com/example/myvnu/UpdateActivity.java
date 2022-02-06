package com.example.myvnu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myvnu.roomdatabase.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class UpdateActivity extends AppCompatActivity {

    Button btnUpdate;
    Button btnReturn;
    TextView txtCurrentVersion;
    TextView txtLatestVersion;
    TextView txtUpdateStatus;
    double currentVersion;
    double latestVersion;
    DatabaseReference mData;
    StorageReference storageRef;
    String CACHE_DIR;
    String IMG_PATH;
    String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        CACHE_DIR = getApplicationContext().getCacheDir().getAbsolutePath();
        IMG_PATH = CACHE_DIR + "/" + Const.IMAGE_FOLDER;
        initViews();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "";
                updateDatabase();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentVersion = checkVersion();
        txtCurrentVersion.setText(Double.toString(currentVersion));
        initCloudDatabaseRef();
    }

    private void initViews(){
        btnUpdate = (Button) findViewById(R.id.buttonUpdateReal);
        btnReturn = (Button) findViewById(R.id.buttonReturnHome);
        txtCurrentVersion = (TextView) findViewById(R.id.textViewCurrentVersion);
        txtLatestVersion = (TextView) findViewById(R.id.textViewLatestVersion);
        txtUpdateStatus = (TextView) findViewById(R.id.textViewUpdateStatus);
        txtUpdateStatus.setMovementMethod(new ScrollingMovementMethod());
    }

    private void initCloudDatabaseRef(){
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("version").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                latestVersion = (Double) snapshot.getValue();
                txtLatestVersion.setText(Double.toString(latestVersion));
                System.out.println("Latest version = " + latestVersion);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private double checkVersion() {
        File versionFile = new File( CACHE_DIR + "/places/version.txt");
        String v;
        try (Scanner input = new Scanner(versionFile)) {
            v = input.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0.9f;
        }
        System.out.println("Local version = " + v);
        System.out.println("Cloud version = " + latestVersion);
        return Double.parseDouble(v);
    }

    public void updateDatabase(){

        status = "Phiên bản hiện tại: " + Double.toString(currentVersion) + "\n";
        status = "Phiên bản mới nhất: " + Double.toString(latestVersion) + "\n";
        if(currentVersion == latestVersion){
            status = status + ("Đây đã là phiên bản mới nhất.\n");
            txtUpdateStatus.setText(status);
        }
        else{
            status = status + ("Đang cập nhật lên phiên bản " + Double.toString(latestVersion) + "\nVui lòng đợi...\n\n");
            txtUpdateStatus.setText(status);
            DBAction dbAction = new DBAction();

            // Tạo cơ sở dữ liệu mới nhất
            mData.child("data").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    HashMap<String, Object> dat = (HashMap<String, Object>) task.getResult().getValue();
                    dat.forEach((key, value) -> {
                        HashMap<String, Object> h = (HashMap<String, Object>) value;
                        Place p = new Place(h);
                        status = status + "+" + p.getTitle() + "\n";
                        txtUpdateStatus.setText(status);
                        dbAction.insert(UpdateActivity.this, p);
                    });
                    // Lấy danh sách file ảnh mới nhất
                    ArrayList<String> latestImages = new ArrayList<String>();
                    latestImages.addAll(dbAction.getAllImages(UpdateActivity.this));
                    ArrayList<String> latestIcons = new ArrayList<String>();
                    latestIcons.addAll(dbAction.getAllIcons(UpdateActivity.this));

                    // Lấy danh sách file ảnh local hiện tại
                    ArrayList<String> currentImages = new ArrayList<String>();
                    String imgPath = getApplicationContext().getCacheDir().getAbsolutePath() + "/places";
                    File directory = new File(imgPath);
                    File[] files = directory.listFiles();
                    for(int i = 0; i < files.length; i++){
                        currentImages.add(files[i].getName());
                    }

                    // Tạo danh sách file ảnh cần tải bổ sung (set)
                    Set<String> newImages = new HashSet<String>();
                    for(int i = 0; i < latestImages.size(); i++){
                        if(latestImages.get(i) != null && !currentImages.contains(latestImages.get(i)))
                            newImages.add(latestImages.get(i));
                    }
                    for(int i = 0; i < latestIcons.size(); i++){
                        if(latestIcons.get(i) != null && !currentImages.contains(latestIcons.get(i)))
                            newImages.add(latestIcons.get(i));
                    }

                    // Tải xuống các file ảnh bổ sung
                    Iterator<String> iterator = newImages.iterator();
                    while (iterator.hasNext()) {
                        String imgFile = iterator.next();
                        status = status + "New image: " + imgFile + "\n";
                        txtUpdateStatus.setText(status);
                        downloadImage(imgFile);
                    }
                    /*
                    for(int i = 0; i < newImages.size(); i++){
                        status = status + "New image: " + newImages.get(i) + "\n";
                        txtUpdateStatus.setText(status);
                        downloadImage(newImages.get(i));
                    }
                    */

                    // Cập nhật xong
                    writeVersionFile(latestVersion);
                    currentVersion = latestVersion;
                    txtCurrentVersion.setText(Double.toString(currentVersion));
                    status = status + "---> CẬP NHẬT XONG: Phiên bản " + Double.toString(currentVersion);
                    txtUpdateStatus.setText(status);
                    Toast.makeText(UpdateActivity.this, "Đã cập nhật xong!.", Toast.LENGTH_SHORT).show();
                }
            });



        }
    }

    public void downloadImage(String filename) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("images/" + filename);
        StorageReference islandRef = storageRef;

        File rootPath = new File(CACHE_DIR, "places");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,filename);

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ","local file created: " + localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ","local file not created: " + exception.toString());
            }
        });
    }

    public void writeVersionFile(double _version){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(CACHE_DIR + "/places/version.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println(Double.toString(_version));
        writer.close();
    }

    /* FOR UPDATE FULL PACKAGE - FUTURE WORKS
    public void downloadUpdatePackage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("assets.zip");
        StorageReference islandRef = storageRef;

        File rootPath = new File(Environment.getExternalStorageDirectory(), "");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,"update.zip");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local tem file created  created " +localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });
    }

    public void unzip(String _zipFile, String _targetLocation) {
        System.out.println("Unzipping");
        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            System.out.println(fin.toString());
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }
                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void dirChecker(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            //Do something
        }
        else {file.mkdirs();}
    }
     */
}