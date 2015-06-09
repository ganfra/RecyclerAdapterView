package fr.ganfra.recycleradapterview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

public class RecyclerAdapterView extends RecyclerView {

    private static final String LOG_TAG = RecyclerAdapterView.class.getSimpleName();


    /*
     * **********************************************************************************
     * ATTRIBUTES
     * **********************************************************************************
     */

    private View mEmptyView;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;

    /*
     * **********************************************************************************
     * CONSTRUCTORS
     * **********************************************************************************
     */
    public RecyclerAdapterView(Context context) {
        super(context);
    }

    public RecyclerAdapterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerAdapterView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {

        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mAdapterDataObserver);
        }

        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mAdapterDataObserver);
            checkIfEmpty();
        }

    }


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
     * INNER CLASSES
     * **********************************************************************************
     */

    public static abstract class Adapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
        public abstract Object getItem(int position);

        private RecyclerAdapterView mRecyclerAdapterView;

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

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerAdapterView mRecyclerAdapterView;


        public ViewHolder(final View itemView) {
            super(itemView);


            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mRecyclerAdapterView != null) {
                        final int position = getAdapterPosition();
                        final long id = getItemId();

                        mRecyclerAdapterView.getOnItemClickListener().onItemClick(null, view, position, id);

//                        RecyclerEventBus.postItemClick(new RecyclerEventBus.ItemClickEvent(view, position, id));
                    }
                }
            });

            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mRecyclerAdapterView != null) {
                        final int position = getAdapterPosition();
                        final long id = getItemId();

                        mRecyclerAdapterView.getOnItemLongClickListener().onItemLongClick(null, view, position, id);


//                        RecyclerEventBus.postItemLongClick(new RecyclerEventBus.ItemLongClickEvent(view, position, id));
                    }
                    return true;
                }
            });
        }
    }
}
