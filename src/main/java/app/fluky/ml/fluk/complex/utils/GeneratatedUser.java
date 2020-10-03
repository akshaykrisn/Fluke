package app.fluky.ml.fluk.complex.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.InviteEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import app.fluky.ml.fluk.complex.MainActivity;

public class GeneratatedUser {

    private static final String NAME_KEY = "Name";
    private static final String PHONE_KEY = "Phone";
    private static final String AADHAR_KEY = "Aadhar";
    private static final String ZIP_KEY = "Zip";
    private static final String AGE_KEY = "Age";
    private static final String COMPLETE = "IsComplete";
    private static final String SERT = "Sert";
    private static final String REFERRAL = "ref";
    private static final String TOTAL_ENTRIES = "TotalEntries";
    public static int requestAccepted = 0;

    public static void Add_Additional_Details(final String nam, final String aadr, String zip, String agee, final String referralC) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference photo = db.collection("user").document(currentUser.getUid());
        photo.update(AGE_KEY, agee);
        photo.update(NAME_KEY, nam);
        photo.update(AADHAR_KEY, aadr);
        photo.update(REFERRAL, nam.substring(0, 4).toUpperCase() + aadr.substring(0, 5));

        photo.update(ZIP_KEY, zip)
                .addOnSuccessListener(aVoid -> photo.update(COMPLETE, "1")
                        .addOnSuccessListener(aVoid1 -> verifyRefe(referralC, nam.substring(0, 4).toUpperCase() + aadr.substring(0, 5))));
        requestAccepted = 1;

        Map<String, Object> userPoint = new HashMap<>();
        userPoint.put("count", "nope");
        userPoint.put("Active", "1");

