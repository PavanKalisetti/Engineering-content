package com.engineeringcontent.org;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class TypeWriter extends AppCompatTextView {

    private static final int TYPE_MODE = 1;
    private static final int DELETE_MODE = 2;

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 150;
    private int mMode = TYPE_MODE;

    private final Handler mHandler = new Handler();
    private final Runnable mCharacterAdder = new Runnable() {
        @Override
        public void run() {
            if (mMode == TYPE_MODE) {
                setText(mText.subSequence(0, mIndex++));
                if (mIndex <= mText.length()) {
                    mHandler.postDelayed(mCharacterAdder, mDelay);
                } else {
                    mMode = DELETE_MODE;
                    mHandler.postDelayed(mCharacterAdder, mDelay * 2);
                }
            } else if (mMode == DELETE_MODE) {
                if (mIndex > 0) {
                    setText(mText.subSequence(0, --mIndex));
                }
                if (mIndex == 0) {
                    mMode = TYPE_MODE;
                    mHandler.postDelayed(mCharacterAdder, mDelay);
                } else {
                    mHandler.postDelayed(mCharacterAdder, mDelay / 2);
                }
            }
        }
    };


    public TypeWriter(Context context) {
        super(context);
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // this works for one time typing and deleting
//    public void animateText(CharSequence text) {
//        mText = text;
//        mIndex = 0;
//        mMode = TYPE_MODE;
//        setText("");
//        mHandler.removeCallbacks(mCharacterAdder);
//        mHandler.postDelayed(mCharacterAdder, mDelay);
//    }


    // this works for typing and deleting untill we call deletetext() function
    public void animatedText(CharSequence text) {
        mText = text;
        mIndex = 0;
        setText("");
        mMode = TYPE_MODE;
        mHandler.removeCallbacks(mCharacterAdder);
        mHandler.postDelayed(mCharacterAdder, mDelay);

    }


    public void deleteText() {
        mHandler.removeCallbacks(mCharacterAdder);
        mIndex = 0;
        setText("");
    }


    public void setCharacterDelay(long delay) {
        mDelay = delay;
    }

}
