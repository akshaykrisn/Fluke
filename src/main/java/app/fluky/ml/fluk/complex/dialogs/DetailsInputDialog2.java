package app.fluky.ml.fluk.complex.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SignUpEvent;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.raywenderlich.android.validatetor.ValidateTor;
import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TrueSDK;
import com.truecaller.android.sdk.TrueSdkScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.MainActivity;
import app.fluky.ml.fluk.complex.utils.GeneratatedUser;
import app.fluky.ml.fluk.complex.utils.VerhoeffAlgorithm;
import app.fluky.ml.fluk.dialog.CustomAlertDialogue;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import io.fabric.sdk.android.Fabric;

public class DetailsInputDialog2 extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    FirebaseFirestore db;
    private FirebaseStorage storage;
    public static int APP_REQUEST_CODE = 99;
    private FirebaseAuth mAuth;
    private static boolean stateChang = false, calledbefore = false;
    private boolean trueInitialized;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private AssetManager am;
    private Typeface typefaceBold;
    private Typeface typefaceMedium;
    private static boolean almosttheretxtActivated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.dialog_input_details);

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

        SharedPreferences prefs = getSharedPreferences("IsCompleteP", MODE_PRIVATE);
        String restoredText = prefs.getString("IsComplete", null);
        if (restoredText != null) {
            if (restoredText.equals("1")) {
                MainActivity.doness = true;
                Intent intent = new Intent(DetailsInputDialog2.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        } else {
            am = this.getApplicationContext().getAssets();
            typefaceBold = Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "goobold.ttf"));
            typefaceMedium = Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "goomedium.ttf"));
            mAuth = FirebaseAuth.getInstance();
            stateChang = false;
            if (mAuth.getCurrentUser() == null) {
                SignIn();
            } else {
                isLoading(true);
                aftergooglesignup();
            }
        }

    }

    private void StartInput() {
        isLoading(true);
        new CountDownTimer(500, 250) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                UPDATE_ADDITIONAL_INFO();
            }

        }.start();
    }

    private void StartError(final String tit, final String des, final String but) {
        new CountDownTimer(500, 250) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                showWarning(tit, des, but);
            }

        }.start();
    }

    private void StartMain() {
        isLoading(true);
        new CountDownTimer(500, 250) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                MainActivity.doness = true;
                Intent intent = new Intent(DetailsInputDialog2.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        }.start();
    }

    private void UPDATE_ADDITIONAL_INFO() {
        isLoading(false);
        ArrayList<String> lineHint = new ArrayList<>();
//        lineHint.add("Aadhar");
        lineHint.add("Zip");
        lineHint.add("Age");
        lineHint.add("Referral Code(Optional)");

        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(DetailsInputDialog2.this)
                .setStyle(CustomAlertDialogue.Style.INPUT)
                .setTitle("Update Profile")
//                .setMessage(
//                        "Make sure your Aadhar is linked to the same phone number."
//                )
                .setNegativeText("Later")
                .setPositiveText("Submit")
                .setPositiveColor(R.color.positive)
                .setTitleColor(R.color.one)
                .setMessageColor(R.color.one)
                .setMessageFont(typefaceMedium)
                .setTitleFont(typefaceBold)
                .setOnNegativeClicked(((view, dialog) -> {
                    setStringAtxt("Going home...");
                    isLoading(true);
                    MainActivity.doness = true;
                    SharedPreferences prefs = getSharedPreferences("IsCompleteP", MODE_PRIVATE);
                    String restoredText = prefs.getString("IsCompleteGGGG", null);
                    if (restoredText == null) {
                        SharedPreferences.Editor editor = getSharedPreferences("IsCompleteP", MODE_PRIVATE).edit();
                        editor.putString("IsCompleteGGGG", "1");
                        editor.apply();
                        MainActivity.newhere = true;
                    }  else MainActivity.newhere = false;
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && !user.isEmailVerified()) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                    }
                                });
                    }
                    StartMain();
                    dialog.dismiss();
                }))
                .setOnInputClicked((view, dialog, inputList) -> {
                    if (inputList.size() >= 2) {

                        ValidateTor validateTor = new ValidateTor();
//                        if (validateAadharNumber(inputList.get(0)) &&
                        if (
                                (validateTor.isNumeric(inputList.get(0)) && validateTor.isAtleastLength(inputList.get(0), 6) && validateTor.isAtMostLength(inputList.get(0), 6)) &&
                                        (validateTor.isNumeric(inputList.get(1)) && Integer.parseInt(inputList.get(1)) > 10 && Integer.parseInt(inputList.get(1)) < 120)) {
                            setStringAtxt("All done! \nGoing home...");

                            if (inputList.get(2) == "" || inputList.get(2) == null) {
                                GeneratatedUser.Add_Additional_Details("0", inputList.get(0), inputList.get(1), null);
                            } else
                                GeneratatedUser.Add_Additional_Details("0", inputList.get(0), inputList.get(1), inputList.get(2).toUpperCase());

                            MainActivity.doness = true;
                            MainActivity.newhere = true;

                            SharedPreferences.Editor editor = getSharedPreferences("IsCompleteP", MODE_PRIVATE).edit();
                            editor.putString("IsComplete", "1");
                            editor.apply();


                            Answers.getInstance().logSignUp(new SignUpEvent()
                                    .putMethod("Google SignUP")
                                    .putSuccess(true));

                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {

                                            }
                                        });
                            }
                            StartMain();
                            dialog.dismiss();
                        } else {
                            StartError("Details not verified", "You either did a typo, or something went wrong. Please try again", "OK");
                            dialog.dismiss();
                        }
                    } else {
                        StartError("Details not verified", "You either did a type, or something went wrong. Please try again", "OK");
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .setLineInputHint(lineHint)
                .setPositiveTypeface(typefaceBold)
                .setAlertTypeface(typefaceBold)
                .build();
        alert.show();
    }


    public static boolean validateAadharNumber(String aadharNumber) {
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if (isValidAadhar) {
            isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }

    private void showWarning(String sT, String msg, String button) {
        isLoading(false);
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(DetailsInputDialog2.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setTitle(sT)
                .setMessage(msg)
                .setNegativeText(button)
                .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        StartInput();
                        dialog.dismiss();
                    }
                })
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
    }


    private void showErr() {
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(DetailsInputDialog2.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setTitle("We can't connect")
                .setMessage("Looks like you don't have a stable internet connection. Please try again later")
                .setPositiveText("Okay")
                .setPositiveTypeface(typefaceBold)
                .setAlertTypeface(typefaceMedium)
                .setOnPositiveClicked((view, dialog) -> dialog.dismiss())
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
    }

    public void SignIn() {
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());
        isLoading(true);

        new CountDownTimer(500, 250) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                isLoading(false);
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme)
                                .build(),
                        RC_SIGN_IN);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        }.start();
    }

    public void PhoneSignIn() {
        isLoading(true);

        new CountDownTimer(500, 250) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                isLoading(false);
                phoneLogin();
            }

        }.start();
    }

    public void SignOut() {
        isLoading(true);
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public void DeleteUser() {
        isLoading(true);
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void isLoading(boolean isLoading) {
        ProgressBar pbar = findViewById(R.id.loading_spinnerDialog);
        TextView txtalmost = findViewById(R.id.almostthretest);
        CardView cardView = findViewById(R.id.gifViewCardDialog);
        if (isLoading) {
            pbar.setAlpha(1);
            cardView.setAlpha(1);
            txtalmost.setAlpha(1);
        } else {
            txtalmost.setAlpha(0);
            pbar.setAlpha(0);
            cardView.setAlpha(0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (trueInitialized) {
            try {
                TrueSDK.getInstance().onActivityResultObtained(this, resultCode, data);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            isLoading(true);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                aftergooglesignup();
            } else {
                isLoading(false);
                CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(DetailsInputDialog2.this)
                        .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                        .setTitle("Error")
                        .setMessage("Something went wrong. Try Again?")
                        .setNegativeText("OK")
                        .setOnNegativeClicked((view, dialog) -> {
                            SignIn();
                            dialog.dismiss();
                        })
                        .setCancelable(false)
                        .setDecorView(getWindow().getDecorView())
                        .build();
                alert.show();
            }
        }

        if (requestCode == APP_REQUEST_CODE) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    String accountKitId = account.getId();
                    PhoneNumber phoneNumber = account.getPhoneNumber();
                    String phoneNumberString = null;
                    if (phoneNumber != null) {
                        phoneNumberString = phoneNumber.toString();
                    }
                    if (phoneNumberString.contains("+91") && phoneNumberString.length() == 13) {
                        processSignUp(phoneNumberString);
                    } else {
                        cantSignUp();
                    }
                }

                @Override
                public void onError(final AccountKitError error) {
                    isLoading(false);
                    String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ")[0];
                    CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(DetailsInputDialog2.this)
                            .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                            .setTitle("Oops " + name)
                            .setMessage("Something went wrong. Try Again?")
                            .setNegativeText("OK")
                            .setOnNegativeClicked((view, dialog) -> {
                                fbLogin();
                                dialog.dismiss();
                            })
                            .setCancelable(false)
                            .setDecorView(getWindow().getDecorView())
                            .build();
                    alert.show();
                }
            });
        }
    }

    private void aftergooglesignup() {
        almosttheretxtActivated = true;
        setStringAtxt("Welcome");
        MainActivity.newhere = true;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        try {
            firestore.setFirestoreSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DocumentReference user = firestore.collection("user").document(firebaseUser.getUid());
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.get("IsComplete") == null) {
                    isLoading(false);
                    CustomAlertDialogue.Builder alert22 = new CustomAlertDialogue.Builder(DetailsInputDialog2.this)
                            .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                            .setTitle("Hi " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                            .setTitleColor(Color.BLUE)
                            .setMessage("One more step to be a winner")
                            .setPositiveText("OK")
                            .setOnPositiveClicked((view, dialog) -> {
                                trueInitialized = true;
                                startTrueSdkGen();
                                stateChang = true;
                                dialog.dismiss();
                            })
                            .setCancelable(false)
                            .setDecorView(getWindow().getDecorView())
                            .build();
                    alert22.show();
                } else if (doc.get("IsComplete").toString().equals("0")) {
                    StartInput();
                    isLoading(false);
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("IsCompleteP", MODE_PRIVATE).edit();
                    editor.putString("IsComplete", "1");
                    editor.apply();

                    Answers.getInstance().logLogin(new LoginEvent()
                            .putMethod("Google SignUP")
                            .putSuccess(true));

                    MainActivity.doness = true;
                    StartMain();
                }
            }
        })
                .addOnFailureListener(e -> {
                    showErr();
                });
    }
    private FirebaseAnalytics mFirebaseAnalytics;

    private void processSignUp(String phone) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        isLoading(true);
        setStringAtxt("Authenticating");
        if (phone != null) {
            db = FirebaseFirestore.getInstance();
            DocumentReference user = db.collection("phones").document(phone);
            user.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.get("active") != null) {
                        if (doc.get("active").equals("1")) {
                            isLoading(false);
                            String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ")[0];
                            CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(DetailsInputDialog2.this)
                                    .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                                    .setTitle("Oops " + name)
                                    .setMessage("That phone is already taken. Feel free to contact support@fluke.ml")
                                    .setNegativeText("USE ANOTHER")
                                    .setOnNegativeClicked((view, dialog) -> {
                                        fbLogin();
                                        dialog.dismiss();
                                    })
                                    .setCancelable(false)
                                    .setDecorView(getWindow().getDecorView())
                                    .build();
                            alert.show();
                            Toast.makeText(this, "That phone is already taken. Feel free to contact support@fluke.ml", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        GeneratatedUser.Make_User(true, phone);
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, phone);
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
                        StartInput();
                        isLoading(false);
                    }
                }
            })
                    .addOnFailureListener(e -> {
                        showErr();
                    });

        } else cantSignUp();
    }

    private void cantSignUp() {
        isLoading(false);
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(DetailsInputDialog2.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setTitle("We're not here yet!")
                .setMessage("Coming Soon...")
                .setNegativeText("OK")
                .setOnNegativeClicked((view, dialog) -> {
                    StartInput();
                    dialog.dismiss();
                })
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
        SignOut();
        DeleteUser();
        finishAndRemoveTask();
    }

    @Override
    public void onBackPressed() {

    }

    public void phoneLogin() {
        startTrueSdkGen();
    }

    private void fbLogin() {
        isLoading(true);
        String[] whitelist = {"IN"};
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN)
                        .setDefaultCountryCode("IN")
                        .setReadPhoneStateEnabled(true)
                        .setReceiveSMS(true)
                        .setSMSWhitelist(whitelist);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    private void startTrueSdkGen() {
        TrueSdkScope trueScope = new TrueSdkScope.Builder(this, sdkCallback)
                .consentMode(TrueSdkScope.CONSENT_MODE_POPUP)
                .consentTitleOption(TrueSdkScope.SDK_CONSENT_TITLE_VERIFY)
                .build();

        TrueSDK.init(trueScope);

        if (TrueSDK.getInstance().isUsable()) {
            TrueSDK.getInstance().getUserProfile(this);
        } else {
            if (!calledbefore) {
                fbLogin();
                calledbefore = true;
            }
        }
    }

    private final ITrueCallback sdkCallback = new ITrueCallback() {

        @Override
        public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {

            if (trueProfile.phoneNumber.contains("+91") && trueProfile.phoneNumber.length() == 13) {
                processSignUp(trueProfile.phoneNumber);
            } else cantSignUp();
        }

        @Override
        public void onFailureProfileShared(@NonNull final TrueError trueError) {
            if (!calledbefore) {
                fbLogin();
                setStringAtxt("Authenticating");
                calledbefore = true;
            }
        }

        @Override
        public void onOtpRequired() {

        }
    };

    private void setStringAtxt(String gg) {
        try {
            ((TextView) findViewById(R.id.almostthretest)).setText(gg);
        } catch (NullPointerException sss) {
            sss.printStackTrace();
        } catch (IllegalStateException ssss) {
            ssss.printStackTrace();
        } catch (RuntimeException ss) {
            ss.printStackTrace();
        }
    }
}
