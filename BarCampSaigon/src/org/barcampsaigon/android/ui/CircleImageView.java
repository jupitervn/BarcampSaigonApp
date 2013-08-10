package org.barcampsaigon.android.ui;

import org.barcampsaigon.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        // init shader
        BitmapShader shader;
        if (getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(),
                        true);
                shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                        Shader.TileMode.CLAMP);

                // init paint
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setShader(shader);
                int circleCenter = getWidth() / 2;

                // circleCenter is the x or y of the view's center
                // radius is the radius in pixels of the cirle to be drawn
                // paint contains the shader that will texture the shape
                paint.setColor(getResources().getColor(R.color.tweet_avatar_bound_color));
                paint.setShader(null);
                canvas.drawCircle(circleCenter, circleCenter, getWidth() / 2, paint);
                paint.setShader(shader);
                canvas.drawCircle(circleCenter, circleCenter, getWidth() / 2 - 10, paint);

            }
        } else {
            super.onDraw(canvas);
        }
    }
}
