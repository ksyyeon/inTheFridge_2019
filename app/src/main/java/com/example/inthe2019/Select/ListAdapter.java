package com.example.inthe2019.Select;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inthe2019.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Food> arrayList;
    Bitmap bitmap;
    Drawable drawable;

    public ListAdapter(Context context, int layout, ArrayList<Food> arrayList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        Food food =
                new Food(arrayList.get(position).getRecipecode(),
                        arrayList.get(position).getName(),
                        arrayList.get(position).getCountrycode(),
                        arrayList.get(position).getCategorycode(),
                        arrayList.get(position).getTime(),
                        arrayList.get(position).getKcal(),
                        arrayList.get(position).getAmount(),
                        arrayList.get(position).getImg(),
                        arrayList.get(position).getWeight());
        return food;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final int pos = position;

        if(view == null)
            view = View.inflate(context, layout, null);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(arrayList.get(pos).getImg());

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

            drawable = new BitmapDrawable(context.getResources(), bitmap);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ImageView image = view.findViewById( R.id.img);
        image.setImageBitmap(bitmap);

        TextView name = view.findViewById(R.id.name);
        name.setText(arrayList.get(pos).getName());

        TextView country = view.findViewById(R.id.country);
        String c ="";
        switch (arrayList.get(pos).getCountrycode()) {
            case "3020001":
                c = "한국";
                break;
            case "3020002":
                c = "서양";
                break;
            case "3020003":
                c = "일본";
                break;
            case "3020004":
                c = "중국";
                break;
            case "3020005":
                c = "동남아시아";
                break;
            case "3020006":
                c = "이탈리아";
                break;
            case "3020009":
                c = "퓨전";
                break;
        }
        country.setText("국가: "+ c);

        TextView category = view.findViewById(R.id.category);
        category.setText("정확도: "+ arrayList.get(pos).getWeight());

        TextView time = view.findViewById(R.id.time);
        time.setText("조리시간: " + arrayList.get(pos).getTime());

        TextView kcal = view.findViewById(R.id.kcal);
        kcal.setText("열량: " + arrayList.get(pos).getKcal() +"kcal");

        TextView amount = view.findViewById(R.id.amount);
        amount.setText("분량: "+ arrayList.get(pos).getAmount());

        return view;
    }
}
