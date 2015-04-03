package zq.whu.zhangshangwuda.ui.emptyclassroom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import zq.whu.zhangshangwuda.adapter.EListAdapter;
import zq.whu.zhangshangwuda.adapter.WheelViewAdapter;
import zq.whu.zhangshangwuda.entity.Classroom;
import zq.whu.zhangshangwuda.entity.EmptyClassroomInfo;
import zq.whu.zhangshangwuda.tools.GsonUtils;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.tools.SharedPreferencesUtils;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.find.FindContentActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

public class EmptyClassroomFragment extends Fragment {

    
	private static final String FILE_NAME = "info";
	private static final String LESSONS_INFO_URL = "http://115.29.17.73:8001/lessons/get.json";
	@ViewInject(R.id.rl_cancel_ok)
	private RelativeLayout rlCancelOk;// 取消，确定按钮所在布局

	@ViewInject(R.id.btn_cancel)
	private Button btnCnacel;// 取消按钮

	@ViewInject(R.id.btn_ok)
	// 确定按钮
	private Button btnOk;

	@ViewInject(R.id.place_lessons_view)
	private View placeLessonView;// 地点和时间的显示

	@ViewInject(R.id.ib_modify_place)
	private ImageButton ibModiferPlace;// 修改地点按钮

	@ViewInject(R.id.ib_modify_class_num)
	private ImageButton ibModiferClassNum;// 修改第几节课按钮

	@ViewInject(R.id.tv_place)
	private TextView tvPlace;// 显示地点

	@ViewInject(R.id.tv_class_num)
	private TextView tvClassNum;// 显示第几节课

	@ViewInject(R.id.expandableListView)
	private ExpandableListView eListView;

	@ViewInject(R.id.wg_place_leseons_choose)
	private WheelGroup wgPlaceLessonsChoose;

	private List<String> AREAS_LIST;// 装所有的区名

	private List<List<String>> BUILDINGS_LIST;// 装所有的教学楼名字

	private List<List<List<Classroom>>> ALL_CLASSROOM_STATE_LIST;// 存放全校所有教室的空闲情况

	// 当前选择
	private List<String> areasList;

	private List<List<String>> buildingsList;

	private List<List<List<Classroom>>> allClassroomStateList;

	private EListAdapter mEListAdapter;

	private Context mContext;

	private WheelView buildingWheelView;
	private WheelView areaWheelView;
	private WheelView fromWheelView;
	private WheelView toWheelView;

	private String[][] buildingsArray = {
			{ "教一", "教三", "教四", "教五", "数", "新外", "枫", "法", "老外", "计" },
			{ "一教", "十教", "十一教", "四教", "五教", "大创", }, { "教一", "教二", "附三" },
			{ "一教", "二教", "三教", "五教", "六教", "七教" }, { "教学楼" } };
	
	private  String[] areasArray={"1区","2区","3区","4区","国软"};
	private String[] fromLessonsArray={"第1节","第2节","第3节","第4节","第5节","第6节","第7节","第8节","第9节","第10节","第11节","第12节","第13节"};
	private String[] toLessonsArray={"到1节","到2节","到3节","到4节","到5节","到6节","到7节","到8节","到9节","到10节","到11节","到12节","到13节"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_empty_classroom, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		  int nowWeek = LessonsTool.getNowWeek(getActivity());
		  FindContentActivity.FindActivityActionBar.setSubtitle("第" + nowWeek +
		 "周"); FindContentActivity.FindActivityActionBar
		 .setTitle(R.string.empty_classroom); initData();
		 
	}

	

	
	private void initData() {
		// 初始化字段
		ALL_CLASSROOM_STATE_LIST = new ArrayList<List<List<Classroom>>>();
		BUILDINGS_LIST = new ArrayList<List<String>>();
		AREAS_LIST = new ArrayList<String>();
		areasList = new ArrayList<String>();
		buildingsList = new ArrayList<List<String>>();
		allClassroomStateList = new ArrayList<List<List<Classroom>>>();
		mContext = getActivity();
		AREAS_LIST = Arrays.asList(areasArray);
		for (int i = 0; i < buildingsArray.length; i++) {
			BUILDINGS_LIST.add(Arrays.asList(buildingsArray[i]));
		}
		String resultJson = SharedPreferencesUtils.getString(mContext,
				FILE_NAME, LESSONS_INFO_URL);
		if (!TextUtils.isEmpty(resultJson)) {
			processData(resultJson);
		}
		accessInternet();
		// 给控件设置监听
		setListener();

	}

