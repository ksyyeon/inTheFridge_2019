package com.example.inthe2019.Search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.example.inthe2019.MainActivity;
import com.example.inthe2019.R;
import com.example.inthe2019.Sauce.sauce;
import com.example.inthe2019.Recipe.recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class search extends AppCompatActivity {
    ListView list;
    AutoCompleteTextView edit;
    SQLiteDatabase db;
    DBHelper helper;
    ArrayAdapter<Memo> aa;
    List<Memo> ds;
    List<String> ls;

    // URL 설정.
    String basic = "http://fb07f75a.ngrok.io/inthe2019/inthe_basic.php";
    //10.0.2.2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_search);

        BottomNavigationView navView = findViewById( R.id.nav_view );
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        navView.getMenu( ).getItem( 1 ).setChecked( false );
        navView.getMenu( ).getItem( 2 ).setChecked( false );

        list = findViewById(R.id.list);
        edit = findViewById(R.id.edit);
        helper = new DBHelper(this);
        ls = new ArrayList<String>();
        settingList();
        ds = new ArrayList<>();
        getAllMemos();

        edit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                ls));

        aa = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                ds);
        list.setAdapter(aa);

        setEnabled(aa.getCount() > 0);

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button add = (Button) findViewById(R.id.add);
                if (edit.getText().length() != 0)
                    add.setEnabled(true);
                else
                    add.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        edit.addTextChangedListener(tw);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Memo item = (Memo) list.getItemAtPosition(position);
                String fname = item.getMemo();

                NetworkTask networkTask = new NetworkTask(basic, null, fname);
                networkTask.execute();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener( ) {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId( )) {
                case R.id.navigation_menu1:
                    Intent intent1 = new Intent( search.this, MainActivity.class );
                    startActivity( intent1 );
                    return true;
                case R.id.navigation_menu2:
                    Intent intent2 = new Intent( search.this, camera.class );
                    startActivity( intent2 );
                    return true;
                case R.id.navigation_menu3:
                    Intent intent3 = new Intent( search.this, search.class );
                    startActivity( intent3 );
                    return true;
                case R.id.navigation_menu4:
                    Intent intent4 = new Intent( search.this, sauce.class );
                    startActivity( intent4 );
                    return true;
            }
            return false;
        }
    };

    public void onClick(View v) {
        db = helper.getWritableDatabase();
        ContentValues values;
        switch (v.getId()) {
            case R.id.add:
                String s = edit.getText().toString();
                values = new ContentValues();
                values.put("memo", s); //여기서 읽은 내용을 memo 필드에 넣어라
                long id = db.insert("memos", null, values);
                ds.add(new Memo(id, s));

                NetworkTask networkTask = new NetworkTask(basic, null, s);
                networkTask.execute();

                break;
            case R.id.del:
                Memo memo = aa.getItem(0);
                db.delete("memos", "_id=" + memo.getId(), null);
                ds.remove(0);
        }
        aa.notifyDataSetChanged();
        setEnabled(aa.getCount() > 0);
        edit.setText("");
    }

    void getAllMemos() {
        db = helper.getReadableDatabase();
        Cursor c = db.query("memos", new String[]{"_id", "memo"},
                null, null, null, null, null);
        while (c.moveToNext()) {
            Memo memo = new Memo(c.getLong(0), c.getString(1));
            ds.add(memo);
        }
        c.close(); //커서 안의 내용이 지워지는 경우도 있어서 주의
        helper.close();
    }

    void setEnabled(boolean enabled) {
        findViewById(R.id.del).setEnabled(enabled);
    }

    void settingList() {
        ls.add("나물비빔밥");
        ls.add("오곡밥");
        ls.add("잡채밥");
        ls.add("콩나물밥");
        ls.add("약식");
        ls.add("호박죽");
        ls.add("흑임자죽");
        ls.add("카레라이스");
        ls.add("오므라이스");
        ls.add("감자수제비");
        ls.add("냉면");
        ls.add("동치미막국수");
        ls.add("열무김치냉면");
        ls.add("채소국수");
        ls.add("해물국수");
        ls.add("만둣국");
        ls.add("다시마냉국");
        ls.add("두부국");
        ls.add("두부조개탕");
        ls.add("무맑은국");
        ls.add("미역국");
        ls.add("미역냉국");
        ls.add("생태국");
        ls.add("연어까르파치오");
        ls.add("오이냉국");
        ls.add("해산물샐러드");
        ls.add("재첩국");
        ls.add("구운감자와도미구이");
        ls.add("쇠고기산적");
        ls.add("쇠고기양송이볶음");
        ls.add("팥국수");
        ls.add("죽순표고버섯볶음나물");
        ls.add("부추표고버섯볶음");
        ls.add("두부드레싱과 채소샐러드");
        ls.add("콩나물무침");
        ls.add("우엉조림");
        ls.add("바질토마토두부샐러드");
        ls.add("멸치볶음");
        ls.add("갈치무조림");
        ls.add("닭불고기");
        ls.add("두부다시마말이");
        ls.add("팥칼국수");
        ls.add("국수계란말이");
        ls.add("유부계란찜");
        ls.add("쇠고기무국");
        ls.add("두부스테이크");
        ls.add("라볶이");
        ls.add("모듬채소볶음");
        ls.add("오뎅국");
    }

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
        edit = findViewById(R.id.edit);

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
            Intent intent = new Intent(search.this, recipe.class);
            intent.putExtra("ig", toRecipe.toString());
            intent.putExtra("sb", "");
            startActivity(intent);
        } else {
            Toast.makeText(search.this, "해당 요리를 조회할 수 없습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}