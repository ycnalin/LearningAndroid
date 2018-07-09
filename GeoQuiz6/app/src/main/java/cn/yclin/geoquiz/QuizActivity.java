package cn.yclin.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mCheatButton;
    private TextView mCheatRemainTimesTextView;
    private TextView mQuestionTextView;
    public static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ARRAY = "array";
    private static final String KEY_CHEAT = "cheat";
    private static final String KEY_CHEATTIMES = "cheat_times";
    private static final int REQUEST_CODE_CHEAT = 0;
    private boolean mIsCheater;
    private int mCheatTimes = 0;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia,true),
            new Question(R.string.question_oceans,true),
            new Question(R.string.question_mideast,false),
            new Question(R.string.question_africa,false),
            new Question(R.string.question_americas,true),
            new Question(R.string.question_asia,true)
    };

    private int[] mAnswerSheet = null;
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnswerSheet = savedInstanceState.getIntArray(KEY_ARRAY);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEAT,false);
            mCheatTimes = savedInstanceState.getInt(KEY_CHEATTIMES);
        }

        mQuestionTextView = (TextView) findViewById(R.id.textView);

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mTrueButton.isEnabled()) {
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    mIsCheater = false;
                    updateQuestion();
                }
            }
        });

        mTrueButton = (Button)findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkAnswer(true);
               buttonEnable(false);
            }
        });

        mFalseButton = (Button)findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
                buttonEnable(false);
            }
        });

        mNextButton = (Button)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mTrueButton.isEnabled()) {
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    mIsCheater = false;
                    updateQuestion();
                }
            }
        });

        mPrevButton = (Button)findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentIndex > 0) {
                    mCurrentIndex = (mCurrentIndex + 5) % mQuestionBank.length;
                    updateQuestion();
                }
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start cheatActivity
                boolean answer_is_true = mQuestionBank[mCurrentIndex].ismAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this,answer_is_true);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        if(mCheatTimes == 3) mCheatButton.setEnabled(false);

        mCheatRemainTimesTextView = findViewById(R.id.cheat_times_textView);
        mCheatRemainTimesTextView.setText("cheat x" + (3-mCheatTimes));

        if(mAnswerSheet == null) {
            mAnswerSheet = new int[mQuestionBank.length];
            for (int i = 0; i < mAnswerSheet.length; ++i)
                mAnswerSheet[i] = -1;
        }
        updateQuestion();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putIntArray(KEY_ARRAY,mAnswerSheet);
        savedInstanceState.putBoolean(KEY_CHEAT,mIsCheater);
        savedInstanceState.putInt(KEY_CHEATTIMES,mCheatTimes);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart() called");
        if(mCheatTimes == 3)
            mCheatButton.setEnabled(false);
        mCheatRemainTimesTextView.setText("cheat x" + (3-mCheatTimes));
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"onPause() called");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            if(mIsCheater) ++mCheatTimes;
        }
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getmTextResId();
        mQuestionTextView.setText(question);

        if(mAnswerSheet[mCurrentIndex] != -1)
            buttonEnable(false);
        else buttonEnable(true);
        //buttonEnable(true);
        //buttonClickable(true);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].ismAnswerTrue();
        int messageResId = 0;

        if(mIsCheater){
            messageResId = R.string.judgment_toast;
            mAnswerSheet[mCurrentIndex] = (userPressedTrue == answerIsTrue)?1:0;
        }
        else{
            if(userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mAnswerSheet[mCurrentIndex] = 1;
            }
            else {
                messageResId = R.string.incorrect_toast;
                mAnswerSheet[mCurrentIndex] = 0;
            }
        }


        final Toast toast = Toast.makeText(QuizActivity.this,messageResId,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,300);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1000);

        //answered all question
        if(mCurrentIndex == mQuestionBank.length - 1){
            double score = 0;
            for(int ans:mAnswerSheet){
                score += ans;
            }
            score = score/mQuestionBank.length * 100;
            final Toast toast1 = Toast.makeText(QuizActivity.this,String.format("%.1f",score)+"%",Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.TOP,0,300);
            toast1.show();
        }

    }

    private void buttonClickable(boolean state){
        //mTrueButton.setEnabled(state);
        mTrueButton.setClickable(state);
        //mFalseButton.setEnabled(state);
        mFalseButton.setClickable(state);
    }

    private void buttonEnable(boolean state){
        mTrueButton.setEnabled(state);
        mFalseButton.setEnabled(state);
    }



}
