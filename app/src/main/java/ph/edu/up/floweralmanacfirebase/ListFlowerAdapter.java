package ph.edu.up.floweralmanacfirebase;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by victo on 7/10/2017.
 */

public class ListFlowerAdapter extends ArrayAdapter<Flower> {
    public static String path = "";

    public ListFlowerAdapter(Context context, int resources, List<Flower> objects) {
        super(context, resources, objects);
    }

    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.content_flower_main, parent, false);
        }

        TextView textName = (TextView) convertView.findViewById(R.id.item_name);
        TextView textEase = (TextView) convertView.findViewById(R.id.item_ease);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);

        Flower flower = getItem(position);

        textName.setText(flower.getFlowerName());
        textEase.setText(flower.getEase());

        if (!flower.getPhotoUrl().equals("dummyData")) {
            Glide.with(imageView.getContext())
                    .load(flower.getPhotoUrl())
                    .into(imageView);
        } else {

            String ease = flower.getEase();

            if (ease.equals("Easy")) {
                imageView.setImageResource(R.mipmap.inspired);
            } else if (ease.equals("Medium")) {
                imageView.setImageResource(R.mipmap.happy);
            } else if (ease.equals("Difficult")){
                imageView.setImageResource(R.mipmap.laugh);
            }
        }
        return convertView;
    }
}
