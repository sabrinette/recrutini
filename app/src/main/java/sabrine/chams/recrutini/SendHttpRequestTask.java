package sabrine.chams.recrutini;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.android.volley.VolleyLog.TAG;

public class SendHttpRequestTask extends AsyncTask<String, Void, Bitmap> {
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
            connection.disconnect();
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