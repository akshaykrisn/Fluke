package app.fluky.ml.fluk.frags;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.RatingEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.irozon.sneaker.Sneaker;
import com.kobakei.ratethisapp.RateThisApp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.jellytoolbar.listener.JellyListener;
import com.yalantis.jellytoolbar.widget.JellyToolbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.MainActivity;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import app.fluky.ml.fluk.complex.utils.ImageSaver;
import app.fluky.ml.fluk.dialog.CustomAlertDialogue;
import app.fluky.ml.fluk.frags.utils2.UserHistoryAdapter;
import app.fluky.ml.fluk.frags.utils2.user_entry_history_item;
import app.fluky.ml.fluk.tooltip.Tooltip;
import app.fluky.ml.fluk.xtras.HistoryUser;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class ProfileActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        try {
            if (((SlidingUpPanelLayout)findViewById(R.id.sliding_layout_profile)).getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
                ((SlidingUpPanelLayout)findViewById(R.id.sliding_layout_profile)).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            else {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final int interval = 1000; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable = () -> {


        ValueAnimator animator1 = ValueAnimator.ofInt(35, 0);

        animator1.addUpdateListener(valueAnimator -> {
            findViewById(R.id.profilePicture).setPadding(0, (Integer) valueAnimator.getAnimatedValue(), 0, 0);

            float x = 1.0f - (Integer) valueAnimator.getAnimatedValue() / (float) 25;
            if (x < 0.8)
                findViewById(R.id.profilePicture).setAlpha(x);
            findViewById(R.id.temp31377).setAlpha(x);
            findViewById(R.id.sliding_layoutDown_profile).setAlpha(x);

        });

        animator1.setDuration(500);
        animator1.start();
//        initiateHistory();
        findViewById(R.id.gifViewCardProfile).setAlpha(0);
        ((SlidingUpPanelLayout) findViewById(R.id.sliding_layout_profile)).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    };

    private JellyToolbar toolbar;
    private LinearLayout lin;
    private Intent intent;

    private TextView country1TextView;
    private TextView country2TextView;
    private int countryOffset1;
    private int countryOffset2;
    private long countryAnimDuration;
    private FirebaseFirestore db;
    private File actualImageForCompressor;
    private File compressedImageForCompressor;
    private ImageSwitcher imageSwitcher;

    private TextView userName;
    private TextView userPhone;
    private ImageSwitcher userPic;
    private FirebaseUser user;
    private TextView userAddress;
    private TextView userAadhar;
    private int loadedNow;
    private int historyCount;
    private List<String> productNames;

    private List<user_entry_history_item> itemList = new ArrayList<>();
    private RecyclerView recyclerview;
    private UserHistoryAdapter userHistoryAdapter;
    public static String earnedBadge = "";
    public static boolean earnedNow = false;
    public static int whichBadge;
    private static SmileRating smileRating;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private static int loadstate = 0;
    private AssetManager am;
    private Typeface typefaceBold;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

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

        smileRating = (SmileRating) findViewById(R.id.smile_rating);
        beforeLoad();
        OverScrollDecoratorHelper.setUpOverScroll(((HorizontalScrollView) findViewById(R.id.temp46)));
        OverScrollDecoratorHelper.setUpStaticOverScroll(((SlidingUpPanelLayout) findViewById(R.id.sliding_layout_profile)), OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        OverScrollDecoratorHelper.setUpStaticOverScroll(((LinearLayout) findViewById(R.id.tempbadgin)), OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        fonting();

        if (MainActivity.availableNowForProfileRef) {
            MainActivity.availableNowForProfileRef = false;
            SharedPreferences prefs = getSharedPreferences("badgeCount", MODE_PRIVATE);
            int restoredC = prefs.getInt("earned", 0);
            if (MainActivity.howManFProfile >= 6) {
                findViewById(R.id.browz).setAlpha(1f);
                if (restoredC < 6) {
                    earnedNow = true;
                    earnedBadge = "Bronze badge for 6 referrals";
                    whichBadge = R.drawable.badgethreea;
                }
            }
            if (MainActivity.howManFProfile >= 50) {
                findViewById(R.id.silvr).setAlpha(1f);
                if (restoredC < 50) {
                    earnedNow = true;
                    earnedBadge = "Silver badge for 50 referrals";
                    whichBadge = R.drawable.badgetwoa;
                }
            }
            if (MainActivity.howManFProfile >= 100) {
                findViewById(R.id.gldhr).setAlpha(1f);
                if (restoredC < 100) {
                    earnedNow = true;
                    earnedBadge = "Gold badge for 100 referrals";
                    whichBadge = R.drawable.badgeonea;
                }
            }

            SharedPreferences.Editor editor = getSharedPreferences("badgeCount", MODE_PRIVATE).edit();
            editor.putInt("earned", MainActivity.howManFProfile);
            editor.apply();
        }


        RateThisApp.Config config = new RateThisApp.Config(2, 5);
        RateThisApp.init(config);
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    private void fonting() {
        am = this.getApplicationContext().getAssets();

        typefaceBold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goobold.ttf"));

        ((TextView) findViewById(R.id.username)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.userphone)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.ticketCountProfile)).setTypeface(typefaceBold);

        smileRating.setTypeface(typefaceBold);


        findViewById(R.id.tempbadgin).setOnClickListener(view -> Tooltip.make(ProfileActivity.this,
                new Tooltip.Builder(101)
                        .anchor(findViewById(R.id.tempbadgin), Tooltip.Gravity.TOP)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 1500)
                        .activateDelay(300)
                        .showDelay(300)
                        .text("You'll earn badges as you progress")
                        .maxWidth(500)
                        .withArrow(true)
                        .withOverlay(true)
                        .typeface(typefaceBold)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show());



        SharedPreferences prefs = getSharedPreferences("rating", MODE_PRIVATE);
        int restoredRating = prefs.getInt("rate", 0);
        if (restoredRating != 0) {
            if (restoredRating == 1)
                smileRating.setSelectedSmile(BaseRating.TERRIBLE);
            if (restoredRating == 2)
                smileRating.setSelectedSmile(BaseRating.BAD);
            if (restoredRating == 3)
                smileRating.setSelectedSmile(BaseRating.OKAY);
            if (restoredRating == 4)
                smileRating.setSelectedSmile(BaseRating.GOOD);
            if (restoredRating == 5)
                smileRating.setSelectedSmile(BaseRating.GREAT);
        }

        smileRating.setOnSmileySelectionListener((smiley, reselected) -> {
            if (!reselected) {
                int TAG = 0;
                switch (smiley) {
                    case SmileRating.BAD:
                        TAG = 2;
                        break;
                    case SmileRating.GOOD:
                        TAG = 4;
                        break;
                    case SmileRating.GREAT:
                        TAG = 5;
                        break;
                    case SmileRating.OKAY:
                        TAG = 3;
                        break;
                    case SmileRating.TERRIBLE:
                        TAG = 1;
                        break;
                }
                Answers.getInstance().logRating(new RatingEvent()
                        .putRating(TAG*2)
                        .putContentType("Stars")
                        .putContentId(Integer.toString(TAG)));
                SharedPreferences.Editor editor = getSharedPreferences("rating", MODE_PRIVATE).edit();
                editor.putInt("rate", TAG);
                editor.apply();
            }
        });
    }

    private boolean isExpanded = false;
    private boolean isExpanded2 = false;
    private void beforeLoad() {
        isLoading(true);
        updateSerts();
        loadedNow = 0;
        historyCount = 0;
        productNames = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (user != null)
            dataFromFirebase();
        new setLocalProfilePictureTask().execute();

        imageSwitcher = findViewById(R.id.profilePicture);
        imageSwitcher.setInAnimation(this, R.anim.fade_in);
        imageSwitcher.setOutAnimation(this, R.anim.fade_out);
        imageSwitcher.setFactory(new ImageViewFactory());
        imageSwitcher.setImageDrawable(getDrawable(R.drawable.userpp));


        intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("Package", "Me");

        toolbar = findViewById(R.id.toolbarProfile);
        toolbar.setJellyListener(jellyListener);


        lin = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_tab, null);
        lin.setBackgroundResource(R.color.colorTransparent);

        toolbar.setContentView(lin);

        findViewById(R.id.profileUn).setAlpha(1.0F);
        findViewById(R.id.profileUn).setBackgroundResource(R.drawable.rounded_tab);
        adjustDimensions();

        SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout_profile);

        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

        CardView cardView = findViewById(R.id.sliding_layout_profile_card);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                int x262 = (int) (24 - slideOffset * 24);
                params.leftMargin = x262;
                params.rightMargin = x262;
                cardView.setLayoutParams(params);
                findViewById(R.id.floatprofile).setAlpha(1 - slideOffset);
                cardView.setRadius(24 - 20 * slideOffset);
                findViewById(R.id.tempSmilee).setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    findViewById(R.id.tempSmilee).setVisibility(View.GONE);
                    isExpanded2 = false;
                }
                if (newState == SlidingUpPanelLayout.PanelState.DRAGGING && previousState == SlidingUpPanelLayout.PanelState.COLLAPSED && !isExpanded) {
                    findViewById(R.id.tempSmilee).setVisibility(View.VISIBLE);
                    isExpanded = true;
                }
                if (newState == SlidingUpPanelLayout.PanelState.DRAGGING && previousState == SlidingUpPanelLayout.PanelState.EXPANDED && !isExpanded2) {
                    findViewById(R.id.floatprofile).setVisibility(View.VISIBLE);
                    isExpanded2 = true;
                }
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {

                    Tooltip.make(ProfileActivity.this,
                            new Tooltip.Builder(101)
                                    .anchor(((CardView) findViewById(R.id.tempSmilee)), Tooltip.Gravity.TOP)
                                    .closePolicy(new Tooltip.ClosePolicy()
                                            .insidePolicy(true, false)
                                            .outsidePolicy(true, false), 1500)
                                    .activateDelay(300)

                                    .showDelay(300)
                                    .text("Rate your experience on the go")
                                    .maxWidth(500)
                                    .withArrow(true)
                                    .withOverlay(true)
                                    .typeface(typefaceBold)
                                    .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                    .build()
                    ).show();
                    isExpanded = false;
                    findViewById(R.id.floatprofile).setVisibility(View.GONE);
                    AssetManager am = getApplicationContext().getAssets();

                    Typeface typefaceBold = Typeface.createFromAsset(am,
                            String.format(Locale.US, "fonts/%s", "goobold.ttf"));

                    Tooltip.make(ProfileActivity.this,
                            new Tooltip.Builder(101)
                                    .anchor(((CardView) findViewById(R.id.sliding_layout_profile_card)), Tooltip.Gravity.LEFT)
                                    .closePolicy(new Tooltip.ClosePolicy()
                                            .insidePolicy(true, false)
                                            .outsidePolicy(true, false), 1500)
                                    .activateDelay(300)
                                    .showDelay(300)
                                    .text("If your display is small, Slide right for more options")
                                    .maxWidth(500)
                                    .withArrow(true)
                                    .withOverlay(true)
                                    .typeface(typefaceBold)
                                    .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                    .build()
                    ).show();
                }
                toolbar.collapse();
            }
        });
    }


    private void afterLoad() {
        if (loadstate > 1) {
            handler.postAtTime(runnable, System.currentTimeMillis() + interval);
            handler.postDelayed(runnable, interval);
        }
    }

    private void adjustDimensions() {
    }

    private void isLoading(boolean isLoading) {
        ProgressBar pbar = findViewById(R.id.loading_spinnerProfile);
        if (isLoading) {
            pbar.setAlpha(1);
        } else {
            pbar.setAlpha(0);
        }
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
        toolbar.clearAnimation();
        toolbar.clearFocus();
        toolbar.init();
        transi();
    }

    public void imtwoClick(View view) {
        //do nothing
    }

    public void imthreeClick(View view) {
        intent = new Intent(this, SocialActivity.class);
        intent.putExtra("Package", "Social");
        toolbar.collapse();
        toolbar.clearAnimation();
        toolbar.clearFocus();
        toolbar.init();
        transi();
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

    public void navigateToHistory(View view) {
        Intent i = new Intent(ProfileActivity.this, HistoryUser.class);
        i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(i);
    }

    private class ImageViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            final ImageView imageView = new ImageView(ProfileActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final ViewGroup.LayoutParams lp = new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);

            return imageView;
        }
    }

    public void uploadPressed(View view) {
        chooseImage();
    }

    public void updatePhoneNumber(View view) {

    }

    public void updateAddress(View view) {

    }

    public void updateAadhar(View view) {

    }

    private void dataFromFirebase() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userName = findViewById(R.id.username);
        userPhone = findViewById(R.id.userphone);

        db = FirebaseFirestore.getInstance();
        DocumentReference dbNow = db.collection("user").document(user.getUid());

        dbNow.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                userName.setText(doc.get("Name").toString());
                userPhone.setText(doc.get("Phone").toString());
                loadstate++;
                afterLoad();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErr();
                    }
                });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        isLoading(true);
        findViewById(R.id.gifViewCardProfile).setAlpha(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                actualImageForCompressor = FileUtil.from(this, filePath);
                compressImage(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            isLoading(false);
            findViewById(R.id.gifViewCardProfile).setAlpha(0);
        }
    }

    private void uploadImage(final Bitmap bitmapB) {

        if (bitmapB != null) {
            FirebaseStorage storage;
            StorageReference storageReference;
            final FirebaseUser user;

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();

            final StorageReference ref = storageReference.child("UserImages/" + user.getUid());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapB.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    showErr();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    isLoading(false);
                    findViewById(R.id.gifViewCardProfile).setAlpha(0);
                }
            });
        }
    }

    private class setLocalProfilePictureTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Bitmap bitmap = null;
            if (user != null) {
                bitmap = new ImageSaver(getApplicationContext()).
                        setFileName(user.getUid() + ".png").
                        setDirectoryName("FlukyProfiler").
                        load();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            userPic = findViewById(R.id.profilePicture);

            if (bitmap != null) {
                Drawable drawable = new BitmapDrawable(bitmap);
                userPic.setImageDrawable(drawable);
            } else {
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    Download_DP_From_Firebase();
            }

            loadstate++;
            afterLoad();
        }
    }

    private class saveLocalProfilePictureTask extends AsyncTask<Bitmap, Void, Bitmap> {
        @Override
        protected void onPostExecute(Bitmap bitmaped) {
            super.onPostExecute(bitmaped);
            userPic = findViewById(R.id.profilePicture);
            Drawable drawable = new BitmapDrawable(bitmaped);
            userPic.setImageDrawable(drawable);
            uploadImage(bitmaped);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap bitmapA = null;

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            new ImageSaver(getApplicationContext()).
                    setFileName(user.getUid() + ".png").
                    setDirectoryName("FlukyProfiler").
                    save(bitmaps[0]);
            bitmapA = new ImageSaver(getApplicationContext()).
                    setFileName(user.getUid() + ".png").
                    setDirectoryName("FlukyProfiler").
                    load();
            return bitmapA;
        }
    }

    private io.reactivex.disposables.CompositeDisposable mDisposable;

    public void compressImage(View view) {
        mDisposable = new CompositeDisposable();

        mDisposable.add(new Compressor(this)
                .compressToFileAsFlowable(actualImageForCompressor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        compressedImageForCompressor = file;
                        Bitmap bmp = BitmapFactory.decodeFile(compressedImageForCompressor.getAbsolutePath());
                        new saveLocalProfilePictureTask().execute(bmp);
                        mDisposable.dispose();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }));
    }

    private void Download_DP_From_Firebase() {
        FirebaseStorage storage;
        StorageReference storageReference;
        final FirebaseUser user;
        isLoading(true);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        final StorageReference ref = storageReference.child("UserImages/" + user.getUid());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageLoader.getInstance().loadImage(uri.toString(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Drawable drawable = new BitmapDrawable(loadedImage);
                        userPic.setImageDrawable(drawable);
                        new ImageSaver(getApplicationContext()).
                                setFileName(user.getUid() + ".png").
                                setDirectoryName("FlukyProfiler").
                                save(loadedImage);
                        isLoading(false);
                    }
                });
            }
        }).addOnFailureListener(exception -> {
            isLoading(false);
            Sneaker.with(ProfileActivity.this)
                    .setTitle("Profile picture" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), R.color.white) // Title and title color
                    .setMessage("Update your profile picture by tapping on the camera icon below") // Message and message color
                    .setDuration(5000) // Time duration to show
                    .autoHide(true)
                    .setCornerRadius(16)
                    .setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/" + "goobold.ttf")) // Custom font for title and message
                    .setHeight(130)// Height of the Sneaker layout
                    .sneak(R.color.two);
        });

    }

    private void updateSerts() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference perkDoc = db.collection("user").document(firebaseUser.getUid());
        perkDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                String serts = doc.get("Sert").toString();
                ((TextView) findViewById(R.id.ticketCountProfile)).setText(serts);
            }
        })
                .addOnFailureListener(e -> {
                    showErr();
                });
    }


    private void showErr() {
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(ProfileActivity.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setTitle("We can't connect")
                .setMessage("Looks like you don't have a stable internet connection. Please try again later")
                .setPositiveText("Okay")
                .setPositiveTypeface(typefaceBold)
                .setOnPositiveClicked((view, dialog) -> dialog.dismiss())
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
    }

}
