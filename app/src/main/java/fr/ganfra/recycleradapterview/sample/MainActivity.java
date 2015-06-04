package fr.ganfra.recycleradapterview.sample;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fr.ganfra.recycleradapterview.RecyclerAdapterView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.listView)
    RecyclerAdapterView recyclerAdapterView;

    @InjectView(R.id.emptyView)
    TextView emptyView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;


    private List<City> cities = new ArrayList<>();

    private MyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createCities();
        adapter = new MyAdapter(this);
        createCities();


        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        recyclerAdapterView.setAdapter(adapter);
        recyclerAdapterView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapterView.setEmptyView(emptyView);


        recyclerAdapterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final City city = adapter.getItem(i);
                Toast.makeText(MainActivity.this, "CLICK on : "+city.name, Toast.LENGTH_LONG).show();

            }
        });


    }

    @OnClick(R.id.addButton)
    public void addButtonOnClick() {
        adapter.addData(cities);
    }

    private void createCities() {

        for (int i = 0; i < 10; i++) {
            cities.add(new City("City " + (i + 1), Uri.parse("")));
        }
    }


    static class MyAdapter extends RecyclerAdapterView.Adapter<MyAdapter.MyHolder> {


        private Context mContext;
        private List<City> mCityItems;

        public MyAdapter(final Context context) {
            mContext = context;
        }

        public void setData(final List<City> items) {
            this.mCityItems = items;
            notifyDataSetChanged();
        }

        public void addData(final List<City> items) {
            if (mCityItems == null) {
                setData(items);
            } else {
                mCityItems.addAll(items);
                notifyDataSetChanged();
            }
        }


        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            final View view = inflater.inflate(R.layout.item_country, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            final City city = mCityItems.get(position);
            holder.populateViews(city);
        }

        @Override
        public int getItemCount() {
            return mCityItems != null ? mCityItems.size() : 0;
        }

        @Override
        public City getItem(int position) {
            return mCityItems.get(position);
        }

        static class MyHolder extends RecyclerAdapterView.ViewHolder {

            private View itemView;

            @InjectView(R.id.image)
            ImageView imageView;
            @InjectView(R.id.text)
            TextView textView;

            public MyHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
                this.itemView = itemView;
            }

            public void populateViews(final City city) {

                textView.setText(city.name);

                final Context context = itemView.getContext();

                Picasso.with(context)
                        .load(city.pictureUri)
                        .into(imageView);


            }

        }


    }


}
