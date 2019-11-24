package sabrine.chams.recrutini;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        return super.getView(position, convertView, parent);
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
