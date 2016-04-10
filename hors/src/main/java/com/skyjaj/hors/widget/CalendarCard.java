package com.skyjaj.hors.widget;

import android.content.Context;
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;  
import android.view.ViewConfiguration;

import com.skyjaj.hors.bean.CustomDate;
import com.skyjaj.hors.bean.ScheduleOfMonth;
import com.skyjaj.hors.utils.DateUtil;

import java.sql.Timestamp;
import java.util.List;

/** 
 * 自定义日历卡 
 *
 */  
public class CalendarCard extends View {  
  
    private static final int TOTAL_COL = 7; // 7列  
    private static final int TOTAL_ROW = 6; // 6行  
    private Paint mCirclePaint; // 绘制圆形的画笔
    private Paint mTextPaint; // 绘制文本的画笔
    private Paint mLinePaint;//画直线的笔
    private Paint mBgPaint;//画背景的笔
    private int mViewWidth; // 视图的宽度
    private int mViewHeight; // 视图的高度  
    private int mCellSpace; // 单元格间距  
    private Row rows[] = new Row[TOTAL_ROW],nextRows[]=new Row[TOTAL_ROW]; // 行数组，每个元素代表一行
    private CustomDate mShowDate; // 自定义的日期，包括year,month,day
    private OnCellClickListener mCellClickListener; // 单元格点击回调事件  
    private int touchSlop; //  

    private Cell mClickCell;  
    private Cell mClickDownCell;
    private float mDownX;
    private float mDownY;

    private List<ScheduleOfMonth> scheduleOfMonths;
    private List<ScheduleOfMonth> nextMonthSchedules;

    private boolean isDataLoad;//数据是否已经加载了
    private boolean isShowNextMonthTitle;//是否显示下一月的月份标题
    private String nextMonthTitle;

  
    /** 
     * 单元格点击的回调接口 
     *  
     */
    public interface OnCellClickListener {  
        void ensureDate(CustomDate date,ScheduleOfMonth scheduleOfMonth);//选中排班表中有的日期
    }

  
    public CalendarCard(Context context, AttributeSet attrs, int defStyleAttr) {  
        super(context, attrs, defStyleAttr);  
        init(context);  
    }  
  
    public CalendarCard(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context);  
    }  
  
    public CalendarCard(Context context) {  
        super(context);  
        init(context);  
    }  
  
    public CalendarCard(Context context, OnCellClickListener listener) {  
        super(context);  
        this.mCellClickListener = listener;  
        init(context);  
    }

    public CalendarCard(Context context, OnCellClickListener listener,List<ScheduleOfMonth> scheduleOfMonths,List<ScheduleOfMonth> nextMonthSchedules,CustomDate showDate) {
        super(context);
        this.mCellClickListener = listener;
        this.scheduleOfMonths = scheduleOfMonths;
        this.nextMonthSchedules=nextMonthSchedules;
        mShowDate = (showDate == null ? new CustomDate() : showDate);
        isShowNextMonthTitle = (nextMonthSchedules == null ? false : true);
        nextMonthTitle = (mShowDate.getMonth()+1)+"月 "+mShowDate.getYear()+"年";
//        Log.i("skyjaj", "scheduleofmonths: "+scheduleOfMonths);
        init(context);
    }



    private void init(Context context) {  
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        mCirclePaint.setStyle(Paint.Style.FILL);  
        mCirclePaint.setColor(Color.parseColor("#F24949")); // 红色圆形
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.WHITE);
        mBgPaint = new Paint((Paint.ANTI_ALIAS_FLAG));
        mBgPaint.setColor(Color.parseColor("#009a00"));
        mBgPaint.setStyle(Paint.Style.FILL); //实心填充
        isDataLoad = false;
