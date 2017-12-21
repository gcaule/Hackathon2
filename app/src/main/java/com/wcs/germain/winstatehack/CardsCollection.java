package com.wcs.germain.winstatehack;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.db.rossdeckview.FlingChief;
import com.db.rossdeckview.FlingChiefListener;
import com.db.rossdeckview.RossDeckView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CardsCollection extends AppCompatActivity implements FlingChiefListener.Actions, FlingChiefListener.Proximity {

    private final static int DELAY = 1000;

    private List<Pair<Pair<CardModel, String>, Integer>> mItems;

    private DeckAdapter mAdapter;

    private View mLeftView;

    private View mUpView;

    private View mRightView;

    private View mDownView;

    private int[] mColors;

    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_collection);

        mColors  = getResources().getIntArray(R.array.cardsBackgroundColors);
        mItems = new ArrayList<Pair<Pair<CardModel, String>, Integer>>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Cards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mItems.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()){
                    CardModel cardModel = new CardModel();
                    cardModel = dsp.getValue(CardModel.class);
                    mItems.add(newItem(cardModel));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAdapter = new DeckAdapter(CardsCollection.this, mItems);

        final RossDeckView mDeckLayout = (RossDeckView) findViewById(R.id.decklayout);
        mDeckLayout.setAdapter(mAdapter);
        mDeckLayout.setActionsListener(CardsCollection.this);
        mDeckLayout.setProximityListener(CardsCollection.this);


        mLeftView = findViewById(R.id.left);
        mUpView = findViewById(R.id.up);
        mRightView = findViewById(R.id.right);
        mDownView = findViewById(R.id.down);

    }

    @Override
    public boolean onDismiss(@NonNull FlingChief.Direction direction, @NonNull View view) {

        Toast.makeText(this, "Nop!", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onDismissed(@NonNull View view) {

        mItems.remove(0);
        mAdapter.notifyDataSetChanged();
        newItemWithDelay(DELAY);
        return true;
    }

    @Override
    public boolean onReturn(@NonNull View view) {
        return true;
    }

    @Override
    public boolean onReturned(@NonNull View view) {
        return true;
    }

    @Override
    public boolean onTapped() {
        return true;
    }



    @Override
    public boolean onDoubleTapped() {
        return true;
    }

    @Override
    public void onProximityUpdate(@NonNull float[] proximities, @NonNull View view) {

        mLeftView.setScaleY((1 - proximities[0] >= 0) ? 1 - proximities[0] : 0);
        mUpView.setScaleX((1 - proximities[1] >= 0) ? 1 - proximities[1] : 0);
        mRightView.setScaleY((1 - proximities[2] >= 0) ? 1 - proximities[2] : 0);
        mDownView.setScaleX((1 - proximities[3] >= 0) ? 1 - proximities[3] : 0);
    }


    private Pair<Pair<CardModel, String>, Integer> newItem(CardModel cardModel){

        Pair<Pair<CardModel, String>, Integer> res = new Pair<>(new Pair<>(cardModel, "USERTOSENDTEST"), mColors[mCount]);
        mCount = (mCount >= mColors.length - 1) ? 0 : mCount + 1;
        return res;
    }


    private void newItemWithDelay(int delay) {
        for (int i = 0; i < mItems.size(); i++) {
            final Pair<Pair<CardModel, String>, Integer> res = newItem(mItems.get(i).first.first);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mItems.add(res);
                    mAdapter.notifyDataSetChanged();
                }
            }, delay);
        }
    }
}
