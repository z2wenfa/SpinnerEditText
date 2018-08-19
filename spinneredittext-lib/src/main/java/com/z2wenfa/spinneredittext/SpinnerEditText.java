package com.z2wenfa.spinneredittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个既可以编辑又可以下拉选中的自定义View
 * 1.获得一个列表内容 在编辑框文本变化时 动态显示相关列表内容 (完成)
 * 2.点击列表内容能够修改编辑框显示文本 同时出发文本修改变化事件 文本修改后光标改到文本末尾 (完成)
 * 3.当Foucs发生变化且是选中时显示相关列表内容(完成)
 * 4.点击选中项触发事件 获得选中的Bean (完成)
 * 5.移除固定的TextSize值,允许自定义。(完成 2018.8.18)
 * 6.添加pop_textsize设置pop的文本大小,添加pop_textcolor设置pop的文本颜色。(完成 2018.8.18)
 * 7.添加pop_height自定义弹出框的高度,pop_max_height设置pop最大高度。(完成 2018.8.18)
 * <p>
 * Created by z2wenfa on 2017/2/14.
 */

public class SpinnerEditText<T> extends AppCompatEditText {
    private Context context;
    private int childHeight;


    private DrawableLeftListener mLeftListener;
    private DrawableRightListener mRightListener;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_EXCEPTION = 1;

    private boolean isNecessary = false;//是否是必须条件
    private boolean autoCheckStatusByTextIsEmpty = false;//是否自动判断状态通过判断文本是否异常
    private int status = STATUS_NORMAL;//当前显示状态 异常状态编辑框 Stoke颜色设置为红色


    private static final int TYPE_UP = 0;//Pop向上显示
    public static final int TYPE_DOWN = 1;//Pop向下显示

    public int showType = TYPE_UP;//Popupwindow显示类型

    private boolean autoCheckShowType = true;//自动根据Pop的高端选择显示在文本框的上方还是下方

    private boolean forbidShowPopOnce = false;//禁止弹出一次Pop选择框

    private int pop_textColor;
    private float pop_textSize;
    private float pop_minHeight;
    private float pop_maxHeight;

