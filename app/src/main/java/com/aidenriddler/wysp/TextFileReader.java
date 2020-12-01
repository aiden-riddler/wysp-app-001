package com.aidenriddler.wysp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TextFileReader {
    private String line;
    private ArrayList<String> wordList;

    public ArrayList<String> ReadFile(Context ctx, int resID){
        wordList = new ArrayList();
        InputStream inputStream = ctx.getResources().openRawResource(resID);

        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputReader);

        try {
            while ((line = bufferedReader.readLine()) != null){
                wordList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

       return wordList;
    }
}
