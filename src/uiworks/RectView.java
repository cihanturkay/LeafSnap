package uiworks;

import com.leaf.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class RectView extends ImageView {

	private Paint paint, touchPaint;
	private int radius = 25, rectWidth,rectHeight;
	private Rect rect;
	private Rect belowRect, aboveRect;
	private float width, height;
	float endDistance, startDistance;
	int startX = 0, startY = 0, lastX, lastY;
	private final float THRESHOLD = 30;
	//ScaleGestureDetector scaleGestureDetector;

	public RectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		touchPaint = new Paint();
		touchPaint.setColor(R.drawable.red);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		paint.setColor(R.drawable.green);

		rect = new Rect();
		belowRect = new Rect();
		aboveRect = new Rect();
		//scaleGestureDetector = new ScaleGestureDetector(this.getContext(), this);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		int action = event.getAction();
		float touchX = event.getX();
		float touchY = event.getY();

//		this.scaleGestureDetector.onTouchEvent(event);
//
//		if (scaleGestureDetector.isInProgress())
//			return true;

		if (action == MotionEvent.ACTION_DOWN) {
			System.out.println("ACTION_DOWN");
			startX = (int) event.getX();
			startY = (int) event.getY();
			
			rectWidth = rect.right - rect.left;
			rectHeight = rect.bottom - rect.top;

			 System.out.println("rect left : " + belowRect.left + " right : " +
					 belowRect.right + " top : " + belowRect.top
			 + "  bottom : " + belowRect.bottom);

		} else if (action == MotionEvent.ACTION_MOVE) {
			// System.out.println("ACTION_MOVE");
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			int newLeft = (lastX - startX) ;
			int newTop = (lastY - startY);

			// System.out.println("lastx : " + lastX + " startX : " + startX +
			// " lasty :" + lastY);
//			
//			if(rect.left<5 || rect.right > width -5 || rect.top < 5 || rect.bottom > height -5)
//				return true ;

			if (belowRect.contains((int) (touchX), (int) (touchY))) {
				System.out.println("belowresttt");
				belowRect.set(lastX - radius, lastY - radius, lastX + radius, lastY + radius);
				rect.set(belowRect.left + radius, rect.top, rect.right, belowRect.bottom - radius);
				invalidate();
				return true;

			}else if (aboveRect.contains((int) (touchX), (int) (touchY))) {
				System.out.println("above recttt");
				aboveRect.set(lastX - radius, lastY - radius, lastX + radius, lastY + radius);
				rect.set(rect.left, aboveRect.top + radius, aboveRect.right - radius, rect.bottom);
				invalidate();
				return true;

			}else if (rect.contains((int) (touchX), (int) (touchY))) {
				System.out.println("rect moveee");
				rect.set(lastX - rectWidth/2, lastY - rectHeight/2, lastX + rectWidth/2, lastY + rectHeight/2);
				belowRect.set(rect.left - radius, rect.bottom - radius, rect.left + radius, rect.bottom + radius);
				aboveRect.set(rect.right - radius, rect.top - radius, rect.right + radius, rect.top + radius);
				invalidate();
				return true;

				// System.out.println("newleft : " + newLeft + "newtop : " +
				// newTop);

			} else
				return false;

		}
		return true;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawRect(rect, paint);
		canvas.drawRect(aboveRect, touchPaint);
		canvas.drawRect(belowRect, touchPaint);

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		System.out.println("width : " + (right - left) + " height :" + (bottom - top));
		this.width = (right - left);
		this.height = (bottom - top);
		System.out.println("width : " + width + " height : " + height);
		rect.set((int) width / 4, (int) height / 4, (int) (3 * width / 4), (int) (3 * height / 4));
		belowRect.set(rect.left - radius, rect.bottom - radius, rect.left + radius, rect.bottom + radius);
		aboveRect.set(rect.right - radius, rect.top - radius, rect.right + radius, rect.top + radius);

		invalidate();
	}

//	@Override
//	public boolean onScale(ScaleGestureDetector ges) {
//		endDistance = ges.getCurrentSpan();
//
//		if (Math.abs(startDistance - endDistance) >= THRESHOLD) {
//			if (startDistance > endDistance) {
//
//				// pincout
//				System.out.println("pincout");
//				if (Math.abs(rect.left - rect.right) > 40 && Math.abs(rect.bottom - rect.top) > 40) {
//					if (ges.getFocusY() / ges.getFocusX() > 1) {
//						rect.top = rect.top + 3;
//						rect.bottom = rect.bottom - 3;
//					} else {
//						rect.left = rect.left + 3;
//						rect.right = rect.right - 3;
//					}
//					invalidate();
//					return true;
//				}
//				return false;
//
//			} else if (startDistance < endDistance) {
//				// pincin
//				System.out.println("pincin");
//				if (Math.abs(rect.left - rect.right) < width * 95 / 100) {
//
//					if (ges.getFocusY() / ges.getFocusX() > 1) {
//						rect.top = rect.top - 3;
//						rect.bottom = rect.bottom + 3;
//					} else {
//						rect.left = rect.left - 3;
//						rect.right = rect.right + 3;
//					}
//
//					invalidate();
//					return true;
//				}
//				return false;
//
//			}
//		}
//
//		return true;
//	}
//
//	@Override
//	public boolean onScaleBegin(ScaleGestureDetector ges) {
//		startDistance = ges.getCurrentSpan();
//		return true;
//	}
//
//	@Override
//	public void onScaleEnd(ScaleGestureDetector ges) {
//
//	}

	public Bitmap getDrawnBitmap() {
		// Drawable draw = this.getDrawable();
		// Bitmap bitmap = ((BitmapDrawable)draw).getBitmap();
		// bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		// bitmap = bitmap.copy(Bitmap.Config.ARGB_8888 ,true);
		// Canvas canvas = new Canvas();
		// canvas.setBitmap(bitmap);
		// System.out.println("bitmap width : "+bitmap.getWidth() +
		// " bitmap height : "+ bitmap.getHeight());
		// float xRatio = bitmap.getWidth()/width;
		// float yRatio = bitmap.getHeight()/height;
		// Paint paint = new Paint();
		// paint.setAntiAlias(true);
		// paint.setStyle(Paint.Style.STROKE);
		// paint.setStrokeWidth(5);
		// paint.setColor(Color.GREEN);
		// //Rect rect = new Rect((int)(this.rect.left*xRatio),
		// (int)(this.rect.top*yRatio), (int)(this.rect.right*xRatio),
		// (int)(this.rect.bottom*yRatio));
		// canvas.drawRect(rect, paint);
		// return bitmap;
		return null;
	}

	/*
	 * returns x,y,width and height of the rectangle
	 */
	public String getRectCordinates(float bitmapHeight, float bitmapWidth) {

		float xratio = bitmapWidth / width;
		float yratio = bitmapHeight / height;

		int left = (int) (((float) rect.left) * xratio);
		int right = (int) (((float) rect.right) * xratio);
		int top = (int) (((float) rect.top) * yratio);
		int bottom = (int) (((float) rect.bottom) * yratio);

		String coordinates = new String();
		coordinates += Integer.toString(left) + ",";
		coordinates += Integer.toString(top) + ",";
		coordinates += Integer.toString(Math.abs(right - left)) + ",";
		coordinates += Integer.toString(Math.abs(bottom - top));
		return coordinates;

	}

}