    public SpinnerEditText(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public SpinnerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public SpinnerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }


    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            return false;
        }
        return super.onTextContextMenuItem(id);
    }


    private void init(AttributeSet attrs) {

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerEditText);
            pop_textColor = typedArray.getColor(R.styleable.SpinnerEditText_pop_textcolor, Color.BLACK);
            pop_textSize = typedArray.getDimension(R.styleable.SpinnerEditText_pop_textsize, 0f);
            pop_minHeight = typedArray.getDimension(R.styleable.SpinnerEditText_pop_min_height, 40f);
            pop_maxHeight = typedArray.getDimension(R.styleable.SpinnerEditText_pop_max_height, 0f);
            typedArray.recycle();
        }

        setLongClickable(false);
        childHeight = dp2px(context, 40);

        this.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        this.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        //文本变换事件
        addTextChangedListener(new TextWatchAdapter() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (popupWindow != null && SpinnerEditText.this.hasFocus()) {
                    handler.removeMessages(2);
                    handler.sendEmptyMessage(2);
                }

                if (autoCheckStatusByTextIsEmpty) {
                    if (TextUtils.isEmpty(charSequence.toString())) {
                        setStatus(STATUS_EXCEPTION);
                    } else {
                        setStatus(STATUS_NORMAL);
                    }

                }
            }
        });

        //焦点变换事件
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (popupWindow != null && hasFocus) {
                    betterShow(getText().toString(), 250);
                }

                for (OnFocusChangeListener onFocusChangeListener : onFocusChangeListenerList) {
                    onFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && isFocused()) {
                    betterShow(getText().toString(), 250);
                } else {
                    dismissPopupWindow();
                }
            }
        });


        setRightCompoundDrawable(R.drawable.vector_drawable_arrowdown);
        int paddingleft = dp2px(context, 5);
        int paddingright = dp2px(context, 10);
        setPadding(paddingleft, 0, paddingright, 0);

        setDrawableRightListener(new DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {


                if (popupWindow == null) {
                    return;
                }
                if (popupWindowIsShowing) {
                    popupWindow.dismiss();
                    popupWindowIsShowing = false;

                } else {
                    requestFocus();
                    betterShow("", 0);
                }
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    dismissPopupWindow();
                    showPopUpwindow(msg.obj.toString());
                    popupWindowIsShowing = true;
                    break;
                case 2:
                    setSelectedItem(null);
                    if (popupWindow != null && isFocused()) {
                        betterShow(getText().toString(), 250);
                    } else {
                        dismissPopupWindow();
                    }
                    break;
                case 3:
                    dismissPopupWindow();
                    break;
                case 4:
                    popupWindowIsShowing = false;
                    break;
            }
        }
    };


    //优化过后的Popupwindo显示方法
    private void betterShow(String searchStr, long delayTime) {

        if (searchStr.equals("")) {
            handler.removeMessages(1);
        }

        if (!handler.hasMessages(1, "")) {

            if (forbidShowPopOnce) {
                forbidShowPopOnce = false;
                return;
            }

            Message message = Message.obtain();
            message.what = 1;
            message.obj = searchStr;
            handler.sendMessageDelayed(message, delayTime);
        }

    }

    //设置右侧图标
    public void setRightCompoundDrawable(int resId) {

        int start = dp2px(getContext(), 0);
        int end = dp2px(getContext(), 20);
        Drawable drawable = null;
        if (resId > 0) {
            drawable = getContext().getResources().getDrawable(resId);
            drawable.setBounds(start, start, end, end);
        }

        setCompoundDrawables(null, null, drawable, null);
    }

    private class ViewHolder {
        TextView itemTextView;
    }

    public void setList(String key, List<T> itemList) {
        this.realShowItemList.clear();
        this.realShowItemList.addAll(itemList);
        this.itemList.clear();
        this.itemList.addAll(getFilterList(key, itemList));
        initOrUpdateListPopupWindow();
    }

    public void setList(List<T> itemList) {
        setList("", itemList);
    }

    private Map<String, List<T>> map = new HashMap<>();//根据文本值获得对应的集合

    private List<T> getFilterList(String key, List<T> addList) {
        if (map.get(key) == null) {
            List<T> list = new ArrayList<>();
            list.addAll(addList);
            map.put(key, list);
        } else if (isAlwaysClearList()) {
            map.put(key, addList);
        }
        return map.get(key);
    }

    //显示当前文本下的列表
    private void showPopUpwindow(String text) {
        if (!needShowSpinner) return;


        if (itemList.isEmpty()) {
            return;
        }

        if (!isAlwaysShowAllItemList()) {
            realShowItemList.clear();
            if (text.trim().equals("")) {
                realShowItemList.addAll(itemList);
            } else {
                for (T item : itemList) {
                    String content = item.toString();
                    if (content == null) continue;
                    if (content.toLowerCase().contains(text.toLowerCase())) {
                        realShowItemList.add(item);
                    }
                }
            }
        }


        if (!realShowItemList.isEmpty()) {
            updateHeightAndShow();

        } else {
            handler.sendEmptyMessage(3);
        }
    }


    protected void dismissPopupWindow() {
        if (popupWindow != null)
            popupWindow.dismiss();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T t, SpinnerEditText<T> var1, View var2, int position, long var4, String selectContent);
    }

    private OnItemClickListener<T> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private List<OnFocusChangeListener> onFocusChangeListenerList = new ArrayList<>();

    public void addOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        onFocusChangeListenerList.add(onFocusChangeListener);
    }

    //获得当前值
    public String getValue() {
        String value = getText().toString();
        if (value.equals("null")) {
            return null;
        }
        return value.trim();
    }

    private T selectedItem;

    public T getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(T selectedItem) {
        this.selectedItem = selectedItem;
    }

    private boolean alwaysShowAllItemList;//是否总是显示全部的条目

    public boolean isAlwaysShowAllItemList() {
        return alwaysShowAllItemList;
    }

    public void setAlwaysShowAllItemList(boolean alwaysShowAllItemList) {
        this.alwaysShowAllItemList = alwaysShowAllItemList;
    }

    private boolean alwaysClearList = true;//是否总是清空集合

    public boolean isAlwaysClearList() {
        return alwaysClearList;
    }

    public void setAlwaysClearList(boolean alwaysClearList) {
        this.alwaysClearList = alwaysClearList;
    }

    private boolean needShowSpinner = true;//是否需要显示下拉框

    public boolean isNeedShowSpinner() {
        return needShowSpinner;
    }

    public void setNeedShowSpinner(boolean needShowSpinner) {
        this.needShowSpinner = needShowSpinner;
    }

    public int selectedItemPosition;

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
//        popupWindow.setSelection(selectedItemPosition);
    }

    public void setDrawableLeftListener(DrawableLeftListener listener) {
        this.mLeftListener = listener;
    }

    public void setDrawableRightListener(DrawableRightListener listener) {
        this.mRightListener = listener;
    }

    public interface DrawableLeftListener {
        void onDrawableLeftClick(View view);
    }

    public interface DrawableRightListener {
        void onDrawableRightClick(View view);
    }

    public void dismissRightIcon() {
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        setDrawableRightListener(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mRightListener != null) {

                    int eventX = (int) event.getRawX();
                    int eventY = (int) event.getRawY();

                    Rect rect = new Rect();
                    getGlobalVisibleRect(rect);

                    rect.left = rect.right - dp2px(context, 48);
                    if (rect.contains(eventX, eventY)) {
                        return true;
                    }

                }
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                if (mLeftListener != null) {
                    Drawable drawableLeft = getCompoundDrawables()[DRAWABLE_LEFT];
                    if (drawableLeft != null && event.getRawX() <= (getLeft() + drawableLeft.getBounds().width())) {
                        mLeftListener.onDrawableLeftClick(this);
                        return true;
                    }
                }


                if (mRightListener != null && event.getAction() == MotionEvent.ACTION_UP) {

                    int eventX = (int) event.getRawX();
                    int eventY = (int) event.getRawY();

                    Rect rect = new Rect();
                    getGlobalVisibleRect(rect);

                    rect.left = rect.right - dp2px(context, 48);
                    if (rect.contains(eventX, eventY)) {
                        mRightListener.onDrawableRightClick(this);
                        return true;
                    }

                }

                break;

        }
        return super.onTouchEvent(event);
    }

    //------------------------------初始化Popupwindow ----------------------------
    private static final int TYPE_WRAP_CONTENT = 0, TYPE_MATCH_PARENT = 1;
    private boolean popupWindowIsShowing = false;//当前Popupwindow是否正在显示
    private PopupWindow popupWindow;
    private List<T> itemList = new ArrayList<>();
    private List<T> realShowItemList = new ArrayList<>();//过滤后显示的集合
    private BaseAdapter adapter;
    private ListView listView;
    private FrameLayout popupView;

    public List<T> getRealShowItemList() {
        return realShowItemList;
    }

    public List<T> getItemList() {
        return itemList;
    }


    @SuppressLint("WrongConstant")
    private void initOrUpdateListPopupWindow() {

        if (popupWindow == null) {

            popupWindow = new PopupWindow(context);
            listView = new ListView(context);
            setVerticalScrollBarEnabled(true);
            listView.setBackground(getResources().getDrawable(R.drawable.graybox));
            popupView = new FrameLayout(context);
            popupView.setBackgroundColor(Color.GRAY);
            popupView.addView(listView);
            popupWindow.setContentView(popupView);
//            popupView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

            adapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return realShowItemList.size();
                }

                @Override
                public T getItem(int position) {
                    return realShowItemList.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    ViewHolder holder = null;
                    if (convertView == null) {
                        holder = new ViewHolder();
                        convertView = LayoutInflater.from(context).inflate(R.layout.item_listpopupwindow, null, false);
                        holder.itemTextView = (TextView) convertView.findViewById(R.id.tv);
                        convertView.setTag(holder);

                        if (pop_textColor != 0)
                            holder.itemTextView.setTextColor(pop_textColor);
                        if (pop_textSize != 0)
                            holder.itemTextView.setTextSize(px2dp(context, pop_textSize));

                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }

                    if (realShowItemList != null) {
                        final String itemName = realShowItemList.get(position).toString();
                        if (holder.itemTextView != null) {
                            holder.itemTextView.setText(itemName);
                        }
                    }

                    convertView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            T t = realShowItemList.get(position);

                            int i = position;
                            for (int i1 = 0; i1 < itemList.size(); i1++) {
                                if (itemList.get(i1).toString().equals(t.toString())) {
                                    i = i1;
                                    break;
                                }
                            }

                            String selectedContent = t.toString();

                            setSelectedItemPosition(i);
                            SpinnerEditText.this.setSelectedItem(t);
                            if (onItemClickListener != null)
                                onItemClickListener.onItemClick(t, SpinnerEditText.this, v, i, i, selectedContent);

                            forbidShowPopOnce = true;
                            if (!itemList.isEmpty() && i < itemList.size()) {
                                SpinnerEditText.this.setText(selectedContent);
                                setSelectedItem(itemList.get(i));
                                setSelectedItemPosition(i);
                            } else {
                                setText("");
                            }

                            setSelection(getText().toString().length());
                            handler.removeMessages(1);
                            popupWindow.dismiss();
                        }
                    });

                    return convertView;
                }
            };


            listView.setAdapter(adapter);
            popupWindow.setWidth(AbsListView.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(AbsListView.LayoutParams.WRAP_CONTENT);
            popupWindow.setSoftInputMode(ListPopupWindow.INPUT_METHOD_NEEDED);

            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    handler.sendEmptyMessageDelayed(4, 100);
                }
            });


            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setAnimationStyle(R.style.AnimationFromButtom);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(false);
        }
        adapter.notifyDataSetChanged();
    }

    private int willShowHeight;

    private void updateHeightAndShow() {
        post(new Runnable() {
            @Override
            public void run() {

                willShowHeight = realShowItemList.size() * childHeight;
                if (pop_maxHeight > 0 && willShowHeight > pop_maxHeight) {
                    willShowHeight = (int) pop_maxHeight;
                }

                Rect rect = new Rect();
                getGlobalVisibleRect(rect);

                if (autoCheckShowType) {
                    if (rect.top <= willShowHeight || willShowHeight < pop_maxHeight) {
                        showType = TYPE_DOWN;
                    } else {
                        showType = TYPE_UP;
                    }
                }


                if (willShowHeight < pop_minHeight)
                    willShowHeight = (int) pop_minHeight;

                popupWindow.setHeight(willShowHeight);
                listView.setLayoutParams(new FrameLayout.LayoutParams(getWidth(), willShowHeight));

                initOrUpdateListPopupWindow();


                if (showType == TYPE_UP) {
                    showAsPopUp(SpinnerEditText.this);
                } else {
                    showAsPopBottom(SpinnerEditText.this);
                }

            }
        });
    }

    /**
     * anchor上方
     *
     * @param anchor
     */
    public void showAsPopUp(View anchor) {
        showAsPopUp(anchor, 0, dp2px(context, 0));
    }

    public void showAsPopBottom(View anChor) {
        popupWindow.showAsDropDown(anChor, 0, 0);
    }

    private void showAsPopUp(View anchor, int xoff, int yoff) {
        popupWindow.setAnimationStyle(R.style.AnimationUpPopup);
        popupView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int[] location = new int[2];
        anchor.getLocationInWindow(location);


        //计算显示位置 如果高度不够自动调整到合适高度
        int offsetY = -getHeight() - willShowHeight;
        if (offsetY + location[1] < 0) {
            popupWindow.setHeight(location[1] - getHeight() / 2);
            listView.setLayoutParams(new FrameLayout.LayoutParams(getWidth(), location[1] - getHeight() / 2));
        }

        popupWindow.showAsDropDown(anchor, 0, offsetY);
    }

    public void dismissPop() {
        if (popupWindow != null)
            popupWindow.dismiss();
    }


    //------------------------------初始化Popupwindow ----------------------------


    /**
     * 根据手机的分辨率从 DP 的单位 转成为PX(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px转换为与之相等的dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //-----------设置自动判断Popupwindow显示类型---------------------

    //手动设置显示类型 自动判断显示类型失效
    public void setShowType(int showType) {
        this.showType = showType;
        autoCheckShowType = false;
    }

    //设置是否自动判断显示类型
    public void setAutoCheckShowType(boolean autoCheckShowType) {
        this.autoCheckShowType = autoCheckShowType;
    }

    //-----------设置自动判断Popupwindow显示类型---------------------

    //-------------------根据文本值为空判断状态是否为异常-----------------

    //获得当前编辑框的状态是否异常
    public int getStatus() {
        return status;
    }

    //设置自动根据文本内容是否为空 设置是否异常
    public void autoCheckStatusByTextIsEmpty(Boolean autoCheckStatusByTextIsEmpty) {
        this.autoCheckStatusByTextIsEmpty = autoCheckStatusByTextIsEmpty;
        if (TextUtils.isEmpty(getText())) {
            setStatus(STATUS_EXCEPTION);
        } else {
            setStatus(STATUS_NORMAL);
        }
    }

    //设置当前SpinnerEdit的状态
    public void setStatus(int status) {
        if (status == STATUS_NORMAL) {
            this.status = STATUS_NORMAL;
            setBackgroundResource(R.drawable.whitebox);

        } else if (status == STATUS_EXCEPTION) {
            this.status = STATUS_EXCEPTION;
            setBackgroundResource(R.drawable.whitebox_with_readstroke);
        }
    }

    public void setNecessary(boolean necessary) {
        isNecessary = necessary;
    }

    public boolean isNecessary() {
        return isNecessary;
    }
    //-------------------根据文本值为空判断状态是否为异常-----------------
}
