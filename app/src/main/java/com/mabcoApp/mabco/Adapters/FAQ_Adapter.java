package com.mabcoApp.mabco.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mabcoApp.mabco.Classes.FAQ;
import com.mabcoApp.mabco.R;

import java.util.ArrayList;
import java.util.List;

//This class will include both parent(question) and child(answer) classes
public class FAQ_Adapter extends RecyclerView.Adapter<FAQ_Adapter.ItemViewHolder> {

    private List<FAQ> mList;
    private ArrayList<String> question;
    private List<String> list = new ArrayList<>();

    public FAQ_Adapter(List<FAQ> mList, ArrayList<String> question) {
        this.mList = mList;
        this.question = question;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_parent_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        FAQ model = mList.get(position);


        boolean isExpandable = model.isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

        if (isExpandable) {
            holder.mArrowImage.setImageResource(R.drawable.arrow_up);
        } else {
            holder.mArrowImage.setImageResource(R.drawable.arrow_down);
        }
        holder.question.setText(question.get(position));
        NestedAdapter adapter = new NestedAdapter(list);
        holder.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.nestedRecyclerView.setHasFixedSize(true);
        holder.nestedRecyclerView.setAdapter(adapter);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setExpandable(!model.isExpandable());
                list = model.getNestedList();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private RelativeLayout expandableLayout;
        private TextView question;
        private ImageView mArrowImage;
        private RecyclerView nestedRecyclerView;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            mArrowImage = itemView.findViewById(R.id.arro_imageview);
            nestedRecyclerView = itemView.findViewById(R.id.child_rv);
        }
    }

}

class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.NestedViewHolder> {

    private List<String> mList;

    public NestedAdapter(List<String> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_nested_item, parent, false);
        return new NestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        holder.mTv.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class NestedViewHolder extends RecyclerView.ViewHolder {
        private TextView mTv;

        public NestedViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.nestedItemTv);
        }
    }
}
