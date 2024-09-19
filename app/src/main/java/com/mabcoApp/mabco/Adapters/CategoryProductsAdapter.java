package com.mabcoApp.mabco.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mabcoApp.mabco.ui.Product.CategoryProductsFragment;

public class CategoryProductsAdapter extends FragmentStateAdapter {
    String searchtext = "";

    public void setSearchtext(String searchtext) {
        this.searchtext = searchtext;
    }

    public CategoryProductsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public CategoryProductsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public CategoryProductsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {

            case 0:
                return new CategoryProductsFragment("00",searchtext,position );
            case 1:
                return new CategoryProductsFragment("09",searchtext,position );
            case 2:
                return new CategoryProductsFragment("01",searchtext,position );
            case 3:
                return new CategoryProductsFragment("02",searchtext,position );
            case 4:
                return new CategoryProductsFragment("07",searchtext,position );
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

}
