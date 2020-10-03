package app.fluky.ml.fluk.data;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

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

import app.fluky.ml.fluk.complex.utils.GeneratatedUser;

public class ActiveBid {
    private Bitmap bidMainImage, bidSecImage;
    private String bidHeading, bidId, bidDescription, bidInfoUrl, endTime, bidAddress;
    private int IS_BID_COMPLETE, userHistoryCount;


    public void recycleBmp1() {
        try {
            this.bidMainImage.recycle();
        } catch (NullPointerException ee) {
            ee.printStackTrace();
        } catch (IllegalStateException eez) {
            eez.printStackTrace();
        } catch (RuntimeException sfs) {
            sfs.printStackTrace();
        } catch (Exception eeeee) {
            eeeee.printStackTrace();
        }
    }

    public void recycleBm2() {
        try {
            this.bidSecImage.recycle();
        } catch (NullPointerException ee) {
            ee.printStackTrace();
        } catch (IllegalStateException eez) {
            eez.printStackTrace();
        } catch (RuntimeException sfs) {
            sfs.printStackTrace();
        } catch (Exception eeeee) {
            eeeee.printStackTrace();
        }
    }

    public ActiveBid(Bitmap bidMainImage, Bitmap bidSecImage, String bidHeading, String bidId, String bidDescription, String bidInfoUrl) {
        this.bidMainImage = bidMainImage;
        this.bidSecImage = bidSecImage;
        this.bidHeading = bidHeading;
        this.bidId = bidId;
        this.bidDescription = bidDescription;
        this.bidInfoUrl = bidInfoUrl;
    }

    public int getUserHistoryCount() {
        return userHistoryCount;
    }

    public void setUserHistoryCount(int userHistoryCount) {
        this.userHistoryCount = userHistoryCount;
    }

    public void setBidAddress(String bidAddress) {
        this.bidAddress = bidAddress;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getIS_BID_COMPLETE() {
        return IS_BID_COMPLETE;
    }

    public void setIS_BID_COMPLETE(int IS_BID_COMPLETE) {
        this.IS_BID_COMPLETE = IS_BID_COMPLETE;
    }

    public ActiveBid() {
        IS_BID_COMPLETE = 0;
    }

    public Bitmap getBidMainImage() {
        return bidMainImage;
    }

    public void setBidMainImage(Bitmap bidMainImage) {
        this.bidMainImage = bidMainImage;
    }

    public Bitmap getBidSecImage() {
        return bidSecImage;
    }

    public void setBidSecImage(Bitmap bidSecImage) {
        this.bidSecImage = bidSecImage;
    }

    public String getBidHeading() {
        return bidHeading;
    }

    public void setBidHeading(String bidHeading) {
        this.bidHeading = bidHeading;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getBidDescription() {
        return bidDescription;
    }

    public void setBidDescription(String bidDescription) {
        this.bidDescription = bidDescription;
    }

    public String getBidInfoUrl() {
        return bidInfoUrl;
    }

    public void setBidInfoUrl(String bidInfoUrl) {
        this.bidInfoUrl = bidInfoUrl;
    }

    /**************************************************************************************/

    public void submitBid(int howMuch) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference perkDoc = db.collection("user").document(firebaseUser.getUid());
        perkDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.get("Sert") != null) {
                        if (Integer.parseInt(doc.get("Sert").toString()) >= howMuch) {
                            proceed_to_submit_bid(howMuch);
                        }
                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


    }

    private void proceed_to_submit_bid(int howMuch) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference document = db.collection("activebids").document(bidAddress).collection("bidders").document("users");
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.get(firebaseUser.getPhoneNumber()) != null) {
                    document.update(firebaseUser.getPhoneNumber(), Integer.parseInt(doc.get(firebaseUser.getPhoneNumber()).toString()) + howMuch)
                            .addOnSuccessListener(aVoid -> {
                                GeneratatedUser.submitBid(howMuch, bidId);
                            });
                } else {

                    Map<String, Object> value = new HashMap<>();
                    value.put(firebaseUser.getPhoneNumber(), Integer.toString(howMuch));

                    document.set(value, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                GeneratatedUser.submitBid(howMuch, bidId);
                            });
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
