package app.fluky.ml.fluk.xtras;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.fluky.ml.fluk.R;
import app.fluky.ml.fluk.complex.dialogs.DetailsInputDialog2;
import app.fluky.ml.fluk.dialog.CustomAlertDialogue;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class HistoryUser extends AppCompatActivity {

    private List<upcoming_list_item> itemList20 = new ArrayList<>();
    private RecyclerView recyclerview20;
    private MyAdapter mAdapter20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_history);

        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                .enabled(true) //default: true
                .showErrorDetails(false) //default: true
                .showRestartButton(false) //default: true
                .logErrorOnRestart(false) //default: true
                .trackActivities(false) //default: false
                .minTimeBetweenCrashesMs(2000) //default: 3000
                .errorDrawable(R.mipmap.ic_launcher_round) //default: bug image
                .restartActivity(DetailsInputDialog2.class) //default: null (your app's launch activity)
                .errorActivity(DetailsInputDialog2.class) //default: null (default error activity)
                .eventListener(null) //default: null
                .apply();

        recyclerview20 = (RecyclerView) findViewById(R.id.recycler_viewUP);
        mAdapter20 = new MyAdapter(itemList20);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getApplicationContext());
        recyclerview20.setLayoutManager(mLayoutManger);
        recyclerview20.setItemAnimator(new DefaultItemAnimator());
        recyclerview20.setAdapter(mAdapter20);

        AssetManager am = this.getApplicationContext().getAssets();

        Typeface typefaceBold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "goobold.ttf"));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference perkDoc = db.collection("history").document("history");
        perkDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.get("historymain") != null) {
                    upcoming_list_item item;
                    String[] ent = doc.get("historymain").toString().split(";");
                    for (int i = 0; i < ent.length - 1; i += 2) {
                        item = new upcoming_list_item(ent[i], ent[i + 1]);
                        itemList20.add(item);
                    }
                    mAdapter20.notifyDataSetChanged();
                    recyclerview20.setAdapter(mAdapter20);
                    findViewById(R.id.gifViewCardHistory).setAlpha(0);
                }
            }
        })
                .addOnFailureListener(e -> {
                    CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(HistoryUser.this)
                            .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                            .setTitle("We can't connect")
                            .setMessage("Looks like you don't have a stable internet connection. Please try again later")
                            .setPositiveText("Okay")
                            .setPositiveTypeface(typefaceBold)
                            .setOnPositiveClicked((view, dialog) -> dialog.dismiss())
                            .setDecorView(getWindow().getDecorView())
                            .build();
                    alert.show();
                });
    }
}
