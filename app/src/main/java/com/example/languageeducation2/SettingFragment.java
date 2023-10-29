package com.example.languageeducation2;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingFragment extends Fragment {

    private Button buttonStartTestWithVariants, buttonStartTestWithInput;
    private FrameLayout frameLayout;

    private  RadioButton english,german;
    private RadioGroup languageGroup;

    private SeekBar seekBarCountQuestions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_setting, container, false);

        buttonStartTestWithInput = (Button) myView.findViewById(R.id.test_with_input);
        buttonStartTestWithVariants = (Button) myView.findViewById(R.id.test_with_variants);

        languageGroup = (RadioGroup) myView.findViewById(R.id.language_group);

        english = (RadioButton) myView.findViewById(R.id.english_radio_button);
        german = (RadioButton) myView.findViewById(R.id.german_radio_button);
        english.setChecked(true);

        RadioButton time20 = (RadioButton) myView.findViewById(R.id.time20);
        RadioButton time30 = (RadioButton) myView.findViewById(R.id.time30);
        RadioButton time60 = (RadioButton) myView.findViewById(R.id.time60);
        time20.setChecked(true);

        seekBarCountQuestions = myView.findViewById(R.id.count_questions_seekbar);
        TextView countQuestions = myView.findViewById(R.id.count_questions_text);
        customSeekBar(countQuestions,seekBarCountQuestions);



        buttonStartTestWithVariants.setOnClickListener(new View.OnClickListener() {
            Integer time;
            WordsMapProcessor  wordsMapProcessor;
            @Override
            public void onClick(View view) {

               int id = languageGroup.getCheckedRadioButtonId();
                if(id == R.id.english_radio_button){
                   wordsMapProcessor =  makeCorrectWordsMapProcessor(Language.ENGLISH);
                }else{
                    wordsMapProcessor =  makeCorrectWordsMapProcessor(Language.GERMANY);
                }

                if(time20.isChecked()) time = 20;
                else if (time30.isChecked()) time = 30;
                else time = 60;

                String[] word = wordsMapProcessor.getRandomEntry(new String[]{});

                TestWithVariantFragment testFragment = TestWithVariantFragment.newInstance(
                        seekBarCountQuestions.getProgress(),
                        time
                );

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, testFragment);
                ft.commit();
            }
        });
        buttonStartTestWithInput.setOnClickListener(new View.OnClickListener() {
            Integer time;
            WordsMapProcessor wordsMapProcessor;

            @Override
            public void onClick(View view) {

                int id = languageGroup.getCheckedRadioButtonId();

                if(id == R.id.english_radio_button){
                    wordsMapProcessor = makeCorrectWordsMapProcessor(Language.ENGLISH);
                }else{
                   wordsMapProcessor =  makeCorrectWordsMapProcessor(Language.GERMANY);
                }

                if(time20.isChecked()) time = 20;
                else if (time30.isChecked()) time = 30;
                else time = 30;

                String[] word = wordsMapProcessor.getRandomEntry(new String[]{});
                TestWithInputFragment testFragment = TestWithInputFragment.newInstance(
                        seekBarCountQuestions.getProgress(),
                        time
                );
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, testFragment);
                ft.commit();
            }
        });

        return myView;
    }

    WordsMapProcessor makeCorrectWordsMapProcessor(Language choice) {

        MainActivity activity = (MainActivity) getActivity();
        String[] word, translate;

        if (choice == Language.ENGLISH) {
            word = getResources().getStringArray(R.array.english_word);
            translate = getResources().getStringArray(R.array.english_translate);
        } else {
            word = getResources().getStringArray(R.array.german_word);
            translate = getResources().getStringArray(R.array.german_translate);
        }

        Map<String, String> wordsAndTranslateMap = new HashMap<>();

        for (int i = 0; i < word.length && i < translate.length; i++) {
            wordsAndTranslateMap.put(word[i], translate[i]);
        }

        activity.setWordsMapProcessor(new WordsMapProcessor(wordsAndTranslateMap));
        WordsMapProcessor.wordsMap = wordsAndTranslateMap;
        return ((MainActivity) getActivity()).getWordsMapProcessor();
    }

    void customSeekBar(TextView textView, SeekBar seekBar){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                textView.setText("Кількість запитаннь : " + String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}