package com.helloandroid.canvastutorial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class Panel extends SurfaceView implements SurfaceHolder.Callback{
	private CanvasThread canvasthread;
	
	//Zoom & pan touch event
     int y_old=0,y_new=0;int zoomMode=0;
     float pinch_dist_old=0,pinch_dist_new=0;
     int zoomControllerScale=1;//new and old pinch distance to determine Zoom scale
        // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // Remember some things for zooming
        PointF start = new PointF();
        PointF mid = new PointF();
        float oldDist = 1f;
 
        // We can be in one of these 3 states
    static final int NONE = 0;
    static final int PAN = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    
    Paint bluePaint,redPaint;
    Bitmap kangaroo;
    Bitmap overlayCircles,BlueCirclesBmp,RedCirclesBmp;
    Canvas overlayBlueCanvas,overlayRedCanvas;

     private static final String TAG = "DebugTag";
     
     float[] dst;
     float[] src;
     float[] matrixValues;
     
     float bmpWidth,bmpHeight;
     int originalbmpWidth,originalbmpHeight;
     float newDist;
     
     int toClear=2;
     float circleRadius=60;
        
    public Panel(Context context, AttributeSet attrs) {
		super(context, attrs); 
		
		// TODO Auto-generated constructor stub
	    getHolder().addCallback(this);
	    canvasthread = new CanvasThread(getHolder(), this);
	    setFocusable(true);
	    
	    bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    bluePaint.setColor(Color.BLUE);
	    bluePaint.setStrokeWidth(3);  
	    bluePaint.setStyle(Paint.Style.FILL);  
	    bluePaint.setAlpha(100);
	    
	    
	    redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    redPaint.setColor(Color.RED);
	    redPaint.setStrokeWidth(3);  
	    redPaint.setStyle(Paint.Style.FILL);  
	    redPaint.setAlpha(100);
		
		kangaroo = BitmapFactory.decodeResource(getResources(),
				R.drawable.sid);
		kangaroo=Bitmap.createScaledBitmap(kangaroo	, 960,540, true);
		
		originalbmpWidth=kangaroo.getWidth();
		originalbmpHeight=kangaroo.getHeight();
		
		BlueCirclesBmp=Bitmap.createBitmap(kangaroo.getWidth(),kangaroo.getHeight(),Bitmap.Config.ARGB_8888);
		RedCirclesBmp=Bitmap.createBitmap(kangaroo.getWidth(),kangaroo.getHeight(),Bitmap.Config.ARGB_8888);
		
		overlayBlueCanvas=new Canvas(BlueCirclesBmp);
		overlayRedCanvas=new Canvas(RedCirclesBmp);
		
		
		dst = new float[2];
		dst[0]=0;
		dst[1]=0;
		
		src=new float[2];
		src[0]=0;
		src[1]=0;
		
		
		matrixValues=new float[9];
	}

	 
	 public Panel(Context context) 
	 {
		   super(context);
		    getHolder().addCallback(this);
		    canvasthread = new CanvasThread(getHolder(), this);
		    setFocusable(true);
		    
		    bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		    bluePaint.setColor(Color.BLUE);
		    bluePaint.setStrokeWidth(3);  
		    bluePaint.setStyle(Paint.Style.FILL);  
		    bluePaint.setAlpha(70);
		    
		    
		    redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		    redPaint.setColor(Color.RED);
		    redPaint.setStrokeWidth(3);  
		    redPaint.setStyle(Paint.Style.FILL);  
		    redPaint.setAlpha(70);
		    
		    
			kangaroo = BitmapFactory.decodeResource(getResources(),
					R.drawable.sid);
			kangaroo=Bitmap.createScaledBitmap(kangaroo	, 960,540, true);
			
			originalbmpWidth=kangaroo.getWidth();
			originalbmpHeight=kangaroo.getHeight();
			
			BlueCirclesBmp=Bitmap.createBitmap(kangaroo.getWidth(),kangaroo.getHeight(),Bitmap.Config.ARGB_8888);
			RedCirclesBmp=Bitmap.createBitmap(kangaroo.getWidth(),kangaroo.getHeight(),Bitmap.Config.ARGB_8888);
			
			overlayBlueCanvas=new Canvas(BlueCirclesBmp);
			overlayRedCanvas=new Canvas(RedCirclesBmp);
			
			
			
			
			dst = new float[2];
			dst[0]=0;
			dst[1]=0;
			
			src=new float[2];
			src[0]=0;
			src[1]=0;
			
			matrixValues=new float[9];
	 }
	 
	 @Override
	 
     public boolean onTouchEvent(MotionEvent event){
           
		  	 PanZoomWithTouch(event);
       
             invalidate();//necessary to repaint the canvas
             return true;
  }

	@Override
	public void onDraw(Canvas canvas) 
	{
		
		 
			canvas.drawColor(Color.BLACK);
		
			canvas.drawBitmap(kangaroo,matrix,null);
		    canvas.drawBitmap(BlueCirclesBmp,matrix,bluePaint);
		    canvas.drawBitmap(RedCirclesBmp,matrix,redPaint);


		//canvas.drawCircle(src[0], src[1], 30, bluePaint);
	}
	
	public void Render(Canvas canvas) 
	{
		//Log.d("ondraw", "lefutott");
		
		 
			canvas.drawColor(Color.BLACK);
		
			canvas.drawBitmap(kangaroo,matrix,null);
		    canvas.drawBitmap(BlueCirclesBmp,matrix,bluePaint);
		    canvas.drawBitmap(RedCirclesBmp,matrix,redPaint);
		    
	}
	
	 
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	    canvasthread.setRunning(true);
	    canvasthread.start();
	    
	    Log.d(TAG,"Dimensions of the image "+kangaroo.getWidth()+"  "+kangaroo.getHeight());
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		canvasthread.setRunning(false);
		while (retry) {
			try {
				canvasthread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}

	}
	
	void PanZoomWithTouch(MotionEvent event){
		
		 if(Canvastutorial.activateViewMovement)
		 {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
     
     
        case MotionEvent.ACTION_DOWN://when first finger down, get first point
         savedMatrix.set(matrix);
         start.set(event.getX(), event.getY());
         Log.d(TAG, "mode=PAN");
         mode = PAN;
         break;
      case MotionEvent.ACTION_POINTER_DOWN://when 2nd finger down, get second point
         oldDist = spacing(event); 
         Log.d(TAG, "oldDist=" + oldDist);
         if (oldDist > 10f) {
            savedMatrix.set(matrix);
            midPoint(mid, event); //then get the mide point as centre for zoom
            mode = ZOOM;
            Log.d(TAG, "mode=ZOOM");
         }
         break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:       //when both fingers are released, do nothing
         mode = NONE;
         Log.d(TAG, "mode=NONE");
         break;
      case MotionEvent.ACTION_MOVE:     //when fingers are dragged, transform matrix for panning
         if (mode == PAN) {
            // ...
            matrix.set(savedMatrix);
            matrix.postTranslate(event.getX() - start.x,
                  event.getY() - start.y);
            Log.d(TAG,"Mapping rect");
                        //start.set(event.getX(), event.getY());
         }
         else if (mode == ZOOM) { //if pinch_zoom, calculate distance ratio for zoom
           newDist = spacing(event);
            Log.d(TAG, "newDist=" + newDist);
            if (newDist > 10f) {
               matrix.set(savedMatrix);
               float scale = newDist / oldDist;
               matrix.postScale(scale, scale, mid.x, mid.y);
            }
         }
         
         
         break;
        }
        
		 }
        
        src[0]=event.getX();
		src[1]=event.getY();
		

		matrix.invert(matrix); 
		matrix.mapPoints(dst, src);
		matrix.invert(matrix);
		
		matrix.getValues(matrixValues);
//		Log.d(TAG,"Matrix Scale Value : X =" +savedMatrix.MSCALE_X +"  Y =" +savedMatrix.MSCALE_Y );
//		Log.d(TAG,"Matrix Skew Value : X =" +savedMatrix.MSKEW_X +" Y =" +savedMatrix.MSKEW_Y);
//		Log.d(TAG,"Matrix Translate Value : X =" +matrix.MTRANS_X +" Y =" +matrix.MTRANS_Y);
		
		/*
		 * [Scale X, Skew X, Transform X
			Skew Y, Scale Y, Transform Y
			Perspective 0, Perspective 1, Perspective 2]
		 */
		
//		for(int i=0;i<matrixValues.length;i++)
//		{
//			
//			Log.d(TAG,"For i value ="+i+" the corresponding value is"+matrixValues[i]);
//		}
		
//		bmpWidth=originalbmpWidth*matrixValues[0];
//		bmpHeight=originalbmpHeight*matrixValues[4];
		
		Log.d(TAG,"the modified bitmap width and height"+bmpWidth+"   "+ bmpHeight);
		
		
//	    overlayCanvas.drawCircle(event.getX()*matrixValues[0]+matrixValues[2],event.getY()*matrixValues[4]+matrixValues[5],30,bluePaint);
		
		circleRadius=40/matrixValues[0];
		
		if(Canvastutorial.activeLayer==1)
	    overlayBlueCanvas.drawCircle(dst[0],dst[1], circleRadius, bluePaint);
	    
		if(Canvastutorial.activeLayer==2)
		    overlayRedCanvas.drawCircle(dst[0],dst[1], circleRadius, redPaint);
		   
		
//	    overlayCanvas.drawCircle(dst[0],dst[1], 30, redPaint);
			//
//	    Log.d(TAG,"the modified values are :"+(event.getX()-matrixValues[2])*matrixValues[0]+ "    "+(event.getY()/matrixValues[4]-matrixValues[5]));
//	    
//	    Log.d(TAG,"The Modified Touch Values based on the matrix are  :"+dst[0]+"   "+dst[1]);
	    
	 
		//overlayCanvas.drawCircle(event.getRawX()*matrixValues[0]+matrixValues[2],event.getRawY()*matrixValues[4]+matrixValues[5],30,bluePaint);
		//overlayCanvas.drawCircle(src[0], src[1], 40, bluePaint);
		
}

         /** Determine the space between the first two fingers */
   private float spacing(MotionEvent event) {
      // ...
      float x = event.getX(0) - event.getX(1);
      float y = event.getY(0) - event.getY(1);
      return FloatMath.sqrt(x * x + y * y);
   }

   /** Calculate the mid point of the first two fingers */
   private void midPoint(PointF point, MotionEvent event) {
      // ...
      float x = event.getX(0) + event.getX(1);
      float y = event.getY(0) + event.getY(1);
      point.set(x / 2, y / 2);
   }


}   