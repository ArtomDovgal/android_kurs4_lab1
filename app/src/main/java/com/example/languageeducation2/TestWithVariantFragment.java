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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TestWithVariantFragment extends Fragment {

    TextView wordTextView,displayTime;
    Button firstButton, secondButton, thirdButton,fourthButton, buttonGoToNext;
    Integer numCorrectAnswers,numberOfQuestionsLeft;
    WordsMapProcessor wordsMapProcessor = new WordsMapProcessor(WordsMapProcessor.wordsMap);
    CountDownTimer countDownTimer;
    String word,translate;
    Long timeRemainingMillis;

    static int[] correctAndWrongButtonIds = new int[2];

    static ArrayList<String> usedWords;

    ArrayList<String> textButtons;

    public static TestWithVariantFragment newInstance( Integer numberOfQuestionLeft, Integer time) {
        TestWithVariantFragment fragment = new TestWithVariantFragment();
        Bundle args = new Bundle();
        args.putInt("number_of_question_left",numberOfQuestionLeft);
        args.putInt("time", time);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_with_variants, container, false);
        firstButton = view.findViewById(R.id.button_variant1);
        secondButton = view.findViewById(R.id.button_variant2);
        thirdButton = view.findViewById(R.id.button_variant3);
        fourthButton = view.findViewById(R.id.button_variant4);
        wordTextView = view.findViewById(R.id.test_word_variant);
        buttonGoToNext = view.findViewById(R.id.go_to_next_fragment_variants);
        displayTime = view.findViewById(R.id.time_display_variant);

        if(savedInstanceState != null) {
            numCorrectAnswers = savedInstanceState.getInt("numCorrectAnswers");
            numberOfQuestionsLeft = savedInstanceState.getInt("numberOfQuestionsLeft");
            buttonGoToNext.setText(savedInstanceState.getString("textOnButtonGoToNext"));
            textButtons = savedInstanceState.getStringArrayList("textButtons");
            firstButton.setText(textButtons.get(0));
            secondButton.setText(textButtons.get(1));
            thirdButton.setText(textButtons.get(2));
            fourthButton.setText(textButtons.get(3));

            int[] arrCorrectAndWrong = savedInstanceState.getIntArray("correctAndWrongButtonsId");

            if (savedInstanceState.getBoolean("buttonsEnabled") == false) {
                buttonGoToNext.setVisibility(View.VISIBLE);
                setDefaultButtonsColor();
                if (arrCorrectAndWrong[0] != 0)
                    view.findViewById(arrCorrectAndWrong[0]).setBackgroundColor(Color.GREEN);
                if (arrCorrectAndWrong[1] != 0)
                    view.findViewById(arrCorrectAndWrong[1]).setBackgroundColor(Color.RED);

                firstButton.setEnabled(false);
                secondButton.setEnabled(false);
                thirdButton.setEnabled(false);
                fourthButton.setEnabled(false);
            }
            word = savedInstanceState.getString("word");
            translate = savedInstanceState.getString("translate");
            Integer remainTime = (int) savedInstanceState.getLong("timeRemainingMillis");
            if (remainTime > 0) {
                startTimer(remainTime);
            } else {
                displayTime.setText(savedInstanceState.getString("textTimer"));
            }
        }else{
            numCorrectAnswers = 0;
            String[] wordAndTranslate = wordsMapProcessor.getRandomEntry(new String[]{});
            word = wordAndTranslate[0];
            translate = wordAndTranslate[1];
            numberOfQuestionsLeft = getArguments().getInt("number_of_question_left");
            usedWords = new ArrayList<String>();
            usedWords.add(word);
            setVariantButtons(wordsMapProcessor.getNonAssociatedElements(word), translate);
            startTimer(getArguments().getInt("time")*1000);
        }

        wordTextView.setText(word);

        firstButton.setOnClickListener(view1 -> onClicks(view1));
        secondButton.setOnClickListener(view1 -> onClicks(view1));
        thirdButton.setOnClickListener(view1 -> onClicks(view1));
        fourthButton.setOnClickListener(view1 -> onClicks(view1));

            buttonGoToNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (numberOfQuestionsLeft == 1) {

                        Integer numWrongAnswers = usedWords.size() - numCorrectAnswers;
                        ResultFragment resultFragment = ResultFragment.newInstance(
                                numCorrectAnswers, numWrongAnswers
                        );
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_layout, resultFragment);
                        ft.commit();

                    } else {
                        String[] wordAndTranslate = wordsMapProcessor.getRandomEntry(usedWords.toArray(new String[0]));
                        word = wordAndTranslate[0];
                        translate = wordAndTranslate[1];
                        usedWords.add(word);

                        setVariantButtons(wordsMapProcessor.getNonAssociatedElements(word),translate);
                        setButtonsEnabled();
                        setDefaultButtonsColor();
                        buttonGoToNext.setVisibility(View.INVISIBLE);
                        numberOfQuestionsLeft = numberOfQuestionsLeft -1;
                        wordTextView.setText(word);
                        startTimer(getArguments().getInt("time")*1000);

                    }
                }
            });
        return view;
    }

    public void onClicks(View view){
        countDownTimer.cancel();
        timeRemainingMillis = 0L;
        Button button = (Button) view;

        if(button.getText().equals(translate)){
            button.setBackgroundColor(Color.GREEN);
            correctAndWrongButtonIds[0] = button.getId();
            numCorrectAnswers++;
        }else{
            button.setBackgroundColor(Color.RED);
            correctAndWrongButtonIds[1] = button.getId();
            if(firstButton.getText().equals(translate)){
                correctAndWrongButtonIds[0] = firstButton.getId();
                firstButton.setBackgroundColor(Color.GREEN);}
            else if(secondButton.getText().equals(translate)){
                correctAndWrongButtonIds[0] = secondButton.getId();
                secondButton.setBackgroundColor(Color.GREEN);
            }
            else if(thirdButton.getText().equals(translate)){
                correctAndWrongButtonIds[0] = thirdButton.getId();
                thirdButton.setBackgroundColor(Color.GREEN);
            }
            else{
                correctAndWrongButtonIds[0] = firstButton.getId();
                fourthButton.setBackgroundColor(Color.GREEN);
            }
        }
        firstButton.setEnabled(false);
        secondButton.setEnabled(false);
        thirdButton.setEnabled(false);
        fourthButton.setEnabled(false);

        if(numberOfQuestionsLeft == 1){
            buttonGoToNext.setText(getResources().getString(R.string.finish_ua));
        }
        buttonGoToNext.setVisibility(View.VISIBLE);

    }

    void setVariantButtons(List<String> variants, String correctAnswer){
        variants.add(correctAnswer);
        Collections.shuffle(variants);
        firstButton.setText(variants.get(0));
        secondButton.setText(variants.get(1));
        thirdButton.setText(variants.get(2));
        fourthButton.setText(variants.get(3));

        textButtons = (ArrayList<String>) variants;

    }

    void setButtonsEnabled(){
        firstButton.setEnabled(true);
        secondButton.setEnabled(true);
        thirdButton.setEnabled(true);
        fourthButton.setEnabled(true);
    }
    void setDefaultButtonsColor(){
        int defaultColor = ContextCompat.getColor(getContext(), R.color.main_color);

        firstButton.setBackgroundColor(defaultColor);
        secondButton.setBackgroundColor(defaultColor);
        thirdButton.setBackgroundColor(defaultColor);
        fourthButton.setBackgroundColor(defaultColor);
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
                        firstButton.setEnabled(false);
                        secondButton.setEnabled(false);
                        thirdButton.setEnabled(false);
                        fourthButton.setEnabled(false);

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
        countDownTimer.cancel();
        outState.putInt("numCorrectAnswers", numCorrectAnswers);
        outState.putInt("numberOfQuestionsLeft", numberOfQuestionsLeft);

        if(timeRemainingMillis != null && timeRemainingMillis != 0)
            outState.putLong("timeRemainingMillis", timeRemainingMillis);
        else
            outState.putLong("timeRemainingMillis", 0);

        outState.putBoolean("isButtonGoToNextVisible", buttonGoToNext.isCursorVisible());
        outState.putString("textOnButtonGoToNext",buttonGoToNext.getText().toString());
        outState.putBoolean("buttonsEnabled",firstButton.isEnabled());
        outState.putString("word",word);
        outState.putString("translate", translate);
        outState.putIntArray("correctAndWrongButtonsId",correctAndWrongButtonIds);
        outState.putStringArrayList("textButtons", textButtons);
        outState.putString("textTimer",displayTime.getText().toString());
    }


}


