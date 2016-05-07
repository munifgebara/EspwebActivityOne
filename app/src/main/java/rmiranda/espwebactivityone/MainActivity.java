package rmiranda.espwebactivityone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String SHOULD_TAKE_PIC = "shouldTakePic";
    private Uri imageToUploadUri;

    private ImageView mImageView;
    private Button btTakePic;
    private Button btShare;
    private static Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.mImageView);
        btTakePic = (Button) findViewById(R.id.btTakePic);
        btShare = (Button) findViewById(R.id.btShare);

        btTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageToUploadUri == null) {
                    Log.d("MYLOG", "BitmapIsNull");
                    return;
                }

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageToUploadUri);
                shareIntent.setType("image/jpeg");
                startActivityForResult(Intent.createChooser(shareIntent, "Share"), 2);
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    "POST_IMAGE",  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            image.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        imageToUploadUri = Uri.fromFile(image);

        Log.d("MYLOG", "ImagePath " + imageToUploadUri.getPath());

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MYLOG", "activity result");

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(data == null) {
                imageBitmap = BitmapFactory.decodeFile(imageToUploadUri.getPath());
                mImageView.setImageBitmap(imageBitmap);
                return;
            }

            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        } else if(requestCode == 2 && resultCode == RESULT_OK) {
            File f = new File(imageToUploadUri.getPath());
            f.delete();
            Log.d("MYLOG", "Deleted file");
        }
    }
}
