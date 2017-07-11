package ph.edu.up.floweralmanacfirebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by victo on 7/11/2017.
 */

public class ViewActivity extends AppCompatActivity {
    //Incorporate post to FB feature - another oAuth

    public final static String KEY = "ph.edu.up.viewactivity.KEY";
    public final static String NAME = "ph.edu.up.viewactivity.NAME";
    public final static String EASE = "ph.edu.up.viewactivity.EASE";
    public final static String INST = "ph.edu.up.viewactivity.INST";
    public final static String URL = "ph.edu.up.viewactivity.URL";
    public final static String DEL = "ph.edu.up.viewactivity.DEL";

    public static String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Intent intent = getIntent();

        String nameFlower = intent.getStringExtra(FlowerMainActivity.NAMEFLOWER);
        String easeFlower = intent.getStringExtra(FlowerMainActivity.EASEFLOWER);
        String instFlower = intent.getStringExtra(FlowerMainActivity.INSTFLOWER);
        String keyFlower = intent.getStringExtra(FlowerMainActivity.KEYFLOWER);
        String urlFlower = intent.getStringExtra(FlowerMainActivity.URLFLOWER);

        TextView userText = (TextView) findViewById(R.id.flowerName);
        userText.setText(nameFlower);

        TextView userText1 = (TextView) findViewById(R.id.flowerEase);
        userText1.setText(easeFlower);

        TextView userText2 = (TextView) findViewById(R.id.flowerInst);
        userText2.setText(instFlower);

        TextView userText3 = (TextView) findViewById(R.id.keyField);
        userText3.setText(String.valueOf(keyFlower));

        TextView userText4 = (TextView) findViewById(R.id.urlField);
        userText4.setText(urlFlower);

        ImageView imageView = (ImageView) findViewById(R.id.image);

        if (urlFlower.equals("dummyData")) {
            if (easeFlower.equals("Easy")) {
                imageView.setImageResource(R.mipmap.inspired);
            } else if (easeFlower.equals("Medium")) {
                imageView.setImageResource(R.mipmap.happy);
            } else if (easeFlower.equals("Difficult")){
                imageView.setImageResource(R.mipmap.laugh);
            }
        } else {
            Glide.with(imageView.getContext())
                    .load(urlFlower)
                    .into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_flower_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Intent intent = new Intent();

            TextView textView = (TextView) findViewById(R.id.flowerName);
            String name = textView.getText().toString();

            TextView textView1 = (TextView) findViewById(R.id.flowerEase);
            String ease = textView1.getText().toString();

            TextView textView2 = (TextView) findViewById(R.id.flowerInst);
            String inst = textView2.getText().toString();

            TextView textView3 = (TextView) findViewById(R.id.keyField);
            String keyFlower = textView3.getText().toString();

            TextView textView4 = (TextView) findViewById(R.id.urlField);
            String urlFlower = textView4.getText().toString();

            intent.putExtra(KEY, keyFlower);
            intent.putExtra(NAME, name);
            intent.putExtra(EASE, ease);
            intent.putExtra(INST, inst);
            intent.putExtra(URL, urlFlower);
            intent.putExtra(DEL, "false");

            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;

        } else if (id == R.id.action_delete) {
            Intent intent = new Intent();

            TextView textView = (TextView) findViewById(R.id.flowerName);
            String name = textView.getText().toString();

            TextView textView1 = (TextView) findViewById(R.id.flowerEase);
            String ease = textView1.getText().toString();

            TextView textView2 = (TextView) findViewById(R.id.flowerInst);
            String inst = textView2.getText().toString();

            TextView textView3 = (TextView) findViewById(R.id.keyField);
            String keyFlower = textView3.getText().toString();

            TextView textView4 = (TextView) findViewById(R.id.urlField);
            String urlFlower = textView4.getText().toString();

            intent.putExtra(KEY, keyFlower);
            intent.putExtra(NAME, name);
            intent.putExtra(EASE, ease);
            intent.putExtra(INST, inst);
            intent.putExtra(URL, urlFlower);
            intent.putExtra(DEL, "true");

            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewActivity.this, FlowerMainActivity.class);
        startActivity(intent);
        finish();
    }
}
