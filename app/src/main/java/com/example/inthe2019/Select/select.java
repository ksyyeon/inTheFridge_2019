package com.example.inthe2019.Select;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.inthe2019.R;
import com.example.inthe2019.Search.RequestHttpConnection;
import com.example.inthe2019.Recipe.recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class select extends AppCompatActivity {
    ArrayList<Integer> a;
    String camera;
    String myString = "정확도순";
    ListView listView;
    ListAdapter adapter;
    Spinner s;
    ArrayList<String> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_select);

        Intent intent = getIntent();
        a = (ArrayList<Integer>)intent.getSerializableExtra("rc");
        camera = intent.getStringExtra("sb");

        // URL 설정.
        String basic = "http://fb07f75a.ngrok.io/inthe2019/inthe_basic.php";
        //10.0.2.2

        NetworkTask networkTask = new NetworkTask(basic, null, a);
        networkTask.execute();
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        ArrayList<Integer> recipecode;

        public NetworkTask(String url, ContentValues values, ArrayList<Integer> recipecode) {
            this.url = url;
            this.values = values;
            this.recipecode = recipecode;
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
            jsonParsing(s, recipecode);
            jsonParsing2(s);
        }

    }

    public void jsonParsing(String jsonString, ArrayList<Integer> recipecodes) {
        ArrayList<Food> fa = new ArrayList<>();
        Food food; Integer recipecode;
        String name; String countrycode;
        String categorycode; String time;
        int kcal; String amount;
        String img;

        arrayList.add("정확도순"); arrayList.add("가나다순");
        arrayList.add("조리시간순"); arrayList.add("열량순");
        arrayList.add("국가순");

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray basicArray = jsonObject.getJSONArray("basic");

            for (int i = 0; i < recipecodes.size(); i++) {
                for (int j = 0; j < basicArray.length(); j++) {
                    JSONObject basicObject = basicArray.getJSONObject(j);
                    recipecode = basicObject.optInt("recipecode");
                    name = basicObject.optString("name");
                    countrycode = basicObject.optString("countrycode");
                    categorycode = basicObject.optString("categorycode");
                    time = basicObject.optString("time");
                    kcal = basicObject.optInt("kcal");
                    amount = basicObject.optString("amount");
                    img = basicObject.optString("image");

                    if (recipecode == recipecodes.get(i)%1000){
                        food = new Food(recipecode, name, countrycode,
                                categorycode, time, kcal, amount, img , recipecodes.get(i)/1000);
                        fa.add(food);
                    }
                }
            }

            adapter = new ListAdapter(this, R.layout.food, fa);
            listView = (ListView) findViewById(R.id.select_list);
            listView.setAdapter(adapter);

            ArrayAdapter spinnerAdapter = new ArrayAdapter(this,
                    R.layout.support_simple_spinner_dropdown_item, arrayList);

            s = (Spinner)findViewById(R.id.spinner);
            s.setAdapter(spinnerAdapter);

            int spinnerPosition = spinnerAdapter.getPosition(myString);
            s.setSelection(spinnerPosition);

            s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0: //정확도순
                            Comparator<Food> weightAsc = new Comparator<Food>() {
                                @Override
                                public int compare(Food f1, Food f2) {
                                    if(f1.getWeight() < f2.getWeight()) return 1;
                                    else if (f1.getWeight() > f2.getWeight()) return -1;
                                    else return 0;
                                }
                            };
                            Collections.sort(fa, weightAsc);
                            break;
                        case 1: //가나다순
                            Comparator<Food> nameAsc = new Comparator<Food>() {
                                @Override
                                public int compare(Food f1, Food f2) {
                                    return f1.getName().compareTo(f2.getName());
                                }
                            };
                            Collections.sort(fa, nameAsc);
                            break;
                        case 2: //조리시간순
                            Comparator<Food> timeAsc = new Comparator<Food>() {
                                @Override
                                public int compare(Food f1, Food f2) {
                                    return f1.getTime().compareTo(f2.getTime());
                                }
                            };
                            Collections.sort(fa, timeAsc);
                            break;
                        case 3: //열량순
                            Comparator<Food> kcalAsc = new Comparator<Food>() {
                                @Override
                                public int compare(Food f1, Food f2) {
                                    if(f1.getKcal() > f2.getKcal()) return 1;
                                    else if (f1.getKcal() < f2.getKcal()) return -1;
                                    else return 0;
                                }
                            };
                            Collections.sort(fa, kcalAsc);
                            break;
                        case 4: //국가순
                            Comparator<Food> countryAsc = new Comparator<Food>() {
                                @Override
                                public int compare(Food f1, Food f2) {
                                    return f1.getCountrycode().compareTo(f2.getCountrycode());
                                }
                            };
                            Collections.sort(fa, countryAsc);
                            break;
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void jsonParsing2(String jsonString) {
        listView = findViewById(R.id.select_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                Food item = (Food) listView.getItemAtPosition(position);

                StringBuffer toRecipe = new StringBuffer();
                String total1; String total2;
                String image; String recipecode;
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
                        if (name.equals(item.getName())) {
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

                Intent intent = new Intent(select.this, recipe.class);
                intent.putExtra("ig", toRecipe.toString());
                intent.putExtra("sb", camera);
                startActivity(intent);
            }
        });
    }
}