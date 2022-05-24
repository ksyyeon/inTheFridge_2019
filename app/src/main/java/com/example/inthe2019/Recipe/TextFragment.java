package com.example.inthe2019.Recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inthe2019.R;

public class TextFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_recipe, container, false);

        TextView process = view.findViewById(R.id.process);

        if (getArguments() != null) {
            Bundle args = getArguments();
            process.setText(args.getString("textRes"));
        }
        return view;
    }
}