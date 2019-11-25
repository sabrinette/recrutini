package sabrine.chams.recrutini;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.net.URL;

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
        viewHolder.img.setImageDrawable(loadImageFromUrl(imgUrls[position],Integer.toString(position)));
        return r;
    }

    private Drawable loadImageFromUrl(String url, String img_id)
    {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, img_id);
            return d;
        } catch (Exception e)
        {
            return null;
        }
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
}
