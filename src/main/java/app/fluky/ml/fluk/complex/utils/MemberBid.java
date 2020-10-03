package app.fluky.ml.fluk.complex.utils;

import android.graphics.Bitmap;

public class MemberBid {
    private String bidId;
    private String bidMainImage;
    private String bidSecImage;
    private int finalI;
    private Bitmap bitmap1, bitmap2;

    public Bitmap getBitmap1() {
        return bitmap1;
    }

    public void setBitmap1(Bitmap bitmap1) {
        this.bitmap1 = bitmap1;
    }

    public Bitmap getBitmap2() {
        return bitmap2;
    }

    public void setBitmap2(Bitmap bitmap2) {
        this.bitmap2 = bitmap2;
    }

    public MemberBid(String bidId, String bidMainImage, String bidSecImage, int finalI) {
        this.bidId = bidId;
        this.bidMainImage = bidMainImage;
        this.bidSecImage = bidSecImage;
        this.finalI = finalI;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getBidMainImage() {
        return bidMainImage;
    }

    public void setBidMainImage(String bidMainImage) {
        this.bidMainImage = bidMainImage;
    }

    public String getBidSecImage() {
        return bidSecImage;
    }

    public void setBidSecImage(String bidSecImage) {
        this.bidSecImage = bidSecImage;
    }

    public int getFinalI() {
        return finalI;
    }

    public void setFinalI(int finalI) {
        this.finalI = finalI;
    }

    public void recycleBmp1() {
        try {
            this.bitmap1.recycle();
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
    public void recycleBmp2() {
        try {
            this.bitmap2.recycle();
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
}
