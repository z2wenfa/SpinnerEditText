package com.example.z2wenfa.spinneredittextdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.z2wenfa.spinneredittext.SpinnerEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SpinnerEditText<BaseBean> set_name;

    private SpinnerEditText<BaseBean> set_exception;

    private SpinnerEditText<BaseBean> set_diy_att;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSpinnerEditText();

        initCheckExceptionSpinnerEditText();

        initDIYAttSpinnerEditText();
    }

    private void initSpinnerEditText() {
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


    private void initCheckExceptionSpinnerEditText() {
        set_exception = (SpinnerEditText<BaseBean>) findViewById(R.id.set_exception);

        set_exception.setRightCompoundDrawable(R.drawable.vector_drawable_arrowdown);

        set_exception.setOnItemClickListener(new SpinnerEditText.OnItemClickListener<BaseBean>() {
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
        set_exception.setNeedShowSpinner(true);

        set_exception.setList(baseBeanList);
        set_exception.setSelection(0);


        //设置根据文本是否为空判断是否异常
        set_exception.autoCheckStatusByTextIsEmpty(true);
    }

    private void initDIYAttSpinnerEditText(){
        set_diy_att= (SpinnerEditText<BaseBean>) findViewById(R.id.set_div_att);
        set_diy_att.setRightCompoundDrawable(R.drawable.vector_drawable_arrowdown);

        set_diy_att.setOnItemClickListener(new SpinnerEditText.OnItemClickListener<BaseBean>() {
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
        set_diy_att.setNeedShowSpinner(true);

        set_diy_att.setList(baseBeanList);
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
