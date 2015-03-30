package zq.whu.zhangshangwuda.tools;

import com.google.gson.Gson;

public class GsonUtils {
	public static <T>  T getBean(String json,Class<T> cz){
		Gson gson=new Gson();
		T bean=gson.fromJson(json,cz);
		return bean;
	}
}
