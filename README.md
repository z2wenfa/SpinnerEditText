# SpinnerEditText<T>
Android一个可以下拉模糊匹配的Editext

# 效果
![演示图片]()


# 实现功能

 1. 点击右侧图标触发下拉刷新。
 2. 编辑框输入文本能够自动进行模糊匹配,筛选列表后自动显示。
 3. 点击下拉框自动获得传入的范型类的实体。

 
# 常见问题解决
 1. 进入界面后自动触发焦点事件,下拉选择框自动出现:
 
 	> 
 	在父布局添加:<br>
 	android:focusable="true" 
   android:focusableInTouchMode="true"
