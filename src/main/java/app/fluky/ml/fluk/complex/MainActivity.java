package app.fluky.ml.fluk.complex;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.StartCheckoutEvent;
import com.github.javiersantos.piracychecker.PiracyChecker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.irozon.sneaker.Sneaker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ramotion.fluidslider.FluidSlider;
import com.robinhood.ticker.TickerUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;
import com.yalantis.jellytoolbar.listener.JellyListener;
import com.yalantis.jellytoolbar.widget.JellyToolbar;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.cards.SliderAdapter;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import app.fluky.ml.fluk.complex.utils.DecodeBitmapTask;
import app.fluky.ml.fluk.complex.utils.GeneratatedUser;
import app.fluky.ml.fluk.complex.utils.ImageSaver;
import app.fluky.ml.fluk.complex.utils.MemberBid;
import app.fluky.ml.fluk.data.ActiveBid;
import app.fluky.ml.fluk.data.QuizLoading;
import app.fluky.ml.fluk.frags.ProfileActivity;
import app.fluky.ml.fluk.frags.SocialActivity;
import app.fluky.ml.fluk.tooltip.Tooltip;
import app.fluky.ml.fluk.ui.CardSliderLayoutManager;
import app.fluky.ml.fluk.ui.CardSnapHelper;
import app.fluky.ml.fluk.xtras.NetWorkNotAvailable;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import kotlin.Unit;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private boolean isLockeds = false;

    public static boolean firstTimeUserBonus = false;
    public static boolean referralCodeValidHai = false;

    private void referralCodeValidGo() {
        String titlesss = "";
        String desss = "";
        if (referralCodeValidHai) {
            titlesss = "Congrats";
            desss = "Your friends just gave you 10 flukes. Here's a bonus of 15 flukes from us!";
        } else {
            titlesss = "Welcome to Fluke";
            desss = "Here's a joining bonus of 15 flukes";
        }

        Sneaker.with(MainActivity.this)
                .setTitle(titlesss, R.color.white) // Title and title color
                .setMessage(desss, R.color.white) // Message and message color
                .setDuration(3000) // Time duration to show
                .autoHide(true) // Auto hide Sneaker view
                .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                .setHeight(130)// Height of the Sneaker layout
                .setCornerRadius(16)
                .sneak(R.color.colorAccent);

        nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog);
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

        referralCodeValidHai = false;
        firstTimeUserBonus = false;
    }

    public static boolean checkReferralValid = false;
    public static int howmanyrefereee = 1;

    private void dialogItNow() {
        checkReferralValid = false;

        Sneaker.with(MainActivity.this)
                .setTitle("Congrats!", R.color.white) // Title and title color
                .setMessage("You just earned " + Integer.toString(howmanyrefereee) + " flukes by sharing!", R.color.white) // Message and message color
                .setDuration(3000) // Time duration to show
                .autoHide(true) // Auto hide Sneaker view
                .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                .setHeight(130)// Height of the Sneaker layout
                .setCornerRadius(16)
                .sneak(R.color.successfulu);

        nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog);
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

    private com.robinhood.ticker.TickerView tickerView;// 1 Second
    private Handler handler = new Handler();
    private static int loadCount = 0;
    private Runnable runnable = new Runnable() {
        public void run() {

            view2 = findViewById(R.id.down_linear_layout);
            findViewById(R.id.lcedContent).setVisibility(View.VISIBLE);
            findViewById(R.id.sliding_layoutDown).setVisibility(View.VISIBLE);
            ValueAnimator animator1 = ValueAnimator.ofInt(view2.getPaddingTop(), 0);
            Sneaker.hide();
            animator1.addUpdateListener(valueAnimator -> {
                findViewById(R.id.gifViewCardMain).setAlpha((Integer) valueAnimator.getAnimatedValue() / (float) 200);
                view2.setPadding(0, (Integer) valueAnimator.getAnimatedValue(), 0, 0);

                float x = 1.0f - (Integer) valueAnimator.getAnimatedValue() / (float) 25;
                findViewById(R.id.lcedContent).setAlpha(x);
                findViewById(R.id.endtimetextviewCard).setAlpha(x);
                findViewById(R.id.recycler_view).setAlpha(x);
                findViewById(R.id.sliding_layoutDown).setAlpha(x);
                findViewById(R.id.blurCardLayout).setAlpha(x);
                findViewById(R.id.toolbarHome).setAlpha(x);
                findViewById(R.id.frame1).setAlpha(x);
                findViewById(R.id.entrrcountCard).setAlpha(x);
                if (x == 1.0) {
                    SharedPreferences prefs = getSharedPreferences("HomeTutorial", MODE_PRIVATE);
                    String restoredText = prefs.getString("done", null);
                    if (restoredText == null) {
                        spotlit();
                    }
                }
            });

            tickerView.setText(bidCount.get(bidId.get(0)));
            animator1.setDuration(500);
            animator1.start();
            isLoading(false);
        }
    };

    private static int xx = 0;
    private static int yy = 0;
    private FirebaseFirestore db;
    private AnimationDrawable anim;
    private JellyToolbar toolbar;
    private List<Bitmap> pics;
    private List<Bitmap> maps;
    private List<String> countries;
    private List<String> bidId;
    private List<String> bidDescription;
    private List<String> bidInfoUrl;
    private List<ActiveBid> ActiveBids;
    private int SuccessfulBidLoaded;
    public static String refCode;
    public static String serts;
    private static boolean panelUpForFirstTime;
    private List<String> endTimes;
    private Map<String, String> bidCount;


    private SliderAdapter sliderAdapter;


    private CardSliderLayoutManager layoutManger;
    private RecyclerView recyclerView;
    private ImageSwitcher mapSwitcher;
    private SlidingUpPanelLayout mLayout;
    public static Bitmap currentBit;
    public static String currentDes;

    private Intent intent;

    private TextView country1TextView;
    private TextView country2TextView;
    private int countryOffset1;
    private int countryOffset2;
    private long countryAnimDuration;
    private int currentPosition;
    private LinearLayout view2;
    private static Long entrycount;
    public static boolean newhere = false;
    public static boolean doness = false;
    private ScrollView ticketShowView;
    private HorizontalScrollView tickInside;
    private HorizontalScrollView main_buying_view;
    private LinearLayout main_item_share_ad;

    private DecodeBitmapTask decodeMapBitmapTask;
    private DecodeBitmapTask.Listener mapLoadListener;
    private Typeface typefaceBold, typefaceRegular;
    private CountDownTimer cTimers;

    @Override
    public void onUserInteraction() {
        if (cTimers != null) {
            try {
                cTimers.cancel();
                Tooltip.removeAll(this);
                cTimers.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onUserInteraction();
    }

    private void checkInactivity() {
        cTimers = new CountDownTimer(35000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                showTipsInactive();
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        mLayout = findViewById(R.id.sliding_layout);
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.resume(this);
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    private com.github.javiersantos.piracychecker.enums.Display piracyCheckerDisplay = com.github.javiersantos.piracychecker.enums.Display.DIALOG;
    private com.github.javiersantos.piracychecker.enums.Display piracyCheckerDisplay2 = com.github.javiersantos.piracychecker.enums.Display.ACTIVITY;
    private FirebaseDatabase database;
    public static boolean shareJammed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        if (!isNetworkAvailable()) {
            Intent i = new Intent(MainActivity.this, NetWorkNotAvailable.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            panelUpForFirstTime = false;
            startActivity(i);
        } else {
            if (!doness) {
                startSignUp();
            } else {

                isLoading(true);
                proceedAccount();
                if (newhere) {
                    newhere = false;
                    loadCount++;

                    new CountDownTimer(1000, 500) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {

                            Sneaker.with(MainActivity.this)
                                    .setTitle("Welcome aboard", R.color.white) // Title and title color
                                    .setMessage("Initial setup takes time.\nIn the meantime, check your email for verification. Ignore if you already have.", R.color.white) // Message and message color
                                    .setDuration(999999999) // Time duration to show
                                    .autoHide(true) // Auto hide Sneaker view
                                    .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                                    .setHeight(170)// Height of the Sneaker layout
                                    .setCornerRadius(16)
                                    .sneak(R.color.colorAccent);

                            nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog2);
                            viewKonfetti.build()
                                    .addColors(R.color.rainbowOne, R.color.rainbowSeven, R.color.rainbowFive, R.color.rainbowSix, R.color.rainbowFour, R.color.rainbowThree, R.color.rainbowTwo)
                                    .setDirection(0.0, 359.0)
                                    .setSpeed(1f, 5f)
                                    .setFadeOutEnabled(true)
                                    .setTimeToLive(1000)
                                    .addShapes(Shape.RECT, Shape.CIRCLE)
                                    .addSizes(new Size(12, 5))
                                    .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                                    .streamFor(20, 5000L);
                        }
                    }.start();
                }
            }
        }
    }

    private void fonting() {
        AssetManager am = this.getApplicationContext().getAssets();

        typefaceBold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goobold.ttf"));
        typefaceRegular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goooregular.ttf"));

        ((TextView) findViewById(R.id.ticketCountMain)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.endtimetextviewOne)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.temp2)).setTypeface(typefaceRegular);
        ((TextView) findViewById(R.id.textssad)).setTypeface(typefaceRegular);
        ((TextView) findViewById(R.id.textsssBoost)).setTypeface(typefaceRegular);
        ((TextView) findViewById(R.id.referralCode)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.temp3)).setTypeface(typefaceRegular);
        ((TextView) findViewById(R.id.temp6)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.temp7)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.temp8)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.temp9)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.temp10)).setTypeface(typefaceBold);
        ((com.robinhood.ticker.TickerView) findViewById(R.id.entrrcount)).setTypeface(typefaceBold);

        ((TextView) findViewById(R.id.textViewFluid)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.proceedBidText)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.temp11)).setTypeface(typefaceRegular);
        ((TextView) findViewById(R.id.tv_country_1)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.tv_country_2)).setTypeface(typefaceBold);
    }

    private void proceedAccount() {
        if (loadCount == 0 && !newhere) {
            new CountDownTimer(2000, 500) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    showTip();
                }
            }.start();
        }
        database = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
        checkTotalRef();
    }

    public static int howManF = 6;
    public static int howManFProfile = 0;
    public static boolean availableNowForProfileRef = false;
    private boolean stings = false;
    private boolean alwaysStings = false;

    private void checkTotalRef() {

        DocumentReference user = db.collection("trefer").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.get("count") != null) {
                    addRealtimeUpdateSert();
                    beforeContentLoad();
                    checkInactivity();
                    ReadFromFirestore();
                    fonting();
                    availableNowForProfileRef = true;
                    howManFProfile = Integer.parseInt(doc.get("count").toString());
                    howManF = 6 - Integer.parseInt(doc.get("count").toString());
                    if (!(Integer.parseInt(doc.get("count").toString()) >= 6)) {
                        isLockeds = true;
                        stings = true;
                        alwaysStings = true;
                    }
                }
            }
        })
                .addOnFailureListener(e -> showErr());
    }

    private void startSignUp() {
        Intent intent = new Intent(MainActivity.this, DetailsInputDialog2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        isLoading(false);
    }

    private void showTip() {
        loadCount++;

        String[] texts = {"Feeling lucky?", "Cool gifts, waiting for you...", "If you like us, rate us on the go with the profile tab."};
        String[] tips = {"Play quiz everyday at 7 PM to win...", "You can get more entries by watching ads.", "Share this app with your friends and family to get free wins.", "Please rate us on Playstore, your opinion is valuable to us."};
        Random random = new Random();
        int tox = random.nextInt(2);
        if (tox == 1) {
            String name1 = "Anon";
            try {
                name1 = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            Sneaker.with(MainActivity.this)
                    .setTitle("Hey " + name1, R.color.white) // Title and title color
                    .setMessage(texts[random.nextInt(texts.length)], R.color.white) // Message and message color
                    .setDuration(999999999) // Time duration to show
                    .autoHide(true)
                    .setCornerRadius(16)
                    .setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                    .setHeight(130)// Height of the Sneaker layout
                    .sneak(R.color.colorAccent);
        } else {
            Sneaker.with(MainActivity.this)
                    .setTitle("Tip", R.color.white) // Title and title color
                    .setMessage(tips[random.nextInt(tips.length)], R.color.white) // Message and message color
                    .setDuration(999999999) // Time duration to show
                    .setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                    .autoHide(true) // Auto hide Sneaker view
                    .setHeight(130)// Height of the Sneaker layout
                    .sneak(R.color.two);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ReadFromFirestore() {
        for (int i = 0; i < 5; i++) {
            String ind = Integer.toString(i + 1);
            DocumentReference user = db.collection("activebids").document(ind);
            final int finalI = i;
            user.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    final DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        ActiveBids.get(finalI).setBidHeading(doc.get("bidHeading").toString());
                        ActiveBids.get(finalI).setBidDescription(doc.get("bidDescription").toString());
                        ActiveBids.get(finalI).setBidId(doc.get("bidId").toString());
                        ActiveBids.get(finalI).setBidInfoUrl(doc.get("bidInfoUrl").toString());
                        ActiveBids.get(finalI).setEndTime(doc.get("endTime").toString());
                        ActiveBids.get(finalI).setBidAddress(ind);

                        DocumentReference users = db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("History").document(doc.get("bidId").toString());
                        users.get().addOnCompleteListener(taskss -> {
                            if (taskss.isSuccessful()) {
                                DocumentSnapshot docss = taskss.getResult();
                                if (docss.get(doc.get("bidId").toString()) != null) {
                                    bidCount.put(doc.get("bidId").toString(), docss.get(doc.get("bidId").toString()).toString());
                                } else {
                                    bidCount.put(doc.get("bidId").toString(), "0");
                                }
                            }
                        })
                                .addOnFailureListener(e -> {
                                    showErr();
                                });

                        new ImageLoaderTaskPr().execute(
                                new MemberBid(doc.get("bidId").toString(), doc.get("bidMainImage").toString(), doc.get("bidSecImage").toString(), finalI)
                        );
                    }
                }
            })
                    .addOnFailureListener(e -> {
                        showErr();
                    });
        }
    }

    private class ImageLoaderTaskPr extends AsyncTask<MemberBid, Void, MemberBid> {

        @Override
        protected MemberBid doInBackground(MemberBid... memberBids) {
            memberBids[0].setBitmap1(new ImageSaver(getApplicationContext()).
                    setFileName(memberBids[0].getBidId() + "1" + Integer.toString(memberBids[0].getFinalI()) + "glareContent" + ".png").
                    setDirectoryName("FlukyProfiler").
                    load());
            memberBids[0].setBitmap2(new ImageSaver(getApplicationContext()).
                    setFileName(memberBids[0].getBidId() + "2" + Integer.toString(memberBids[0].getFinalI()) + "glareContent" + ".png").
                    setDirectoryName("FlukyProfiler").
                    load());
            return memberBids[0];
        }

        @Override
        protected void onPostExecute(MemberBid memberBid) {
            super.onPostExecute(memberBid);

            if (memberBid.getBitmap1() != null) {
                ActiveBids.get(memberBid.getFinalI()).setBidMainImage(memberBid.getBitmap1());
                if (memberBid.getBitmap2() != null) {
                    ActiveBids.get(memberBid.getFinalI()).setBidSecImage(memberBid.getBitmap2());
                    initiateSingleBid(memberBid.getFinalI());
                } else {
                    ImageLoader.getInstance().loadImage(memberBid.getBidSecImage(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ActiveBids.get(memberBid.getFinalI()).setBidSecImage(loadedImage);
                            saveImageToSaver(memberBid.getBidId(), memberBid.getFinalI(), 2, loadedImage);
                            initiateSingleBid(memberBid.getFinalI());
                        }
                    });
                }
            } else {
                ImageLoader.getInstance().loadImage(memberBid.getBidMainImage(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        ActiveBids.get(memberBid.getFinalI()).setBidMainImage(loadedImage);
                        saveImageToSaver(memberBid.getBidId(), memberBid.getFinalI(), 1, loadedImage);
                        ImageLoader.getInstance().loadImage(memberBid.getBidSecImage(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                ActiveBids.get(memberBid.getFinalI()).setBidSecImage(loadedImage);
                                saveImageToSaver(memberBid.getBidId(), memberBid.getFinalI(), 2, loadedImage);
                                initiateSingleBid(memberBid.getFinalI());
                            }
                        });
                    }
                });
            }
        }
    }

    private void saveImageToSaver(final String bidId, final int mainOrSec, final int index, final Bitmap bitmap) {
        Thread saveThread = new Thread() {
            @Override
            public void run() {
                new ImageSaver(getApplicationContext()).
                        setFileName(bidId + Integer.toString(index) + Integer.toString(mainOrSec) + "glareContent" + ".png").
                        setDirectoryName("FlukyProfiler").
                        save(bitmap);
            }
        };
        saveThread.start();

    }

    private Bitmap loadImageFromSaver(final String bidId, final int mainOrSec, final int index) {
        try {
            return new ImageSaver(getApplicationContext()).
                    setFileName(bidId + Integer.toString(index) + Integer.toString(mainOrSec) + "glareContent" + ".png").
                    setDirectoryName("FlukyProfiler").
                    load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private LinearLayout belowTicketShowView;
    private CardView ccccc, accca, bottom_Card_View;


    private void afterContentLoad() {
        sliderAdapter = new SliderAdapter(pics, 5, new OnCardClickListener());
        initRecyclerView();
        initCountryText();
        initSwitchers();
        int interval = 1000;
        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);

        mLayout = findViewById(R.id.sliding_layout);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        LinearLayout container = findViewById(R.id.layout_inside);

        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);
        final FluidSlider slider = findViewById(R.id.fluidSliderBoom);
        slider.setBubbleText("0");
        slider.setPosition(0);
        belowTicketShowView = findViewById(R.id.BelowTicketShowView);
        ccccc = ((CardView) findViewById(R.id.mainCardBack));
        accca = findViewById(R.id.proceedBid);
        bottom_Card_View = findViewById(R.id.bottom_Card_View);
        cTimers.start();
        updatePanelView();
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                slider.setPosition(0);
                slider.setBubbleText("0");
                accca.setVisibility(View.GONE);
                ccccc.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));

                bottom_Card_View.setAlpha(1 - slideOffset);
                ticketShowView.setAlpha(slideOffset);
                belowTicketShowView.setAlpha(1 - slideOffset);
                if (slideOffset == 1) {
                    SharedPreferences prefs = getSharedPreferences("BidTutorial", MODE_PRIVATE);
                    String restoredText = prefs.getString("done", null);
                    if (restoredText == null) {
                        spotlit2();
                    }
                }
                if (!panelUpForFirstTime) {
                    updatePanelView();
                    panelUpForFirstTime = true;
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED && previousState == SlidingUpPanelLayout.PanelState.DRAGGING && checkReferralValid) {
                    dialogItNow();
                }
                if (newState.toString().equals("COLLAPSED")) {
                    xx = 0;
                    ticketShowView.setVisibility(View.GONE);
                    ticketShowView.setAlpha(0);
                } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING && previousState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    if (ticketShowView.getVisibility() == View.INVISIBLE || ticketShowView.getVisibility() == View.GONE) {
                        ticketShowView.setVisibility(View.VISIBLE);
                    }
                    if (!(findViewById(R.id.scrollviewLine).getAlpha() > 1)) {
                        findViewById(R.id.scrollviewLine).setAlpha(1);
                        findViewById(R.id.ticketCountMain).setAlpha(1);
                    }
                    if (xx == 0) {
                        ticketShowView.setVisibility(View.VISIBLE);
                        xx++;
                    }
                }
                if (previousState == SlidingUpPanelLayout.PanelState.EXPANDED && newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    accca.setVisibility(View.GONE);
                    slider.setPosition(0);
                    slider.setBubbleText("0");
                }
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    yy = 0;
                    slider.setBubbleText("0");
                    slider.setPosition(0);
                    belowTicketShowView.setVisibility(View.GONE);
                    belowTicketShowView.setAlpha(0);
                    showSwipeToolTip();
                } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING && previousState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    if (yy == 0) {
                        belowTicketShowView.setVisibility(View.VISIBLE);
                        yy++;
                    }
                    try {
                        Tooltip.removeAll(MainActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (!stings) {
            start_countdown_timer(endTimes.get(0));
        } else {
            ((TextView) findViewById(R.id.endtimetextviewOne)).setText(String.format("%s referrals to unlock", Integer.toString(howManF)));
        }
        setupToolTip();
    }

    private void showSwipeToolTip() {
        Tooltip.make(MainActivity.this,
                new Tooltip.Builder(101)
                        .anchor(((HorizontalScrollView) findViewById(R.id.earnmorecards)), Tooltip.Gravity.BOTTOM)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 2000)
                        .activateDelay(300)
                        .showDelay(300)
                        .text("Scroll right to view more options")
                        .maxWidth(500)
                        .withArrow(true)
                        .withOverlay(true)
                        .typeface(typefaceBold)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show();
    }

    private void showErr() {
        Sneaker.with(MainActivity.this)
                .setTitle("We can't connect", R.color.white) // Title and title color
                .setMessage("Looks like you don't have a stable internet connection(some part of this app functions offline)", R.color.white) // Message and message color
                .setDuration(6000) // Time duration to show
                .autoHide(true) // Auto hide Sneaker view
                .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                .setHeight(130)// Height of the Sneaker layout
                .sneak(R.color.redd);
    }

    public void proceedBid(View view) {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        TransactBid(entrycount);
    }

    private void TransactBid(Long count) {
        if (count > 0) {
            final int pos = layoutManger.getActiveCardPosition();
            findViewById(R.id.gifViewCardMainDown).setAlpha(1);
            Sneaker.with(this)
                    .setMessage("Checking out " + Long.toString(count) + " fluke(s) on " + countries.get(pos), R.color.white) // Title and title color
                    .setTitle("Hang on!", R.color.white) // Message and message color
                    .setDuration(900000000) // Time duration to show
                    .autoHide(true)
                    .setCornerRadius(16)
                    .setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                    .setHeight(200) // Height of the Sneaker layout
                    .sneak(R.color.colorAccent); // Sneak with background color
            String childB = bidId.get(pos);
            DatabaseReference myRef = database.getReference().child(childB).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Long valueThere = 0L;
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.getValue(Long.class) != null) {
                            valueThere = dataSnapshot.getValue(Long.class);
                        }
                    }
                    Long finalValueThere = count;
                    if (valueThere != null)
                        finalValueThere = valueThere + count;

                    Long finalValueThere1 = finalValueThere;
                    myRef.setValue(finalValueThere1)
                            .addOnSuccessListener(aVoid -> {
                                Sneaker.hide();
                                findViewById(R.id.gifViewCardMainDown).setAlpha(0);

                                nl.dionsegijn.konfetti.KonfettiView viewKonfetti = findViewById(R.id.viewKonfettiDialog);
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

                                Answers.getInstance().logStartCheckout(new StartCheckoutEvent()
                                        .putItemCount(count.intValue()));

                                Sneaker.with(MainActivity.this)
                                        .setTitle("Success", R.color.white) // Title and title color
                                        .setMessage("You've got " + count + " entries!", R.color.white) // Message and message color
                                        .setDuration(2000) // Time duration to show
                                        .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                                        .autoHide(true) // Auto hide Sneaker view
                                        .setHeight(200) // Height of the Sneaker layout
                                        .sneak(R.color.successfulu); // Sneak with background color
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                DocumentReference perkDoc = db.collection("user").document(firebaseUser.getUid());
                                perkDoc.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        int val = Integer.parseInt(doc.get("Sert").toString());
                                        DocumentReference contact = db.collection("user").document(firebaseUser.getUid());
                                        Long xxxxxxx = count + Long.parseLong(doc.get("TotalEntries").toString());
                                        contact.update("TotalEntries", Long.toString(xxxxxxx));
                                        contact.update("Sert", val - count)
                                                .addOnSuccessListener(aaVoid -> {
                                                    DocumentReference ffff = perkDoc.collection("History").document(childB);

                                                    Map<String, Object> newPhone = new HashMap<>();
                                                    newPhone.put(childB, finalValueThere1);
                                                    ffff.set(newPhone)
                                                            .addOnSuccessListener(fffffff -> {
                                                                bidCount.put(childB, Long.toString(finalValueThere1));
                                                                if (layoutManger.getActiveCardPosition() == pos) {
                                                                    tickerView.setText(bidCount.get(childB));
                                                                }
                                                            });
                                                });
                                    }
                                })
                                        .addOnFailureListener(e -> {
                                            showErr();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Sneaker.hide();
                                findViewById(R.id.gifViewCardMainDown).setAlpha(0);
                                Sneaker.with(MainActivity.this)
                                        .setTitle("Failed", R.color.white) // Title and title color
                                        .setMessage("Something went wrong. Atleast you haven't lost anything", R.color.white) // Message and message color
                                        .setDuration(4000) // Time duration to show
                                        .autoHide(true) // Auto hide Sneaker view
                                        .setHeight(300)
                                        .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                                        .sneak(Color.RED);
                            });
                    ;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    // ...
                }
            };
            myRef.addListenerForSingleValueEvent(postListener);
        }
    }


    private void initiateFluidSlider(int max) {
        if (max > 25) max = 25;
        if (max < 0) max = 0;
        final int min = 0;
        final int total = max - min;

        final FluidSlider slider = findViewById(R.id.fluidSliderBoom);

        slider.setBubbleText("0");
        slider.setBeginTrackingListener(() -> {
            (findViewById(R.id.textViewFluid)).setVisibility(View.GONE);
            return Unit.INSTANCE;
        });

        slider.setEndTrackingListener(() -> {
            if (slider.getPosition() != 0) {
                accca.setVisibility(View.VISIBLE);
                String tt = "Check out " + countries.get(layoutManger.getActiveCardPosition());
                ((TextView) findViewById(R.id.proceedBidText)).setText(tt);
                ccccc.setCardBackgroundColor(getResources().getColor(R.color.one));
            } else if (slider.getBubbleText() != null) {
                if (slider.getBubbleText().equals("0")) {
                    accca.setVisibility(View.GONE);
                    ccccc.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
            }
            ((TextView) findViewById(R.id.textViewFluid)).setText("Use & Win");
            (findViewById(R.id.textViewFluid)).setVisibility(View.VISIBLE);
            return Unit.INSTANCE;
        });

        // Java 8 lambda
        slider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (min + total * pos));
            entrycount = Long.parseLong(value);
            slider.setBubbleText(value);
            return Unit.INSTANCE;
        });

        slider.setPosition(0.3f);
        slider.setStartText("");
        slider.setEndText("");
    }

    private void updatePanelView() {
        isloadingBack(true);
        serts = "#";
        refCode = "--";

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference perkDoc = db.collection("user").document(firebaseUser.getUid());
        perkDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                serts = doc.get("Sert").toString();
                if (doc.get("ref") != null) {
                    refCode = doc.get("ref").toString();
                }
                if (refCode.equals("--")) {
                    shareJammed = true;
                }
                initiateFluidSlider(Integer.parseInt(serts));
                if (!(serts.equals("#") && refCode.equals("--"))) {
                    findViewById(R.id.scrollviewLine).setVisibility(View.VISIBLE);
                    handler4.postAtTime(runnable4, System.currentTimeMillis() + 500);
                    handler4.postDelayed(runnable4, 50);
                }

            }
        })
                .addOnFailureListener(e -> {
                    showErr();
                });
    }

    private RewardedVideoAd mRewardedVideoAd;

    private void beforeContentLoad() {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        isLoading(true);

        tickerView = findViewById(R.id.entrrcount);
        tickerView.setCharacterLists(TickerUtils.provideNumberList());
        tickerView.setText("...");

        ticketShowView = findViewById(R.id.ticketShowView);
        OverScrollDecoratorHelper.setUpOverScroll(ticketShowView);

        tickInside = findViewById(R.id.earnmorecards);
        OverScrollDecoratorHelper.setUpOverScroll(tickInside);

        main_buying_view = findViewById(R.id.main_buying_view);
        OverScrollDecoratorHelper.setUpOverScroll(main_buying_view);

        main_item_share_ad = findViewById(R.id.main_item_share_ad);

        OverScrollDecoratorHelper.setUpStaticOverScroll(((RelativeLayout) findViewById(R.id.sliding_layoutDown)), OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        OverScrollDecoratorHelper.setUpStaticOverScroll(((CardView) findViewById(R.id.temp65)), OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        OverScrollDecoratorHelper.setUpStaticOverScroll(((CardView) findViewById(R.id.bottom_Card_View)), OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        OverScrollDecoratorHelper.setUpStaticOverScroll(((RelativeLayout) findViewById(R.id.temp645)), OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        OverScrollDecoratorHelper.setUpStaticOverScroll(((SlidingUpPanelLayout) findViewById(R.id.sliding_layout)), OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        panelUpForFirstTime = false;
        entrycount = 0L;
        bidCount = new HashMap<>();
        endTimes = new ArrayList<>();
        pics = new ArrayList<>();
        maps = new ArrayList<>();
        maps = new ArrayList<>();
        pics = new ArrayList<>();
        countries = new ArrayList<>();
        bidDescription = new ArrayList<>();
        bidId = new ArrayList<>();
        bidInfoUrl = new ArrayList<>();
        ActiveBids = new ArrayList<>();
        bidDescription.clear();
        bidInfoUrl.clear();
        bidId.clear();
        countries.clear();

        ActiveBids.add(new ActiveBid());
        ActiveBids.add(new ActiveBid());
        ActiveBids.add(new ActiveBid());
        ActiveBids.add(new ActiveBid());
        ActiveBids.add(new ActiveBid());

        SuccessfulBidLoaded = 0;

        initializeImageLoader();

        ticketShowView.setVisibility(View.GONE);

        toolbar = findViewById(R.id.toolbarHome);
        toolbar.setJellyListener(jellyListener);

        intent = new Intent(this, MainActivity.class);
        intent.putExtra("Package", "Me");

        LinearLayout lin = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.main_layout_tab, null);
        lin.setBackgroundResource(R.color.colorTransparent);

        toolbar.setContentView(lin);

        findViewById(R.id.homeUn).setAlpha(1.0F);
        findViewById(R.id.homeUn).setBackgroundResource(R.drawable.rounded_tab);

        initiateDummyData();

    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange();
                }
            }
        });

        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

        new CardSnapHelper().attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onPause() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.resume(this);
        super.onPause();
        if (isFinishing() && decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true);
        }
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    @Override
    protected void onDestroy() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.resume(this);
        if (bp != null)
            bp.release();
        finish();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void initSwitchers() {

        mapSwitcher = findViewById(R.id.ts_map);
        mapSwitcher.setInAnimation(this, R.anim.fade_in);
        mapSwitcher.setOutAnimation(this, R.anim.fade_out);
        mapSwitcher.setFactory(new ImageViewFactory());
        Drawable drawable = new BitmapDrawable(maps.get(0));
        mapSwitcher.setImageDrawable(drawable);

        mapLoadListener = bitmap -> {
            ((ImageView) mapSwitcher.getNextView()).setImageBitmap(bitmap);
            mapSwitcher.showNext();
        };
    }

    private void initCountryText() {
        countryAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        countryOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
        countryOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
        country1TextView = findViewById(R.id.tv_country_1);
        country2TextView = findViewById(R.id.tv_country_2);

        country1TextView.setX(countryOffset1);
        country2TextView.setX(countryOffset2);
        country1TextView.setText(countries.get(0));
        tickerView.setText(bidCount.get(bidId.get(0)));
        country2TextView.setAlpha(0f);
    }

    private void setCountryText(String text, boolean left2right) {
        final TextView invisibleText;
        final TextView visibleText;
        if (country1TextView.getAlpha() > country2TextView.getAlpha()) {
            visibleText = country1TextView;
            invisibleText = country2TextView;
        } else {
            visibleText = country2TextView;
            invisibleText = country1TextView;
        }

        final int vOffset;
        if (left2right) {
            invisibleText.setX(0);
            vOffset = countryOffset2;
        } else {
            invisibleText.setX(countryOffset2);
            vOffset = 0;
        }

        invisibleText.setText(text);

        final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
        final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
        final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", countryOffset1);
        final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(iAlpha, vAlpha, iX, vX);
        animSet.setDuration(countryAnimDuration);
        animSet.start();
    }

    private void onActiveCardChange() {
        findViewById(R.id.sliding_layoutDown).setVisibility(View.VISIBLE);
        final int pos = layoutManger.getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }
        toolbar.collapse();
        onActiveCardChange(pos);

        if (referralCodeValidHai || firstTimeUserBonus) {
            referralCodeValidGo();
        }
        if (ProfileActivity.earnedNow) {
            Sneaker.with(this)
                    .setTitle("Congrats", R.color.white) // Title and title color
                    .setMessage(ProfileActivity.earnedBadge, R.color.white) // Message and message color
                    .setDuration(2000) // Time duration to show
                    .autoHide(true)
                    .setIcon(ProfileActivity.whichBadge)
                    .setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                    .setHeight(100) // Height of the Sneaker layout
                    .sneak(R.color.successfulu);
            ProfileActivity.earnedNow = false;
        }

    }

    private void onActiveCardChange(int pos) {
        if (!alwaysStings) {
            try {
                mCountDownTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            start_countdown_timer(endTimes.get(pos));
        } else {
            stings = true;
            ((TextView) findViewById(R.id.endtimetextviewOne)).setText(String.format("%s referrals to unlock", Integer.toString(howManF)));
        }
        tickerView.setText(bidCount.get(bidId.get(pos)));

        ((TextView) findViewById(R.id.textViewFluid)).setText("How much you'll use?");
        int animH[] = new int[]{R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[]{R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

        setCountryText(countries.get(pos % countries.size()), left2right);

        showMap(maps.get(pos % maps.size()));

        currentPosition = pos;
    }

    private void showMap(Bitmap bitmap) {
        if (decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true);
        }

        final int w = mapSwitcher.getWidth();
        final int h = mapSwitcher.getHeight();

        decodeMapBitmapTask = new DecodeBitmapTask(getResources(), bitmap, w, h, mapLoadListener);
        decodeMapBitmapTask.execute();
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

    private Handler handler4 = new Handler();
    private Runnable runnable4 = () -> {


        ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);

        ((TextView) findViewById(R.id.referralCode)).setText(refCode);
        isloadingBack(false);

        animator1.addUpdateListener(valueAnimator -> {
            float x = (Integer) valueAnimator.getAnimatedValue() / (float) 10;
            ((TextView) findViewById(R.id.ticketCountMain)).setAlpha(x);
            if (x == 0.0) {
                ValueAnimator animator2 = ValueAnimator.ofInt(10, 0);
                ((TextView) findViewById(R.id.ticketCountMain)).setText(serts);

                animator2.addUpdateListener(valueAnimator2 -> {
                    float xy = (Integer) valueAnimator.getAnimatedValue() / (float) 10;
                    ((TextView) findViewById(R.id.ticketCountMain)).setAlpha(1f - xy);
                });

                animator2.setDuration(500);
                animator2.start();
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

    private Handler handler5 = new Handler();
    private Runnable runnable5 = () -> {

        ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);
        main_buying_view.setVisibility(View.VISIBLE);

        animator1.addUpdateListener(valueAnimator -> {
            float x = (Integer) valueAnimator.getAnimatedValue() / (float) 10;
            tickInside.setAlpha(x);
            main_buying_view.setAlpha(1f - x);
            if ((Integer) valueAnimator.getAnimatedValue() == 0) {
                tickInside.setVisibility(View.GONE);
            }
        });

        animator1.setDuration(500);
        animator1.start();
    };

    private Handler handler6 = new Handler();
    private Runnable runnable6 = () -> {

        ValueAnimator animator1 = ValueAnimator.ofInt(10, 0);

        tickInside.setVisibility(View.VISIBLE);

        animator1.addUpdateListener(valueAnimator -> {

            float x = (Integer) valueAnimator.getAnimatedValue() / (float) 10;
            tickInside.setAlpha(1f - x);
            main_buying_view.setAlpha(x);
            if ((Integer) valueAnimator.getAnimatedValue() == 0) {
                tickInside.setVisibility(View.VISIBLE);
                main_buying_view.setVisibility(View.GONE);
            }
        });

        animator1.setDuration(500);
        animator1.start();
    };


    public void shareNow(View view) {

        String msg = "I'm inviting you to use Fluke, a simple and free way to win amazing gifts. Here my code: (" + refCode + ")." +
                " Just enter it while signing up. You'll be getting extra benefits instantly. https://fluke.ml/app";

        if (shareJammed) {
            msg = "I'm inviting you to use Fluke, a simple and free way to win cool gifts. https://fluke.ml/app";
        }

        try {
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
            panelUpForFirstTime = false;
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            startActivity(sendIntent);
        }
    }

    public void shareLoad(View view) {
            handler2.postAtTime(runnable2, System.currentTimeMillis() + 500);
            handler2.postDelayed(runnable2, 50);
            if (!shareJammed) {
                Tooltip.make(MainActivity.this,
                        new Tooltip.Builder(101)
                                .anchor(((CardView) findViewById(R.id.sharemainHomeLayout)), Tooltip.Gravity.BOTTOM)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, false)
                                        .outsidePolicy(true, false), 8000)
                                .activateDelay(300)
                                .showDelay(300)
                                .text("Share your referral code and earn 10 flukes per signup")
                                .maxWidth(500)
                                .withArrow(true)
                                .withOverlay(true)
                                .typeface(typefaceBold)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                ).show();
            } else {
                Tooltip.make(MainActivity.this,
                        new Tooltip.Builder(101)
                                .anchor(((CardView) findViewById(R.id.sharemainHomeLayout)), Tooltip.Gravity.BOTTOM)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, false)
                                        .outsidePolicy(true, false), 8000)
                                .activateDelay(300)
                                .showDelay(300)
                                .text("Update your profile now to earn by sharing")
                                .maxWidth(500)
                                .withArrow(true)
                                .withOverlay(true)
                                .typeface(typefaceBold)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                ).show();
            }
    }

    public void shareLoadBack(View view) {
        handler3.postAtTime(runnable3, System.currentTimeMillis() + 500);
        handler3.postDelayed(runnable3, 50);
    }

    public void boostLoad(View view) {
        new PiracyChecker(this)
                .display(piracyCheckerDisplay)
                .enableUnauthorizedAppsCheck(true)
                .blockIfUnauthorizedAppUninstalled("license_checker", "block101")
                .start();
        handler5.postAtTime(runnable5, System.currentTimeMillis() + 500);
        handler5.postDelayed(runnable5, 50);

        Tooltip.make(MainActivity.this,
                new Tooltip.Builder(101)
                        .anchor(((CardView) findViewById(R.id.boostmainhomeLayout)), Tooltip.Gravity.BOTTOM)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 8000)
                        .activateDelay(300)
                        .showDelay(300)
                        .text("You can top up your fluke balance here")
                        .maxWidth(500)
                        .withArrow(true)
                        .withOverlay(true)
                        .typeface(typefaceBold)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show();
    }

    public void boostLoadBack(View view) {
        handler6.postAtTime(runnable6, System.currentTimeMillis() + 500);
        handler6.postDelayed(runnable6, 50);
    }

    private void isloadingBack(boolean isloading) {
        if (isloading) {
            findViewById(R.id.isloadingBackCard).setVisibility(View.VISIBLE);
        } else findViewById(R.id.isloadingBackCard).setVisibility(View.GONE);
    }

    private String adId;

    private void loadRewardedVideoAd() {
        adId = "ca-app-pub-3648291950784680/5413622092";
//        adId = "ca-app-pub-3940256099942544/5224354917";
        findViewById(R.id.textssad).setAlpha(0.3f);
        findViewById(R.id.imgssad).setAlpha(0.3f);
        mRewardedVideoAd.loadAd(adId,
                new AdRequest.Builder().build());
    }

    public void showAd(View view) {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        findViewById(R.id.textssad).setAlpha(1f);
        findViewById(R.id.imgssad).setAlpha(1f);
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
        GeneratatedUser.addSerts(2);
        Sneaker.with(this)
                .setTitle("Verifying", R.color.white) // Title and title color
                .setMessage("", R.color.white) // Message and message color
                .setDuration(900000) // Time duration to show
                .autoHide(true)
                .setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
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
                    .setHeight(200)
                    .setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message// Height of the Sneaker layout
                    .sneak(R.color.successfulu);
        });

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        findViewById(R.id.textssad).setAlpha(0.3f);
        findViewById(R.id.imgssad).setAlpha(0.3f);
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    private void addRealtimeUpdateSert() {
        DocumentReference perkDoc = db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        perkDoc.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.d("ERROR", e.getMessage());
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                updatePanelView();
            }
        });
    }

    public void showQuiz(View view) {
        try {
            try {
                panelUpForFirstTime = false;
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            } catch (NullPointerException dd) {
                dd.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent j = new Intent(this, QuizLoading.class);
            j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ticketShowView.setVisibility(View.VISIBLE);
            startActivity(j);
            for (int i = 0; i < 5; i++) {
                maps.get(i).recycle();
                pics.get(i).recycle();
                ActiveBids.get(i).recycleBmp1();
                ActiveBids.get(i).recycleBm2();
            }
            maps.clear();
            pics.clear();
            pics = null;
            maps = null;
            findViewById(R.id.sliding_layoutDown).setVisibility(View.GONE);
            finish();
        } catch (RuntimeException es) {
            es.printStackTrace();
        }
    }


    private class ImageViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            final ImageView imageView = new ImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final LayoutParams lp = new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);

            return imageView;
        }
    }

    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final CardSliderLayoutManager lm = (CardSliderLayoutManager) recyclerView.getLayoutManager();
            if (lm != null && lm.isSmoothScrolling()) {
                return;
            }

            final int activeCardPosition = lm.getActiveCardPosition();
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int clickedPosition = recyclerView.getChildAdapterPosition(view);
            if (clickedPosition == activeCardPosition) {
                final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                currentDes = bidDescription.get(clickedPosition);
                currentBit = pics.get(clickedPosition);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                panelUpForFirstTime = false;
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                startActivity(intent);
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
                onActiveCardChange(clickedPosition);
            }
        }

    }


    private JellyListener jellyListener = new JellyListener() {
        @Override
        public void onCancelIconClicked() {
            toolbar.collapse();
        }

        @Override

        public void onToolbarExpandingStarted() {
            super.onToolbarExpandingStarted();
        }
    };

    public void imoneClick(View view) {
        //do nothing
    }

    public void imtwoClick(View view) {
        intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("Package", "Profile");
        toolbar.collapse();
        transi();
    }

    public void imthreeClick(View view) {
        intent = new Intent(this, SocialActivity.class);
        intent.putExtra("Package", "Social");
        toolbar.collapse();
        transi();
    }

    public void transi() {

        Bundle b = intent.getExtras();

        assert b != null;
        if (!Objects.requireNonNull(b.get("Package")).toString().equals("Me")) {
            for (int i = 0; i < 5; i++) {
                maps.get(i).recycle();
                pics.get(i).recycle();
                ActiveBids.get(i).recycleBmp1();
                ActiveBids.get(i).recycleBm2();
            }
            maps.clear();
            pics.clear();
            pics = null;
            maps = null;
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            panelUpForFirstTime = false;
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            findViewById(R.id.sliding_layoutDown).setVisibility(View.GONE);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        try {
            if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            else {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void spotlit() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        int[] locationOne = new int[2];
        (findViewById(R.id.coordinatorone)).getLocationOnScreen(locationOne);
        SimpleTarget firstTarget = new SimpleTarget.Builder(this)
                .setPoint(locationOne[0], locationOne[1])
                .setShape(new Circle(width / 3))
                .setTitle("Timer")
                .setDescription("Participate by the timer ends")
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
                .setPoint(width, height / 2)
                .setShape(new Circle(height / 6))
                .setTitle("Catalog")
                .setDescription("Browse winnable things")
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

        SimpleTarget thirdTarget = new SimpleTarget.Builder(this)
                .setPoint(width / 2, height)
                .setShape(new Circle(width / 3f))
                .setTitle("Proceed")
                .setDescription("Swipe Up & win it")
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


        SimpleTarget fourthTarget = new SimpleTarget.Builder(this)
                .setPoint(width, 0f)
                .setShape(new Circle(height / 4))
                .setTitle("Profile & features")
                .setDescription("Navigate to your stats")
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
                .setTargets(secondTarget, firstTarget, thirdTarget, fourthTarget)
                .setClosedOnTouchedOutside(true)
                .setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onEnded() {
                        SharedPreferences.Editor editor = getSharedPreferences("HomeTutorial", MODE_PRIVATE).edit();
                        editor.putString("done", "ele");
                        editor.apply();
                    }
                })
                .start();
    }

    public void spotlit2() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        int[] locationOne = new int[2];
        (findViewById(R.id.coordinatortwo)).getLocationOnScreen(locationOne);
        SimpleTarget firstTarget = new SimpleTarget.Builder(this)
                .setPoint(width / 2, -height / 3)
                .setShape(new Circle(height / 1.5f))
                .setTitle("Balance")
                .setDescription("Number of balls indicates remaining balance(Flukes)")
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
                .setPoint(width / 2, height + height / 3)
                .setShape(new Circle(height / 1.5f))
                .setTitle("Confirm Participation")
                .setDescription("Use balance & wait for result. \nEasy :)")
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
                .setTargets(firstTarget, secondTarget)
                .setClosedOnTouchedOutside(true)
                .setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onEnded() {
                        SharedPreferences.Editor editor = getSharedPreferences("BidTutorial", MODE_PRIVATE).edit();
                        editor.putString("done", "ele");
                        editor.apply();
                        if (stings) {
                            stings = false;
                            Sneaker.with(MainActivity.this)
                                    .setTitle("This product is locked", R.color.white) // Title and title color
                                    .setMessage("No worries, you can still participate; we'll just hide the deadline from you.\nUnlock by referring Fluke to 6 peoples", R.color.white) // Message and message color
                                    .setDuration(5000) // Time duration to show
                                    .autoHide(true)
                                    .setTypeface(typefaceBold) // Custom font for title and message
                                    .setHeight(150) // Height of the Sneaker layout
                                    .sneak(R.color.three);
                        }
                    }
                })
                .start();
    }


    private void initiateSingleBid(int index) {
        if (ActiveBids.get(index).getIS_BID_COMPLETE() == 0) {
            ActiveBids.get(index).setIS_BID_COMPLETE(1);
            countries.add(SuccessfulBidLoaded, ActiveBids.get(index).getBidHeading());
            endTimes.add(SuccessfulBidLoaded, ActiveBids.get(index).getEndTime());
            maps.add(SuccessfulBidLoaded, ActiveBids.get(index).getBidSecImage());
            pics.add(SuccessfulBidLoaded, ActiveBids.get(index).getBidMainImage());
            bidDescription.add(SuccessfulBidLoaded, ActiveBids.get(index).getBidDescription());
            bidId.add(SuccessfulBidLoaded, ActiveBids.get(index).getBidId());
            bidInfoUrl.add(SuccessfulBidLoaded, ActiveBids.get(index).getBidInfoUrl());
            SuccessfulBidLoaded++;
        }
        if (SuccessfulBidLoaded == 5) {
            afterContentLoad();
            GeneratatedUser.checkReferral();
        }
    }

    private void initiateDummyData() {
        for (int i = 0; i < 5; i++) {
            countries.add("Processing");
            pics.add(null);
            maps.add(null);
            bidInfoUrl.add("flyky.ml");
            bidDescription.add("fluky.ml");
            bidId.add("fluky.ml");
        }
    }

    private void isLoading(boolean isLoading) {
        ProgressBar pbar = findViewById(R.id.loading_spinnerMain);
        if (isLoading) {
            pbar.setAlpha(1);
        } else {
            pbar.setAlpha(0);
        }
    }

    private void initializeImageLoader() {
        if (!StaticsMain.getInstance().isImageLoaderInitialized) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .build();
            ImageLoader.getInstance().init(config);
            ImageLoader.getInstance().clearMemoryCache();
            ImageLoader.getInstance().clearDiskCache();
        }
        StaticsMain.getInstance().isImageLoaderInitialized = true;
    }


    private BillingProcessor bp;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean purchasejammed = false;

    public void onClickPurch(View v) {
        new PiracyChecker(this)
                .display(piracyCheckerDisplay2)
                .enableUnauthorizedAppsCheck(true)
                .blockIfUnauthorizedAppUninstalled("license_checker", "block101")
                .start();

        if (!purchasejammed) {
            purchasejammed = true;
            isloadingBack(true);
            String childB = "unspecified";
            int atu = 1;
            switch (v.getId()) {
                case R.id.purchase10:
                    childB = "ugfi1";
                    atu = 11;
                    break;
                case R.id.purchase100:
                    childB = "ugfi2";
                    atu = 101;
                    break;
                case R.id.purchase500:
                    childB = "ugfi3";
                    atu = 501;
                    break;
                case R.id.purchase1000:
                    childB = "ugfi4";
                    atu = 1001;
                    break;
                case R.id.purchase3000:
                    childB = "ugfi5";
                    atu = 3001;
                    break;
            }
            DocumentReference user = db.collection("UGFI").document("UGFIs");
            String finalChildB = childB;
            int finalAtu = atu;
            user.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        if (!BillingProcessor.isIabServiceAvailable(getApplicationContext())) {
                            Sneaker.with(MainActivity.this)
                                    .setDuration(5000)
                                    .setHeight(300)
                                    .setTitle("Error!!")
                                    .setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                                    .setMessage("Something went wrong! Please try again later...")
                                    .sneakError();
                            isloadingBack(false);
                        }
                        bp = new BillingProcessor(getApplicationContext(), doc.get("ugfi").toString(), new BillingProcessor.IBillingHandler() {
                            @Override
                            public void onProductPurchased(String productId, TransactionDetails details) {
                                isloadingBack(true);
                                bp.consumePurchase(doc.get("ugfi1").toString());
                                bp.consumePurchase(doc.get("ugfi2").toString());
                                bp.consumePurchase(doc.get("ugfi3").toString());
                                bp.consumePurchase(doc.get("ugfi4").toString());
                                bp.consumePurchase(doc.get("ugfi5").toString());
                                Sneaker.with(MainActivity.this)
                                        .setTitle("Verifying", R.color.white) // Title and title color
                                        .setMessage("Hang on, while we process your request.", R.color.white) // Message and message color
                                        .setDuration(1000) // Time duration to sho
                                        .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                                        .autoHide(true) // Auto hide Sneaker view
                                        .setHeight(200) // Height of the Sneaker layout
                                        .sneak(R.color.three);
                                GeneratatedUser.addSerts(finalAtu);

                                int price = 0;
                                switch (v.getId()) {
                                    case R.id.purchase10:
                                        price = 10;
                                        break;
                                    case R.id.purchase100:
                                        price = 21;
                                        break;
                                    case R.id.purchase500:
                                        price = 48;
                                        break;
                                    case R.id.purchase1000:
                                        price = 55;
                                        break;
                                    case R.id.purchase3000:
                                        price = 66;
                                        break;
                                }

                                Answers.getInstance().logPurchase(new PurchaseEvent()
                                        .putItemPrice(BigDecimal.valueOf(price))
                                        .putCurrency(Currency.getInstance("INR"))
                                        .putItemName(details.toString())
                                        .putItemType("Donate")
                                        .putItemId(productId)
                                        .putSuccess(true));

                                DatabaseReference myRef = database.getReference().child("Product").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(details.toString());
                                myRef.setValue(finalAtu).addOnSuccessListener(f -> {
                                    Sneaker.hide();
                                    Sneaker.with(MainActivity.this)
                                            .setTitle("Success", R.color.white) // Title and title color
                                            .setMessage("Thanks for purchasing...", R.color.white) // Message and message color
                                            .setDuration(1000) // Time duration to show
                                            .autoHide(true) // Auto hide Sneaker view
                                            .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                                            .setHeight(200) // Height of the Sneaker layout
                                            .sneak(R.color.successfulu);
                                });
                                bp.consumePurchase(doc.get("ugfi1").toString());
                                bp.consumePurchase(doc.get("ugfi2").toString());
                                bp.consumePurchase(doc.get("ugfi3").toString());
                                bp.consumePurchase(doc.get("ugfi4").toString());
                                bp.consumePurchase(doc.get("ugfi5").toString());
                                isloadingBack(false);
                                purchasejammed = false;
                            }

                            @Override
                            public void onBillingError(int errorCode, Throwable error) {
                                isloadingBack(false);
                                purchasejammed = false;
                            }

                            @Override
                            public void onBillingInitialized() {
                                bp.consumePurchase(doc.get("ugfi1").toString());
                                bp.consumePurchase(doc.get("ugfi2").toString());
                                bp.consumePurchase(doc.get("ugfi3").toString());
                                bp.consumePurchase(doc.get("ugfi4").toString());
                                bp.consumePurchase(doc.get("ugfi5").toString());
                                bp.purchase(MainActivity.this, doc.get(finalChildB).toString());
                            }

                            @Override
                            public void onPurchaseHistoryRestored() {

                            }
                        });

                    }
                }
            })
                    .addOnFailureListener(e -> {
                        isloadingBack(false);
                        showErr();
                        purchasejammed = false;
                    });
        }
    }


    private Long startTime;
    private CountDownTimer mCountDownTimer;

    private void start_countdown_timer(String endTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        formatter.setLenient(false);


        long milliseconds = 0;

        Date endDate;
        try {
            endDate = formatter.parse(endTime);
            milliseconds = endDate.getTime();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        startTime = System.currentTimeMillis();

        mCountDownTimer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                startTime = startTime - 1;
                Long serverUptimeSeconds =
                        (millisUntilFinished - startTime) / 1000;

                Long date = Long.parseLong(String.format("%d", serverUptimeSeconds / 86400));
                if (date <= 1) {
                    ((CardView) findViewById(R.id.endtimetextviewCard)).setCardBackgroundColor(getResources().getColor(R.color.redd));
                } else {
                    ((CardView) findViewById(R.id.endtimetextviewCard)).setCardBackgroundColor(getResources().getColor(R.color.darkbluess));
                }
                String timesss = date +
                        " d:" + String.format("%d", (serverUptimeSeconds % 86400) / 3600) +
                        " hr:" + String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60) +
                        " min:" + String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60) + " sec";

                ((TextView) findViewById(R.id.endtimetextviewOne)).setText(timesss);
            }

            @Override
            public void onFinish() {

            }
        }.start();


    }

    private void setupToolTip() {
        if (!stings) {
            ((CardView) findViewById(R.id.endtimetextviewCard)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tooltip.make(MainActivity.this,
                            new Tooltip.Builder(101)
                                    .anchor(((CardView) findViewById(R.id.endtimetextviewCard)), Tooltip.Gravity.TOP)
                                    .closePolicy(new Tooltip.ClosePolicy()
                                            .insidePolicy(true, false)
                                            .outsidePolicy(true, false), 3000)
                                    .activateDelay(300)
                                    .showDelay(300)
                                    .text("Timer by which participation is open")
                                    .maxWidth(500)
                                    .withArrow(true)
                                    .withOverlay(true)
                                    .typeface(typefaceBold)
                                    .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                    .build()
                    ).show();
                }
            });
        } else {
            ((CardView) findViewById(R.id.endtimetextviewCard)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tooltip.make(MainActivity.this,
                            new Tooltip.Builder(101)
                                    .anchor(((CardView) findViewById(R.id.endtimetextviewCard)), Tooltip.Gravity.TOP)
                                    .closePolicy(new Tooltip.ClosePolicy()
                                            .insidePolicy(true, false)
                                            .outsidePolicy(true, false), 5000)
                                    .activateDelay(300)
                                    .showDelay(300)
                                    .text("Refer this app to 6 people to unlock. Swipe up, select \"share and earn\" to do so")
                                    .maxWidth(500)
                                    .withArrow(true)
                                    .withOverlay(true)
                                    .typeface(typefaceBold)
                                    .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                    .build()
                    ).show();
                }
            });
        }

        ((CardView) findViewById(R.id.entrrcountCard)).setOnClickListener(view -> Tooltip.make(MainActivity.this,
                new Tooltip.Builder(101)
                        .anchor(((CardView) findViewById(R.id.entrrcountCard)), Tooltip.Gravity.LEFT)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 3000)
                        .activateDelay(300)
                        .showDelay(300)
                        .text("Number of times you've participated")
                        .maxWidth(500)
                        .withArrow(true)
                        .withOverlay(true)
                        .typeface(typefaceBold)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show());

        ((RelativeLayout) findViewById(R.id.howmnayticket)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tooltip.make(MainActivity.this,
                        new Tooltip.Builder(101)
                                .anchor(((RelativeLayout) findViewById(R.id.howmnayticket)), Tooltip.Gravity.LEFT)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, false)
                                        .outsidePolicy(true, false), 3000)
                                .activateDelay(300)
                                .showDelay(300)
                                .text("Your fluke balance")
                                .maxWidth(500)
                                .withArrow(true)
                                .withOverlay(true)
                                .typeface(typefaceBold)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                ).show();
            }
        });

        ((CardView) findViewById(R.id.quizMainHomeLayout)).setOnLongClickListener(view -> {
            Tooltip.make(MainActivity.this,
                    new Tooltip.Builder(101)
                            .anchor(((CardView) findViewById(R.id.quizMainHomeLayout)), Tooltip.Gravity.BOTTOM)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 3000)
                            .activateDelay(300)
                            .showDelay(300)
                            .text("Play quiz everyday at 7 PM and win lots of Flukes")
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .typeface(typefaceBold)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
            ).show();
            return false;
        });


        ((CardView) findViewById(R.id.admainhomelayout)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Tooltip.make(MainActivity.this,
                        new Tooltip.Builder(101)
                                .anchor(((CardView) findViewById(R.id.admainhomelayout)), Tooltip.Gravity.BOTTOM)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, false)
                                        .outsidePolicy(true, false), 3000)
                                .activateDelay(300)
                                .showDelay(300)
                                .text("We'll reward you for watching ad at 10 flukes/hr")
                                .maxWidth(500)
                                .withArrow(true)
                                .withOverlay(true)
                                .typeface(typefaceBold)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                ).show();
                return false;
            }
        });
    }

    private void showTipsInactive() {
        if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {

            Tooltip.make(MainActivity.this,
                    new Tooltip.Builder(101)
                            .anchor(((CardView) findViewById(R.id.entrrcountCard)), Tooltip.Gravity.TOP)
                            .activateDelay(300)
                            .showDelay(300)
                            .text("Number of times you've participated")
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .typeface(typefaceBold)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
            ).show();

            Tooltip.make(MainActivity.this,
                    new Tooltip.Builder(101)
                            .anchor(bottom_Card_View, Tooltip.Gravity.TOP)
                            .activateDelay(300)
                            .showDelay(300)
                            .text("Slide up from here to proceed")
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .typeface(typefaceBold)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
            ).show();
        } else if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            Tooltip.make(MainActivity.this,
                    new Tooltip.Builder(101)
                            .anchor(bottom_Card_View, Tooltip.Gravity.TOP)
                            .activateDelay(300)
                            .showDelay(300)
                            .text("Select the number of entries you want and tap here to confirm participation")
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .typeface(typefaceBold)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
            ).show();

            Tooltip.make(MainActivity.this,
                    new Tooltip.Builder(101)
                            .anchor(findViewById(R.id.earnmorecards), Tooltip.Gravity.TOP)
                            .activateDelay(300)
                            .showDelay(300)
                            .text("Scroll right to view more options")
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .typeface(typefaceBold)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
            ).show();
        }
    }
}