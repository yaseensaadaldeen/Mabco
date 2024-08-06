package com.mabcoApp.mabco.ui.Offers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.ui.Product.OfferProductDialog;

import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class OffersFullScreenFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String ARG_OFFERS = "offers";
    private StoriesProgressView storiesProgressView;
    private ImageView storyImage;
    private boolean isLongPressed = false;
    private List<Offer> offers;
    private int currentIndex;
    CardView offer_Desc_card;
    TextView offer_Desc;
    RealtimeBlurView blure_view ;
    private GestureDetector gestureDetector;
    Context context;

    public static OffersFullScreenFragment newInstance(int position, List<Offer> offers) {
        OffersFullScreenFragment fragment = new OffersFullScreenFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable(ARG_OFFERS, (java.io.Serializable) offers);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offers_full_screen, container, false);
        context=getContext();
        storiesProgressView = view.findViewById(R.id.stories_progress_view);
        storyImage = view.findViewById(R.id.story_image);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        offer_Desc = view.findViewById(R.id.offer_desc);
        blure_view = view.findViewById(R.id.blure_view);
        offer_Desc_card = view.findViewById(R.id.offer_desc_card);

        blure_view.setBlurRadius(100.0f);

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt("KEY_CURRENT_INDEX", 0);
        }

        Bundle args = getArguments();
        if (args != null) {
            currentIndex = args.getInt(ARG_POSITION);
            offers = (List<Offer>) args.getSerializable(ARG_OFFERS);
            setupStories();
        }

        closeButton.setOnClickListener(v -> closeFragment());

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closeFragment();
            }
        });
        // Set up touch listener for pausing and resuming stories
        storiesProgressView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isLongPressed) {
                            storiesProgressView.pause();
                            offer_Desc_card.setVisibility(View.GONE);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isLongPressed) {
                            float x = motionEvent.getX();
                            if (x < v.getWidth() / 2) {
                                // Left side clicked
                                storiesProgressView.reverse();
                            } else {
                                // Right side clicked
                                storiesProgressView.skip();
                            }
                            isLongPressed = false;
                        } else {
                            storiesProgressView.resume();
                            offer_Desc_card.setVisibility(View.VISIBLE);
                            isLongPressed = false;
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        storiesProgressView.resume();
                        offer_Desc_card.setVisibility(View.VISIBLE);
                        isLongPressed = false;
                        break;
                }
                return false;
            }
        });
        storiesProgressView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = true;
                storiesProgressView.pause();
                offer_Desc_card.setVisibility(View.GONE);
                return true;
            }
        });

        return view;
    }
    public void openDialog(Offer offer) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        OfferProductDialog listDialog = new OfferProductDialog(context, offer, navController, "Home") {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        };
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(listDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1700;
        listDialog.show();
        listDialog.getWindow().setAttributes(lp);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }
    private void setupStories() {
        if (offers == null || offers.isEmpty()) return;

        storiesProgressView.setStoriesCount(offers.size());
        storiesProgressView.setStoryDuration(5000L); // 5 seconds per story
        storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
            @Override
            public void onNext() {
                currentIndex++;
                if (currentIndex < offers.size()) {
                    showStory(currentIndex);
                } else {
                    closeFragment();
                }
            }

            @Override
            public void onPrev() {
                if ((currentIndex - 1) >= 0) {
                    currentIndex--;
                    showStory(currentIndex);
                }
            }

            @Override
            public void onComplete() {
                closeFragment();
            }
        });

        // Start stories progress view
        storiesProgressView.startStories(currentIndex);

        // Show the first story
        showStory(currentIndex);
    }

    private void showStory(int index) {
        if (index < 0 || index >= offers.size()) return;

        Offer offer = offers.get(index);
        Glide.with(this).load("https://" + offer.getOffer_image_url()).into(storyImage);
        offer_Desc.setText(offer.getOffer_title());
        offer_Desc_card.setOnClickListener(v->{
            openDialog(offer);
            closeFragment();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        storiesProgressView.destroy();
    }

    private void closeFragment() {
        // Restore the system UI visibility
        showSystemUI();
        // Pop the fragment from the back stack
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void hideSystemUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.INVISIBLE);

        // Hide status bar and navigation bar for full-screen experience
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();
        }
        return actionBar;
    }

    private void showSystemUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);

        // Restore the system UI visibility
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
}