	private void initPlaceLessonsChooseWheelGroup() {
		List<WheelView> wheelContainer = wgPlaceLessonsChoose
				.getWheelContainer();
		areaWheelView = wheelContainer.get(0);
		areaWheelView.setAdapter(new WheelViewAdapter(AREAS_LIST, 2));
		areaWheelView.setId(0);
		areaWheelView.setVisibleItems(3);
		areaWheelView.addChangingListener(new WheeViewChanangeListener());

		buildingWheelView = wheelContainer.get(1);
		buildingWheelView.setAdapter(new WheelViewAdapter(
				BUILDINGS_LIST.get(0), 3));
		buildingWheelView.setVisibleItems(3);
		buildingWheelView.setId(1);
		buildingWheelView.addChangingListener(new WheeViewChanangeListener());

		fromWheelView = wheelContainer.get(2);
		fromWheelView.setAdapter(new WheelViewAdapter(Arrays
				.asList(fromLessonsArray), 4));
		fromWheelView.setId(2);
		fromWheelView.addChangingListener(new WheeViewChanangeListener());

		toWheelView = wheelContainer.get(3);
		toWheelView.setAdapter(new WheelViewAdapter(
				Arrays.asList(toLessonsArray), 4));
		toWheelView.setId(3);
		toWheelView.addChangingListener(new WheeViewChanangeListener());
	}

	private void setListener() {
		ibModiferClassNum.setOnClickListener(new ButtonOnclickListener());
		ibModiferPlace.setOnClickListener(new ButtonOnclickListener());
		btnCnacel.setOnClickListener(new ButtonOnclickListener());
		btnOk.setOnClickListener(new ButtonOnclickListener());
		tvPlace.setOnClickListener(new ButtonOnclickListener());
		tvClassNum.setOnClickListener(new ButtonOnclickListener());
	}

	private void processData(String json) {

		ALL_CLASSROOM_STATE_LIST = getAllClassroomFreeTime(1, 13);

		int buildingNum = SharedPreferencesUtils.getInt(mContext, FILE_NAME,
				"buildingNum", -1);
		int areaNum = SharedPreferencesUtils.getInt(mContext, FILE_NAME,
				"areaNum", -1);
		int from = SharedPreferencesUtils.getInt(mContext, FILE_NAME, "from",
				-1);
		int to = SharedPreferencesUtils.getInt(mContext, FILE_NAME, "to", -1);

		// 设置adapter
		mEListAdapter = new EListAdapter(mContext, areasList, buildingsList,
				allClassroomStateList);
		if (buildingNum == -1 && areaNum == -1 && from == -1 && to == -1) {
			areasList.clear();
			areasList.addAll(AREAS_LIST);
			buildingsList.addAll(BUILDINGS_LIST);
			allClassroomStateList.addAll(ALL_CLASSROOM_STATE_LIST);
		} else {
			updateShow(areaNum, buildingNum, from, to);
		}

		eListView.setAdapter(mEListAdapter);
		// 初始化地方，课时选择wheelgroup
		initPlaceLessonsChooseWheelGroup();
	}

	// 查寻某个时段全校教室的空闲情况 这个方法需在访问网络成功后调用，以保证数据的更新
	public List<List<List<Classroom>>> getAllClassroomFreeTime(int from, int to) {
		String json = SharedPreferencesUtils.getString(mContext, FILE_NAME,
				LESSONS_INFO_URL);
		EmptyClassroomInfo resultBean = GsonUtils.getBean(json,
				EmptyClassroomInfo.class);
		Map<String, TreeMap<String, TreeMap<String, List<String>>>> areasMap = resultBean
				.getInfo();
		// 所有区名
		Set<String> areaNameSet = areasMap.keySet();
		ArrayList<List<List<Classroom>>> allClassroomStateList = new ArrayList<List<List<Classroom>>>();
		for (Iterator<String> areaNameIt = areaNameSet.iterator(); areaNameIt
				.hasNext();) {
			Map<String, TreeMap<String, List<String>>> buildingsMap = areasMap
					.get(areaNameIt.next());
			Set<String> buildingNameSet = buildingsMap.keySet();
			// 盛放一个区的所有教室空闲情况
			List<List<Classroom>> areaClassroomStateList = new ArrayList<List<Classroom>>();
			for (Iterator<String> buildingNameIt = buildingNameSet.iterator(); buildingNameIt
					.hasNext();) {
				Map<String, List<String>> classroomsMap = buildingsMap
						.get(buildingNameIt.next());
				Set<String> classroomNameSet = classroomsMap.keySet();
				// 盛放一栋楼的所有的教室空闲情况
				List<Classroom> buildingClassroomStateList = new ArrayList<Classroom>();
				for (Iterator<String> classroomNameIt = classroomNameSet
						.iterator(); classroomNameIt.hasNext();) {
					String classroomName = classroomNameIt.next();
					List<String> lessonStateList = classroomsMap
							.get(classroomName);
					Classroom classroom = new Classroom(classroomName,
							lessonStateList);
					classroom.query(from, to);
					// 如果有空闲时段才添加
					if (classroom.isFree()) {
						buildingClassroomStateList.add(classroom);
					}
					Collections.sort(buildingClassroomStateList);
				}
				areaClassroomStateList.add(buildingClassroomStateList);
			}
			allClassroomStateList.add(areaClassroomStateList);
		}
		return allClassroomStateList;
	}

