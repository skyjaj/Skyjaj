package com.skyjaj.hors.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.skyjaj.hors.activities.PointOfTimeActivity;
import com.skyjaj.hors.bean.CustomDate;
import com.skyjaj.hors.bean.ScheduleOfMonth;
import com.skyjaj.hors.utils.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/3/1.
 */
public class PointOfTimeView extends View{

    private Paint mTextPaint;
    private float mViewWidth, mViewHeight,mCellWithX=300,mCellHeightY=145;//view宽度、高度、元素宽高
    private int TOTAL_ROW, TOTAL_COL;//行列均为4
    private Row rows[];//行

    private int total;//总数
    private int itemSpace=20;//元素间隙

    private boolean isFillData;//是否加载过数据

    private Cell mChooseCell;//选中元素
    private Cell mClickDown;//按下元素
    private float mDownX,mDownY;//记录按下坐标

    private int touchSlop;

    private List<String> mDatas;
    private OnItemClikListener mOnItemClikListener;



    //回调接口
    public interface OnItemClikListener {
        //点击的项
        void itemClick(String item);
    }




    public PointOfTimeView(Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        init(context);
    }

    public PointOfTimeView(Context context,OnItemClikListener listener, List<String> datas, int total) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.total = (total==0?0:total);
        mDatas=datas;
        this.mOnItemClikListener = listener;
        init(context);
    }


    public PointOfTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//无锯齿
        mTextPaint.setColor(Color.parseColor("#5e5d5c"));
        isFillData = false;

    }

    private int getMeasuredLength(int length, boolean isWidth) {
        int specMode = MeasureSpec.getMode(length);
        int specSize = MeasureSpec.getSize(length);
        int size;
        int padding = isWidth ? getPaddingLeft() + getPaddingRight()
                : getPaddingTop() + getPaddingBottom();
        if (specMode == MeasureSpec.EXACTLY) {
            size = specSize;
        } else {
            size = isWidth ? padding + 100 / 4 : 3000
                    + padding;
            if (specMode == MeasureSpec.AT_MOST) {
                size = Math.min(size, specSize);
            }
        }
        return size;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.i("skyjaj", "widthMeasureSpec  :" + widthMeasureSpec + "  heightMeasureSpec " + heightMeasureSpec);
//        Log.i("skyjaj", " MeasureSpec.getSize(widthMeasureSpec)  :" + MeasureSpec.getSize(widthMeasureSpec));
        //转换成px
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mCellWithX =mViewWidth/4;
        mCellHeightY = mCellWithX/2;
        itemSpace = (int) (mViewWidth / 4 / (3 - 1));
        TOTAL_COL = 3;
        TOTAL_ROW = total / TOTAL_COL + 1;
//        Log.i("skyjaj","  TOTAL_ROW "+TOTAL_ROW);
        mViewHeight = (int) (TOTAL_ROW*(mCellHeightY + itemSpace) + itemSpace);
        setMeasuredDimension(widthMeasureSpec, (int) mViewHeight == 0 ? 3000 : (int) mViewHeight);
//        Log.i("skyjaj","  mViewHeight "+mViewHeight);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        mViewWidth = w;
//        mViewHeight = h;
//        mCellWithX =mViewWidth/4;
//        mCellHeightY = mCellWithX/2;
//        itemSpace = (int) (mViewWidth / 4 / (3 - 1));
//        TOTAL_COL = 3;
//        TOTAL_ROW = total / TOTAL_COL + 1;
//        mViewHeight = (rows != null ? rows[rows.length - 1].cells[0].j + itemSpace : h);
//        mViewHeight = (int) (TOTAL_ROW*(mCellHeightY + itemSpace) + itemSpace);
//        Log.i("skyjaj", "mCellWithX : " + mCellWithX + " mCellHeightY: " + mCellHeightY+" mViewHeight:"+mViewHeight);
        if (mTextPaint != null) {
            mTextPaint.setTextSize(mCellHeightY/ 2);
        }
        if (rows == null||rows.length==0) {
            rows = new Row[TOTAL_ROW];
        }
//        if (!isFillData) {
        fillData();
//        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.i("skyjaj", "onDraw..");
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                measureClickCell(mDownX, mDownY, MotionEvent.ACTION_DOWN);
                break;
            case MotionEvent.ACTION_UP:
//                Log.i("skyjaj", "ACTION_UP");
                measureClickCell(event.getX(), event.getY(), MotionEvent.ACTION_UP);
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.i("skyjaj", "ACTION_MOVE");
//                int x = (int) (event.getX() - mDownX);
//                int y = (int) (event.getY() - mDownY);
//                //左右移动可以
//                if (Math.abs(y) < touchSlop) {
//                    mClickDown = null;
//                    invalidate();
//                }
                break;
            default:
                break;
        }

        return true;
    }




    private void fillData() {
        //初始化行列坐标
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i+j*TOTAL_COL;
                if (i == 0) {
                    rows[j].cells[i] = new Cell(i, j*mCellHeightY+j*itemSpace+mCellHeightY/2,position<total?mDatas.get(position):null);
                } else if (j==TOTAL_ROW-1) {
                    for (int k = 0; k < total / TOTAL_ROW; k++) {
                        rows[j].cells[i+k] = new Cell((i+k) * mCellWithX + (i+k) * itemSpace, j * mCellHeightY + j * itemSpace + mCellHeightY / 2
                                            ,position<total?mDatas.get(position):null);
                    }
                    break;
                } else {
                    rows[j].cells[i] = new Cell(i * mCellWithX + i * itemSpace, j * mCellHeightY + j * itemSpace + mCellHeightY / 2
                                        ,position<total?mDatas.get(position):null);
                }
            }
        }
        isFillData = true;
        invalidate();
    }


    /**
     * 计算在那个元素上
     * @param rowX
     * @param colY
     * @return
     */
    private Cell measureWhichCell(float rowX, float colY) {
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].cells.length; j++) {
                float x = rows[i].cells[j].i;
                float y = rows[i].cells[j].j;
                if (rowX > x && rowX < x + mCellWithX && colY > y && colY < y + mCellHeightY) {
                    return rows[i].cells[j];
                }
            }
        }
        return null;
    }


    private void measureClickCell(float x, float y,int event) {

        if (event == MotionEvent.ACTION_UP) {
            if (mClickDown != null && x > mClickDown.i && x < mClickDown.i + mCellWithX && y > mClickDown.j&&y < mClickDown.j + mCellHeightY) {
                mChooseCell = mClickDown;
                invalidate();
                mOnItemClikListener.itemClick(mChooseCell.content);
            }else if (mClickDown != null) {
                mClickDown = null;
                invalidate();
            }
            //mCellClickListener.clickDate(date);
            // 刷新界面
        }

        if (event == MotionEvent.ACTION_MOVE) {
//            if (!(mClickDown != null && x > mClickDown.i && x < mClickDown.i + mCellWithX && y > mClickDown.j&&y < mClickDown.j + mCellHeightY)) {
            if (!(mClickDown != null && x > mClickDown.i && x < mClickDown.i + mCellWithX)) {
                mClickDown=null;
                invalidate();
            }
        }

        if (event == MotionEvent.ACTION_DOWN) {
            Cell cellDown = measureWhichCell(x, y);
            if (cellDown != null) {
                mClickDown = cellDown;
                invalidate();
            }
        }
    }

    /**
     * 组元素
     *
     *
     */
    class Row {
        public int j;

        Row(int j) {
            this.j = j;
        }

        public Cell[] cells = new Cell[TOTAL_COL];

        // 绘制单元格
        public void drawCells(Canvas canvas) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) {
                    cells[i].drawSelf(canvas);
                }
            }
        }
    }

    /**
     * 单元格元素
     *
     */
    class Cell {
        public float i;//x坐标
        public float j;//y坐标
        public String content;

        public Cell(float i, float j,String content) {
            super();
            this.i = i;
            this.j = j;
            this.content = content;
        }

        public void drawSelf(Canvas canvas) {
            RectF rectF = new RectF((int) i, (int) j, (int) (i + mCellWithX), (int) (j + mCellHeightY));

            if (content != null) {
                canvas.drawRoundRect(rectF, 5, 5, mTextPaint);
            }

            if (content!=null&&mChooseCell != null && mChooseCell.i == i && j == mChooseCell.j) {
                mTextPaint.setColor(Color.parseColor("#FFB4E0B1"));
                canvas.drawRoundRect(rectF,5,5, mTextPaint);
                mTextPaint.setColor(Color.parseColor("#5e5d5c"));
            }

            if (content!=null&&mClickDown != null && mClickDown.i == i && j == mClickDown.j) {
                mTextPaint.setColor(Color.parseColor("#FFB4E0B1"));
                canvas.drawRoundRect(rectF,5,5, mTextPaint);
                mTextPaint.setColor(Color.parseColor("#5e5d5c"));
            }


            float y = j + (mCellHeightY / 2);
            mTextPaint.setColor(Color.RED);
            if (content == null) {
//                canvas.drawText(content, i + (mCellWithX / 2 - mTextPaint.measureText(content) / 2), y + (mTextPaint.measureText(content, 0, 1) / 2), mTextPaint);
            } else {
                canvas.drawText(content, i + (mCellWithX / 2 - mTextPaint.measureText(content) / 2), y + (mTextPaint.measureText(content, 0, 1) / 2), mTextPaint);
            }
            mTextPaint.setColor(Color.parseColor("#5e5d5c"));
        }
    }






}
