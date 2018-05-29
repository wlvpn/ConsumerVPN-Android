package com.wlvpn.slider.whitelabelvpn.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * ImageView that clips to circle. This only supports bitmap drawables.
 */
public class CircleImageView extends AppCompatImageView {

    private static final Bitmap.Config CONFIG = Bitmap.Config.ARGB_8888;
    private final Paint paint;
    private final RectF drawableRect;
    private final Matrix shaderMatrix;

    private BitmapShader bitmapShader;
    private Bitmap bitmap;
    private float radius;
    private int bitmapWidth;
    private int bitmapHeight;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        drawableRect = new RectF();
        shaderMatrix = new Matrix();
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null) {
            return;
        }

        canvas.drawCircle(drawableRect.centerX(), drawableRect.centerY(), radius, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        prepareForDraw();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        prepareForDraw();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        prepareForDraw();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        prepareForDraw();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        prepareForDraw();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        prepareForDraw();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        prepareForDraw();
    }

    /**
     * Calculate drawing area and fetch the bitmap to be drawn.
     */
    private void prepareForDraw() {
        bitmap = bitmapForDrawable(getDrawable());

        if (bitmap == null) {
            invalidate();
            return;
        }

        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        calculateBounds(drawableRect);
        radius = Math.min(drawableRect.height() / 2.0f, drawableRect.width() / 2.0f);
        bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        paint.setShader(bitmapShader);
        calculateShaderMatrix();
    }

    /**
     * Get the bitmap for the set drawable
     *
     * @param drawable bitmap drawable
     * @return bitmap of drawable
     */
    @Nullable
    private Bitmap bitmapForDrawable(@Nullable Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            try {
                Bitmap bitmap = drawable instanceof ColorDrawable
                        ? Bitmap.createBitmap(2, 2, CONFIG)
                        : Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), CONFIG);

                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
    }

    /**
     * Update a rectangle with the calculated bounds.
     *
     * @param rectF to be updated
     */
    private void calculateBounds(RectF rectF) {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        rectF.left = left;
        rectF.top = top;
        rectF.right = left + sideLength;
        rectF.bottom = top + sideLength;
    }

    /**
     * Calculate the display matrix.
     */
    private void calculateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        shaderMatrix.set(null);

        if (bitmapWidth * drawableRect.height() > bitmapHeight * drawableRect.width()) {
            scale = drawableRect.height() / (float) bitmapHeight;
            dx = (drawableRect.width() - bitmapWidth * scale) * 0.5f;
        } else {
            scale = drawableRect.width() / (float) bitmapWidth;
            dy = (drawableRect.height() - bitmapHeight * scale) * 0.5f;
        }

        shaderMatrix.setScale(scale, scale);
        shaderMatrix.postTranslate((int) (dx + 0.5f) + drawableRect.left, (int) (dy + 0.5f) + drawableRect.top);

        bitmapShader.setLocalMatrix(shaderMatrix);
    }

}