package fr.ganfra.recycleradapterview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

public class RecyclerAdapterView extends RecyclerView {

    /*
     * **********************************************************************************
     * CONSTANTS
     * **********************************************************************************
     */
    private static final String LOG_TAG = RecyclerAdapterView.class.getSimpleName();

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider, android.R.attr.dividerHeight,
    };


    /*
     * **********************************************************************************
     * ATTRIBUTES
     * **********************************************************************************
     */

    private View mEmptyView;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;

    private Context mContext;

    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFooterViews = new ArrayList<>();

    // XML ATTRIBUTES
    private Drawable mDivider;
    private int mDividerHeight;

    /*
     * **********************************************************************************
     * CONSTRUCTORS
     * **********************************************************************************
     */
    public RecyclerAdapterView(Context context) {
        this(context, null);
    }

    public RecyclerAdapterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerAdapterView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setLayoutManager(new LinearLayoutManager(context));
        initXmlAttributes(context);
        if (mDivider != null) {
            addItemDecoration(new DividerItemDecoration(mDivider, mDividerHeight));
        }
    }

    private void initXmlAttributes(final Context context) {
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        final Drawable drawable = a.getDrawable(0);
        setDivider(drawable);
        mDividerHeight = a.getInteger(1, mDividerHeight);
        a.recycle();
    }


    public void setDivider(final int res) {
        final Drawable drawable = getContext().getResources().getDrawable(res);
        setDivider(drawable);
    }

    public void setDivider(final Drawable divider) {
        if (divider != null) {
            mDivider = divider;
            mDividerHeight = divider.getIntrinsicHeight();
        } else {
            mDividerHeight = 0;
        }
        requestLayout();
        invalidate();
    }



    /*
     * **********************************************************************************
     * COMMONS FEATURES
     * **********************************************************************************
     */

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        unregisterDataObservers(adapter);
        try {
            adapter = wrapAdapterIfNeeded((RecyclerAdapterView.Adapter) adapter);
        } catch (ClassCastException e) {
            Log.v(LOG_TAG, "Your adapter must extends RecyclerAdapterView.Adapter instead of RecyclerView.Adapter");
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mAdapterDataObserver);
            checkIfEmpty();
        }
    }


    private void unregisterDataObservers(final RecyclerView.Adapter newAdapter) {
        final RecyclerView.Adapter currentAdapter = getAdapter();

        if (currentAdapter != null && newAdapter != null) {
            currentAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
            if (currentAdapter instanceof FixedViewRecyclerAdapter && !(newAdapter instanceof FixedViewRecyclerAdapter)) {
                ((FixedViewRecyclerAdapter) currentAdapter).unregisterWrapperDataObserver();
            }
        }
    }



    /*
     * **********************************************************************************
     * EMPTY VIEW
     * **********************************************************************************
     */

    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            checkIfEmpty();
        }
    };

    public void setEmptyView(final View view) {
        mEmptyView = view;
        checkIfEmpty();
    }

    public final View getEmptyView() {
        return mEmptyView;
    }

    private void checkIfEmpty() {
        final boolean isViewEmpty = isViewEmpty();
        if (mEmptyView != null) {
            this.setVisibility(isViewEmpty ? INVISIBLE : VISIBLE);
            mEmptyView.setVisibility(isViewEmpty ? VISIBLE : INVISIBLE);
        }
    }

    private boolean isViewEmpty() {
        if (getAdapter() != null) {
            final RecyclerView.Adapter adapter = getAdapter();
            return adapter.getItemCount() == 0 ? true : false;
        }
        return true;
    }

    /*
     * **********************************************************************************
     * ON CLICK EVENTS
     * **********************************************************************************
     */


    public void setOnItemClickListener(final AdapterView.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public final AdapterView.OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemLongClickListener(final AdapterView.OnItemLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        mOnItemLongClickListener = listener;
    }

    public final AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    /*
     * **********************************************************************************
     * HEADER AND FOOTER VIEWS
     * **********************************************************************************
     */

    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    public int getFooterCount() {
        return mFooterViews.size();
    }

    public void addFooterView(final View view) {
        addFixedView(view, mFooterViews);
    }

    public void addHeaderView(final View view) {
        addFixedView(view, mHeaderViews);
    }

    private void addFixedView(final View view, final ArrayList<View> fixedViews) {
        RecyclerAdapterView.Adapter adapter = (RecyclerAdapterView.Adapter) getAdapter();
        fixedViews.add(view);
        if (adapter != null) {
            setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private RecyclerAdapterView.Adapter wrapAdapterIfNeeded(Adapter adapter) {
        if (!(adapter instanceof FixedViewRecyclerAdapter) && (getHeaderCount() != 0 || getFooterCount() != 0)) {
            adapter = new FixedViewRecyclerAdapter(mContext, adapter, mHeaderViews, mFooterViews);
            ((FixedViewRecyclerAdapter) adapter).registerWrapperDataObserver();
        }

        return adapter;
    }

    public void removeFooterView(final View view) {
        removeFixedView(view, mFooterViews);
    }

    public void removeHeaderView(final View view) {
        removeFixedView(view, mHeaderViews);
    }

    private void removeFixedView(final View v, final ArrayList<View> fixedViews) {
        int size = fixedViews.size();
        for (int i = 0; i < size; ++i) {
            View view = fixedViews.get(i);
            if (view == v) {
                fixedViews.remove(i);
                break;
            }
        }
        final RecyclerAdapterView.Adapter adapter = (RecyclerAdapterView.Adapter) getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private boolean isFixedView(final int position) {
        boolean isHeaderView = false;
        final RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof FixedViewRecyclerAdapter) {
            isHeaderView = ((FixedViewRecyclerAdapter) adapter).isFixedView(position);
        }
        return isHeaderView;
    }


    /*
     * **********************************************************************************
     * INNER CLASSES
     * **********************************************************************************
     */

    public static abstract class Adapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
        public abstract Object getItem(int position);

        private RecyclerAdapterView mRecyclerAdapterView;


        @Override
        public void onBindViewHolder(VH holder, int position) {

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            if (recyclerView instanceof RecyclerAdapterView) {
                mRecyclerAdapterView = (RecyclerAdapterView) recyclerView;
            }
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            if (mRecyclerAdapterView != null) {
                mRecyclerAdapterView = null;
            }
        }

        @Override
        public void onViewAttachedToWindow(VH holder) {
            super.onViewAttachedToWindow(holder);
            holder.mRecyclerAdapterView = mRecyclerAdapterView;
        }

        @Override
        public void onViewDetachedFromWindow(VH holder) {
            super.onViewDetachedFromWindow(holder);
            holder.mRecyclerAdapterView = null;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerAdapterView mRecyclerAdapterView;

        public ViewHolder(final View itemView) {
            super(itemView);
            if (itemView != null) {
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final long id = getItemId();
                        int position = getAdapterPosition();

                        if (mRecyclerAdapterView != null && mRecyclerAdapterView.getOnItemClickListener() != null && !mRecyclerAdapterView.isFixedView(position)) {
                            position -= mRecyclerAdapterView.getHeaderCount();
                            mRecyclerAdapterView.getOnItemClickListener().onItemClick(null, view, position, id);
                        }
                    }
                });

                itemView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final long id = getItemId();
                        int position = getAdapterPosition();

                        if (mRecyclerAdapterView != null && mRecyclerAdapterView.getOnItemLongClickListener() != null && !mRecyclerAdapterView.isFixedView(position)) {
                            position -= mRecyclerAdapterView.getHeaderCount();
                            mRecyclerAdapterView.getOnItemLongClickListener().onItemLongClick(null, view, position, id);
                        }
                        return true;
                    }
                });
            }
        }
    }
}
