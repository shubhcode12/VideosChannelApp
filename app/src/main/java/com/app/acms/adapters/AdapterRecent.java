package com.app.acms.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.acms.Config;
import com.app.acms.R;
import com.app.acms.databases.DatabaseHandlerFavorite;
import com.app.acms.models.Video;
import com.app.acms.utils.Constant;
import com.app.acms.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterRecent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Video> items = new ArrayList<>();

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private Video pos;
    private CharSequence charSequence = null;
    private DatabaseHandlerFavorite databaseHandler;

    public interface OnItemClickListener {
        void onItemClick(View view, Video obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterRecent(Context context, RecyclerView view, List<Video> items) {
        this.items = items;
        this.context = context;
        lastItemViewDetector(view);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView category_name;
        public TextView video_title;
        public TextView video_duration;
        public TextView total_views;
        public TextView date_time;
        public TextView space;
        public ImageView video_thumbnail;
        public MaterialRippleLayout lyt_parent;
        public MaterialRippleLayout overflow;

        public OriginalViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            video_title = v.findViewById(R.id.video_title);
            video_duration = v.findViewById(R.id.video_duration);
            date_time = v.findViewById(R.id.date_time);
            total_views = v.findViewById(R.id.total_views);
            space = v.findViewById(R.id.space);
            video_thumbnail = v.findViewById(R.id.video_thumbnail);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            overflow = v.findViewById(R.id.ripple_overflow);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.load_more);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            if (!Config.DISPLAY_DATE_AS_TIME_AGO && !Config.ENABLE_VIEW_COUNT) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_no_date_views, parent, false);
                vh = new OriginalViewHolder(v);
            } else if (Config.ENABLE_SINGLE_ROW_COLUMN) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_single_column, parent, false);
                vh = new OriginalViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
                vh = new OriginalViewHolder(v);
            }
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final Video p = items.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.category_name.setText(p.category_name);
            vItem.video_title.setText(p.video_title);
            vItem.video_duration.setText(p.video_duration);
            if (Config.ENABLE_VIEW_COUNT) {
                vItem.total_views.setText(Tools.withSuffix(p.total_views) + " " + context.getResources().getString(R.string.views_count));
            } else {
                vItem.total_views.setVisibility(View.GONE);
            }

            if (Config.ENABLE_DATE_DISPLAY && Config.DISPLAY_DATE_AS_TIME_AGO) {
                PrettyTime prettyTime = new PrettyTime();
                long timeAgo = Tools.timeStringtoMilis(p.date_time);
                vItem.date_time.setText(prettyTime.format(new Date(timeAgo)));
            } else if (Config.ENABLE_DATE_DISPLAY && !Config.DISPLAY_DATE_AS_TIME_AGO) {
                vItem.date_time.setText(Tools.getFormatedDateSimple(p.date_time));
            } else {
                vItem.date_time.setVisibility(View.GONE);
                vItem.space.setVisibility(View.GONE);
            }

            if (p.video_type != null && p.video_type.equals("youtube")) {
                Picasso.with(context)
                        .load(Constant.YOUTUBE_IMAGE_FRONT + p.video_id + Constant.YOUTUBE_IMAGE_BACK)
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(vItem.video_thumbnail);
            } else {
                Picasso.with(context)
                        .load(Config.ADMIN_PANEL_URL + "/upload/" + p.video_thumbnail)
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(vItem.video_thumbnail);
            }

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, p, position);
                    }
                }
            });

            vItem.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = items.get(position);
                    showPopupMenu(vItem.overflow);
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROG;
        }
    }

    public void insertData(List<Video> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / Config.LOAD_MORE;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();

        databaseHandler = new DatabaseHandlerFavorite(context);
        List<Video> data = databaseHandler.getFavRow(pos.vid);
        if (data.size() == 0) {
            popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.favorite_add);
            charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
        } else {
            if (data.get(0).getVid().equals(pos.vid)) {
                popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.favorite_remove);
                charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
            }
        }
    }

    public class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.menu_context_favorite:
                    if (charSequence.equals(context.getString(R.string.favorite_add))) {
                        databaseHandler.AddtoFavorite(new Video(
                                pos.category_name,
                                pos.vid,
                                pos.video_title,
                                pos.video_url,
                                pos.video_id,
                                pos.video_thumbnail,
                                pos.video_duration,
                                pos.video_description,
                                pos.video_type,
                                pos.total_views,
                                pos.date_time
                        ));
                        Toast.makeText(context, context.getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();

                    } else if (charSequence.equals(context.getString(R.string.favorite_remove))) {
                        databaseHandler.RemoveFav(new Video(pos.vid));
                        Toast.makeText(context, context.getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                    }
                    return true;

                case R.id.menu_context_share:

                    String share_title = android.text.Html.fromHtml(pos.video_title).toString();
                    String share_content = android.text.Html.fromHtml(context.getResources().getString(R.string.share_text)).toString();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, share_title + "\n\n" + share_content + "\n\n" + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                    return true;

                default:
            }
            return false;
        }
    }

}