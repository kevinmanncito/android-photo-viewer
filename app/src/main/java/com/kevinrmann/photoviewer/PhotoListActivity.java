package com.kevinrmann.photoviewer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoListActivity extends AppCompatActivity {

    public final static String IMAGE_PATH = "com.kevinrmann.photoviewr.IMAGE_PATH";

    private Cursor cursor;
    private int columnIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        // Set up an array of the Thumbnail Image ID column we want
        String[] projection = {
                MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Thumbnails.DATA
        };
        // Create the cursor pointing to the SDCard
        cursor = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                MediaStore.Images.Thumbnails.IMAGE_ID);
        int imageCount = cursor.getCount();
        setTitle("Images: "+imageCount);
        // Get the column index of the Thumbnails Image ID
        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

        GridView sdcardImages = (GridView) findViewById(R.id.imagesGridView);
        sdcardImages.setAdapter(new ImageAdapter(this));

        // Set up a click listener
        sdcardImages.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // Get the data location of the image
                String[] projection = {MediaStore.Images.Media.DATA};
                cursor = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, // Which columns to return
                        null,       // Return all rows
                        null,
                        null);
                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToPosition(position);
                // Get image filename
                String imagePath = cursor.getString(columnIndex);
                // Use this path to do further processing, i.e. full screen display
                sendMessage(v, imagePath);
            }
        });
    }

    public void sendMessage(View view, String imagePath) {
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(IMAGE_PATH, imagePath);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * Adapter for our image files.
     */
    private class ImageAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;

        public ImageAdapter(Context localContext) {
            context = localContext;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return cursor.getCount();
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.image_with_subtitle, null);

            holder.tv=(TextView) rowView.findViewById(R.id.imageSubtitle);
            holder.img=(ImageView) rowView.findViewById(R.id.imageThumbnail);

            cursor.moveToPosition(position);
            int imageID = cursor.getInt(columnIndex);
            holder.img.setImageURI(Uri.withAppendedPath(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
            holder.img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.img.setPadding(2, 2, 2, 2);


            int tempIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(tempIndex);
            String fileName=filePath.substring(filePath.lastIndexOf("/")+1);
            holder.tv.setText(fileName);

            return rowView;

        }
    }

    static class Holder {
        TextView tv;
        ImageView img;
    }
}
