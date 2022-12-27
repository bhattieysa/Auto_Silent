package com.example.myfyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView logoImg = (ImageView) findViewById(R.id.mainLogo);

        //HIDES ACTION BAR
        getSupportActionBar().hide();


        //ROTATION ANIMATION FOR THE IMAGEVIEW
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(500);
        animation.setDuration(2000);
        logoImg.setAnimation(animation);
        logoImg.startAnimation(animation);


        TextView swipeText = (TextView)findViewById(R.id.mainText);

        //SETTING FADE ANIMATION TO TEXTVIEW
        swipeText.startAnimation(AnimationUtils.loadAnimation(SplashScreen.this, R.anim.right_to_left_swipe));
        swipeText.animate().alpha(1f).setDuration(1000);
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setStartOffset(500);
        fadeIn.setDuration(2000);
        swipeText.setAnimation(fadeIn);


        // TRYING TO SLEEP THE MAIN THREAD TO RUN THE SPLASH FOR CERTAIN TIME IN MILLIS
        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(4000);
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };myThread.start();



    }
}