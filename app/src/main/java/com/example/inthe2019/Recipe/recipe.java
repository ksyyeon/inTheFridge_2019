package com.example.inthe2019.Recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inthe2019.MainActivity;
import com.example.inthe2019.R;
import com.example.inthe2019.Sauce.sDBHelper;
import com.example.inthe2019.Sauce.sauce;
import com.example.inthe2019.Search.RequestHttpConnection;
import com.example.inthe2019.Search.camera;
import com.example.inthe2019.Search.search;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class recipe extends AppCompatActivity {
    String a;
    String b;
    ImageView food;
    Bitmap bitmap;

    TextView name;
    TextView ingres; TextView im;
    TextView fdd; TextView fm;
    TextView ss; TextView sm;
    TextView ch; TextView cm;

    SQLiteDatabase db;
    sDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_recipe);

        BottomNavigationView navView = findViewById( R.id.nav_view );
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        Intent intent = getIntent();
        a = intent.getExtras().getString("ig");
        b = intent.getExtras().getString("sb");

        helper = new sDBHelper(this);
        db = helper.getReadableDatabase();

        // URL 설정.
        String recipe = "http://fb07f75a.ngrok.io/inthe2019/inthe_recipe.php";
        //10.0.2.2

        NetworkTask networkTask = new NetworkTask(recipe, null, a);
        networkTask.execute();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener( ) {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId( )) {
                case R.id.navigation_menu1:
                    Intent intent1 = new Intent( recipe.this, MainActivity.class );
                    startActivity( intent1 );
                    return true;
                case R.id.navigation_menu2:
                    Intent intent2 = new Intent( recipe.this, camera.class );
                    startActivity( intent2 );
                    return true;
                case R.id.navigation_menu3:
                    Intent intent3 = new Intent( recipe.this, search.class );
                    startActivity( intent3 );
                    return true;
                case R.id.navigation_menu4:
                    Intent intent4 = new Intent( recipe.this, sauce.class );
                    startActivity( intent4 );
                    return true;
            }
            return false;
        }
    };
    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        String select;

        public NetworkTask(String url, ContentValues values, String select) {
            this.url = url;
            this.values = values;
            this.select = select;
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
            jsonParsing(s, select);
        }
    }

    //소스 DB
    void getAllData() {
    }

    public void jsonParsing(String jsonString, String select) {
        StringBuffer ingredients = new StringBuffer();
        StringBuffer iamount = new StringBuffer();
        StringBuffer found = new StringBuffer();
        StringBuffer famount = new StringBuffer();
        StringBuffer sauces = new StringBuffer();
        StringBuffer samount = new StringBuffer();
        StringBuffer checked = new StringBuffer();
        StringBuffer camount = new StringBuffer();

        String[] selected = select.split("\n");
        ArrayList<String> sauceArray = new ArrayList<>();
        ArrayList<String> processArray = new ArrayList<>();
        String process;
        String order;
        String recipecode;

        db = helper.getReadableDatabase();
        Cursor c = db.query("sauce", new String[]{"_id", "name", "date", "chk"},
                null, null, null, null, null);
        while (c.moveToNext()) {
            if(c.getString(3).equals("1")){
                sauceArray.add(c.getString(1));
            }
        }
        c.close();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray recipeArray = jsonObject.getJSONArray("recipe");

            for (int j = 0; j < recipeArray.length(); j++) {
                JSONObject basicObject = recipeArray.getJSONObject(j);
                process = basicObject.optString("process");
                order = basicObject.optString("order");
                recipecode = basicObject.optString("recipecode");
                if (recipecode.equals(selected[0])){
                    processArray.add("STEP " + order + "\n" + process);
                }
            }

            ViewPager viewPager = findViewById(R.id.viewPager);
            FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
            viewPager.setAdapter(fragmentAdapter);

            for (int i = 0; i < processArray.size(); i++) {
                TextFragment textFragment = new TextFragment();
                Bundle bundle = new Bundle();
                bundle.putString("textRes", processArray.get(i));
                textFragment.setArguments(bundle);
                fragmentAdapter.addItem(textFragment);
            }
            fragmentAdapter.notifyDataSetChanged();

            String[] total1 = selected[3].split("&");
            if (b.length() > 0) {
                String[] sb = b.split(" ");
                ArrayList<String> fd = new ArrayList<>();
                for (String s : sb)
                    fd.add(s);
                for (int i = 0; i < total1.length; i++) {
                    if (total1[i].contains(" ")) {
                        if (fd.contains(total1[i].substring(0, total1[i].indexOf(" ")))) {
                            found.append(total1[i].substring(0, total1[i].indexOf(" ")) + "\n");
                            famount.append(total1[i].substring(total1[i].indexOf(" ") + 1) + "\n");
                        } else {
                            ingredients.append(total1[i].substring(0, total1[i].indexOf(" ")) + "\n");
                            iamount.append(total1[i].substring(total1[i].indexOf(" ") + 1) + "\n");
                        }
                    } else {
                        if (fd.contains(total1[i])) {
                            found.append(total1[i] + "\n");
                            famount.append("약간" + "\n");
                        } else {
                            ingredients.append(total1[i] + "\n");
                            iamount.append("약간" + "\n");
                        }
                    }
                }
            } else {
                for (int i = 0; i < total1.length; i++) {
                    if (total1[i].contains(" ")) {
                        ingredients.append(total1[i].substring(0, total1[i].indexOf(" ")) + "\n");
                        iamount.append(total1[i].substring(total1[i].indexOf(" ") + 1) + "\n");
                    } else {
                        ingredients.append(total1[i] + "\n");
                        iamount.append("약간" + "\n");
                    }
                }
            }

            String[] total2 = selected[4].split("&");
            for (int i = 0; i < total2.length; i++) {
                if (total2[i].contains(" ")) {
                    if (sauceArray.contains(total2[i].substring(0, total2[i].indexOf(" ")))) {
                        checked.append(total2[i].substring(0, total2[i].indexOf(" ")) + "\n");
                        camount.append(total2[i].substring(total2[i].indexOf(" ") + 1)+ "\n");
                    } else {
                        sauces.append(total2[i].substring(0, total2[i].indexOf(" "))+ "\n");
                        samount.append(total2[i].substring(total2[i].indexOf(" ") + 1)+ "\n");
                    }
                } else {
                    if (sauceArray.contains(total2[i])) {
                        checked.append(total2[i] + "\n");
                        camount.append("약간" + "\n");
                    } else {
                        sauces.append(total2[i] + "\n");
                        samount.append("약간" + "\n");
                    }
                }
            }

            food = findViewById(R.id.food);
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        URL url = new URL(selected[2]);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();

            try {
                thread.join();

                food.setImageBitmap(bitmap);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            name = findViewById(R.id.name);
            name.setText(selected[1]);

            ingres = findViewById(R.id.ingredients);
            ingres.setText(erase(ingredients));

            im = findViewById(R.id.iamount);
            im.setText(erase(iamount));

            fdd = findViewById(R.id.found);
            fdd.setText(erase(found));
            fdd.setTextColor(Color.parseColor("#d05325"));

            fm = findViewById(R.id.famount);
            fm.setText(erase(famount));
            fm.setTextColor(Color.parseColor("#d05325"));

            ss = findViewById(R.id.sauces);
            ss.setText(erase(sauces));

            sm = findViewById(R.id.samount);
            sm.setText(erase(samount));

            ch = findViewById(R.id.checked);
            ch.setText(erase(checked));
            ch.setTextColor(Color.parseColor("#568235"));

            cm = findViewById(R.id.camount);
            cm.setText(erase(camount));
            cm.setTextColor(Color.parseColor("#568235"));

            if (b.length()==0 && TextUtils.isEmpty(checked)) {
                fdd.setVisibility(View.GONE);
                fm.setVisibility(View.GONE);
                ch.setVisibility(View.GONE);
                cm.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String erase(StringBuffer sb) {
        if(!TextUtils.isEmpty(sb))
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    class FragmentAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<>();
        FragmentAdapter(FragmentManager fm) {
            super(fm);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
        void addItem(Fragment fragment) {
            fragments.add(fragment);
        }
    }
}