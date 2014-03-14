package com.msx.android.jx3fwq;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.msx.android.jx3fwq.SosUniversalAdapter.HasMorePagesListener;

/**
 * 一个ListView保持在列表的顶部固定一个view 固定view，可以推及解散需要。 它也支持通过设置自定义视图作为加载的分页指标
 */
public class SosUniversalListView extends ListView implements
		HasMorePagesListener {
	public static final String TAG = SosUniversalListView.class.getSimpleName();

	public View listFooter;
	boolean footerViewAttached = false;

	private View mHeaderView;
	private boolean mHeaderViewVisible;
	
    private int mHeaderViewWidth;
    private int mHeaderViewHeight;
    
	private SosUniversalAdapter adapter;
	private int addHeadViewHeight;

	public void setPinnedHeaderView(View view) {
		mHeaderView = view;

		// Disable vertical fading when the pinned header is present
		// TODO change ListView to allow separate measures for top and bottom
		// fading edge;
		// in this particular case we would like to disable the top, but not the
		// bottom edge.
		if (mHeaderView != null) {
			setFadingEdgeLength(0);
		}
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mHeaderView != null) {
			measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
			mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
			mHeaderView.getMeasuredWidth();
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {		
		super.onLayout(changed, left, top, right, bottom);
		if(getHeaderViewsCount()>0){
			addHeadViewHeight=getChildAt(0).getMeasuredHeight();
		}
		if (mHeaderView != null) {
			 mHeaderView.layout(0,addHeadViewHeight, mHeaderViewWidth, mHeaderViewHeight+addHeadViewHeight);
			configureHeaderView(getFirstVisiblePosition());
		}
	}

	public void configureHeaderView(int position) {
		if (mHeaderView == null) {
			return;
		}

		int state = adapter.getPinnedHeaderState(position-getHeaderViewsCount());
		switch (state) {
		case SosUniversalAdapter.PINNED_HEADER_GONE: {
			mHeaderViewVisible = false;
			break;
		}

		case SosUniversalAdapter.PINNED_HEADER_VISIBLE: {
			if (adapter.getCount() < 1)
				return;
			adapter.configurePinnedHeader(mHeaderView, Math.max(0, position-1), 255);
			mHeaderView.setVisibility(View.VISIBLE);
			if (mHeaderView.getTop() != 0) {
				mHeaderView.layout(0, addHeadViewHeight, mHeaderViewWidth, mHeaderViewHeight+addHeadViewHeight);
			}
			mHeaderViewVisible = true;
			break;
		}

		case SosUniversalAdapter.PINNED_HEADER_PUSHED_UP: {
			View firstView = getChildAt(0);
			if (firstView != null) {
				int bottom = firstView.getBottom();
				int headerHeight = mHeaderView.getHeight();
				int y;
				int alpha;
				if (bottom < headerHeight) {
					y = (bottom - headerHeight);
					alpha = 255 * (headerHeight + y) / headerHeight;
				} else {
					y = 0;
					alpha = 255;
				}
				adapter.configurePinnedHeader(mHeaderView, Math.max(0, position-1), alpha);
				if (mHeaderView.getTop() != y) {
					mHeaderView.layout(0, y, mHeaderViewWidth,
							mHeaderViewHeight + y);
				}
				mHeaderViewVisible = true;
			}
			break;
		}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mHeaderViewVisible) {
			drawChild(canvas, mHeaderView, getDrawingTime());
		}
	}

	public SosUniversalListView(Context context) {
		super(context);
	}

	public SosUniversalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SosUniversalListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setLoadingView(View listFooter) {
		this.listFooter = listFooter;
	}

	public View getLoadingView() {
		return listFooter;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (!(adapter instanceof SosUniversalAdapter)) {
			throw new IllegalArgumentException(
					SosUniversalListView.class.getSimpleName()
							+ " must use adapter of type "
							+ SosUniversalAdapter.class.getSimpleName());
		}

		// previous adapter
		if (this.adapter != null) {
			this.adapter.setHasMorePagesListener(null);
			this.setOnScrollListener(null);
		}

		this.adapter = (SosUniversalAdapter) adapter;
		((SosUniversalAdapter) adapter).setHasMorePagesListener(this);
		this.setOnScrollListener((SosUniversalAdapter) adapter);

		View dummy = new View(getContext());
		super.addFooterView(dummy);
		super.setAdapter(adapter);
		super.removeFooterView(dummy);
	}

	@Override
	public SosUniversalAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void noMorePages() {
		if (listFooter != null) {
			this.removeFooterView(listFooter);
		}
		footerViewAttached = false;
	}

	@Override
	public void mayHaveMorePages() {
		if (!footerViewAttached && listFooter != null) {
			this.addFooterView(listFooter);
			footerViewAttached = true;
		}
	}

	public boolean isLoadingViewVisible() {
		return footerViewAttached;
	}
}
