package com.rednow.poetry.ui.animation1.milaucher.utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.rednow.poetry.R;


public class Animations{
	Animation DelDown,DelUp;
	public Animation getDownAnimation(Context context){
		return AnimationUtils.loadAnimation(context, R.anim.mi_laucher_del_down);
	}
}