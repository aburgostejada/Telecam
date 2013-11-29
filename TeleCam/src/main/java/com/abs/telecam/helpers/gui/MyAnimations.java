package com.abs.telecam.helpers.gui;
import android.app.Activity;
import android.view.View;
import android.view.animation.AnimationUtils;


public class MyAnimations {
    public static void animateFor(Activity a,  int animation, View item){
       item.startAnimation(AnimationUtils.loadAnimation(a, animation));
   }
}