        db.collection("sertdue").document(nam.substring(0, 4).toUpperCase() + aadr.substring(0, 5)).set(userPoint, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {

                });

    }

    public static void Add_Additional_Details(final String aadr, String zip, String agee, final String referralC) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference photo = db.collection("user").document(currentUser.getUid());

        photo.get().addOnCompleteListener(tasksfs -> {
            if (tasksfs.isSuccessful()) {
                DocumentSnapshot docsf = tasksfs.getResult();

                photo.update(AGE_KEY, agee);
                photo.update(AADHAR_KEY, aadr);
                photo.update(ZIP_KEY, zip)
                        .addOnSuccessListener(aVoid -> photo.update(COMPLETE, "1")
                                .addOnSuccessListener(aVoid1 -> verifyRefe(referralC, currentUser.getDisplayName().substring(0, 4).toUpperCase() + docsf.get("Phone").toString().substring(5, 10))));
                requestAccepted = 1;
                MainActivity.firstTimeUserBonus = true;
            }
        })
                .addOnFailureListener(e -> {
                });
    }

    private static void verifyRefe(final String referralC, final String myCode) {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (referralC != null && referralC.length() == 9) {

            DocumentReference user = db.collection("sertdue").document(referralC);
            user.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    final DocumentSnapshot doc = task.getResult();
                    if (doc.get("Active") != null) {
                        if (doc.get("Active").toString().equals("1")) {
                            MainActivity.referralCodeValidHai = true;
                            int addingamount = 0;
                            if (doc.get("count").toString().equals("nope")) {
                                addingamount = 1;
                            }
                            else {
                                addingamount = Integer.parseInt(doc.get("count").toString()) + 1;
                            }

                            DocumentReference pem = db.collection("sertdue").document(referralC);
                            Map<String, Object> userinfo = new HashMap<>();
                            userinfo.put("Active" , "1");
                            userinfo.put("count", addingamount);

                            pem.set(userinfo)
                                    .addOnSuccessListener(aVoid -> {

                                    });
                            final DocumentReference photo = db.collection("user").document(currentUser.getUid());
                            photo.update(SERT, "25")
                                    .addOnSuccessListener(aVoid -> photo.update(COMPLETE, "1")
                                            .addOnSuccessListener(aVoid1 -> {
                                            }));
                        }
                    }
                }
            })
                    .addOnFailureListener(e -> {
                    });
        }
    }

    public static void checkReferral() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference goal =  db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        goal.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.get("ref") != null) {
                    DocumentReference pela =  db.collection("sertdue").document(doc.get("ref").toString());
                    pela.get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            DocumentSnapshot doc2 = task2.getResult();
                            if (!doc2.get("count").equals("nope")) {
                                int x = Integer.parseInt(doc2.get("count").toString());
                                if (x>0) {
                                    Answers.getInstance().logInvite(new InviteEvent()
                                            .putMethod("ReferralAccepted"));

                                    MainActivity.checkReferralValid = true;
                                    MainActivity.howmanyrefereee = x*10;
                                    addSerts(x*10);
                                    pela.update("count", "nope")
                                            .addOnSuccessListener(aVoid -> {
                                                callCheckRere(x);
                                            });
                                }

                            }

                        }
                    })
                            .addOnFailureListener(e -> {
                            });
                }
            }
        })
                .addOnFailureListener(e -> {
                });
    }

    private static void callCheckRere(int vale) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference pela =  db.collection("trefer").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        pela.get().addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
                DocumentSnapshot doc2 = task2.getResult();
                if (doc2.get("count") != null) {
                    int x = Integer.parseInt(doc2.get("count").toString()) + vale;
                    if (x>0) {
                        pela.update("count", Integer.toString(x))
                                .addOnSuccessListener(aVoid -> {

                                });
                    }

                }

            }
        })
                .addOnFailureListener(e -> {
                });
    }

    public static void Make_User(boolean phoneProvided, String phonenumber) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        Map<String, Object> newUser = new HashMap<>();
        if (phoneProvided) {
            newUser.put(PHONE_KEY, phonenumber);
        }
        else {
            newUser.put(PHONE_KEY, currentUser.getPhoneNumber());
        }

        newUser.put(NAME_KEY, currentUser.getDisplayName());
        newUser.put(AADHAR_KEY, null);
        newUser.put(ZIP_KEY, null);
        newUser.put(AGE_KEY, null);
        newUser.put(TOTAL_ENTRIES, "0");

        newUser.put(COMPLETE, "0");
        newUser.put(SERT, "15");
        newUser.put(REFERRAL, currentUser.getDisplayName().substring(0, 4).toUpperCase() + phonenumber.substring(5, 10));


        db.collection("user").document(currentUser.getUid()).set(newUser)
                .addOnSuccessListener(aVoid -> {

                });
        Map<String, Object> newPhone = new HashMap<>();
        newPhone.put("active", "1");
        db.collection("phones").document(phonenumber).set(newPhone)
                .addOnSuccessListener(aVoid -> {

                });

        Map<String, Object> userPoint = new HashMap<>();
        userPoint.put("count", "nope");
        userPoint.put("Active", "1");

        db.collection("sertdue").document(currentUser.getDisplayName().substring(0, 4).toUpperCase() +phonenumber.substring(5, 10)).set(userPoint, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {

                });

        Map<String, Object> userPoint2 = new HashMap<>();
        userPoint2.put("count", "0");

        db.collection("trefer").document(currentUser.getUid()).set(userPoint2, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {

                });
    }


    /**************************************************************************************/


    public static int getUsersCurrentPerkCount() {
        final int[] value = {-1};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference perkDoc = db.collection("user").document(firebaseUser.getUid());
        perkDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    value[0] = Integer.parseInt(doc.get("Sert").toString());
                    Log.i("Operation21", Integer.toString(value[0]));
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
        return value[0];
    }

    public static void addSerts(int count) {
        final int[] value = {-1};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference perkDoc = db.collection("user").document(firebaseUser.getUid());
        perkDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                value[0] = Integer.parseInt(doc.get("Sert").toString());
                DocumentReference contact = db.collection("user").document(firebaseUser.getUid());
                contact.update(SERT, value[0] + count)
                        .addOnSuccessListener(aVoid -> {

                        });
            }
        })
                .addOnFailureListener(e -> {
                });
    }

    public static void submitBid(int howMuch, String bidId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference document = db.collection("user").document(firebaseUser.getUid()).collection("bidHistory").document(bidId);
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.get("Count") != null) {
                    document.update("Count", Integer.parseInt(doc.get("Count").toString()) + howMuch)
                            .addOnSuccessListener(aVoid -> {

                            });
                } else {

                    Map<String, Object> value = new HashMap<>();
                    value.put("Count", Integer.toString(howMuch));

                    document.set(value, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {

                            });
                }
            }
        })
                .addOnFailureListener(e -> {
                });
    }

    public static String getMyParticularBidSertTicketInfo(String bidId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String[] result = {"0"};
        DocumentReference document = db.collection("user").document(firebaseUser.getUid()).collection("bidHistory").document(bidId);
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.get("Count") != null) {
                    result[0] = doc.get("Count").toString();
                }
            }
        })
                .addOnFailureListener(e -> {
                });
        return result[0];
    }

}