//        getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。
//      如果小于这个距离就不触发移动控件，如viewpager就是用这个距离来判断用户是否翻页
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();  

        initDate();  
    }  
  
    private void initDate() {
        fillData();
        if (isShowNextMonthTitle) {
            fillNextMonthData();
        }
    }

    //装载数据
    private void fillData() {
        int monthDay =mShowDate.day; // 今天
//        Log.i("skyjaj", "monthday :" + monthDay);
        int lastMonthDays = DateUtil.getMonthDays(mShowDate.year,  
                mShowDate.month - 1); // 上个月的天数  
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.year,  
                mShowDate.month); // 当前月的天数  
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,
                mShowDate.month);  
        boolean isCurrentMonth = false;  
        if (DateUtil.isCurrentMonth(mShowDate)) {  
            isCurrentMonth = true;  
        }  
        int day = 0;  
        for (int j = 0,k=0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL; // 单元格位置  
                // 这个月的  
                if (position >= firstDayWeek  
                        && position < firstDayWeek + currentMonthDays) {  
                    day++;  
                    rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(  
                            mShowDate, day), State.CURRENT_MONTH_DAY, i*mCellSpace, j*mCellSpace);
                    // 今天
                    if (isCurrentMonth && day == monthDay ) {
                        CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
                        rows[j].cells[i] = new Cell(date, State.TODAY, i*mCellSpace, j*mCellSpace);
                    }
  
                    if (isCurrentMonth && day > monthDay) { // 如果比这个月的今天要大，表示还没到  

                        if (scheduleOfMonths != null && k < scheduleOfMonths.size()) {
                            rows[j].cells[i] = new Cell(
                                    CustomDate.modifiDayForObject(mShowDate, day),
                                    State.UNREACH_DAY, i*mCellSpace, j*mCellSpace, scheduleOfMonths.get(k));
                            k++;
                       } else {
                            rows[j].cells[i] = new Cell(
                                    CustomDate.modifiDayForObject(mShowDate, day),
                                    State.UNREACH_DAY, i*mCellSpace, j*mCellSpace);

                        }
                    }
  
                    // 过去一个月  
                } else if (position < firstDayWeek) {  
                    rows[j].cells[i] = new Cell(new CustomDate(mShowDate.year,  
                            mShowDate.month - 1, lastMonthDays  
                                    - (firstDayWeek - position - 1)),  
                            State.PAST_MONTH_DAY, i*mCellSpace, j*mCellSpace);
                    // 下个月
                } else if (position >= firstDayWeek + currentMonthDays) {  
                    rows[j].cells[i] = new Cell((new CustomDate(mShowDate.year,  
                            mShowDate.month + 1, position - firstDayWeek  
                                    - currentMonthDays + 1)),  
                            State.NEXT_MONTH_DAY, i*mCellSpace, j*mCellSpace);
                }
            }  
        }
        invalidate();
    }

    private void fillNextMonthData() {
        Log.i("skyjaj" , "fillnextmonthdata");
        int lastMonthDays = DateUtil.getMonthDays(mShowDate.year,
                mShowDate.month ); // 上个月的天数
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.year,
                mShowDate.month+1); // 当前月的天数
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,
                mShowDate.month+1);
        boolean isCurrentMonth = false;
        int day = 0;
        for (int j = 0, k = 0; j < TOTAL_ROW; j++) {
            nextRows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL; // 单元格位置
                // 这个月的
                if (position >= firstDayWeek
                        && position < firstDayWeek + currentMonthDays) {
                    day++;
                    if (nextMonthSchedules != null && k < nextMonthSchedules.size()) {
                        nextRows[j].cells[i] = new Cell(
                                CustomDate.modifiDayForObject(mShowDate, day),
                                State.UNREACH_DAY, i * mCellSpace, j * mCellSpace + mViewWidth, nextMonthSchedules.get(k), true);
                        k++;
                    } else {
                        nextRows[j].cells[i] = new Cell(
                                CustomDate.modifiDayForObject(mShowDate, day),
                                State.UNREACH_DAY, i * mCellSpace, j * mCellSpace + mViewWidth, null, true);
                    }

                    // 过去一个月
                } else if (position < firstDayWeek) {
                    nextRows[j].cells[i] = new Cell(new CustomDate(mShowDate.year,
                            mShowDate.month - 1, lastMonthDays
                            - (firstDayWeek - position - 1)),
                            State.PAST_MONTH_DAY, i * mCellSpace, j * mCellSpace + mViewWidth, null, true);
                    // 下个月
                } else if (position >= firstDayWeek + currentMonthDays) {
                    nextRows[j].cells[i] = new Cell((new CustomDate(mShowDate.year,
                            mShowDate.month + 1, position - firstDayWeek
                            - currentMonthDays + 1)),
                            State.NEXT_MONTH_DAY, i * mCellSpace, j * mCellSpace + mViewWidth, null, true);
                }
            }
        }
        invalidate();
    }

    @Override  
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);

            }
        }


        for (int i = 0; i < TOTAL_ROW; i++) {
            if (nextRows[i] != null) {
                nextRows[i].drawCells(canvas);
            }
        }

        //列线条
        for (int j = 0,col=0; j <=TOTAL_COL; j++) {
            col=j*mCellSpace;
            canvas.drawLine(col,0,col,6*mCellSpace,mLinePaint);
        }

        //行线条
        for (int k = 0,row=0; k <=TOTAL_ROW; k++) {
            row = k * mCellSpace;
            canvas.drawLine(0,row,7*mCellSpace,row,mLinePaint);
        }

        if (isShowNextMonthTitle) {
            mTextPaint.setColor(Color.GREEN);
            canvas.drawText(nextMonthTitle, 3 * mCellSpace-mTextPaint.measureText(nextMonthTitle)/2, 6 * mCellSpace + mTextPaint.measureText("1", 0, 1) + mCellSpace / 2, mTextPaint);

            //列线条
            for (int j = 0,col=0; j <=TOTAL_COL; j++) {
                col=j*mCellSpace;
                canvas.drawLine(col,7*mCellSpace,col,6*mCellSpace+7*mCellSpace,mLinePaint);
            }

            //行线条
            for (int k = 0,row=0; k <=TOTAL_ROW; k++) {
                row = k * mCellSpace+mViewWidth;
                canvas.drawLine(0,row,7*mCellSpace,row,mLinePaint);
            }
            mTextPaint.setColor(Color.DKGRAY);
        }

    }
  
    @Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(w, h, oldw, oldh);  
        mViewWidth = w;  
        mViewHeight = h;  
        mCellSpace = Math.min(mViewHeight / TOTAL_ROW, mViewWidth / TOTAL_COL);  
        mTextPaint.setTextSize(mCellSpace / 3);
        fillData();
        if (isShowNextMonthTitle) {
            fillNextMonthData();
        }
