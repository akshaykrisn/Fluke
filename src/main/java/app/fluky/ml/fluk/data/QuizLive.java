package app.fluky.ml.fluk.data;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.MainActivity;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class QuizLive extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<String> questions;
    private List<Answers> answer;
    private TextSwitcher bidIsLive;
    private static String[] headings = {"Live now", "Good Luck"};
    private static int headingPos = 0;
    private SlidingUpPanelLayout mLayout;
    private static boolean activated = false;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_live);

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
        questions = new ArrayList<>();
        answer = new ArrayList<>();

        bidIsLive = findViewById(R.id.availableLaterLive);
        bidIsLive.setInAnimation(QuizLive.this, android.R.anim.fade_in);
        bidIsLive.setOutAnimation(QuizLive.this, android.R.anim.fade_out);

        fonting();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    bidIsLive.setText(headings[headingPos]);
                    headingPos++;
                    if (headingPos == 2) headingPos = 0;
                });
            }
        }, 0, 5000);

        nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog98);
        viewKonfetti.build()
                .addColors(R.color.one, Color.RED, Color.MAGENTA, Color.GREEN)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1000)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                .streamFor(25, 10000L);

    }

    private void fonting() {
        AssetManager am = this.getApplicationContext().getAssets();

        Typeface typefaceRegular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goooregular.ttf"));

        ((TextView) findViewById(R.id.temp4664465Live)).setTypeface(typefaceRegular);

    }

    public void sartQuiz(View view) {
        Intent i = new Intent(this, QuizMain.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
