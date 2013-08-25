package uiworks;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
	public SurfaceHolder mHolder;
	public Camera camera;
	public int width, height;

	@SuppressWarnings("deprecation")
	public Preview(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.setZOrderOnTop(true);
		mHolder = getHolder();
		mHolder.setFormat(PixelFormat.TRANSPARENT);
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		System.out.println("Preview constructor 1");
	}

	// Called once the holder is ready
	public void surfaceCreated(SurfaceHolder holder) {
		camera = getCameraInstance();
		Parameters parameters = camera.getParameters();
		parameters.set("jpeg-quality", 100);
		parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
		parameters.setPictureFormat(ImageFormat.JPEG);
		List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
		Size size = sizes.get(Integer.valueOf((sizes.size() - 1) / 2)); 
		parameters.setPictureSize(size.width, size.height);
		camera.setParameters(parameters);
		camera.setDisplayOrientation(90);

		try {
			camera.setPreviewDisplay(holder);
			camera.setPreviewCallback(new PreviewCallback() {
				// Called for each frame previewed
				public void onPreviewFrame(byte[] data, Camera camera) {
					Preview.this.invalidate();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Called when the holder is destroyed
	public void surfaceDestroyed(SurfaceHolder holder) {

		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {

		}
		return c;
	}

	// Called when holder has changed
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) { // <15>
		camera.startPreview();
	}

}
