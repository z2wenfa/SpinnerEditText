package com.z2wenfa.spinneredittext;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * TextWatcher的实现抽象类
 * Created by z2wenfa on 2016/11/10.
 */
public abstract class TextWatchAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void afterTextChanged(Editable s) {

    }
}
