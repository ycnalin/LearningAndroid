package cn.yclin.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE =
            "cn.yclin.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "cn.yclin.geoquiz.answer_shown";
    private static final String KEY_ANSWER_STATE = "answer_state";
    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private TextView mApiTextView;
    private boolean mIsAnswerShown = false;
    private Button mShowAnswerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if(savedInstanceState != null){
            mIsAnswerShown = savedInstanceState.getBoolean(KEY_ANSWER_STATE,false);
        }

        mApiTextView = findViewById(R.id.api_textView);
        mApiTextView.setText("API Level " +Build.VERSION.SDK_INT);


        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAnswerTextView = findViewById(R.id.answer_text_view);
        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAnswerIsTrue)
                    mAnswerTextView.setText(R.string.true_button);
                else
                    mAnswerTextView.setText(R.string.false_button);
                mShowAnswerButton.setEnabled(false);
                setAnswerShownResult(true);
                mIsAnswerShown = true;
            }
        });
        // avoid reconfiguration data leak
        if(mIsAnswerShown){
            if(mAnswerIsTrue)
                mAnswerTextView.setText(R.string.true_button);
            else
                mAnswerTextView.setText(R.string.false_button);
            mShowAnswerButton.setEnabled(false);
            setAnswerShownResult(true);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_ANSWER_STATE, mIsAnswerShown);

    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

}
