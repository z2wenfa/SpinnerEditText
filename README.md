# SpinnerEditText<T>
Android一个可以下拉模糊匹配的Editext

#	实现效果

 ![实现效果](https://github.com/z2wenfa/SpinnerEditText/blob/master/screenshot/test.gif)


 ![自动判断状态效果](https://github.com/z2wenfa/SpinnerEditText/blob/master/screenshot/SpinnerEditTextShow2.gif)

# 实现功能

 1. 点击右侧图标触发下拉刷新。
 2. 编辑框输入文本能够自动进行模糊匹配,筛选列表后自动显示。
 3. 点击下拉框自动获得传入的范型类的实体。
 4. 可以设置是否自动根据编辑框文本是否为空来判断状态是否异常(边框Stoke颜色自动变为红色):
 
         
           //设置根据文本是否为空判断是否异常    
           set_exception.autoCheckStatusByTextIsEmpty(true);
       
 
# 常见问题解决
 1. 进入界面后自动触发焦点事件,下拉选择框自动出现:
  
 	在父布局添加:<br>
 	android:focusable="true" <br>
  android:focusableInTouchMode="true"
