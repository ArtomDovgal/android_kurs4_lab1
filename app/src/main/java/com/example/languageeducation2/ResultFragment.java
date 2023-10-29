package com.example.languageeducation2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ResultFragment extends Fragment {


    TextView numWrongAnswers, numCorrectAnswers;
    Button toMainFrame;

    public static ResultFragment newInstance(Integer numCorrectAnswers, Integer numWrongAnswers) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putInt("num_correct_answers", numCorrectAnswers);
        args.putInt("num_wrong_answers",numWrongAnswers);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        numWrongAnswers = view.findViewById(R.id.number_wrong_answers);
        numCorrectAnswers = view.findViewById(R.id.number_correct_answers);
        toMainFrame = view.findViewById(R.id.to_main_page);
        numCorrectAnswers.setText("BB");
        Integer correct = getArguments().getInt("num_correct_answers");
        numCorrectAnswers.setText(correct.toString());
        Integer wrong = getArguments().getInt("num_wrong_answers");
        numWrongAnswers.setText(wrong.toString());


        toMainFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingFragment settingFragment = new SettingFragment();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, settingFragment);
                ft.commit();
            }
        });
        return view;
    }
}