	// 访问网络获取数据
	private void accessInternet() {
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, LESSONS_INFO_URL,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// 数据写入sp中缓存

						SharedPreferencesUtils.putString(mContext, FILE_NAME,
								LESSONS_INFO_URL, responseInfo.result);
						processData(responseInfo.result);

					}

					@Override
					public void onFailure(HttpException error, String msg) {

					}
				});
	}

	private class ButtonOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.tv_place:
			case R.id.tv_class_num:
			case R.id.ib_modify_place:
			case R.id.ib_modify_class_num:

				rlCancelOk.setVisibility(View.VISIBLE);
				wgPlaceLessonsChoose.setVisibility(View.VISIBLE);
				FindContentActivity.FindActivityActionBar.hide();
				placeLessonView.setVisibility(View.GONE);
				break;
			case R.id.btn_cancel:
				btnCancleEvent();

				break;
			case R.id.btn_ok:
				btnCancleEvent();
				List<WheelView> wheelContainer = wgPlaceLessonsChoose
						.getWheelContainer();
				int areaNum = wheelContainer.get(0).getCurrentItem();
				int buildingNum = wheelContainer.get(1).getCurrentItem();
				int from = wheelContainer.get(2).getCurrentItem() + 1;
				int to = wheelContainer.get(3).getCurrentItem() + 1;
				updateShow(areaNum, buildingNum, from, to);
				// 记录选择的状况，以便下次启动时加载
				SharedPreferencesUtils.putInt(mContext, FILE_NAME, "areaNum",
						areaNum);
				SharedPreferencesUtils.putInt(mContext, FILE_NAME,
						"buildingNum", buildingNum);
				SharedPreferencesUtils
						.putInt(mContext, FILE_NAME, "from", from);
				SharedPreferencesUtils.putInt(mContext, FILE_NAME, "to", to);

				break;
			default:
				break;
			}
		}
	}

	// 选取指定的区域教室，时段后，用来更新一些显示信息
	private void updateShow(int areaNum, int buildingNum, int from, int to) {
		areasList.clear();
		areasList.add(AREAS_LIST.get(areaNum));
		buildingsList.clear();
		ArrayList<String> buliding = new ArrayList<String>();
		buliding.add(BUILDINGS_LIST.get(areaNum).get(buildingNum));
		buildingsList.add(buliding);
		allClassroomStateList.clear();
		List<List<List<Classroom>>> allClassroomFreeTime = getAllClassroomFreeTime(
				from, to);
		List<List<Classroom>> buildingClassroomState = new ArrayList<List<Classroom>>();
		buildingClassroomState.add(allClassroomFreeTime.get(areaNum).get(
				buildingNum));
		allClassroomStateList.add(buildingClassroomState);
		mEListAdapter.notifyDataSetChanged();
		tvPlace.setText(AREAS_LIST.get(areaNum) + "  "
				+ BUILDINGS_LIST.get(areaNum).get(buildingNum));
		tvClassNum.setText(from + " 到  " + to + " 节 ");

	}

	private void btnCancleEvent() {
		rlCancelOk.setVisibility(View.GONE);
		wgPlaceLessonsChoose.setVisibility(View.GONE);
		FindContentActivity.FindActivityActionBar.show();
		placeLessonView.setVisibility(View.VISIBLE);

	}

	private class WheeViewChanangeListener implements OnWheelChangedListener {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			switch (wheel.getId()) {
			case 0:
				// 如果选在国软这里，设置不能循环
				if (newValue == 4) {
					buildingWheelView.setCyclic(false);
				} else {
					buildingWheelView.setCyclic(true);
				}
				buildingWheelView.setAdapter(new WheelViewAdapter(
						BUILDINGS_LIST.get(newValue), 3));
				buildingWheelView.setCurrentItem(0);
				break;
			case 1:

				break;
			case 2:
				if (toWheelView.getCurrentItem() < newValue) {
					toWheelView.setCurrentItem(newValue);
				}
				break;
			case 3:
				if (fromWheelView.getCurrentItem() > newValue) {
					fromWheelView.setCurrentItem(newValue);
				}
				break;
			default:
				break;
			}
		}

	}

}
