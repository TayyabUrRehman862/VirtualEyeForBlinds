package com.example.virtualeyeforblinds.extraClasses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.example.virtualeyeforblinds.extraClasses.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class OverlayView extends View {
    private ArrayList<Coordinate> coordinates; // List of coordinates to draw rectangles
    private Paint paint;
    private Paint p2;

    public OverlayView(Context context) {
        super(context);
        init();
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        p2=new Paint();
        paint = new Paint();
        paint.setColor(Color.GREEN); // Set the rectangle color
        paint.setStyle(Paint.Style.STROKE); // Set the rectangle style to stroke
        paint.setStrokeWidth(5);
        p2.setColor(Color.GREEN);
        p2.setTextSize(25);

    }

    public void setCoordinates(ArrayList<Coordinate> coordinates) {
        this.coordinates = coordinates;
        // Trigger a redraw when the coordinates are updated
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Check if coordinates are available
        if (coordinates != null) {
            // Iterate over the coordinates and draw rectangles for each one
            for (Coordinate coordinate : coordinates) {
                int left = coordinate.getXmin();
                int top = coordinate.getYmax();
                int right = coordinate.getXmax();
                int bottom = coordinate.getYmin();

                // Draw a rectangle based on the coordinates
                canvas.drawRect(left, top, right, bottom, paint);

                String label = coordinate.getLabel();
                //String distance = coordinate.getDistance();

                // Calculate text position above the rectangle
                int textX = (left + right) / 2; // Centered horizontally
                int textY = top - 20; // Adjust the distance from the rectangle

                // Draw label and distance text
                canvas.drawText(label, textX, textY, p2);
                //canvas.drawText(distance, textX, textY + 20, paint); // Adjust the vertical spacing
            }
        }
    }
}
