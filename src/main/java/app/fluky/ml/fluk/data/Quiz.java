package app.fluky.ml.fluk.data;

import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.irozon.sneaker.Sneaker;

import java.io.OutputStream;
import java.util.Locale;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.MainActivity;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import app.fluky.ml.fluk.complex.utils.GeneratatedUser;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class Quiz extends AppCompatActivity implements RewardedVideoAdListener {

    private LinearLayout main_item_share_ad;
    private HorizontalScrollView tickInside;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

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

        fonting();
        main_item_share_ad = findViewById(R.id.main_item_share_ad_bd);
        tickInside = findViewById(R.id.earnmorecards32);
        OverScrollDecoratorHelper.setUpOverScroll(tickInside);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        ((TextView) findViewById(R.id.referralCode_bd)).setText(MainActivity.refCode);
    }

    public void shareLoadbd(View view) {
        if (MainActivity.shareJammed) {
            Toast.makeText(this, "Update your profile now to earn by sharing", Toast.LENGTH_SHORT).show();
        }
        handler2.postAtTime(runnable2, System.currentTimeMillis() + 500);
        handler2.postDelayed(runnable2, 50);
    }

    public void shareLoadBack_bd(View view) {
        handler3.postAtTime(runnable3, System.currentTimeMillis() + 500);
        handler3.postDelayed(runnable3, 50);
    }

    public void shareNowbd(View view) {

        String msg = "I'm inviting you to use Fluke, a simple and free way to win cool gifts. Here my code: (" + MainActivity.refCode + ")." +
                " Just enter it while signing up. You'll be getting extra benefits instantly. https://fluke.ml/app";

        if (MainActivity.shareJammed) {
            msg = "I'm inviting you to use Fluke, a simple and free way to win cool gifts. https://fluke.ml/app";
        }

        try {
            Uri imageUri = Uri.parse("android.resource://app.fluky.ml.fluk/drawable/ashareintent.jpg");
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ashareintent);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "title");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);


            OutputStream outstream;
            try {
                outstream = getContentResolver().openOutputStream(uri);
                icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                outstream.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }
            share.putExtra(Intent.EXTRA_TEXT, msg);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share Image"));
        } catch (Exception e) {
            e.printStackTrace();

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    msg);
            sendIntent.setType("text/plain");
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(sendIntent);
        }
    }

    private Handler handler2 = new Handler();
    private Runnable runnable2 = () -> {


        ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);
        main_item_share_ad.setVisibility(View.VISIBLE);

        animator1.addUpdateListener(valueAnimator -> {
            float x = (Integer) valueAnimator.getAnimatedValue() / (float) 10;
            tickInside.setAlpha(x);
            main_item_share_ad.setAlpha(1f - x);
            if ((Integer) valueAnimator.getAnimatedValue() == 0) {
                tickInside.setVisibility(View.GONE);
            }
        });

        animator1.setDuration(500);
        animator1.start();
    };

    private Handler handler3 = new Handler();
    private Runnable runnable3 = () -> {

        ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);

        tickInside.setVisibility(View.VISIBLE);

        animator1.addUpdateListener(valueAnimator -> {

            float x = (Integer) valueAnimator.getAnimatedValue() / (float) 10;
            tickInside.setAlpha(1f - x);
            main_item_share_ad.setAlpha(x);
            if ((Integer) valueAnimator.getAnimatedValue() == 0) {
                tickInside.setVisibility(View.VISIBLE);
                main_item_share_ad.setVisibility(View.GONE);
            }
        });

        animator1.setDuration(500);
        animator1.start();
    };

    private String adId;

    private void loadRewardedVideoAd() {
        adId = "ca-app-pub-3648291950784680/5413622092";
//        adId = "ca-app-pub-3940256099942544/5224354917";
        findViewById(R.id.textssadbd).setAlpha(0.3f);
        findViewById(R.id.imgssadbd).setAlpha(0.3f);
        mRewardedVideoAd.loadAd(adId,
                new AdRequest.Builder().build());
    }

    public void showAdbd(View view) {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        findViewById(R.id.textssadbd).setAlpha(1f);
        findViewById(R.id.imgssadbd).setAlpha(1f);
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        GeneratatedUser.addSerts(2);
        Sneaker.with(this)
                .setTitle("Verifying", R.color.white) // Title and title color
                .setMessage("", R.color.white) // Message and message color
                .setDuration(900000) // Time duration to show
                .autoHide(true) // Auto hide Sneaker view
                .setHeight(200) // Height of the Sneaker layout
                .sneak(R.color.colorAccent);
        DatabaseReference myRef = database.getReference().child("Ad").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(adId.split("/")[0] + adId.split("/")[1]);
        myRef.setValue(1).addOnSuccessListener(f -> {
            Sneaker.hide();
            Sneaker.with(this)
                    .setTitle("Congratulations", R.color.white) // Title and title color
                    .setMessage("2 flukes credited", R.color.white) // Message and message color
                    .setDuration(1000) // Time duration to show
                    .autoHide(true) // Auto hide Sneaker view
                    .setHeight(200) // Height of the Sneaker layout
                    .sneak(R.color.successfulu);
        });
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        findViewById(R.id.textssadbd).setAlpha(0.3f);
        findViewById(R.id.imgssadbd).setAlpha(0.3f);
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    private void fonting() {
        AssetManager am = this.getApplicationContext().getAssets();

        Typeface typefaceRegular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goooregular.ttf"));

        ((TextView) findViewById(R.id.availableLater)).setTypeface(typefaceRegular);
        ((TextView) findViewById(R.id.temp4664464)).setTypeface(typefaceRegular);
        ((TextView) findViewById(R.id.temp4664461)).setTypeface(typefaceRegular);
    }

}
