package app.fluky.ml.fluk.frags;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yalantis.jellytoolbar.listener.JellyListener;
import com.yalantis.jellytoolbar.widget.JellyToolbar;

import java.util.Locale;
import java.util.Objects;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.MainActivity;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class SocialActivity extends AppCompatActivity {

    private JellyToolbar toolbar;
    private LinearLayout lin;
    private Intent intent;
    private LinearLayout view2;



    private final int interval = 800; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {

            view2 = (LinearLayout) findViewById(R.id.coming_soon_linear);

            ValueAnimator animator1 = ValueAnimator.ofInt(view2.getPaddingTop(), 0);

            animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    view2.setPadding(0, (Integer) valueAnimator.getAnimatedValue(), 0, 0);
                    view2.setAlpha(1.0f - (Integer) valueAnimator.getAnimatedValue() / (float) 25);
                }
            });

            animator1.setDuration(300);
            animator1.start();
        }
    };

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

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

        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);


        intent = new Intent(this, SocialActivity.class);
        intent.putExtra("Package", "Me");

        toolbar = (JellyToolbar) findViewById(R.id.toolbarSocial);
        toolbar.setJellyListener(jellyListener);

        lin = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_tab, null);
        lin.setBackgroundResource(R.color.colorTransparent);

        toolbar.setContentView(lin);

        ((ImageView) findViewById(R.id.socialUn)).setAlpha(1.0F);
        ((ImageView) findViewById(R.id.socialUn)).setBackgroundResource(R.drawable.rounded_tab);
        fonting();
    }

    private void fonting() {
        AssetManager am = this.getApplicationContext().getAssets();

        Typeface typefaceBold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goobold.ttf"));
        Typeface typefaceRegular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goooregular.ttf"));

        ((TextView)findViewById(R.id.comingsoonText)).setTypeface(typefaceBold);
        ((TextView)findViewById(R.id.temp1)).setTypeface(typefaceRegular);

    }


    private JellyListener jellyListener = new JellyListener() {
        @Override
        public void onCancelIconClicked() {
            toolbar.collapse();
        }
    };

    public void imoneClick(View view) {
        intent = new Intent(this, MainActivity.class);
        intent.putExtra("Package", "Main");
        toolbar.collapse();
        transi();
    }

    public void imtwoClick(View view) {
        intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("Package", "Profile");
        toolbar.collapse();
        transi();
    }

    public void imthreeClick(View view) {
        //do nothing
    }

    public void transi() {
        Bundle b = intent.getExtras();

        assert b != null;
        if (!Objects.requireNonNull(b.get("Package")).toString().equals("Me")) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
