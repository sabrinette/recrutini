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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

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
    private Activity context;

    public ListViewAdapter(Activity context, String[] offernames, String[] descriptions, String[] imgUrls) {
        super(context, R.layout.listview_layout, offernames);
        this.context = context;
        this.offernames = offernames;
        this.descriptions = descriptions;
        this.imgUrls = imgUrls;
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
        SendHttpRequestTask sh = new SendHttpRequestTask();
        sh.setImageView(viewHolder.img);
        sh.execute(imgUrls[position]);
        return r;
    }
    class ViewHolder{
        TextView name;
        TextView description;
        ImageView img;
        ViewHolder(View v)
        {
            name = v.findViewById(R.id.offer_name);
            description = v.findViewById(R.id.offer_desc);
            img = v.findViewById(R.id.offer_img);
        }
    }
    private class SendHttpRequestTask extends AsyncTask<String, Void, Bitmap> {
        ImageView v;
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            v.setImageBitmap(result);
        }
        void setImageView(ImageView v)
        {
            this.v = v;
        }
    }
}
