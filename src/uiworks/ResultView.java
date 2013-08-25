package uiworks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class ResultView extends View{

	public boolean IsShowingResult; //flag for showing result image
	public Bitmap resultImage; 
	public Paint paintBlack;
	
	public ResultView(Context context, AttributeSet attributeSet) {
		// TODO Auto-generated constructor stub
		super(context, attributeSet);
		IsShowingResult = false;
		paintBlack = new Paint();
		paintBlack.setStyle(Paint.Style.FILL);
		paintBlack.setColor(Color.BLACK);
	}
	
	
	public ResultView(Context context) {
		super(context);
		IsShowingResult = false;
		paintBlack = new Paint();
		paintBlack.setStyle(Paint.Style.FILL);
		paintBlack.setColor(Color.BLACK);
	}
	
	//draws an image bitmap on the view
	protected void onDraw(Canvas canvas){
		if (IsShowingResult)
		{
			//get result image size
			Rect src = new Rect(0, 0, resultImage.getWidth(), resultImage.getHeight());
			
			//get current screen size
			Rect dst=new Rect(0,0,canvas.getWidth(),canvas.getHeight());
			
			//draw the bitmap
			canvas.drawBitmap(resultImage, src, dst, paintBlack);
		}
	}
}