//        Log.i("skyjaj", "on size changed mCellSpace:"+mCellSpace);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, 2 * widthMeasureSpec + mCellSpace);
//        Log.i("skyjaj", "onMeasure..");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {  
        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            mDownX = event.getX();  
            mDownY = event.getY();
            int cold = (int) (mDownX / mCellSpace);
            int rowd = (int) (mDownY / mCellSpace);
            measureClickCell(cold, rowd,MotionEvent.ACTION_DOWN);
            break;
        case MotionEvent.ACTION_UP:  
            float disX = event.getX();
            float disY = event.getY();
            int col = (int) (mDownX / mCellSpace);
            int row = (int) (mDownY / mCellSpace);
            int controlCol = col*mCellSpace;
            int controlRow = row*mCellSpace;

            if ((disX < (controlCol + mCellSpace) && (disX >=controlCol) && disY > controlRow && disY < controlRow + mCellSpace)) {
                measureClickCell(col, row, MotionEvent.ACTION_UP);
            }

            break;
        case MotionEvent.ACTION_MOVE:
            float mDisX = event.getX();
            float mDisY = event.getY();
            int mCol = (int) (mDownX / mCellSpace);
            int mRow = (int) (mDownY / mCellSpace);
            if (!(mDisX < mCol * mCellSpace + mCellSpace && mDisX > mCol * mCellSpace && mDisY > mRow * mCellSpace && mDisY < mRow * mCellSpace + mCellSpace)) {
                mClickDownCell = null;
                invalidate();
            }
            break;
        default:  
            break;  
        }  
  
        return true;  
    }





    /**
     * 计算点击的单元格
     *
     * @param col
     * @param row
     * @param event
     */
    private void measureClickCell(int col, int row, int event) {
//        Log.i("skyjaj", col + " row " + row);
        //row==7去掉第七行,已经用来显示下一月份标题
        if (col >= TOTAL_COL || row >= TOTAL_ROW * 2+1 || row == 6)
            return;
        //如果下一个月没有信息直接返回
        if (!isShowNextMonthTitle && (col >= TOTAL_COL || row >= TOTAL_ROW)) {
            return;
        }
        int index = row -6;
        if (event == MotionEvent.ACTION_UP) {

            if (row < TOTAL_ROW && rows[row] != null) {
                mClickCell = new Cell(rows[row].cells[col].date,
                        rows[row].cells[col].state, rows[row].cells[col].i,
                        rows[row].cells[col].j);

                CustomDate date = rows[row].cells[col].date;
                date.week = col;
                mCellClickListener.ensureDate(date, rows[row].cells[col].scheduleOfMonth);
                // 刷新界面
                update();
            } else if (row > TOTAL_ROW && index >= 0 && nextRows[index-1] != null) {
                mClickCell = new Cell(nextRows[index-1].cells[col].date,
                        nextRows[index-1].cells[col].state, rows[index-1].cells[col].i,
                        nextRows[index-1].cells[col].j);

                CustomDate date = nextRows[index-1].cells[col].date;
                date.week = col;
                mCellClickListener.ensureDate(date, nextRows[index-1].cells[col].scheduleOfMonth);
                // 刷新界面
                update();
            }

        } else if (event == MotionEvent.ACTION_DOWN) {

            if (row < TOTAL_ROW && rows[row] != null) {
                mClickDownCell = new Cell(rows[row].cells[col].date,
                        rows[row].cells[col].state, rows[row].cells[col].i,
                        rows[row].cells[col].j);
                // 刷新界面
                update();
            } else if (row > TOTAL_ROW && index >= 0 && nextRows[index-1] != null) {
                mClickDownCell = new Cell(nextRows[index-1].cells[col].date,
                        nextRows[index-1].cells[col].state, nextRows[index-1].cells[col].i,
                        nextRows[index-1].cells[col].j);
                // 刷新界面
                update();
            }

        }
    }
  
    /** 
     * 组元素 
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
        public CustomDate date;  
        public State state;
        public int i;
        public int j;
        public boolean isNext;
        public ScheduleOfMonth scheduleOfMonth;
  
        public Cell(CustomDate date, State state, int i, int j) {  
            super();  
            this.date = date;  
            this.state = state;  
            this.i = i;  
            this.j = j;  
        }

        public Cell(CustomDate date, State state, int i, int j,ScheduleOfMonth scheduleOfMonth) {
            super();
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
            this.scheduleOfMonth = scheduleOfMonth;
        }


        public Cell(CustomDate date, State state, int i, int j,ScheduleOfMonth scheduleOfMonth,boolean isNext) {
            super();
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
            this.scheduleOfMonth = scheduleOfMonth;
            this.isNext = isNext;
        }

        public void drawSelf(Canvas canvas) {
            switch (state) {  
            case TODAY: // 今天  
                mTextPaint.setColor(Color.parseColor("#fffffe"));  
                canvas.drawCircle((float) (i + 0.5)+mCellSpace/2, (float) (j + 0.5)+mCellSpace/2, mCellSpace / 3,mCirclePaint);
                break;  
            case CURRENT_MONTH_DAY: // 当前月日期
                    mTextPaint.setColor(Color.BLACK);
                break;
            case PAST_MONTH_DAY: // 过去一个月  
            case NEXT_MONTH_DAY: // 下一个月  
                mTextPaint.setColor(Color.parseColor("#d1d4d1"));
                //绘制背景
//                canvas.drawRect(i * mCellSpace, j * mCellSpace, i * mCellSpace + mCellSpace, j * mCellSpace + mCellSpace, mTextPaint);
                canvas.drawRect(i, j , i + mCellSpace, j + mCellSpace, mTextPaint);
                mTextPaint.setColor(Color.GRAY);
                break;  
            case UNREACH_DAY: // 还未到的天
                if (scheduleOfMonth != null) {
                    mTextPaint.setColor(Color.BLUE);
                }else{
                    mTextPaint.setColor(Color.WHITE);
                }
                break;
            default:  
                break;  
            }



            if (mClickCell!=null&&i==mClickCell.i && j==mClickCell.j) {
                canvas.drawRect(i,j,i+mCellSpace,j+mCellSpace,mBgPaint);
            }

            if (mClickDownCell != null && i == mClickDownCell.i && j == mClickDownCell.j) {
                canvas.drawRect(i,j,i+mCellSpace,j+mCellSpace,mBgPaint);
            }

            // 绘制文字  
            String content = date.day + "";

            if (isNext&&isShowNextMonthTitle) {
//                Log.i("skyjaj", "isShowNextMonthTitle .."+i+" j:"+j+" content"+content);
                mTextPaint.setColor(Color.DKGRAY);
                canvas.drawText(content,
                        (float) ((i + 0.5) - mTextPaint
                                .measureText(content) / 2) + mCellSpace / 2, (float) ((j + 0.7) + mTextPaint
                                .measureText(content, 0, 1) / 2) + mCellSpace / 2, mTextPaint);
                mTextPaint.setColor(Color.WHITE);
            } else {

                mTextPaint.setColor(Color.DKGRAY);
                canvas.drawText(content,
                        (float) ((i + 0.5) - mTextPaint
                                .measureText(content) / 2)+mCellSpace/2, (float) ((j + 0.7) + mTextPaint
                                .measureText(content, 0, 1) / 2)+mCellSpace/2, mTextPaint);
                mTextPaint.setColor(Color.WHITE);
            }

            if (scheduleOfMonth != null) {
                //早班为1 ，晚班为2 ，休息为3
                mTextPaint.setTextSize(mCellSpace / 4);
                float dis = mTextPaint.measureText("早班");
                if (scheduleOfMonth.getPeriodOfTime() == 1) {
                    canvas.drawText("早班", i + mCellSpace - dis, j+dis/2,mTextPaint );
                }else if (scheduleOfMonth.getPeriodOfTime() == 2) {
                    canvas.drawText("晚班",  i + mCellSpace - dis, j+dis/2,mTextPaint );
                } else if (scheduleOfMonth.getPeriodOfTime() == 3) {
//                    canvas.drawText("休息", i * mCellSpace + mCellSpace - dis, j * mCellSpace + dis / 2, mTextPaint);
                    canvas.drawText("休息", i + mCellSpace - dis, j+dis/2,mTextPaint );
                }
                mTextPaint.setTextSize(mCellSpace / 3);
            }
        }


    }
  
    /** 
     *  
     * 单元格的状态 当前月日期，过去的月的日期，下个月的日期
     */  
    enum State {
        TODAY,CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, UNREACH_DAY;
    }  
  

    public void update() {  
        fillData();
        invalidate();  
    }  
  
}  