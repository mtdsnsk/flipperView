package com.example.viewflipper;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends ActionBarActivity implements
		View.OnClickListener {

	private ViewFlipper viewFlipper;
	private int ViewFlipperPageNum;
	private int ViewFlipperCurrentPageNum;
	private HorizontalScrollView hsv;
	private LinearLayout hsvLinear;
	private Button[] button;
	private GestureDetector mGestureDetector;
	private TypedArray images;
	private TypedArray colors;
	private TypedArray button_designs;
	private int currentButtonNo;
	private int device_width;
	private int device_height;
	Animation inFromRightAnimation;
	Animation inFromLeftAnimation;
	Animation outToRightAnimation;
	Animation outToLeftAnimation;
	private static final String TAG = "ViewFlipper";

	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// ウィンドウ非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		mGestureDetector = new GestureDetector(this, mOnGestureListener);

		images = getResources().obtainTypedArray(R.array.test_array_drawable);
		colors = getResources().obtainTypedArray(R.array.colors);
		button_designs = getResources().obtainTypedArray(R.array.buttons);

		getDeviceSize();

		// レイアウトの紐付け
		viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
		viewFlipper.setAutoStart(false); // 自動でスライドショーを開始しない
		viewFlipper.setFlipInterval(1000); // 更新間隔(ms単位)
		hsv = (HorizontalScrollView) findViewById(R.id.hsv1);
		hsvLinear = (LinearLayout) findViewById(R.id.hsvLinear);
		// ボタンのインスタンスを作成
		button = new Button[images.length()];
		createButtons();
		// アニメーションをセットする
		setAnimations();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// ボタンのデザインをセットする
		setButtonDesign(button[viewFlipper.getDisplayedChild()]);
	}

	@SuppressLint("NewApi")
	private void getDeviceSize() {
		// 画面サイズを取得する
		Display display = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		display.getRealSize(p);
		device_width = p.x;
		device_height = p.y;
		Log.d(TAG, "DeviceSize width:" + p.x + " heoght:" + p.y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onClick(View v) {
		// Log
		Log.d(TAG, "button old id:" + currentButtonNo + "new id:" + v.getId());
		// 前のボタンを非選択にする
		Button old_btn = (Button) findViewById(currentButtonNo);
		old_btn.setSelected(false);
		// 選択ボタンを更新
		currentButtonNo = v.getId();
		// 現在のページとの差
		int difNum = v.getId() - viewFlipper.getDisplayedChild();
		// ボタンを選択状態にする
		Button btn = (Button) findViewById(v.getId());
		btn.setSelected(true);
		if (difNum > 0) { // 差がプラスなら進める
			for (int i = 0; i < difNum; i++) {
				viewFlipper.showNext();
			}
		} else { // 差がマイナスなら戻る
			difNum = Math.abs(difNum);
			for (int i = 0; i < difNum; i++) {
				viewFlipper.showPrevious();
			}
		}
		// 現在のページの取得
		setPageNum();
		// ボタンの見た目を変える
		setButtonDesign(btn);
	}

	private final SimpleOnGestureListener mOnGestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1 // TouchDown時のイベント
				, MotionEvent e2 // TouchDown後、指の移動毎に発生するイベント
				, float velocityX // X方向の移動距離
				, float velocityY) // Y方向の移動距離
		{
			// 絶対値の取得
			float dx = Math.abs(velocityX);
			float dy = Math.abs(velocityY);
			// 指の移動方向(縦横)および距離の判定
			if (dx > dy && dx > 800) {
				// 指の移動方向(左右)の判定
				if (e1.getX() < e2.getX()) {
					viewFlipper.setInAnimation(inFromLeftAnimation);
					viewFlipper.setOutAnimation(outToRightAnimation);
					viewFlipper.showPrevious();
				} else {
					viewFlipper.setInAnimation(inFromRightAnimation);
					viewFlipper.setOutAnimation(outToLeftAnimation);
					viewFlipper.showNext();
				}
				// 現在のページの取得
				//setPageNum();
				// 現在のViewと関連のあるボタンの取得
				Button btn = (Button) findViewById(viewFlipper
						.getDisplayedChild());
				// ボタンを再描画する
				setButtonDesign(btn);
				return true;
			}
			return false;
		}
	};

	protected void setAnimations() {
		inFromRightAnimation = AnimationUtils.loadAnimation(this,
				R.anim.right_in);
		inFromLeftAnimation = AnimationUtils
				.loadAnimation(this, R.anim.left_in);
		outToRightAnimation = AnimationUtils.loadAnimation(this,
				R.anim.right_out);
		outToLeftAnimation = AnimationUtils
				.loadAnimation(this, R.anim.left_out);
	}

	private void setPageNum() {
		// 現在のページの取得
		ViewFlipperPageNum = viewFlipper.getChildCount();
		ViewFlipperCurrentPageNum = viewFlipper.getDisplayedChild();
	}

	@SuppressLint("Recycle")
	private void createButtons() {
		Resources res = getResources();
		Drawable drawableDesign;
		// Drawable drawableDesign = res.getDrawable(R.drawable.buttondesign3);
		String[] stringArray = { "漫画", "トップニュース", "社会", "経済", "スポーツ", "ライフハック",
				"テクノロジー", "芸能", "おもしろ", "ゴシップ", "ギャンブル", "はてな", "なぞなぞ", "屋形船" };
		/*
		 * HashMap map = new HashMap(); // map.put("キー", "値") ; map.put("name",
		 * "Hirose"); map.put("score", 90); map.put("age", 15);
		 */

		for (int i = 0; i < images.length(); i++) {
			int color = colors.getColor(i, i);
			drawableDesign = button_designs.getDrawable(i);
			// ボタンをホリゾンタルスクロールビューに追加
			button[i] = new Button(this); // ボタン生成
			button[i].setId(i); // リソースID設定
			button[i].setText(stringArray[i]); // ボタンテキスト設定
			button[i].setTextColor(Color.WHITE); // 色
			button[i].setOnClickListener(this);
			button[i] = setBtnBackground(button[i], drawableDesign);
			// button[i].setBackgroundColor(color);
			hsvLinear.addView(button[i]);
			// イメージを追加
			Drawable drawable = images.getDrawable(i);
			addFlipperViewLayout(drawable, color);
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void addFlipperViewLayout(Drawable drawable, int color) {
		// ライナーレイアウト
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(Color.WHITE);
		// インライナーレイアウト
		LinearLayout inlayout = new LinearLayout(this);
		inlayout.setOrientation(LinearLayout.VERTICAL);
		inlayout.setBackgroundColor(color);
		inlayout.setPadding(0, 10, 0, 0);
		// レイアウトにテキストビューを追加
		/*
		 * TextView text = new TextView(this);
		 * text.setText("This is a TextView sample."); layout.addView(text, new
		 * LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,
		 * LinearLayout.LayoutParams.WRAP_CONTENT));
		 */
		// レイアウトにイメージビューを追加
		ImageView image = new ImageView(this);
		image.setImageDrawable(drawable);

		// WebView
		WebView myWebView = new WebView(this);
		myWebView.loadUrl("file:///android_asset/sample1.html");
		// リンクを踏んだときにデフォルトのブラウザに飛ばない
		myWebView.setWebViewClient(new WebViewClient());
		//myWebView.setOnDragListener(new mOnGestureListener());
		// ズームを有効
		// myWebView.getSettings().setBuiltInZoomControls(true);
		// JavaScriptを有効
		// myWebView.getSettings().setJavaScriptEnabled(true);

		layout.addView(image, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		layout.addView(myWebView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		// inLayoutに包括する
		inlayout.addView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		// ViewFlipperにレイアウトを追加
		viewFlipper.addView(inlayout);
	}

	private void setButtonDesign(Button btn) {
		// Resources res = getResources();
		// Drawable drawableDesign1 = res.getDrawable(R.drawable.buttondesign);
		// Drawable drawableDesign2 = res.getDrawable(R.drawable.buttondesign2);
		for (int i = 0; i < button.length; i++) {
			button[i].setTextSize(19);
			// button[i] = setBtnBackground(button[i], drawableDesign2);
		}
		btn.setTextSize(28);
		// btn = setBtnBackground(btn, drawableDesign1);
		// Log.d(TAG, "scroll x:" + btn.getLeft() + "scroll y:" + btn.getTop());
		hsv.scrollTo(btn.getLeft() - (device_width - btn.getWidth()) / 2,
				btn.getTop());
		return;
	}

	@SuppressLint("NewApi")
	public static Button setBtnBackground(Button btn, Drawable d) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			btn.setBackgroundDrawable(d);
		} else {
			btn.setBackground(d);
		}
		return btn;
	}
}
