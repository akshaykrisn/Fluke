package app.fluky.ml.fluk.xtras;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.MainActivity;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class NetWorkNotAvailable extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);

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
    }

    @Override
    public void onBackPressed() {

    }

    private boolean retryJammed = false;
    public void CheckConnection(View view) {
        if (!retryJammed) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                retryJammed = true;
                ((TextView) findViewById(R.id.mesNetwork)).setText("Connected! Starting app..");
                nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog2454111);
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

                new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        String s = "Going back in " + Long.toString(millisUntilFinished / 1000);
                        ((TextView) findViewById(R.id.retryconnecting)).setText(s);
                    }

                    public void onFinish() {
                        Intent i = new Intent(NetWorkNotAvailable.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }

                }.start();

            }
        }
    }
}
