package edu.orangecoastcollege.cs273.cs273superheroes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Flag Quiz"; // Debugging

    private static final int SUPERHEROES_IN_QUIZ = 10;

    private Button[] mButtons = new Button[4];
    private List<Superhero> mAllSuperheroList;  // all the superheroes loaded from JSON
    private List<Superhero> mQuizSuperheroList; // superheroes in current quiz (just 10 of them)
    private List<Superhero> mSuperHeroTracker;
    private List<String> mFilteredSuperheroList;
    private Superhero mCorrectSuperhero; // correct superhero for the current question
    private String mCorrectAnswer;
    private int mTotalGuesses; // number of total guesses made
    private int mCorrectGuesses; // number of correct guesses
    private SecureRandom rng; // used to randomize the quiz
    private Handler handler; // used to delay loading next superhero

    private TextView mQuestionNumberTextView; // shows current question #
    private ImageView mFlagImageView; // displays a flag
    private TextView mAnswerTextView; // displays correct answer
    private TextView mGuessSuperheroTextView;

    private String mHeroAttribute; // Stores what superhero is selected

    /**
     * Loads the created quiz on the screen. Will show incorrect and correct button clicks.
     * Shows the countries flag. Offers a setting button which allows the users to filter between
     * different regions and how many buttons are displayed on the screen.
     * @param savedInstanceState Loads the saved instance from the fragment class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Let's register the OnSharedPreferenceListener
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);

        mQuizSuperheroList = new ArrayList<>(SUPERHEROES_IN_QUIZ);

        rng = new SecureRandom();
        handler = new Handler();

        mQuestionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        mFlagImageView = (ImageView) findViewById(R.id.flagImageView);
        mAnswerTextView = (TextView) findViewById(R.id.answerTextView);
        mGuessSuperheroTextView = (TextView) findViewById(R.id.guessSuperheroTextView);

        mButtons[0] = (Button) findViewById(R.id.button);
        mButtons[1] = (Button) findViewById(R.id.button2);
        mButtons[2] = (Button) findViewById(R.id.button3);
        mButtons[3] = (Button) findViewById(R.id.button4);

        try{
            mAllSuperheroList = JSONLoader.loadJSONFromAsset(this);
        } catch (IOException e){
            Log.e(TAG, "Error loading JSON file", e);
        }

        mHeroAttribute = preferences.getString(getString(R.string.pref_selected), getString(R.string.pref_def));

        updateHero();
        resetQuiz();
    }

    /**
     * Sets up and starts a new quiz.
     */
    public void resetQuiz() {
        mCorrectGuesses = 0;
        mTotalGuesses = 0;
        mQuizSuperheroList.clear();

        // Size is 0 after clear
        while(mQuizSuperheroList.size() < SUPERHEROES_IN_QUIZ) {
            int randomPosition = rng.nextInt(mSuperHeroTracker.size()); // Fills a random position to be placed in one of the button
            Superhero randomHero = mSuperHeroTracker.get(randomPosition);

            if (!mQuizSuperheroList.contains(randomHero))
                mQuizSuperheroList.add(randomHero);
        }
        loadNextSuperhero();
    }

    /**
     * Method initiates the process of loading the next flag for the quiz, showing
     * the flag's image and then 4 buttons, one of which contains the correct answer.
     */
    private void loadNextSuperhero() {
        mCorrectSuperhero = mQuizSuperheroList.remove(0);

        mAnswerTextView.setText("");
        int questionNumber = SUPERHEROES_IN_QUIZ - mQuizSuperheroList.size();
        mQuestionNumberTextView.setText(getString(R.string.question, questionNumber, SUPERHEROES_IN_QUIZ));

        AssetManager am = getAssets();

        try {
            InputStream stream = am.open(mCorrectSuperhero.getFileName());
            Drawable image = Drawable.createFromStream(stream, mCorrectSuperhero.getUserName());
            mFlagImageView.setImageDrawable(image);
        } catch (IOException e) {
            Log.e(TAG, "@string/load_image_error" + mCorrectSuperhero.getFileName(), e);
        }


        if (mHeroAttribute.equals(getString(R.string.hero_name))) {
            mCorrectAnswer = mCorrectSuperhero.getName();
            mGuessSuperheroTextView.setText(getString(R.string.guess_hero_name));
        }
        else if (mHeroAttribute.equals(getString(R.string.hero_power))) {
            mCorrectAnswer = mCorrectSuperhero.getSuperpower();
            mGuessSuperheroTextView.setText(getString(R.string.guess_hero_power));
        }
        else {
            mCorrectAnswer = mCorrectSuperhero.getOneThing();
            mGuessSuperheroTextView.setText(getString(R.string.guess_hero_thing));
        }

        do {
            Collections.shuffle(mFilteredSuperheroList);
        } while (mFilteredSuperheroList.subList(0, mButtons.length).contains(mCorrectAnswer));


        for (int i = 0; i < mButtons.length; ++i) {
            mButtons[i].setEnabled(true);
            mButtons[i].setText(mFilteredSuperheroList.get(i));
        }

        mButtons[rng.nextInt(mButtons.length)].setText(mCorrectAnswer);
    }

    /**
     * Handles the click event of one of the 4 buttons indicating the guess of a country's name
     * to match the flag image displayed.  If the guess is correct, the country's name (in GREEN) will be shown,
     * followed by a slight delay of 2 seconds, then the next flag will be loaded.  Otherwise, the
     * word "Incorrect Guess" will be shown in RED and the button will be disabled.
     * @param v button click
     */
    public void makeGuess(View v) {
        Button clickedButton = (Button) v;
        String guess = clickedButton.getText().toString();
        mTotalGuesses++;
        if(guess.equals(mCorrectAnswer)) {
            for (Button b : mButtons)
                b.setEnabled(false);

            mCorrectGuesses++;
            mAnswerTextView.setText(mCorrectAnswer);
            mAnswerTextView.setTextColor(ContextCompat.getColor(this, R.color.correct_answer));

            if (mCorrectGuesses < SUPERHEROES_IN_QUIZ) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextSuperhero();
                    }
                }, 2000); // Milliseconds
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.results, mTotalGuesses, (double) mCorrectGuesses / mTotalGuesses * 100));
                builder.setPositiveButton(getString(R.string.reset_quiz), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetQuiz();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
            }
        } else {
            clickedButton.setEnabled(false);
            mAnswerTextView.setText(getString(R.string.incorrect_answer));
            mAnswerTextView.setTextColor(ContextCompat.getColor(this, R.color.incorrect_answer));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);

        return super.onOptionsItemSelected(item);
    }

    SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            mHeroAttribute = sharedPreferences.getString(getString(R.string.pref_selected), getString(R.string.pref_def));

            updateHero();
            resetQuiz();

            Toast.makeText(MainActivity.this, R.string.restarting_quiz, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Updates the quiz with what ever hero attribute the user has selected (Name, Power, Thing).
     * Creates a relational String pair. The superhero is one String (mSuperHeroTracker) and the
     * heroes' selected attribute is the second String (mFilteredSuperheroList).
     */
    private void updateHero(){
        mFilteredSuperheroList = new ArrayList<>();
        mSuperHeroTracker = new ArrayList<>();

        for (Superhero sh: mAllSuperheroList){
            mSuperHeroTracker.add(sh);

            if ((mHeroAttribute.equals(getString(R.string.pref_def)))
                    || mHeroAttribute.equals(getString(R.string.hero_name)))
                mFilteredSuperheroList.add(sh.getName());
            else if(mHeroAttribute.equals(getString(R.string.hero_power)))
                mFilteredSuperheroList.add(sh.getSuperpower());
            else
                mFilteredSuperheroList.add(sh.getOneThing());
        }
    }
}
