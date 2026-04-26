package com.aplikasi.checkyourmood;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import androidx.core.content.FileProvider;
import android.media.ExifInterface;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private BottomNavigationView bottomNavigationView;
    private ImageView capturedImageView;
    private Bitmap bitmap;
    private TextView tv_description, tv_mood;
    private Button btn_save, btn_reset;
    private LinearLayout savePanel;
    private final String url = "https://check-mood.dhisproject.my.id/predict";
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private ProgressDialog progressDialog;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menganalisis ekspresi wajah...");
        progressDialog.setCancelable(false);

        savePanel = findViewById(R.id.save_panel);
        btn_save = findViewById(R.id.btn_save);
        btn_reset = findViewById(R.id.btn_reset);
        btn_save.setVisibility(View.GONE);
        btn_reset.setVisibility(View.GONE);
        tv_mood = findViewById(R.id.tv_mood);
        tv_description = findViewById(R.id.tv_description);
        capturedImageView = findViewById(R.id.iv_captured_image);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.camera) {
                    dispatchTakePictureIntent();
                    return true;
                }

                return false;
            }
        });

        btn_save.setOnClickListener(v -> {
            Bitmap resultBitmap = getBitmapFromView(savePanel);
            saveBitmapToGallery(resultBitmap);
        });

        btn_reset.setOnClickListener(v -> {
            capturedImageView.setImageResource(R.drawable.holder);
            tv_mood.setText("-_-");
            tv_description.setText("-____-");
            btn_save.setVisibility(View.GONE);
            btn_reset.setVisibility(View.GONE);
            bitmap = null;
            currentPhotoPath = null;
        });
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST
            );
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalCacheDir();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                android.graphics.BitmapFactory.Options bmOptions = new android.graphics.BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                android.graphics.BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                int targetW = 1024;
                int targetH = 1024;
                int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                bitmap = android.graphics.BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

                ExifInterface ei = new ExifInterface(currentPhotoPath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                android.graphics.Matrix matrix = new android.graphics.Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                sendPictureToAPI(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar resolusi tinggi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);

        // 🔥 PAKSA BACKGROUND PUTIH
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        view.draw(canvas);

        return bitmap;
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        String filename = "Mood_" + System.currentTimeMillis() + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CheckYourMood");

        Uri uri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
        );

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            Toast.makeText(this, "Berhasil disimpan ke Gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPictureToAPI(Bitmap bitmap) {
        progressDialog.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String mood = jsonObject.getString("mood");
                    String description = jsonObject.getString("description");

                    tv_mood.setText(mood);
                    tv_description.setText(description);
                    capturedImageView.setImageBitmap(bitmap);
                    btn_save.setVisibility(View.VISIBLE);
                    btn_reset.setVisibility(View.VISIBLE);

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String errorMsg = error.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Terjadi kesalahan pada server atau jaringan.";
                }
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", imageBase64);
                return params;
            }
        };

        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000, // 30 detik timeout
                0, // Jangan retry ulang secara otomatis (mencegah request dobel ke API)
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}