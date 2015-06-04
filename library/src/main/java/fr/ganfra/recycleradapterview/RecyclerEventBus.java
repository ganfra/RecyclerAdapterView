package fr.ganfra.recycleradapterview;

import android.view.View;

import java.util.Observable;

class RecyclerEventBus extends Observable {

    private static RecyclerEventBus mInstance;

    private RecyclerEventBus() {
    }

    public static RecyclerEventBus getInstance() {
        if (mInstance == null) {
            mInstance = new RecyclerEventBus();
        }

        return mInstance;
    }

    public static void register(final RecyclerAdapterView view) {
        RecyclerEventBus recyclerEventBus = getInstance();
        recyclerEventBus.addObserver(view);
    }

    public static void unregister(final RecyclerAdapterView view) {
        RecyclerEventBus recyclerEventBus = getInstance();
        recyclerEventBus.deleteObserver(view);
    }

    public static void postItemClick(final ItemClickEvent clickItemEvent) {
        postItemEvent(clickItemEvent);
    }

    public static void postItemLongClick(final ItemLongClickEvent longClickItemEvent) {
        postItemEvent(longClickItemEvent);
    }

    public static void postItemEvent(final ItemEvent itemEvent) {
        RecyclerEventBus recyclerEventBus = getInstance();
        recyclerEventBus.setChanged();
        recyclerEventBus.notifyObservers(itemEvent);
    }

    private static abstract class ItemEvent {

        private View mView;
        private int mPosition;
        private long mId;

        public ItemEvent(final View view, final int position, final long id) {
            mView = view;
            mId = id;
            mPosition = position;
        }


        public long getId() {
            return mId;
        }

        public int getPosition() {
            return mPosition;
        }

        public View getView() {
            return mView;
        }
    }

    public static class ItemClickEvent extends ItemEvent {

        public ItemClickEvent(final View view, final int position, final long id) {
            super(view, position, id);
        }

    }

    public static class ItemLongClickEvent extends ItemEvent {
        public ItemLongClickEvent(final View view, final int position, final long id) {
            super(view, position, id);
        }
    }

}