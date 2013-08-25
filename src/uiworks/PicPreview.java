package uiworks;

import com.leaf.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PicPreview extends ImageView {

	private Rect rect = null;
	private Paint paint;
	private float height, width;

	public PicPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(R.drawable.orange);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		System.out.println("width : " + (right - left) + " height :" + (bottom - top));
		this.height = (bottom - top);
		this.width = (right - left);
	}

	public void setRect(String coordinates, float heightOfBitmap, float widthOfBitmap) {
		if (coordinates != null) {
			System.out.println("coordinates : " + coordinates);
			float xratio = width / widthOfBitmap;
			float yratio = height / heightOfBitmap;

			System.out.println("heightOfBitmap : " + heightOfBitmap + " widthOfBitmap : " + widthOfBitmap + "xratio : "
					+ xratio);

			String[] corArray = coordinates.split(",");
			System.out.println(corArray[0] + corArray[1] + corArray[2] + corArray[3]);
			int left = 0, right = 0, top = 0, bottom = 0;
			left = (int) (Float.parseFloat(corArray[0]) * xratio);
			top = (int) (Float.parseFloat(corArray[1]) * yratio);
			right = left + (int) (Float.parseFloat(corArray[2]) * xratio);
			bottom = top + (int) (Float.parseFloat(corArray[3]) * yratio);
			System.out.println("calc coordinates : " + left + " " + top + " " + right + " " + bottom);
			rect = new Rect(left, top, right, bottom);
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (rect != null)
			canvas.drawRect(rect, paint);

	}
}
