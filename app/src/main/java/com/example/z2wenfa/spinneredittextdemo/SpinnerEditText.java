package com.example.z2wenfa.spinneredittextdemo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 一个既可以编辑又可以下拉选中的自定义View
 * 1.获得一个列表内容 在编辑框文本变化时 动态显示相关列表内容 (完成)
 * 2.点击列表内容能够修改编辑框显示文本 同时出发文本修改变化事件 文本修改后光标改到文本末尾 (完成)
 * 3.当Foucs发生变化且是选中时显示相关列表内容(完成)
 * 4.点击选中项触发事件 获得选中的Bean
 * <p>
 * Created by z2wenfa on 2017/2/14.
 */

public class SpinnerEditText<T> extends AppCompatEditText {
    private Context context;

    public SpinnerEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SpinnerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SpinnerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);//设置字体大小
        //文本变换事件
        addTextChangedListener(new TextWatchAdapter() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSelectedItem(null);
                if (listPopupWindow != null && SpinnerEditText.this.hasFocus()) {
                    showPopUpwindow(charSequence.toString());
                }
            }
        });

        //焦点变换事件
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (listPopupWindow != null && hasFocus) {
                    showPopUpwindow(getText().toString());
                }

                for (OnFocusChangeListener onFocusChangeListener : onFocusChangeListenerList) {
                    onFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listPopupWindow != null && isFocused()) {
                    showPopUpwindow(getText().toString());
                }
            }
        });


        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.vector_drawable_arrowdown, 0);
        int paddingleft = SystemUtil.dp2px(context, 5);
        int paddingright = SystemUtil.dp2px(context, 10);
        setPadding(paddingleft, 0, paddingright, 0);

        setDrawableRightListener(new DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {


                if (listPopupWindow == null) {
                    return;
                }
                if (popupWindowIsShowing) {
                    listPopupWindow.dismiss();
                    popupWindowIsShowing = false;

                } else {
                    requestFocus();
                    showPopUpwindow("");
                    popupWindowIsShowing = true;
                }

            }
        });

    }


    private boolean popupWindowIsShowing = false;


    private ListPopupWindow listPopupWindow;
    private List<T> itemList = new ArrayList<>();
    private List<T> realShowItemList = new ArrayList<>();
    private ArrayAdapter<T> adapter;


    private void initOrUpdateListPopupWindow() {
        if (listPopupWindow == null) {
            listPopupWindow = new ListPopupWindow(context);
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, realShowItemList);
            listPopupWindow.setAdapter(adapter);
            listPopupWindow.setAnchorView(this);

            listPopupWindow.setWidth(AbsListView.LayoutParams.WRAP_CONTENT);
            listPopupWindow.setHeight(AbsListView.LayoutParams.WRAP_CONTENT);
            listPopupWindow.setModal(false);

            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    T t = realShowItemList.get(i);
                    String selectedContent = t.toString();

                    setSelectedItemPosition(i);
                    SpinnerEditText.this.setSelectedItem(t);
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(t, SpinnerEditText.this, view, i, l, selectedContent);

                    if (!realShowItemList.isEmpty()) {
                        SpinnerEditText.this.setText(selectedContent);
                        SpinnerEditText.this.setSelection(selectedContent.length());
                    } else {
                        setText("");
                    }
                    listPopupWindow.dismiss();
                }


            });

            listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //延时设置popupwindow显示状态
                    handler.sendEmptyMessageDelayed(1, 100);
                }
            });


        }
        adapter.notifyDataSetChanged();
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
            initOrUpdateListPopupWindow();
            listPopupWindow.show();
        } else {
            //隐藏popupwindow
            handler.sendEmptyMessage(2);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    popupWindowIsShowing = false;
                    break;
                case 2:
                    listPopupWindow.dismiss();
                    break;
            }
        }
    };

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
        listPopupWindow.setSelection(selectedItemPosition);
    }

    private DrawableLeftListener mLeftListener;
    private DrawableRightListener mRightListener;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;


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
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mLeftListener != null) {
                    Drawable drawableLeft = getCompoundDrawables()[DRAWABLE_LEFT];
                    if (drawableLeft != null && event.getRawX() <= (getLeft() + drawableLeft.getBounds().width())) {
                        mLeftListener.onDrawableLeftClick(this);
                        return true;
                    }
                }

                if (mRightListener != null) {
                    int eventX = (int) event.getX();
                    if (this.getRight() - eventX < SystemUtil.dp2px(context, 40)) {//这个数字设置成图片的大概宽度
                        mRightListener.onDrawableRightClick(this);

                        return true;
                    }
                }

                break;
        }
        return super.dispatchTouchEvent(event);
    }


}
