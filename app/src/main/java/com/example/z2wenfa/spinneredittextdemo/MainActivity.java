package com.example.z2wenfa.spinneredittextdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.z2wenfa.spinneredittext.SpinnerEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SpinnerEditText<BaseBean> set_name;

    private SpinnerEditText<BaseBean> set_exception;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSpinnerEditText1();

        initSpinnerEditText2();
    }

    private void initSpinnerEditText1() {
        set_name = (SpinnerEditText<BaseBean>) findViewById(R.id.set_name);

        set_name.setRightCompoundDrawable(R.drawable.vector_drawable_arrowdown);

        set_name.setOnItemClickListener(new SpinnerEditText.OnItemClickListener<BaseBean>() {
            @Override
            public void onItemClick(BaseBean baseBean, SpinnerEditText<BaseBean> var1, View var2, int position, long var4, String selectContent) {
                showToast("名称:" + baseBean.Name + " Id:" + baseBean.Id);
            }
        });

        List<BaseBean> baseBeanList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            BaseBean baseBean = new BaseBean();
            baseBean.Name = "学生:" + i;
            baseBean.Id = i;
            baseBeanList.add(baseBean);
        }
        set_name.setNeedShowSpinner(true);

        set_name.setList(baseBeanList);
        set_name.setSelection(0);
    }


    private void initSpinnerEditText2() {
        set_exception = (SpinnerEditText<BaseBean>) findViewById(R.id.set_exception);

        set_exception.setRightCompoundDrawable(R.drawable.vector_drawable_arrowdown);

        set_exception.setOnItemClickListener(new SpinnerEditText.OnItemClickListener<BaseBean>() {
            @Override
            public void onItemClick(BaseBean baseBean, SpinnerEditText<BaseBean> var1, View var2, int position, long var4, String selectContent) {
                showToast("名称:" + baseBean.Name + " Id:" + baseBean.Id);
            }
        });

        List<BaseBean> baseBeanList = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            BaseBean baseBean = new BaseBean();
            baseBean.Name = "学生:" + i;
            baseBean.Id = i;
            baseBeanList.add(baseBean);
        }
        set_exception.setNeedShowSpinner(true);

        set_exception.setList(baseBeanList);
        set_exception.setSelection(0);


        //设置根据文本是否为空判断是否异常
        set_exception.autoCheckStatusByTextIsEmpty(true);
    }

    public static class BaseBean {
        public String Name;
        public int Id;

        @Override
        public String toString() {
            return Name;
        }
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                .show();
    }
}
