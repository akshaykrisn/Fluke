package app.fluky.ml.fluk.complex.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import app.fluky.ml.fluk.R;


public class DecodeBitmapTask extends AsyncTask<Void, Void, Bitmap> {

    private final Resources resources;
    private final Bitmap bmp;
    private final int reqWidth;
    private final int reqHeight;

    private final Reference<Listener> refListener;

    public interface Listener {
        void onPostExecuted(Bitmap bitmap);
    }

    public DecodeBitmapTask(Resources resources, Bitmap bmp,
                            int reqWidth, int reqHeight,
                            @NonNull Listener listener)
    {
        this.resources = resources;
        this.bmp = bmp;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.refListener = new WeakReference<>(listener);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        final Bitmap result;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            result = getRoundedCornerBitmap(bmp,
                    resources.getDimension(R.dimen.card_corner_radius), reqWidth, reqHeight);
            bmp.recycle();
        } else {
            result = bmp;
        }
        return result;
    }

    @Override
    final protected void onPostExecute(Bitmap bitmap) {
        final Listener listener = this.refListener.get();
        if (listener != null) {
            listener.onPostExecuted(bitmap);
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float pixels, int width, int height) {
        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int sourceWidth = bitmap.getWidth();
        final int sourceHeight = bitmap.getHeight();

        float xScale = (float) width / bitmap.getWidth();
        float yScale = (float) height / bitmap.getHeight();
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (width - scaledWidth) / 2;
        float top = (height - scaledHeight) / 2;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        final RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, targetRect, paint);

        return output;
    }

}