zhangshangwuda
==============

武汉大学自强学堂掌上武大客户端

本软件使用友盟进行统计、在线参数、反馈管理吗，所以需要进行一些必要的配置才能编译

友盟在线参数格式：

1、开学第一天：参数名称term_firstday	参数值例如2014-2-16

2、当前学期课程表连接：lessons_url	参数值例如：http://202.114.74.199/stu/query_stu_lesson.jsp?year=2013&term=%CF%C2&submit=%CF%D4+%CA%BE

PS：你也可以自定义，在LessonsLoginActivity中进行相应修改便可

需要更改的设置：

修改AndroidManifest.xml里面UMENG_APPKEY和UMENG_CHANNEL为自己的友盟KEY

引用的开源项目：

ActionBarSherlock：https://github.com/JakeWharton/ActionBarSherlock

MenuDrawer：https://github.com/SimonVT/android-menudrawer

Android ViewPagerIndicator：https://github.com/JakeWharton/Android-ViewPagerIndicator

Nine Old Androids：https://github.com/JakeWharton/NineOldAndroids

Universal Image Loader for Android：https://github.com/nostra13/Android-Universal-Image-Loader

Android ViewBadger：https://github.com/jgilfelt/android-viewbadger

SwipeBackLayout：https://github.com/Issacw0ng/SwipeBackLayout

View Flow for Android：https://github.com/pakerfeldt/android-viewflow