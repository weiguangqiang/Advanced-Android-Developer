package me.mahakagg.customfancontrollercc2;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DialView extends View {
    // member variables
    private int mSelectionCount; // no. of selections
    private float mWidth; // custom view width
    private float mHeight; // custom view height
    private Paint mTextPaint; // for text in the view
    private Paint mDialPaint; // for dial
    private float mRadius; // radius of the circle
    private int mActiveSelection; // active selection
    private final StringBuffer mTempLabel = new StringBuffer(8); // string buffer for dial label
    private final float[] mTempResult = new float[2]; // float for computeXY result
    private int mFanOnColor;
    private int mFanOffColor;

    public DialView(Context context) {
        super(context);
        init(context, null);
    }

    public DialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40f);

        mDialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // default dial paint
        mFanOnColor = Color.CYAN;
        mFanOffColor = Color.GRAY;
        mSelectionCount = 4;
        // get custom attributes if available
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DialView, 0, 0);
            mFanOnColor = typedArray.getColor(R.styleable.DialView_fanOnColor, mFanOnColor);
            mFanOffColor = typedArray.getColor(R.styleable.DialView_fanOffColor, mFanOffColor);
            mSelectionCount = typedArray.getInt(R.styleable.DialView_selectionIndicators, mSelectionCount);
            typedArray.recycle();
        }

        mDialPaint.setColor(mFanOffColor);
        mActiveSelection = 0; // initialize current selection

        // onClickListener
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rotate selection to the next valid choice.
                mActiveSelection = (mActiveSelection + 1) % mSelectionCount;
                // Set dial background color to green if selection is >= 1.
                if (mActiveSelection >= 1) {
                    mDialPaint.setColor(mFanOnColor);
                } else {
                    mDialPaint.setColor(mFanOffColor);
                }
                // Redraw the view.
                invalidate();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // calculate radius from width, height
        mWidth = w;
        mHeight = h;
        mRadius = (float) (Math.min(mWidth, mHeight)/ 2 * 0.8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the dial
        canvas.drawCircle(mWidth /2, mHeight /2, mRadius, mDialPaint);
        // draw the text labels
        final float labelRadius = mRadius + 20;
        StringBuffer label = mTempLabel;
        for (int i = 0; i < mSelectionCount; i++) {
            float[] xyData = computeXYForPosition(i, labelRadius, true);
            float x = xyData[0];
            float y = xyData[1];
            label.setLength(0);
            label.append(i);
            canvas.drawText(label, 0, label.length(), x, y, mTextPaint);
        }
        // Draw the indicator mark.
        final float markerRadius = mRadius - 35;
        float[] xyData = computeXYForPosition(mActiveSelection, markerRadius, false);
        float x = xyData[0];
        float y = xyData[1];
        canvas.drawCircle(x, y, 20, mTextPaint);
    }

    // method to compute XY coordinates for text label and indicator (0, 1, 2, 3) for the dial
    // pos - position index
    // radius - for outer circle
    // it returns 2 element array for position (0 - X, 1 - Y)
    private float[] computeXYForPosition(final int pos, final float radius, boolean isLabel) {
        float[] result = mTempResult;
        Double startAngle;
        Double angle;
        if (mSelectionCount > 4) {
            startAngle = Math.PI * (3 / 2d);
            angle= startAngle + (pos * (Math.PI / mSelectionCount));
            result[0] = (float) (radius * Math.cos(angle * 2)) + (mWidth / 2);
            result[1] = (float) (radius * Math.sin(angle * 2)) + (mHeight / 2);
            if((angle > Math.toRadians(360)) && isLabel) {
                result[1] += 20;
            }
        } else {
            startAngle = Math.PI * (9 / 8d);
            angle= startAngle + (pos * (Math.PI / mSelectionCount));
            result[0] = (float) (radius * Math.cos(angle)) + (mWidth / 2);
            result[1] = (float) (radius * Math.sin(angle)) + (mHeight / 2);
        }
        return result;
    }

    public void setSelectionCount(int selectionCount) {
        this.mSelectionCount = selectionCount;
        this.mActiveSelection = 0;
        mDialPaint.setColor(mFanOffColor);
        invalidate();
    }
}
