package fr.ganfra.recycleradapterview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

class FixedViewRecyclerAdapter<VH extends RecyclerAdapterView.ViewHolder> extends RecyclerAdapterView.Adapter<VH> {

    private static final String LOG_TAG = FixedViewRecyclerAdapter.class.getSimpleName();

    private static final int TYPE_HEADER = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER = TYPE_HEADER + 1;

    private final RecyclerAdapterView.Adapter mWrappedAdapter;

    private ArrayList<View> mHeaderViews;
    private ArrayList<View> mFooterViews;

    private Context mContext;

    private RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    };


    /*
     * **********************************************************************************
     * CONSTRUCTOR
     * **********************************************************************************
     */

    public FixedViewRecyclerAdapter(final Context context, final RecyclerAdapterView.Adapter adapter, final ArrayList<View> headerViews, final ArrayList<View> footerViews) {
        mContext = context;
        mWrappedAdapter = adapter;
        mHeaderViews = headerViews;
        mFooterViews = footerViews;
    }



    /*
     * **********************************************************************************
     * PUBLIC METHODS
     * **********************************************************************************
     */

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else if (isFooterView(position)) {
            return TYPE_FOOTER;
        } else {
            return mWrappedAdapter.getItemViewType(position - getHeaderCount());
        }
    }


    public RecyclerAdapterView.Adapter<VH> getWrappedAdapter() {
        return mWrappedAdapter;
    }

    @Override
    public Object getItem(int position) {
        final int viewType = getItemViewType(position);
        if (viewType != TYPE_HEADER && viewType != TYPE_FOOTER) {
            return mWrappedAdapter.getItem(position - getHeaderCount());
        }
        return null;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final VH viewHolder;
        if (viewType == TYPE_FOOTER || viewType == TYPE_HEADER) {
            viewHolder = (VH) new FixedViewHolder(new FixedLayout(mContext));
        } else {
            viewHolder = (VH) mWrappedAdapter.onCreateViewHolder(parent, viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        final int type = holder.getItemViewType();
        if (type == TYPE_FOOTER) {
            ((FixedViewHolder) holder).addFixedView(getFooterView(position), position);
        } else if (type == TYPE_HEADER) {
            ((FixedViewHolder) holder).addFixedView(getHeaderView(position), position);
        } else {
            mWrappedAdapter.onBindViewHolder(holder, position - getHeaderCount());
        }
    }

    @Override
    public int getItemCount() {
        int fixedSize = getFooterCount() + getHeaderCount();
        return mWrappedAdapter != null ? fixedSize + mWrappedAdapter.getItemCount() : fixedSize;
    }

    public boolean isFixedView(final int position) {
        return isFooterView(position) || isHeaderView(position);
    }


    public void registerWrapperDataObserver() {
        if (mWrappedAdapter != null) {
            try {
                mWrappedAdapter.registerAdapterDataObserver(mAdapterDataObserver);
            } catch (IllegalStateException e) {
                Log.v(LOG_TAG, e.getLocalizedMessage());
            }
        }
    }

    public void unregisterWrapperDataObserver() {
        if (mWrappedAdapter != null) {
            try {
                mWrappedAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
            } catch (IllegalStateException e) {
                Log.v(LOG_TAG, e.getLocalizedMessage());
            }
        }
    }

    /*
     * **********************************************************************************
     * PRIVATE METHODS
     * **********************************************************************************
     */

    private int getHeaderCount() {
        return mHeaderViews != null ? mHeaderViews.size() : 0;
    }

    private int getFooterCount() {
        return mFooterViews != null ? mFooterViews.size() : 0;
    }

    private boolean isHeaderView(final int position) {
        return position < getHeaderCount();
    }

    private boolean isFooterView(final int position) {
        int exactPosition = mWrappedAdapter != null ? getHeaderCount() + mWrappedAdapter.getItemCount() : getHeaderCount();
        return position >= exactPosition;
    }

    private View getHeaderView(final int position) {
        return mHeaderViews.get(position);
    }

    private View getFooterView(final int position) {
        final int offset = mWrappedAdapter != null ? getHeaderCount() + mWrappedAdapter.getItemCount() : getHeaderCount();
        return mFooterViews.get(position - offset);
    }


    /*
     * **********************************************************************************
     * INNER CLASSES
     * **********************************************************************************
     */

    private static class FixedViewHolder extends RecyclerAdapterView.ViewHolder {

        private FixedLayout mFixedLayout;

        public FixedViewHolder(final FixedLayout fixedLayout) {
            super(fixedLayout);
            mFixedLayout = fixedLayout;
        }

        public void addFixedView(final View content, final int position) {
            mFixedLayout.addFixedView(content, position);
        }
    }

    private static class FixedLayout extends LinearLayout {


        public FixedLayout(final Context context) {
            super(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutParams(layoutParams);
            setOrientation(VERTICAL);

        }

        public void addFixedView(final View view, final int position) {
            if (view != null && view.getParent() == null) {
                addView(view);
            }
        }
    }

}
