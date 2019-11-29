package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final int THUMBNAIL_SIZE = 1000;
    ConstraintLayout registerPage ;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText name;
    TextInputEditText confirmPassword;
    TextInputEditText address;
    TextInputEditText phoneNumber;
    TextView goLogin ;
    Button confirmRegister ;
    Button uploadBtn;
    Uri selectedImage;
    String picturePath;
    Bitmap photo;
    String imgExtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        confirmPassword = findViewById(R.id.confirm_password);
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone_number);
        registerPage = findViewById(R.id.register_page);
        confirmRegister = findViewById(R.id.confirm_register);
        uploadBtn = findViewById(R.id.upload_btn);
        goLogin = findViewById(R.id.go_to_login);
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(getApplicationContext() , LoginActivity.class);
                startActivity(loginActivity);
            }
        });
        confirmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( confirmPassword.getText().toString().equals(password.getText().toString()) ) {
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    final String emailText = email.getText().toString();
                    final String nameText = name.getText().toString();
                    final String addressText = address.getText().toString();
                    final String phoneNumberText = phoneNumber.getText().toString();
                    final String passwordText = new BigInteger(1, md.digest(password.getText().toString().getBytes())).toString(16);
                    StringRequest postRequest = new StringRequest(Request.Method.POST, "https://sabrine-chams.alwaysdata.net/insert_societe.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("mochklaaa", response);
                                    JSONObject res = null;
                                    try {
                                        res = new JSONObject(response);
                                        String success = res.getString("success");
                                        if ( success.equals("1"))
                                        {
                                            Intent loginActivity = new Intent( getApplicationContext(), LoginActivity.class);
                                            startActivity(loginActivity);
                                        }
                                        else if (success.equals("2")){
                                            Snackbar.make(registerPage ," This account already exists !" , Snackbar.LENGTH_LONG).show();
                                        }
                                        else {
                                            Snackbar.make(registerPage ," Registration failed !" , Snackbar.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Snackbar.make(registerPage ," parsing Json failed !" , Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Snackbar.make(registerPage ," Registration failed !" , Snackbar.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("email", emailText);
                            params.put("password", passwordText);
                            params.put("adresse", addressText);
                            params.put("nom", nameText);
                            params.put("num_tel", phoneNumberText);
                            params.put("img", getStringImage(photo));
                            params.put("img_ext", imgExtn);
                            return params;
                        }
                    };
                    queue.add(postRequest);
                }
                else {confirmPassword.setError(" Not same password !");}
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });
    }
    public void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an image"),1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            selectedImage = data.getData();
            try {
                photo = getThumbnail(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Cursor to get image uri to display

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            imgExtn = picturePath.substring(picturePath.lastIndexOf(".") + 1);

            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView imageView = findViewById(R.id.image_prev);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(photo);
        }
    }
    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10,byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encode_image= Base64.encodeToString(imageByteArray,Base64.DEFAULT);
        return encode_image;
    }
    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        //bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
}
