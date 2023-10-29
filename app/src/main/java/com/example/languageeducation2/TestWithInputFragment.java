package com.example.languageeducation2;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestWithInputFragment extends Fragment {

    TextView wordTextView,displayTime;
    EditText editTextEditText;
    Button buttonCheckAnswer,buttonGoToNext;
    Integer numCorrectAnswers,numberOfQuestionsLeft;
    CountDownTimer countDownTimer;
    WordsMapProcessor wordsMapProcessor = new WordsMapProcessor(WordsMapProcessor.wordsMap);
    String word,translate;
    Long timeRemainingMillis;
    static ArrayList<String> usedWords;

    public static TestWithInputFragment newInstance(Integer numberOfQuestionLeft,Integer time) {
        TestWithInputFragment fragment = new TestWithInputFragment();
        Bundle args = new Bundle();
        args.putInt("number_of_question_left",numberOfQuestionLeft);
        args.putInt("time", time);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_with_input, container, false);
        buttonCheckAnswer =view.findViewById(R.id.check_word_input);
        editTextEditText = view.findViewById(R.id.input_variant_word);
        buttonGoToNext =view.findViewById(R.id.go_to_next);
        displayTime = view.findViewById(R.id.time_display_input);
        wordTextView = view.findViewById(R.id.test_word_input);

        if(savedInstanceState != null){
            numCorrectAnswers = savedInstanceState.getInt("numCorrectAnswers");
            numberOfQuestionsLeft = savedInstanceState.getInt("numberOfQuestionsLeft");
            buttonGoToNext.setText(savedInstanceState.getString("textOnButtonGoToNext"));
            word = savedInstanceState.getString("word");
            translate = savedInstanceState.getString("translate");

            String textCheckButton = savedInstanceState.getString("textCheckButton");

            if(textCheckButton.equals(getResources().getString(R.string.сorrect_ua))){
                buttonCheckAnswer.setBackgroundColor(Color.GREEN);
                buttonCheckAnswer.setText(getResources().getString(R.string.сorrect_ua));
                buttonGoToNext.setVisibility(View.VISIBLE);
            }else if(textCheckButton.equals(getResources().getString(R.string.wrong_ua))){
                buttonCheckAnswer.setBackgroundColor(Color.RED);
                buttonCheckAnswer.setText(getResources().getString(R.string.wrong_ua));
                buttonGoToNext.setVisibility(View.VISIBLE);
            }
            if(!savedInstanceState.getBoolean("buttonEnabled")){
                buttonCheckAnswer.setEnabled(false);
            }

            Integer remainTime = (int) savedInstanceState.getLong("timeRemainingMillis");
            if (remainTime > 0) {
                startTimer(remainTime);
            } else {
                displayTime.setText(savedInstanceState.getString("textTimer"));
            }
        }else{
            String[] wordAndTranslate = wordsMapProcessor.getRandomEntry(new String[]{});
            word = wordAndTranslate[0];
            translate = wordAndTranslate[1];

            numberOfQuestionsLeft = getArguments().getInt("number_of_question_left");
            numCorrectAnswers = 0;
            usedWords = new ArrayList<>();
            usedWords.add(word);
            startTimer(getArguments().getInt("time")*1000);
        }
        wordTextView.setText(word);

        buttonCheckAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                if(checkAnswer()){
                    buttonCheckAnswer.setBackgroundColor(Color.GREEN);
                    buttonCheckAnswer.setText(getResources().getString(R.string.сorrect_ua));
                    numCorrectAnswers++;
                }else{
                    buttonCheckAnswer.setBackgroundColor(Color.RED);
                    buttonCheckAnswer.setText(getResources().getString(R.string.wrong_ua));
                }
                buttonCheckAnswer.setEnabled(false);
                if(numberOfQuestionsLeft == 1){
                    buttonGoToNext.setText(getResources().getString(R.string.finish_ua));
                }
                buttonGoToNext.setVisibility(View.VISIBLE);
                timeRemainingMillis = 0L;
            }
        }
        );

        buttonGoToNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(numberOfQuestionsLeft == 1){
                    Integer numWrongAnswers = usedWords.size() - numCorrectAnswers;
                    ResultFragment resultFragment = ResultFragment.newInstance(
                            numCorrectAnswers, numWrongAnswers);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_layout, resultFragment);
                    ft.commit();
                }
                else{
                    editTextEditText.getText().clear();
                    String[] wordAndTranslate = wordsMapProcessor.getRandomEntry(usedWords.toArray(new String[0]));
                    word = wordAndTranslate[0];
                    translate = wordAndTranslate[1];
                    usedWords.add(word);

                    buttonCheckAnswer.setEnabled(true);
                    buttonCheckAnswer.setText(getResources().getString(R.string.check_ua));
                    buttonCheckAnswer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.main_color));
                    buttonGoToNext.setVisibility(View.INVISIBLE);

                    numberOfQuestionsLeft = numberOfQuestionsLeft -1;
                    wordTextView.setText(word);
                    buttonGoToNext.setText(getResources().getString(R.string.next_ua));
                    startTimer(getArguments().getInt("time")*1000);
                }
            }
        });

        return view;
    }

    private Boolean checkAnswer(){
        String input = editTextEditText.getText().toString().toLowerCase();
        return translate.equals(input);
    }

    void startTimer(int seconds){
        countDownTimer =
                new CountDownTimer(seconds, 1000) {

                    public void onTick(long millisUntilFinished) {
                        timeRemainingMillis = millisUntilFinished;
                        displayTime.setText("Часу залишилось: " + millisUntilFinished / 1000 + " секунд");
                    }

                    public void onFinish() {
                        timeRemainingMillis = 0L;
                        buttonCheckAnswer.setEnabled(false);
                        if(numberOfQuestionsLeft == 1){
                            buttonGoToNext.setText(getResources().getString(R.string.finish_ua));
                        }
                        buttonGoToNext.setVisibility(View.VISIBLE);
                        displayTime.setText(getResources().getString(R.string.time_is_end_ua));
                    }
                }.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("word",word);
        outState.putString("translate", translate);
        outState.putInt("numCorrectAnswers", numCorrectAnswers);
        outState.putInt("numberOfQuestionsLeft", numberOfQuestionsLeft);

        if(timeRemainingMillis != null && timeRemainingMillis != 0){
            countDownTimer.cancel();
            outState.putLong("timeRemainingMillis", timeRemainingMillis);
        }
        else{
            outState.putLong("timeRemainingMillis", 0);}

        outState.putString("textOnButtonGoToNext",buttonGoToNext.getText().toString());
        outState.putBoolean("buttonEnabled",buttonCheckAnswer.isEnabled());
        outState.putString("textCheckButton", buttonCheckAnswer.getText().toString());
        outState.putString("translate", translate);
        outState.putString("textTimer",displayTime.getText().toString());
    }
}