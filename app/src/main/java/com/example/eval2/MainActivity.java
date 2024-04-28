package com.example.eval2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private final int REQ_CODE_PERM_STORAGE = 1;
    private final int REQ_CODE_BUKA_GALLERY = 1;
    private final int REQ_CODE_BUKA_KAMERA = 2;
    private DataFileGambar dataFileGambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
    }

    @SuppressWarnings("deprecation")
    private void bukaGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_BUKA_GALLERY);
    }

    public void bukaGalleryClick(View view) {
        int permissionReadStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE_PERM_STORAGE);
        } else {
            bukaGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_PERM_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bukaGallery();
            } else {
                Toast.makeText(this, "Izin tidak diberikan.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private DataFileGambar getDataFileGambar() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("hasil", ".jpg", storageDir);
        return new DataFileGambar(image.getAbsolutePath(), image);
    }

    public void bukaKameraClick(View view) {
        int permissionCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, REQ_CODE_PERM_STORAGE);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                dataFileGambar = getDataFileGambar();
                Uri uri = FileProvider.getUriForFile(this,
                        "com.example.b1_prak1b_13120220008.fileprovider",
                        dataFileGambar.getFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_CODE_BUKA_KAMERA);
                } else {
                    Toast.makeText(this, "Tidak ada aplikasi kamera yang tersedia.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal membuat file sementara untuk kamera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE_BUKA_GALLERY && data != null) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    imageView.setImageBitmap(bitmap);
                }
            } else if (requestCode == REQ_CODE_BUKA_KAMERA) {
                if (dataFileGambar != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(dataFileGambar.getPathfile());
                    imageView.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(this, "Gagal mengambil gambar dari kamera.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}