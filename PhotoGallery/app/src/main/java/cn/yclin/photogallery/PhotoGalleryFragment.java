package cn.yclin.photogallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends VisibleFragment {

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private GridLayoutManager mGridManager;
    private boolean asyncFetching = false;
    //private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    private static final String TAG = "PhotoGalleryFragment";

    private int current_page = 1;
    private int fetched_page = 0;
    private int maxPage = 1;
    private int itemPerPage = 1;
    private boolean pageResetFlag = false;
    private Picasso picasso;
    LruCache lruCache;

    private ProgressDialog mProgressDialog;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems(true);


//        Handler responseHandler = new Handler();
//        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
//        mThumbnailDownloader.setThumbnailDownloadListener(
//                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
//                    @Override
//                    public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
//                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                        photoHolder.bindDrawable(drawable);
//                    }
//                }
//        );
//
//        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
//        Log.i(TAG, "Background thread started");

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in bytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // Use 1/7th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 7;
        lruCache = new LruCache(cacheSize);
        picasso = new Picasso.Builder(getActivity()).memoryCache(lruCache).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        mGridManager = new GridLayoutManager(getActivity(), 3);
        mPhotoRecyclerView.setLayoutManager(mGridManager);
        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int firstItem = mGridManager.findFirstVisibleItemPosition();
                int lastItem = mGridManager.findLastVisibleItemPosition();
                if ((!asyncFetching) && (dy > 0) && (current_page < maxPage) && (lastItem >= mItems.size() - 1)) {
                    Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();
                    updateItems(false);
                }
            }
        });
        setupAdapter();
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                QueryPreferences.setStoredQuery(getActivity(), s);
                updateItems(true);
                searchItem.collapseActionView();
                hideKeyboard(getActivity());
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }


        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems(true);
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems(boolean reset) {
        if(reset) {
            resetPageParameter();
            if(lruCache != null){
                lruCache.clear();
            }
            if (mPhotoRecyclerView != null) {
                mItems.clear();
                mPhotoRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
        else ++current_page;
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute(current_page);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mThumbnailDownloader.clearQueue();
    }

    private void setupAdapter() {
        if (isAdded() && mPhotoRecyclerView.getAdapter() == null) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ImageView mItemImageView;;
        private GalleryItem mGalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            mGalleryItem = galleryItem;
            picasso.get()
                    .load(galleryItem.getUrl()).fit().centerCrop()
                    .placeholder(R.drawable.bill_up_close)
                    .error(R.drawable.bill_up_close)
                    .into(mItemImageView);
        }

        @Override
        public void onClick(View v) {
            //Intent i = new Intent(Intent.ACTION_VIEW, mGalleryItem.getPhotoPageUri());
            Intent i = PhotoPageActivity
                    .newIntent(getActivity(), mGalleryItem.getPhotoPageUri());
            startActivity(i);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            //Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);
            //photoHolder.bindDrawable(placeholder);
            //mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
            photoHolder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Integer, Void, List<GalleryItem>> {
        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Integer... params) {

            asyncFetching = true;
            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos(params[0]);
            } else {
                return new FlickrFetchr().searchPhotos(mQuery, params[0]);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            if(pageResetFlag){  // new search content
                mItems.clear();
                mItems.addAll(items);
                pageResetFlag = false;
            }else{
                mItems.addAll(items);
            }
            setupAdapter();
            itemPerPage = PhotoPages.sPhotoPages.getItemsPerPage();
            maxPage = PhotoPages.sPhotoPages.getTotalPage();
            mPhotoRecyclerView.getAdapter().notifyDataSetChanged();
            Log.d(TAG,"Pages =: " + current_page);
            fetched_page += 1;
            asyncFetching = false;
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

        }
    }

    private void resetPageParameter(){
        current_page = 1;
        fetched_page = 0;
        itemPerPage = 1;
        maxPage = 1;
        pageResetFlag = true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
