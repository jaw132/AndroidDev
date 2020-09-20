package com.example.passwordgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public TextView textView;
    public int MAXLENGTH = 20;
    public int MINLENGTH = 10;
    EditText removeTxt;
    EditText lengthView;
    char[] characters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.password_box);
        System.out.println("Hello");

    }

    @SuppressLint("SetTextI18n")
    public void generatePassword(View v){
        characters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1',
                '2', '3', '4', '5', '6', '7', '8', '9', '0', '!', '"', '£', '$', '%',
                '^', '&', '*', '(', ')', '+', '_', '-', '=', '?', '.', ',', ':', ';',
                '@', '#', '~', '[', ']', '/'};


        SecureRandom random = new SecureRandom();

        // shuffle the characters
        for (int i = 0; i < characters.length; i++){
            int random_seed = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[random_seed];
            characters[random_seed] = temp;
        }


        removeTxt = findViewById(R.id.unwantedChars);
        lengthView = findViewById(R.id.length_box);

        String removeChars = removeTxt.getText().toString();
        String lengthStr = lengthView.getText().toString();

        //process the input
        String charString = "";
        for (char character : characters) {
            boolean rem = false;
            for (int i = 0; i < removeChars.length(); i++) {
                char c = removeChars.charAt(i);
                if (c == character) {
                    rem = true;
                }
            }

            if (!rem) {
                charString += character;
            }

        }

        characters = charString.toCharArray();


        //check for valid input in length box
        if (lengthStr.length() > 2){
            lengthView.setText("Max length 20");
            return;
        }
        else if(lengthStr.length() > 0){
            for (int i = 0; i < lengthStr.length(); i++){
                int asciiVal = (int) lengthStr.charAt(i);
                if (asciiVal < 48 || asciiVal > 57){
                    lengthView.setText("Please enter number between 1 and 20");
                    return;
                }
            }
            if (Integer.parseInt(lengthStr) > 20){
                lengthView.setText("Max length 20");
                return;
            }
        }



        System.out.println(removeChars);
        System.out.println(lengthStr);

        //if we have length input from user then use that
        int pass_length;
        if (lengthStr.length() > 0){
            pass_length = Integer.parseInt(lengthStr);
            if (pass_length > MAXLENGTH){
                return;
            }
        }
        //generate random number for length between 10-20 chars
        else{
            pass_length = random.nextInt(MAXLENGTH-MINLENGTH+1)+MINLENGTH;
        }

        //for range(length) generate random numbers to index character list
        //and append to string
        String password = "";
        int char_length = characters.length;
        System.out.println(char_length);
        for (int i = 0; i < pass_length; i++){
            int random_index = random.nextInt(char_length);
            char passChar = characters[random_index];
            if ((int) passChar >= 97 && (int) passChar <= 122){
                int capital_rand = random.nextInt(2);
                if (capital_rand == 1){
                    passChar = Character.toUpperCase(passChar);
                }
            }
            password += passChar;
        }

        //Send password string back to user
        textView.setText(password);

    }
}