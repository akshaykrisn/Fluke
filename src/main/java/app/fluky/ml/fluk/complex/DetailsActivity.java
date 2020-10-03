package app.fluky.ml.fluk.complex;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

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

        imageView = findViewById(R.id.details_back);
        textView = findViewById(R.id.details_front);

        imageView.setImageBitmap(MainActivity.currentBit);
        textView.setTextColor(Color.WHITE);
        textView.setText(MainActivity.currentDes);
        fonting();
    }

    private void fonting() {
        AssetManager am = this.getApplicationContext().getAssets();

        Typeface typefaceMedium = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goomedium.ttf"));

        ((TextView) findViewById(R.id.details_front)).setTypeface(typefaceMedium);
    }
}
