package app.fluky.ml.fluk.data;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.MainActivity;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import app.fluky.ml.fluk.dialog.CustomAlertDialogue;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class QuizLoading extends AppCompatActivity {

    private FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_loading);
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

        db = FirebaseFirestore.getInstance();
        CheckValidity();
    }

    @Override
    public void onBackPressed() {

    }

    private void CheckValidity() {

        DocumentReference user = db.collection("quiz").document("valid");
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.get("valid") != null) {

                    SharedPreferences prefs = getSharedPreferences(doc.get("id").toString(), MODE_PRIVATE);
                    String restoredText = prefs.getString("done", null);
                    if (restoredText == null) {
                        if (doc.get("valid").toString().equals("1")) {
                            Intent i = new Intent(this, QuizLive.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(this, Quiz.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    }
                    else {
                        nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog24541);
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

                        ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);
                        animator1.addUpdateListener(valueAnimator -> {
                            float x = 1.0f - (Integer) valueAnimator.getAnimatedValue() / (float) 10;
                            findViewById(R.id.winnerviewLoading).setAlpha(x);
                            findViewById(R.id.gettingBackLoading).setAlpha(x);
                            findViewById(R.id.gifViewCardLivingLoading).setAlpha(1-x);
                        });
                        animator1.setDuration(500);
                        animator1.start();

                        new CountDownTimer(6000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                String s = "Going back in " + Long.toString(millisUntilFinished/1000);
                                ((TextView)findViewById(R.id.goingbacktextLoading)).setText(s);
                            }

                            public void onFinish() {
                                Intent i = new Intent(QuizLoading.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }

                        }.start();
                    }
                }
            }
        })
                .addOnFailureListener(e -> {
                    Typeface tt = Typeface.createFromAsset(getApplicationContext().getAssets(),
                            String.format(Locale.US, "fonts/%s", "goobold.ttf"));

                    CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(QuizLoading.this)
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

}
