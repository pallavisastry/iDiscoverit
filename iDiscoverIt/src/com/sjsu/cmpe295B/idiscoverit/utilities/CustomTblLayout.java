package com.sjsu.cmpe295B.idiscoverit.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTblLayout extends TextView{

	Paint mLinePaint = new Paint();
	Paint mMarginPaint = new Paint();
	public CustomTblLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onDraw(Canvas canvas){
		canvas.drawColor(Color.CYAN);
		canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), mLinePaint);
		canvas.drawLine(100, 0, 100, getMeasuredHeight(), mMarginPaint);
		canvas.save();
		canvas.translate(100, 100);
		super.onDraw(canvas);
		canvas.restore();
	}

}
