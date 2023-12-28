package com.example.virtualeyeforblinds.extraClasses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import com.example.virtualeyeforblinds.models.Coordinate;

import java.util.ArrayList;

public class OverlayView extends View {

    boolean isVisible=false;
    private Handler handler;

    private static final long DISAPPEAR_DELAY=2000;


    public String featureAsking;
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
    private int getRandomColor() {
        // Generate random RGB values
        int r = (int) (Math.random() * 256); // Red
        int g = (int) (Math.random() * 256); // Green
        int b = (int) (Math.random() * 256); // Blue

        // Return the color combining the RGB values
        return Color.rgb(r, g, b);
    }

    private void init() {
        p2=new Paint();
        paint = new Paint();

        paint.setStyle(Paint.Style.STROKE); // Set the rectangle style to stroke
        paint.setStrokeWidth(5);

        p2.setTextSize(25);


    }

    public void setCoordinates(ArrayList<Coordinate> coordinates,String choice) {
        this.coordinates = coordinates;
        featureAsking=choice;
        // Trigger a redraw when the coordinates are updated

        invalidate();

        // Schedule the view to disappear after a delay

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

                String label ="";
                // Draw a rectangle based on the coordinates
                int random=getRandomColor();
                paint.setColor(random);
                canvas.drawRect(left, top, right, bottom, paint);

                if(featureAsking.equalsIgnoreCase("person")){
                    label = coordinate.getLabel();
                }
                else if(featureAsking.equalsIgnoreCase("object")){
                    label = coordinate.getLabel()+coordinate.getPosition()+coordinate.getSteps();
                }



                //String distance = coordinate.getDistance();

                // Calculate text position above the rectangle
                int textX = (left + right) / 2; // Centered horizontally
                int textY = top - 20; // Adjust the distance from the rectangle

                // Draw label and distance text
                p2.setColor(random);
                canvas.drawText(label, textX, textY, p2);
                //canvas.drawText(distance, textX, textY + 20, paint); // Adjust the vertical spacing
            }
        }
    }
}
