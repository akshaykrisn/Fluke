package app.fluky.ml.fluk.data;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Display;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.MainActivity;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import app.fluky.ml.fluk.complex.utils.GeneratatedUser;
import app.fluky.ml.fluk.dialog.CustomAlertDialogue;
import app.fluky.ml.fluk.tooltip.Tooltip;
import app.fluky.ml.fluk.wave.WaveLineView;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static app.fluky.ml.fluk.R.color.successfulu;

public class QuizMain extends AppCompatActivity implements RewardedVideoAdListener {
    private RewardedVideoAd mRewardedVideoAd;

    private final int interval = 1000;
    private boolean wavecalled = false;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {

        public void run() {
            tapjammed = false;
            ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);
            animator1.addUpdateListener(valueAnimator -> {
                float x = 1.0f - (Integer) valueAnimator.getAnimatedValue() / (float) 10;
                findViewById(R.id.temp21288).setAlpha(x);
                if (x == 1) {
                    tapjammed = false;
                    if (!wavecalled) {
                        waveLineView.startAnim();
                        wavecalled = true;
                    }
                    SharedPreferences prefs = getSharedPreferences("QuizGspot", MODE_PRIVATE);
                    String restoredText = prefs.getString("done", null);
                    if (restoredText == null) {
                        spotlit();
                    }
                }
            });
            isloading(false);
            animator1.setDuration(500);
            animator1.start();
        }
    };

    private Handler handler9 = new Handler();
    private Runnable runnable9 = new Runnable() {

        public void run() {
            findViewById(R.id.gettingBackabo).setVisibility(View.VISIBLE);

            ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);
            animator1.addUpdateListener(valueAnimator -> {
                float x = 1f - (Integer) valueAnimator.getAnimatedValue() / (float) 10;
                waveLineView.setAlpha(1 - x);
                findViewById(R.id.gettingBackabo).setAlpha(x);
                findViewById(R.id.gettingBack).setAlpha(x);
                findViewById(R.id.winnerview).setAlpha(x);
                if (x == 1) waveLineView.stopAnim();
            });
            isloading(false);
            animator1.setDuration(500);
            animator1.start();
        }
    };

    private Handler handler2 = new Handler();
    private Runnable runnable2 = new Runnable() {
        public void run() {
            ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);
            if (!(questionCount < 6)) {
                calculateScoreOutCome();
            }
            animator1.addUpdateListener(valueAnimator -> {
                float x = 1.0f - (Integer) valueAnimator.getAnimatedValue() / (float) 10;
                findViewById(R.id.temp21288).setAlpha(1 - x);
                if (x == 1.0f) {
                    if (questionCount < 6) {
                        questionSwitcher.setCurrentText(questions.get(questionCount));
                        answer1Switcher.setCurrentText(answer.get(questionCount).getA1());
                        answer2Switcher.setCurrentText(answer.get(questionCount).getA2());
                        answer3Switcher.setCurrentText(answer.get(questionCount).getA3());
                        answer4Switcher.setCurrentText(answer.get(questionCount).getA4());
                        currentAnswer1 = answer.get(questionCount).getA1();
                        currentAnswer2 = answer.get(questionCount).getA2();
                        currentAnswer3 = answer.get(questionCount).getA3();
                        currentAnswer4 = answer.get(questionCount).getA4();
                        resetcolor();
                        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
                        handler.postDelayed(runnable, interval);
                    }
                }
            });
            isloading(false);
            animator1.setDuration(500);
            animator1.start();
        }
    };

    private static int score = 0;
    private static int questionCount = 0;
    private Intent i;
    private List<String> questions;
    private List<Answers> answer;
    private static boolean tapjammed = true;
    private FirebaseFirestore db;
    private TextSwitcher questionSwitcher;
    private TextSwitcher answer1Switcher;
    private TextSwitcher answer2Switcher;
    private TextSwitcher answer3Switcher;
    private TextSwitcher answer4Switcher;

    private static String currentAnswer1;
    private static String currentAnswer2;
    private static String currentAnswer3;
    private static String currentAnswer4;
    private WaveLineView waveLineView;
    private String qId;
    private static boolean completed = false;


    private static int[] originalColor = {
            R.color.answer1, R.color.answer2, R.color.answer3, R.color.answer4
    };
    private static int[] answerIds = {R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4};
    private static int selectedAns;

    private CountDownTimer mccc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_background);

        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                .enabled(true) //default: true
                .showErrorDetails(false) //default: true
                .showRestartButton(false) //default: true
                .logErrorOnRestart(false) //default: true
                .trackActivities(false) //default: false
                .minTimeBetweenCrashesMs(2000) //default: 3000
                .errorDrawable(R.mipmap.ic_launcher_round) //default: bug image
                .restartActivity(DetailsInputDialog2.class) //default: null (your app's launch activity)
                .errorActivity(DetailsInputDialog2.class) //default: null (default error activity)
                .eventListener(null) //default: null
                .apply();

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        mccc = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                String s = "Going back in " + Long.toString(millisUntilFinished / 1000);
                ((TextView) findViewById(R.id.goingbacktext)).setText(s);
            }

            public void onFinish() {
                Intent i = new Intent(QuizMain.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(i);
            }

        };

        nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog2454);
        viewKonfetti.setVisibility(View.VISIBLE);
        tapjammed = false;
        i = new Intent(QuizMain.this, MainActivity.class);
        waveLineView = findViewById(R.id.waveLineView);
        db = FirebaseFirestore.getInstance();
        questions = new ArrayList<>();
        answer = new ArrayList<>();
        isloading(true);
        RecieveQuiz();


        AssetManager am = this.getApplicationContext().getAssets();

        Typeface typefaceBold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goobold.ttf"));
        ((RelativeLayout) findViewById(R.id.questionBack)).setOnClickListener(view -> Tooltip.make(QuizMain.this,
                new Tooltip.Builder(101)
                        .anchor(((RelativeLayout) findViewById(R.id.questionBack)), Tooltip.Gravity.TOP)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 3000)
                        .activateDelay(300)
                        .showDelay(300)
                        .text("Answer this question by tapping on the right answer below it")
                        .maxWidth(500)
                        .withArrow(true)
                        .withOverlay(true)
                        .typeface(typefaceBold)
                        .withStyleId(R.style.ToolTipLayoutDefaultStyle)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show());
    }

    private void initiateSwitchers() {
        questionSwitcher = (TextSwitcher) findViewById(R.id.questionBackLive);
        questionSwitcher.setInAnimation(this, android.R.anim.fade_in);
        questionSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        questionSwitcher.setCurrentText(questions.get(0));

        answer1Switcher = (TextSwitcher) findViewById(R.id.answer1Body);
        answer1Switcher.setInAnimation(this, android.R.anim.fade_in);
        answer1Switcher.setOutAnimation(this, android.R.anim.fade_out);
        answer1Switcher.setCurrentText(answer.get(0).getA1());
        currentAnswer1 = answer.get(0).getA1();

        answer2Switcher = (TextSwitcher) findViewById(R.id.answer2Body);
        answer2Switcher.setInAnimation(this, android.R.anim.fade_in);
        answer2Switcher.setOutAnimation(this, android.R.anim.fade_out);
        answer2Switcher.setCurrentText(answer.get(0).getA2());
        currentAnswer2 = answer.get(0).getA2();

        answer3Switcher = (TextSwitcher) findViewById(R.id.answer3Body);
        answer3Switcher.setInAnimation(this, android.R.anim.fade_in);
        answer3Switcher.setOutAnimation(this, android.R.anim.fade_out);
        answer3Switcher.setCurrentText(answer.get(0).getA3());
        currentAnswer3 = answer.get(0).getA3();

        answer4Switcher = (TextSwitcher) findViewById(R.id.answer4Body);
        answer4Switcher.setInAnimation(this, android.R.anim.fade_in);
        answer4Switcher.setOutAnimation(this, android.R.anim.fade_out);
        answer4Switcher.setCurrentText(answer.get(0).getA4());
        currentAnswer4 = answer.get(0).getA4();

        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);
    }

    private void RecieveQuiz() {
        DocumentReference user = db.collection("quiz").document("questions");
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.get("valid") != null) {
                    if (doc.get("valid").toString().equals("1")) {
                        for (int i = 1; i < 7; i++) {
                            String[] dataa = doc.get(Integer.toString(i)).toString().split(";");
                            questions.add(dataa[0]);
                            answer.add(new Answers(dataa[1], dataa[2], dataa[3], dataa[4]));
                        }
                        qId = doc.get("id").toString();
                        initiateSwitchers();
                    }
                }
            }
        })
                .addOnFailureListener(e -> {
                    Typeface tt = Typeface.createFromAsset(getApplicationContext().getAssets(),
                            String.format(Locale.US, "fonts/%s", "goobold.ttf"));

                    CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(QuizMain.this)
                            .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                            .setTitle("We can't connect")
                            .setMessage("Looks like you don't have a stable internet connection. Please try again later")
                            .setPositiveText("Okay")
                            .setPositiveTypeface(tt)
                            .setOnPositiveClicked((view, dialog) -> dialog.dismiss())
                            .setDecorView(getWindow().getDecorView())
                            .build();
                    alert.show();
                });
    }

    private void resetcolor() {
        for (int i = 0; i < answerIds.length; i++) {
            ((RelativeLayout) findViewById(answerIds[i])).setBackgroundColor(getResources().getColor(originalColor[i]));
        }
    }

    public void TransactQuestion(View view) {
        if (questionCount < 6 && !tapjammed) {
            tapjammed = true;

            selectedAns = view.getId();
            String anssssss = "";
            switch (view.getId()) {
                case R.id.answer1:
                    anssssss = currentAnswer1;
                    break;
                case R.id.answer2:
                    anssssss = currentAnswer2;
                    break;
                case R.id.answer3:
                    anssssss = currentAnswer3;
                    break;
                case R.id.answer4:
                    anssssss = currentAnswer4;
                    break;
            }
            try {
                if (answer.get(questionCount).getRight().equals(anssssss)) {
                    ((RelativeLayout) findViewById(selectedAns)).setBackgroundColor(getResources().getColor(successfulu));
                    score++;
                } else {
                    ((RelativeLayout) findViewById(selectedAns)).setBackgroundColor(getResources().getColor(R.color.redd));
                }
                questionCount++;
                doStuffsIntermediate(score, questionCount);

                ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);
                animator1.addUpdateListener(valueAnimator -> {
                    float x = 1.0f - (Integer) valueAnimator.getAnimatedValue() / (float) 10;
                    for (int i = 0; i < answerIds.length; i++) {
                        if (answerIds[i] != selectedAns) {
                            findViewById(answerIds[i]).setAlpha(x);
                            ((RelativeLayout) findViewById(answerIds[i])).setBackgroundColor(getResources().getColor(originalColor[i]));
                        }
                    }
                });
                animator1.setDuration(500);
                animator1.start();


                proceedToNextQuestion();
            } catch (IndexOutOfBoundsException ee) {
                ee.printStackTrace();
            }
        }
    }

    private void proceedToNextQuestion() {
        handler2.postAtTime(runnable2, System.currentTimeMillis() + interval);
        handler2.postDelayed(runnable2, interval);
    }

    private void doStuffsIntermediate(int sco, int count) {

    }

    private void calculateScoreOutCome() {
        Random random = new Random();
        int sertss = 5;
        if (score == 1) {
            sertss = sertss + random.nextInt(6);
        }
        if (score == 2) {
            sertss = 12;
            sertss = sertss + random.nextInt(9);
        }
        if (score == 3) {
            sertss = 22;
            sertss = sertss + random.nextInt(9);
        }
        if (score == 4) {
            sertss = 32;
            sertss = sertss + random.nextInt(9);
        }
        if (score == 5) {
            sertss = 42;
            sertss = sertss + random.nextInt(9);
        }
        if (score == 6) {
            sertss = 52;
            sertss = sertss + random.nextInt(9);
        }
        GeneratatedUser.addSerts(sertss);
        winnerStuffFinal(sertss);
    }

    private void winnerStuffFinal(int count) {

        findViewById(R.id.gettingBackabo).setOnClickListener(view -> showAdbdLiving(null));

        completed = true;
        tapjammed = false;
        ((TextView) findViewById(R.id.totalCounting)).setText(String.format("+%s", Integer.toString(count)));
        waveLineView.setVisibility(View.GONE);
        handler9.postAtTime(runnable9, System.currentTimeMillis() + interval);
        handler9.postDelayed(runnable9, interval);
        mccc.start();

        SharedPreferences.Editor editor = getSharedPreferences(qId, MODE_PRIVATE).edit();
        editor.putString("done", "1");
        editor.apply();

        nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog2454);
        viewKonfetti.setVisibility(View.VISIBLE);
        viewKonfetti.build()
                .addColors(R.color.one, Color.RED, Color.MAGENTA, Color.GREEN)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1000)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                .streamFor(50, 5000L);

    }

    private void isloading(boolean yesorno) {
        if (yesorno) findViewById(R.id.gifViewCardLiving).setVisibility(View.VISIBLE);
        else findViewById(R.id.gifViewCardLiving).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (!completed) {
            CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(QuizMain.this)
                    .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                    .setTitle("Are you sure?")
                    .setMessage("You're about to leave. Your progress will be lost.")
                    .setNegativeText("Stay here")
                    .setPositiveText("Home")
                    .setOnNegativeClicked((view, dialog) -> dialog.dismiss())
                    .setOnPositiveClicked((view, dialog) -> {
                        questionCount = 0;
                        score = 0;
                        isloading(true);
                        dialog.dismiss();
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    })
                    .setCancelable(true)
                    .setDecorView(getWindow().getDecorView())
                    .build();
            alert.show();
        }
    }

    @Override
    protected void onResume() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.resume(this);
        super.onResume();
        waveLineView.onResume();
    }

    @Override
    protected void onPause() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.pause(this);
        super.onPause();
        waveLineView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.destroy(this);
        super.onDestroy();
        waveLineView.release();
    }


    public void spotlit() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        SimpleTarget firstTarget = new SimpleTarget.Builder(this)
                .setPoint(width / 2, 0)
                .setShape(new Circle(height / 2.3f))
                .setTitle("Live now")
                .setDescription("Keep calm and be quick")
                .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                    @Override
                    public void onStarted(SimpleTarget target) {
                        // do something
                    }

                    @Override
                    public void onEnded(SimpleTarget target) {
                        // do something
                    }
                })
                .build();

        SimpleTarget secondTarget = new SimpleTarget.Builder(this)
                .setPoint(width / 2, height)
                .setShape(new Circle(height / 2f))
                .setTitle("Questions")
                .setDescription("You will have to answer 6 question to complete this quiz")
                .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                    @Override
                    public void onStarted(SimpleTarget target) {
                        // do something
                    }

                    @Override
                    public void onEnded(SimpleTarget target) {
                        // do something
                    }
                })
                .build();


        Spotlight.with(this)
                .setOverlayColor(R.color.background)
                .setDuration(1000L)
                .setAnimation(new DecelerateInterpolator(2f))
                .setTargets(secondTarget, firstTarget)
                .setClosedOnTouchedOutside(true)
                .setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onEnded() {
                        SharedPreferences.Editor editor = getSharedPreferences("QuizGspot", MODE_PRIVATE).edit();
                        editor.putString("done", "ele");
                        editor.apply();
                    }
                })
                .start();
    }

    private String adId;

    private void loadRewardedVideoAdliving() {
        adId = "ca-app-pub-3648291950784680/5413622092";
//        adId = "ca-app-pub-3940256099942544/5224354917";
        mRewardedVideoAd.loadAd(adId,
                new AdRequest.Builder().build());
    }

    private boolean adDone = false;

    public void showAdbdLiving(View view) {
        if (!adDone) {
            loadRewardedVideoAdliving();
            adDone = true;
            findViewById(R.id.progressLives).setVisibility(View.VISIBLE);
            try {
                mccc.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        findViewById(R.id.progressLives).setVisibility(View.GONE);
        mccc.start();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        try {
            findViewById(R.id.progressLives).setVisibility(View.VISIBLE);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            GeneratatedUser.addSerts(10);
            DatabaseReference myRef = database.getReference().child("Ad").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(adId.split("/")[0] + adId.split("/")[1]);
            myRef.setValue(1).addOnSuccessListener(f -> {
                TextView vvv = ((TextView) findViewById(R.id.totalCounting));
                String scoreddd = "+" + Integer.toString(Integer.parseInt(vvv.getText().toString()) + 10);
                vvv.setText(scoreddd);
                ((TextView) findViewById(R.id.goingbacktextabo)).setText("Congrats! You earned +10 Flukes");
                ((CardView) findViewById(R.id.gettingBackabo)).setCardBackgroundColor(getResources().getColor(R.color.successfulu));
                nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog2454);

                viewKonfetti.build()
                        .addColors(R.color.one, Color.RED, Color.MAGENTA, Color.GREEN)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(1000)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5))
                        .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                        .streamFor(50, 5000L);
                findViewById(R.id.progressLives).setVisibility(View.GONE);
                mccc.start();
                try {
                    mccc.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (NullPointerException eee) {
            eee.printStackTrace();
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        findViewById(R.id.progressLives).setVisibility(View.GONE);
        mccc.start();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        findViewById(R.id.progressLives).setVisibility(View.GONE);
        mccc.start();
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(QuizMain.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setTitle("Error")
                .setMessage("If you can't see this ad, it's a known issue. We'll fix that soon")
                .setPositiveText("Okay")
                .setOnPositiveClicked((view, dialog) -> dialog.dismiss())
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
        ((TextView) findViewById(R.id.goingbacktextabo)).setText(R.string.errorloadad);
        ((CardView) findViewById(R.id.gettingBackabo)).setCardBackgroundColor(getResources().getColor(R.color.redd));
    }

    @Override
    public void onRewardedVideoCompleted() {
    }

}
