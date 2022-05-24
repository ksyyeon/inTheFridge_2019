package com.example.inthe2019.Sauce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import com.example.inthe2019.MainActivity;
import com.example.inthe2019.Search.PackageManagerUtils;
import com.example.inthe2019.Search.PermissionUtils;
import com.example.inthe2019.R;
import com.example.inthe2019.Search.camera;
import com.example.inthe2019.Search.search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.soundcloud.android.crop.Crop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sauce extends AppCompatActivity {
    //TEXT API
    static String m;
    Calendar pickedDate = Calendar.getInstance();
    Calendar minDate = Calendar.getInstance();
    Calendar maxDate = Calendar.getInstance();

    private static final String CLOUD_VISION_API_KEY = "AIzaSyBPLsMWvMLO6J9IQUvmqBzdQskjT7Mo3L8";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = sauce.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    //소스 DB
    EditText edit;
    Button add;
    sDBHelper helper;
    ArrayList<SauceMemo> data = new ArrayList<>();
    SQLiteDatabase db;
    SauceAdapter adapter;
    GridView gv;
    AdapterView.AdapterContextMenuInfo info;
    InputMethodManager im;
    ImageView img;
    int position;
    BottomNavigationView navView;
    Switch sw;

    //알람
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_sauce);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navView.getMenu().getItem( 1 ).setChecked( false );
        navView.getMenu().getItem( 2 ).setChecked( false );
        navView.getMenu().getItem( 3 ).setChecked( false );

        helper = new sDBHelper(this);
        gv = findViewById(R.id.gv);
        getAllData();
        adapter = new SauceAdapter(this, R.layout.sauce_context, data);
        gv.setAdapter(adapter);
        registerForContextMenu(gv);
        edit = findViewById(R.id.edit);
        add = findViewById(R.id.add);
        im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        img = new ImageView(this);

        //유통기한
        checkex();
        //알림
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //가이드
        startActivity(new Intent(this,Guide.class));

        sw = (Switch) findViewById(R.id.sw);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Comparator<SauceMemo> dateAsc = new Comparator<SauceMemo>() {
                    @Override
                    public int compare(SauceMemo s1, SauceMemo s2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                        Date first = null;
                        Date second = null;

                        if (TextUtils.isEmpty(s1.getDate())) {
                            if (TextUtils.isEmpty(s2.getDate())) {
                                try {
                                    first = sdf.parse("2099.12.31");
                                    second = sdf.parse("2099.12.31");
                                } catch (ParseException ex) {
                                }
                            } else {
                                try {
                                    first = sdf.parse("2099.12.31");
                                    second = sdf.parse(s2.getDate());
                                } catch (ParseException ex) {
                                }
                            }
                        } else {
                            if (TextUtils.isEmpty(s2.getDate())) {
                                try {
                                    first = sdf.parse(s1.getDate());
                                    second = sdf.parse("2099.12.31");
                                } catch (ParseException ex) {
                                }
                            } else {
                                try {
                                    first = sdf.parse(s1.getDate());
                                    second = sdf.parse(s2.getDate());
                                } catch (ParseException ex) {
                                }
                            }
                        }
                        return first.compareTo(second);
                    }
                };
                Collections.sort(data, dateAsc);
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_menu1:
                    Intent intent1 = new Intent(sauce.this, MainActivity.class);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_menu2:
                    Intent intent2 = new Intent(sauce.this, camera.class);
                    startActivity(intent2);
                    return true;
                case R.id.navigation_menu3:
                    Intent intent3 = new Intent(sauce.this, search.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_menu4:
                    Intent intent4 = new Intent(sauce.this, sauce.class);
                    startActivity(intent4);
                    return true;
            }
            return false;
        }
    };

    //소스 DB
    void getAllData() {
        db = helper.getReadableDatabase();
        Cursor c = db.query("sauce", new String[]{"_id", "name", "date", "chk"},
                null, null, null, null, null);
        while (c.moveToNext()) {
            data.add(new SauceMemo(c.getString(1), c.getString(2), c.getString(3)));
        }
        c.close();

        Comparator<SauceMemo> checkAsc = new Comparator<SauceMemo>() {
            @Override
            public int compare(SauceMemo s1, SauceMemo s2) {
                return s2.getChk().compareTo(s1.getChk());
            }
        };
        Collections.sort(data, checkAsc);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.item, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        position = info.position;
        db = helper.getWritableDatabase();
        ContentValues values;
        switch (item.getItemId()) {
            case R.id.item1:
                AlertDialog.Builder builder = new AlertDialog.Builder(sauce.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, (dialog, which) -> startGalleryChooser())
                        .setNegativeButton(R.string.dialog_select_camera, (dialog, which) -> startCamera());
                builder.create().show();
                break;

            case R.id.item5:
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd");

                                String y = year + "";
                                String m = (month + 1) + "";
                                String d = dayOfMonth + "";
                                if (d.length() == 1) {
                                    d = "0" + dayOfMonth;
                                }
                                if (m.length() == 1) {
                                    m = "0" + (month + 1);
                                }
                                String date = y + "." + m + "." + d;


                                Date da = null;
                                try {
                                    da = dateFormat.parse(date);
                                } catch (ParseException ex) {

                                }

                                db = helper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("date", dateFormat.format(da));
                                values.put("chk", "1");
                                db.update("sauce", values, "name=?", new String[]{data.get(position).getName()});
                                data.set(position, new SauceMemo(data.get(position).getName(), dateFormat.format(da), "1"));
                                adapter.notifyDataSetChanged();
                                Snackbar.make(gv, "유통기한 " + "20" + dateFormat.format(da) + "이 저장되었습니다.", Snackbar.LENGTH_LONG).show();
                                setAlarm();
                            }
                        },
                        pickedDate.get(Calendar.YEAR),
                        pickedDate.get(Calendar.MONTH),
                        pickedDate.get(Calendar.DATE));

                minDate.set(2019, 12 - 1, 01);
                datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());

                maxDate.set(2050, 12 - 1, 31);
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                datePickerDialog.show();
                break;

            case R.id.item2:
                values = new ContentValues();
                values.put("chk", "1");
                db.update("sauce", values, "name=?", new String[]{data.get(position).getName()});
                data.set(position, new SauceMemo(data.get(position).getName(),
                        data.get(position).getDate(), "1"));
                adapter.notifyDataSetChanged();
                break;

            case R.id.item3:
                values = new ContentValues();
                values.put("date", "");
                values.put("chk", "0");
                db.update("sauce", values, "name=?", new String[]{data.get(position).getName()});
                data.set(position, new SauceMemo(data.get(position).getName(), null, "0"));
                adapter.notifyDataSetChanged();
                break;

            case R.id.item4:
                db.delete("sauce", "name =?", new String[]{data.get(position).getName()});
                data.remove(position);
                adapter.notifyDataSetChanged();
                break;
        }
        db.close();
        helper.close();
        return super.onContextItemSelected(item);
    }

    public void sOnclick(View v) {
        db = helper.getWritableDatabase();
        ContentValues values;
        switch (v.getId()) {
            case R.id.add:
                if (edit.getText().length() > 0) {
                    values = new ContentValues();
                    values.put("name", edit.getText().toString());
                    values.put("chk", "1");
                    db.insert("sauce", null, values);
                    data.add(new SauceMemo(edit.getText().toString(), null, "1"));
                } else {
                    Toast.makeText(this, "사용하실 소스의 이름을 입력해주세요!",
                            Toast.LENGTH_SHORT).show();
                }
                edit.setText("");
                im.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                break;
        }
        db.close();
        adapter.notifyDataSetChanged();
        helper.close();
    }

    //TEXT API
    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);

                callCloudVision(bitmap);
                img.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature textDetection = new Feature();
                textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(10);
                add(textDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<sauce> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(sauce activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        public String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();

                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            sauce activity = mActivityWeakReference.get();
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
            Feature labelDetection = new Feature();
            labelDetection.setType("TEXT_DETECTION");
            labelDetection.setMaxResults(10);
            add(labelDetection);
        }});

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> textDetectionTask = new LableDetectionTask(sauce.this, prepareAnnotationRequest(bitmap));
            textDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things";
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message = labels.get(0).getDescription();
        } else {
            message = "nothing";
        }
        m = message;
        Log.d("ig", message);
        return message;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri photoUri = data.getData();
            cropImage(photoUri);
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            cropImage(photoUri);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            img.setImageURI(Crop.getOutput(result));
            uploadImage(Crop.getOutput(result));
            AlertDialog.Builder date = new AlertDialog.Builder(sauce.this);
            date
                    .setView(img)
                    .setPositiveButton("선택", (dialog, which) -> expParsing())
                    .setNegativeButton("취소", (dialog, which) -> {
                    });
            date.create().show();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void expParsing() {
        db = helper.getWritableDatabase();
        ContentValues values;
        if (m.equals("nothing")) {
            Snackbar.make(gv, "사진을 다시 찍어주세요.", Snackbar.LENGTH_LONG).show();
        } else {
            Pattern pattern = Pattern.compile("[0-9]+[./ ]?+[0-9]+[./ ]?+[0-9]+");
            Matcher matcher = pattern.matcher(m);

            String v = " ";
            String a = " ";
            while (matcher.find()) {
                v = matcher.group();
                Log.d("dd", v);
            }

            StringTokenizer stringTokenizer;
            if (v.contains("/")) {
                stringTokenizer = new StringTokenizer(v, "/");
            } else if (v.contains(",")) {
                stringTokenizer = new StringTokenizer(v, ",");
            } else if (v.contains(" ")) {
                stringTokenizer = new StringTokenizer(v, " ");
            } else {
                stringTokenizer = new StringTokenizer(v, " ");
            }
            while (stringTokenizer.hasMoreTokens()) {
                a += stringTokenizer.nextToken() + ".";
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd");

            Date da = null;
            try {
                da = dateFormat.parse(a);
            } catch (ParseException ex) {
            }

            values = new ContentValues();
            values.put("date", dateFormat.format(da));
            values.put("chk", "1");
            db.update("sauce", values, "name=?", new String[]{data.get(position).getName()});
            data.set(position, new SauceMemo(data.get(position).getName(),
                    dateFormat.format(da), "1"));
            db.close();
            adapter.notifyDataSetChanged();
            helper.close();

            Snackbar.make(gv, "유통기한 " + "20" + dateFormat.format(da) + "이 저장되었습니다.", Snackbar.LENGTH_LONG).show();
            setAlarm();
        }
    }

    private void cropImage(Uri photoUri) {
        Uri savingUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(photoUri, savingUri).withAspect(100, 50).start(this);
    }

    public void setAlarm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd");
        Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);

        int num = 0;
        long now = System.currentTimeMillis();

        String time1 = sdf.format(now);
        Date ndate = null;
        Date sdate = null;
        try {
            ndate = sdf.parse(time1);
        } catch (ParseException ex) {
        }

        Log.d("date", time1);
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT _id, name, date FROM sauce where date LIKE '%1%' ORDER BY 2 DESC ", null);

        PendingIntent[] sender = new PendingIntent[c.getCount()];
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                String n = c.getString(1);
                String ad = c.getString(2);
                try {
                    sdate = sdf.parse(ad);
                } catch (ParseException ex) {
                }
                if (ndate.getTime() <= sdate.getTime()) {
                    i.putExtra("name", n);
                    sender[num] = PendingIntent.getBroadcast(sauce.this, id, i,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    int year = Integer.parseInt(ad.substring(0, 2)) + 2000;
                    int month = Integer.parseInt(ad.substring(3, 5)) - 1;
                    int day = Integer.parseInt(ad.substring(6, 8));

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    calendar.set(Calendar.HOUR_OF_DAY, 03);
                    calendar.set(Calendar.MINUTE, 36);
                    calendar.set(Calendar.SECOND, 00);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender[num]);
                    num++;
                }
            }
            i.putExtra("num", num);
        }
    }

    public void checkex() {
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name, date FROM sauce where date LIKE '%1%' ORDER BY 2 DESC ", null);
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd");
        String time1 = sdf.format(now);
        Date ndate = null;
        Date sdate = null;

        try {
            ndate = sdf.parse(time1);
        } catch (ParseException ex) {
        }

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                String n = c.getString(0);
                String ad = c.getString(1);

                try {
                    sdate = sdf.parse(ad);
                } catch (ParseException ex) {
                }
                if (ndate.getTime() > sdate.getTime()) {
                    db = helper.getWritableDatabase();
                    ContentValues values;
                    values = new ContentValues();
                    values.put("chk", "2");
                    db.update("sauce", values, "name=?", new String[]{n});
                    int index=-1;
                    for (int i = 0; i < data.size(); i++) {
                        if(data.get(i).getName().equals(n))
                            index = i;
                    }
                    data.set(index ,new SauceMemo(n ,ad,"2"));
                    adapter.notifyDataSetChanged();
                }
            }
            db.close();
            helper.close();
        }
    }
}