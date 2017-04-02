package com.example.z2wenfa.spinneredittextdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SpinnerEditText<BaseBean> set_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
