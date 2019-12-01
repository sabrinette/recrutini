package sabrine.chams.recrutini;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.TypedArrayUtils;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.android.volley.VolleyLog.TAG;

public class ListViewAdapter extends ArrayAdapter<String> {
    private String[] offernames;
    private String[] descriptions;
    private String[] imgUrls;
    private String[] IDs;
    private Activity context;

    public ListViewAdapter(Activity context, String[] offernames, String[] descriptions, String[] imgUrls,String[] IDs) {
        super(context, R.layout.listview_layout, new ArrayList<String>(Arrays.asList(offernames)));
        this.context = context;
        this.offernames = offernames;
        this.descriptions = descriptions;
        this.imgUrls = imgUrls;
        this.IDs = IDs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if (r == null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.listview_layout, null, true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) r.getTag();
        viewHolder.name.setText(offernames[position]);
        viewHolder.description.setText(descriptions[position]);
        viewHolder.id.setText(IDs[position]);
        SendHttpRequestTask sh = new SendHttpRequestTask();
        sh.setImageView(viewHolder.img);
        sh.execute(imgUrls[position]);
        return r;
    }

    @Override
    public void remove(@Nullable String object) {
        super.remove(offernames[Integer.parseInt(object)]);
        this.offernames = ArrayUtils.remove(offernames, Integer.parseInt(object));
        this.descriptions = ArrayUtils.remove(descriptions, Integer.parseInt(object));
        this.IDs = ArrayUtils.remove(IDs, Integer.parseInt(object));
        this.imgUrls = ArrayUtils.remove(imgUrls, Integer.parseInt(object));
    }

    class ViewHolder{
        TextView name;
        TextView description;
        TextView id;
        ImageView img;
        ViewHolder(View v)
        {
            name = v.findViewById(R.id.offer_name);
            description = v.findViewById(R.id.offer_desc);
            img = v.findViewById(R.id.offer_img);
            id = v.findViewById(R.id.offer_id);
        }
    }
}
