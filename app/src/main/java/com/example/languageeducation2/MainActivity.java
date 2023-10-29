package com.example.languageeducation2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    private static WordsMapProcessor wordsMapProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            SettingFragment settingFragment = new SettingFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, settingFragment);
            ft.commit();
        }

    }

    public WordsMapProcessor getWordsMapProcessor() {
        return wordsMapProcessor;
    }

    public void setWordsMapProcessor(WordsMapProcessor wordsMapProcessor) {
        this.wordsMapProcessor = wordsMapProcessor;
    }
}