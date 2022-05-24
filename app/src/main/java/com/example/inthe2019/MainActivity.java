package com.example.inthe2019;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.inthe2019.Recipe.recipe;
import com.example.inthe2019.Sauce.sauce;
import com.example.inthe2019.Search.RequestHttpConnection;
import com.example.inthe2019.Search.camera;
import com.example.inthe2019.Search.search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ImageButton b1; ImageButton b2;
    ImageButton b3; ImageButton b4;
    ImageButton b5;

    // URL 설정.
    String basic = "http://10.0.2.2/inthe2019/inthe_basic.php";
    //10.0.2.2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        b1 = (ImageButton) findViewById(R.id.rech1); //potato
        b2 = (ImageButton) findViewById(R.id.rech2); //honghab
        b3 = (ImageButton) findViewById(R.id.rec2); //omlet
        b4 = (ImageButton) findViewById(R.id.rec3); //curry
        b5 = (ImageButton) findViewById(R.id.rec4); //mandoo
    }

    public void mOnClick(View view) {
        String fname="";
        switch (view.getId()) {
            case R.id.rech1:
                fname = "감자수프";
                break;
            case R.id.rech2:
                fname = "홍합탕";
                break;
            case R.id.rec2:
                fname = "오믈렛";
                break;
            case R.id.rec3:
                fname = "카레라이스";
                break;
            case R.id.rec4:
                fname = "만둣국";
                break;
        }

        NetworkTask networkTask = new NetworkTask(basic, null, fname);
        networkTask.execute();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_menu1:
                    Intent intent1 = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_menu2:
                    Intent intent2 = new Intent(MainActivity.this, camera.class);
                    startActivity(intent2);
                    return true;
                case R.id.navigation_menu3:
                    Intent intent3 = new Intent(MainActivity.this, search.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_menu4:
                    Intent intent4 = new Intent(MainActivity.this, sauce.class);
                    startActivity(intent4);
                    return true;
            }
            return false;
        }
    };

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        String name;

        public NetworkTask(String url, ContentValues values, String name) {
            this.url = url;
            this.values = values;
            this.name = name;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            jsonParsing(s, name);
        }

    }

    public void jsonParsing(String jsonString, String fname) {
        StringBuffer toRecipe = new StringBuffer();
        String total1;
        String total2;
        String image;
        String recipecode;
        String name;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray basicArray = jsonObject.getJSONArray("basic");

            for (int j = 0; j < basicArray.length(); j++) {
                JSONObject basicObject = basicArray.getJSONObject(j);
                name = basicObject.optString("name");
                image = basicObject.optString("image");
                total1 = basicObject.optString("total1");
                total2 = basicObject.optString("total2");
                recipecode = basicObject.optString("recipecode");
                if (name.equals(fname)) {
                    toRecipe.append(recipecode + "\n");
                    toRecipe.append(name + "\n");
                    toRecipe.append(image + "\n");
                    toRecipe.append(total1 + "\n");
                    toRecipe.append(total2 + "\n");
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (toRecipe.length() > 0) {
            Intent intent = new Intent(MainActivity.this, recipe.class);
            intent.putExtra("ig", toRecipe.toString());
            intent.putExtra("sb", "");
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "해당 요리를 조회할 수 없습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}