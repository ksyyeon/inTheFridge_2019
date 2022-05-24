package com.example.inthe2019.Search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.inthe2019.MainActivity;
import com.example.inthe2019.R;
import com.example.inthe2019.Sauce.sauce;
import com.example.inthe2019.Select.select;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class camera extends AppCompatActivity {
    private ImageButton mMainImage;
    static String m;
    ListView listView;

    private static final String CLOUD_VISION_API_KEY = "AIzaSyBPLsMWvMLO6J9IQUvmqBzdQskjT7Mo3L8";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = camera.class.getSimpleName( );
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_camera );
        mMainImage = findViewById( R.id.vision );

        BottomNavigationView navView = findViewById( R.id.nav_view );
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        navView.getMenu( ).getItem( 1 ).setChecked( false );
        FloatingActionButton camera = findViewById( R.id.camera );
        camera.setOnClickListener( view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( camera.this );
            builder
                    .setMessage( R.string.dialog_select_prompt )
                    .setPositiveButton( R.string.dialog_select_gallery, (dialog, which) -> startGalleryChooser( ) )
                    .setNegativeButton( R.string.dialog_select_camera, (dialog, which) -> startCamera( ) );
            builder.create( ).show( );
        } );
        // URL 설정.
        String ingre = "http://10.0.2.2/inthe2019/inthe_ingre.php";
        //10.0.2.2

        // AsyncTask를 통해 HttpURLConnection 수행.
        FloatingActionButton chk = findViewById( R.id.plus );
        chk.setOnClickListener( view -> {
                    NetworkTask networkTask = new NetworkTask( ingre, null, m );
                    networkTask.execute( );
                }
        );
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener( ) {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId( )) {
                case R.id.navigation_menu1:
                    Intent intent1 = new Intent( camera.this, MainActivity.class );
                    startActivity( intent1 );
                    return true;
                case R.id.navigation_menu2:
                    Intent intent2 = new Intent( camera.this, camera.class );
                    startActivity( intent2 );
                    return true;
                case R.id.navigation_menu3:
                    Intent intent3 = new Intent( camera.this, search.class );
                    startActivity( intent3 );
                    return true;
                case R.id.navigation_menu4:
                    Intent intent4 = new Intent( camera.this, sauce.class );
                    startActivity( intent4 );
                    return true;
            }
            return false;
        }
    };

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        String camera;

        public NetworkTask(String url, ContentValues values, String camera) {
            this.url = url;
            this.values = values;
            this.camera = camera;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection( );
            result = requestHttpURLConnection.request( url, values ); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute( s );
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            jsonParsing( s, camera );
        }
    }

    public void jsonParsing(String jsonString, String camera) {
        ArrayList<Integer> codeArray = new ArrayList<>( );
        ArrayList<Integer> cntList = new ArrayList<>( );
        ArrayList<Integer> weightArray = new ArrayList<>( );
        StringBuffer knameArray = new StringBuffer( );
        String[] camera_result = camera.split( "\n" );
        camera_result = new HashSet<String>( Arrays.asList( camera_result ) ).toArray( new String[0] );

        String ename;
        String kname;
        int recipecode;
        int typecode;

        try {
            JSONObject jsonObject = new JSONObject( jsonString );
            JSONArray ingreArray = jsonObject.getJSONArray( "ingre" );

            for (int i = 0; i < camera_result.length; i++) {
                for (int j = 0; j < ingreArray.length( ); j++) {
                    JSONObject ingreObject = ingreArray.getJSONObject( j );
                    ename = ingreObject.optString( "ename" );
                    kname = ingreObject.optString( "name" );
                    recipecode = ingreObject.optInt( "recipecode" );
                    typecode = ingreObject.optInt( "typecode" );
                    if (ename.equalsIgnoreCase( camera_result[i] )) {
                        codeArray.add( recipecode );
                        knameArray.append( kname + "," );
                        weightArray.add( typecode + recipecode );
                    }
                }
            }

            String[] result = knameArray.toString( ).split( "," );
            final String[] items  = new HashSet<String>( Arrays.asList( result ) ).toArray( new String[0] );

            StringBuffer test = new StringBuffer( );
            ArrayList<Integer> codes = new ArrayList<Integer>( new HashSet<Integer>( codeArray ) ); //레시피코드 중복 제거
            for (int c : codes) {
                int cnt = 0;
                for (int w : weightArray) {
                    if (w % 1000 == c) cnt += w - c;
                }
                if (cnt + c >= 10000) {
                    cntList.add( cnt + c );
                    test.append( cnt + c + "\n" );
                }  //레시피코드 가중치 부여
            }

            ArrayList<Integer> SelectedItems = new ArrayList<>();

            AlertDialog.Builder confirm = new AlertDialog.Builder( camera.this );
            confirm.setTitle( "사용할 재료를 선택하세요" )
                    .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                SelectedItems.add(which);
                            }
                        }
                    })
                    .setPositiveButton( "요리시작>O<", new DialogInterface.OnClickListener( ) {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            StringBuffer sb = new StringBuffer( );
                            for (int i = 0; i < SelectedItems.size(); i++) {
                                int index = (int) SelectedItems.get(i);

                                sb.append(items[index]);
                            }

                            Intent intent = new Intent( camera.this, select.class );
                            intent.putExtra( "rc", cntList );
                            intent.putExtra( "sb", sb.toString( ) );
                            m = "";
                            startActivity( intent );
                        }
                    } )
                    .setNegativeButton( "다시하기o3o", new DialogInterface.OnClickListener( ) {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //인식했던 기록 비우기
                            m = "";
                        }
                    } );
            confirm.create( );
            confirm.show( );
        } catch (JSONException e) {
            e.printStackTrace( );
        }
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission( this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE )) {
            Intent intent = new Intent( );
            intent.setType( "image/*" );
            intent.setAction( Intent.ACTION_GET_CONTENT );
            startActivityForResult( Intent.createChooser( intent, "Select a photo" ),
                    GALLERY_IMAGE_REQUEST );
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA )) {
            Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
            Uri photoUri = FileProvider.getUriForFile( this, getApplicationContext( ).getPackageName( ) + ".provider", getCameraFile( ) );
            intent.putExtra( MediaStore.EXTRA_OUTPUT, photoUri );
            intent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
            startActivityForResult( intent, CAMERA_IMAGE_REQUEST );
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir( Environment.DIRECTORY_PICTURES );
        return new File( dir, FILE_NAME );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage( data.getData( ) );
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile( this, getApplicationContext( ).getPackageName( ) + ".provider", getCameraFile( ) );
            uploadImage( photoUri );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted( requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults )) {
                    startCamera( );
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted( requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults )) {
                    startGalleryChooser( );
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
                                MediaStore.Images.Media.getBitmap( getContentResolver( ), uri ),
                                MAX_DIMENSION );

                callCloudVision( bitmap );
                mMainImage.setImageBitmap( bitmap );
                TextView tv = findViewById( R.id.click );
                tv.setText( "끝내려면  ✔버튼을 클릭하세요" );


            } catch (IOException e) {
                Log.d( TAG, "Image picking failed because " + e.getMessage( ) );
                Toast.makeText( this, R.string.image_picker_error, Toast.LENGTH_LONG ).show( );
            }
        } else {
            Log.d( TAG, "Image picker gave us a null image." );
            Toast.makeText( this, R.string.image_picker_error, Toast.LENGTH_LONG ).show( );
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport( );
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance( );

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer( CLOUD_VISION_API_KEY ) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest( visionRequest );

                        String packageName = getPackageName( );
                        visionRequest.getRequestHeaders( ).set( ANDROID_PACKAGE_HEADER, packageName );

                        String sig = PackageManagerUtils.getSignature( getPackageManager( ), packageName );

                        visionRequest.getRequestHeaders( ).set( ANDROID_CERT_HEADER, sig );
                    }
                };

        Vision.Builder builder = new Vision.Builder( httpTransport, jsonFactory, null );
        builder.setVisionRequestInitializer( requestInitializer );

        Vision vision = builder.build( );

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest( );
        batchAnnotateImagesRequest.setRequests( new ArrayList<AnnotateImageRequest>( ) {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest( );

            // Add the image
            Image base64EncodedImage = new Image( );
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream( );
            bitmap.compress( Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream );
            byte[] imageBytes = byteArrayOutputStream.toByteArray( );

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent( imageBytes );
            annotateImageRequest.setImage( base64EncodedImage );

            // add the features we want
            annotateImageRequest.setFeatures( new ArrayList<Feature>( ) {{
                Feature labelDetection = new Feature( );
                labelDetection.setType( "LABEL_DETECTION" );
                labelDetection.setMaxResults( MAX_LABEL_RESULTS );
                add( labelDetection );
            }} );

            // Add the list of one thing to the request
            add( annotateImageRequest );
        }} );

        Vision.Images.Annotate annotateRequest =
                vision.images( ).annotate( batchAnnotateImagesRequest );
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent( true );
        Log.d( TAG, "created Cloud Vision request object, sending request" );

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<camera> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(camera activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>( activity );
            mRequest = annotate;
        }

        @Override
        public String doInBackground(Object... params) {
            try {
                Log.d( TAG, "created Cloud Vision request object, sending request" );
                BatchAnnotateImagesResponse response = mRequest.execute( );
                return convertResponseToString( response );


            } catch (GoogleJsonResponseException e) {
                Log.d( TAG, "failed to make API request because " + e.getContent( ) );
            } catch (IOException e) {
                Log.d( TAG, "failed to make API request because of other IOException " +
                        e.getMessage( ) );
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            camera activity = mActivityWeakReference.get( );
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest( );

        annotateImageRequest.setFeatures( new ArrayList<Feature>( ) {{
            Feature labelDetection = new Feature( );
            labelDetection.setType( "LABEL_DETECTION" );
            labelDetection.setMaxResults( 10 );
            add( labelDetection );
        }} );

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask( this, prepareAnnotationRequest( bitmap ) );
            labelDetectionTask.execute( );
        } catch (IOException e) {
            Log.d( TAG, "failed to make API request because of other IOException " +
                    e.getMessage( ) );
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth( );
        int originalHeight = bitmap.getHeight( );
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
        return Bitmap.createScaledBitmap( bitmap, resizedWidth, resizedHeight, false );
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder( );
        List<EntityAnnotation> labels = response.getResponses( ).get( 0 ).getLabelAnnotations( );
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                //message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                message.append( String.format( Locale.US, "%s", label.getDescription( ) ) );
                message.append( "\n" );
            }
        } else {
            message.append( "nothing" );
        }
        m += message.toString( );
        Log.d( "ig", message.toString( ) );
        Log.d( "n", m );
        return message.toString( );
    }
}