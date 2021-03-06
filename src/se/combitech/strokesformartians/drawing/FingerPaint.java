package se.combitech.strokesformartians.drawing;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package com.example.android.apis.graphics;

import se.combitech.strokesformartians.SFMIntentFactory;
import se.combitech.strokesformartians.dancing.MartianAnimator;
import se.combitech.strokesformartians.drawing.BrushSizeDialog.SizePickerView;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class FingerPaint extends GraphicsActivity
        implements ColorPickerDialog.OnColorChangedListener, OnClickListener {    

	private float mBrushSize = 12.0f;
    private Paint       mPaint;
    private MaskFilter  mEmboss;
    private MaskFilter  mBlur;
    public MyView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = new MyView( this );
        setContentView( myView );

        mPaint = new Paint();
        mPaint.setAntiAlias( true );
        mPaint.setDither( true );
        mPaint.setColor( 0xFFFF0000);
        mPaint.setStyle( Paint.Style.STROKE );
        mPaint.setStrokeJoin( Paint.Join.ROUND );
        mPaint.setStrokeCap( Paint.Cap.ROUND );
        mPaint.setStrokeWidth( mBrushSize );
        
        mEmboss = new EmbossMaskFilter(	new float[] { 1, 1, 1 },
                                       	0.4f,
                                       	6,
                                       	3.5f );

        mBlur = new BlurMaskFilter(	8, 
        							BlurMaskFilter.Blur.NORMAL );
    }
    
    public void colorChanged( int color ) {
        mPaint.setColor( color );
    }

    public class MyView extends View {
        
        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Path    mBorderPath;
        private Paint   mBitmapPaint;
        private MartianAnimator mMartianAnimator;
        
        
        
        public MyView(Context c) {
            super(c);
            
            mBitmap = Bitmap.createBitmap(	320, 
            								480, 
            								Bitmap.Config.ARGB_8888 );
            
            mCanvas = new Canvas( mBitmap );
            mPath = new Path();
            mBorderPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
        	
        	mMartianAnimator = new MartianAnimator();
        	float[] outline = mMartianAnimator.texCoordBuffer;
            
            
            canvas.drawColor(Color.WHITE);
            
            
            mBitmapPaint.setColor(Color.GREEN);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            int canvasHeight = canvas.getHeight();
            int canvasWidth = canvas.getWidth();
            
            for (int i=0;i<(mMartianAnimator.texCoordBuffer.length)-3;i=i+2) {
            	canvas.drawLine(outline[i]*canvasWidth, (1.0f - outline[i+1])*canvasHeight,
            					outline[i+2]*canvasWidth, (1.0f - outline[i+3])*canvasHeight, 
            					mBitmapPaint);
            }
            
            
            
        	canvas.drawLine(outline[0]*canvasWidth, (1.0f - outline[1])*canvasHeight,
					outline[outline.length-2]*canvasWidth, (1.0f - outline[outline.length-1])*canvasHeight, 
					mBitmapPaint);
//            drawHardcodedOutline(canvas);
            
            canvas.drawPath(mPath, mPaint);
        }
        
        private void drawHardcodedOutline(Canvas canvas) {
        	canvas.drawLine(150, 250, 150, 400, mBitmapPaint);	//Crotch
        	canvas.drawLine(150, 400, 100, 400, mBitmapPaint);
        	canvas.drawLine(100, 400, 100, 100, mBitmapPaint);	//Left leg
        	canvas.drawLine(100, 100, 90, 200, mBitmapPaint);
        	canvas.drawLine(90, 200, 95, 250, mBitmapPaint);	//Arm pit
        	canvas.drawLine(95, 250, 40, 250, mBitmapPaint);	//Elbow
        	canvas.drawLine(40, 250, 40, 200, mBitmapPaint);
        	canvas.drawLine(40, 200, 40, 60, mBitmapPaint);		//Shoulder
        	canvas.drawLine(40, 60, 220, 60, mBitmapPaint);
        	
        	canvas.drawCircle(150, 40, 30, mBitmapPaint);
        }
        
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath( mPath, mPaint );
            // kill this so we don't double draw
            mPath.reset();
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
    
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    private static final int SAVE_MENU_ID = Menu.FIRST + 2;
    private static final int BLUR_MENU_ID = Menu.FIRST + 3;
    private static final int ERASE_MENU_ID = Menu.FIRST + 4;
    private static final int SIZE_MENU_ID = Menu.FIRST + 5;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
        menu.add(0, SAVE_MENU_ID, 0, "Save").setShortcut('3', 's');
        menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
        menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
        menu.add(0, SIZE_MENU_ID, 0, "Brush Size").setShortcut('5', 'z');

        /****   Is this the mechanism to extend with filter effects?
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(
                              Menu.ALTERNATIVE, 0,
                              new ComponentName(this, NotesList.class),
                              null, intent, 0, null);
        *****/
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case COLOR_MENU_ID:
                new ColorPickerDialog( 	this, 
                						this, 
            							mPaint.getColor() ).show();
                return true;
            case SAVE_MENU_ID:
        		Intent intent = SFMIntentFactory.createDancerIntent(this);
        		Matrix myMatrix = new Matrix();
        		myMatrix.setScale(1, -1);
        		Bitmap b = Bitmap.createBitmap( myView.mBitmap,
        				0, 0, 
        				myView.mBitmap.getWidth(), 
        				myView.mBitmap.getHeight(), 
        				myMatrix, 
        				false);
        		Bitmap flippedBitmap = Bitmap.createScaledBitmap(b, 100, 150, false);
        		intent.putExtra( "newBitmap", flippedBitmap );
        		
            	startActivity( intent );
                return true;
            case EMBOSS_MENU_ID:
                if (mPaint.getMaskFilter() != mEmboss) {
                    mPaint.setMaskFilter(mEmboss);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case BLUR_MENU_ID:
                if (mPaint.getMaskFilter() != mBlur) {
                    mPaint.setMaskFilter(mBlur);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case ERASE_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(
                                                        PorterDuff.Mode.CLEAR));
                return true;
            case SIZE_MENU_ID:
//                mPaint.setXfermode(new PorterDuffXfermode(
//                                                    PorterDuff.Mode.SRC_ATOP));
//                mPaint.setAlpha(0x80);
            	
            	new BrushSizeDialog(this, this, mBrushSize).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
	public void onClick(View v) {
		LinearLayout mL = (LinearLayout)v.getParent();
		SizePickerView mView = (SizePickerView)mL.getChildAt(2);
		mBrushSize = mView.getSize();
		(mView.getCallback()).dismiss();
		mPaint.setStrokeWidth(mBrushSize);
	}
	
}
