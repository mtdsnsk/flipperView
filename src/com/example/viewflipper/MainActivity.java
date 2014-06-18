package com.example.viewflipper;

import android.annotation.SuppressLint;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
	private TextView pageNumText;
	private LinearLayout hsvLinear;
	// private Button[] button;
	private GestureDetector mGestureDetector;
	Animation inFromRightAnimation;
	Animation inFromLeftAnimation;
	Animation outToRightAnimation;
	Animation outToLeftAnimation;

	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mGestureDetector = new GestureDetector(this, mOnGestureListener);
		viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
		hsvLinear = (LinearLayout) findViewById(R.id.hsvLinear);
		pageNumText = (TextView) findViewById(R.id.pagenum);

		TypedArray images = getResources().obtainTypedArray(
				R.array.test_array_drawable);
		for (int i = 0; i < images.length(); i++) {
			TextView text = new TextView(this);
			text.setText("This is a TextView sample.");
			Button button;
			button = new Button(this); // ボタン生成
			button.setId(i); // リソースID設定
			button.setText("ボタン" + i); // ボタンテキスト設定
			button.setOnClickListener(this);
			hsvLinear.addView(button);
			Drawable drawable = images.getDrawable(i);
			addFlipperViewLayout(drawable);
		}

		viewFlipper.setAutoStart(false); // 自動でスライドショーを開始しない
		viewFlipper.setFlipInterval(1000); // 更新間隔(ms単位)

		// ページ番号をセット
		setPageNum();
		// アニメーションをセットする
		setAnimations();
		// viewFlipper.setInAnimation(inFromRightAnimation);
		// viewFlipper.setOutAnimation(outToLeftAnimation);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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
		int difNum = v.getId() - getCurrentPageIndex(); // 現在のページとの差
		// 現在フォーカスが当たっているページ
		//View currentPageView = viewFlipper.getCurrentView();
		if (difNum > 0) { // 差がプラスなら進める
			pageNumText.setText("現在ページ:" + getCurrentPageIndex() + "行き先ページ:"
					+ v.getId());
			for (int i = 0; i < difNum; i++) {
				viewFlipper.showNext();
			}
		} else { // 差がマイナスなら戻る		
			difNum = Math.abs(difNum);
			pageNumText.setText("現在ページ:" + getCurrentPageIndex() + "行き先ページ:"
					+ v.getId());
			for (int i = 0; i < difNum; i++) {
				viewFlipper.showPrevious();
			}
		}
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
				ViewFlipperPageNum = viewFlipper.getChildCount();
				ViewFlipperCurrentPageNum = getCurrentPageIndex();
				pageNumText.setText(String
						.valueOf(ViewFlipperCurrentPageNum + 1)
						+ "/"
						+ String.valueOf(ViewFlipperPageNum));
				return true;
			}
			// ページ番号をセット
			setPageNum();
			return false;
		}
	};

	@SuppressWarnings("deprecation")
	private void addFlipperViewLayout(Drawable drawable) {
		// レイアウトにテキストビューを追加
		TextView text = new TextView(this);
		text.setText("This is a TextView sample.");
		ImageView image = new ImageView(this);
		// image.setImageResource(R.drawable.unknown4);
		image.setImageDrawable(drawable);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		layout.addView(text, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		layout.addView(image, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		viewFlipper.addView(layout);
	}

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

	private void pageNumberUpdate() {
		// 総ページ数
		ViewFlipperPageNum = viewFlipper.getChildCount();
		// TextView pagingTotalView = (TextView) findViewById(R.id.pagenum);
		// TextView pagingTotalView = new TextView(this);
		// pagingTotalView.setText("ページ総数:" + ViewFlipperPageNum);
		// hsvLinear.addView(pagingTotalView);
		// viewFlipper.addView(pagingTotalView);
	}

	private int getCurrentPageIndex() {
		// 現在表示しているページ
		int pageNum = viewFlipper.getChildCount();
		View currentPageView = viewFlipper.getCurrentView();
		for (int current = 0; current < pageNum; current++) {
			View pageView = viewFlipper.getChildAt(current);
			if (pageView == currentPageView) {
				return current;
			}
		}
		return 0;
	}

	private void setPageNum() {
		// 現在のページの取得
		ViewFlipperPageNum = viewFlipper.getChildCount();
		ViewFlipperCurrentPageNum = getCurrentPageIndex();
		pageNumText.setText(String.valueOf(ViewFlipperCurrentPageNum + 1) + "/"
				+ String.valueOf(ViewFlipperPageNum));
	}

}
