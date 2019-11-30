package com.liuzozo.stepdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuzozo.stepdemo.ui.BitmapUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.content.Context.MODE_PRIVATE;


/**
 * <pre>
 *     author : lisheny
 *     e-mail : 1020044519@qq.com
 *     time   : 2017/07/10
 *     desc   : 聊天信息列表适配器
 *     version: 1.0
 * </pre>
 */
public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.MyViewHolder> {

    private HomeListBeen homeListBeen;
    private List<HomeListBeen> mDatas;
    private Context mContext;
    private OnItemClickLitener mOnItemClickLitener;

    public HomeListAdapter(Context context, List<HomeListBeen> datas) {
        this.mDatas = datas;
        this.mContext = context;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_home_list, parent, false));
        //设置头像

        SharedPreferences read = mContext.getSharedPreferences("personal_data", MODE_PRIVATE);
        Log.d("TAG",read.getString("state",null));
        if(read.getString("picture_path",null) != null) {
            String file_path = read.getString("picture_path", null);
            Bitmap bitmap0 = BitmapFactory.decodeFile(file_path);
            Bitmap bitmap1 = BitmapUtil.getCircleBitmap(bitmap0);
            Bitmap bitmap = BitmapUtil.changeBitmapSize(bitmap1, 72, 72);
            holder.ivHomeRightIc.setImageBitmap(bitmap);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        homeListBeen = mDatas.get(position);

        //test 仅供显示
        if (position == 0) {
            holder.tvHomeTime.setVisibility(View.GONE);
        }
        if (homeListBeen.getTeller() == 2) {
            holder.tvHomeLeftContent.setVisibility(View.GONE);
            holder.ivHomeLeftIc.setVisibility(View.GONE);
            holder.tvHomeRightContent.setVisibility(View.VISIBLE);
            holder.ivHomeRightIc.setVisibility(View.VISIBLE);
            holder.tvHomeRightContent.setText(homeListBeen.getContent());
        } else if (homeListBeen.getTeller() == 1){
            holder.tvHomeLeftContent.setVisibility(View.VISIBLE);
            holder.ivHomeLeftIc.setVisibility(View.VISIBLE);
            holder.tvHomeRightContent.setVisibility(View.GONE);
            holder.ivHomeRightIc.setVisibility(View.GONE);
            holder.tvHomeLeftContent.setText(homeListBeen.getContent());
        }else {
            //dosomething
        }

        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.tv_home_time)
        TextView tvHomeTime;
        @InjectView(R.id.iv_home_left_ic)
        ImageView ivHomeLeftIc;
        @InjectView(R.id.tv_home_left_content)
        TextView tvHomeLeftContent;
        @InjectView(R.id.iv_home_right_ic)
        ImageView ivHomeRightIc;
        @InjectView(R.id.tv_home_right_content)
        TextView tvHomeRightContent;
        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}