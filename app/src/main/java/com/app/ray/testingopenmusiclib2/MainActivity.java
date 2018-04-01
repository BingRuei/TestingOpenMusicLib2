package com.app.ray.testingopenmusiclib2;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    ListView musiclist;
    Cursor musiccursor;
    int music_column_index;
    int count;
    MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_phone_music_grid();
    }

    // 音樂檔案匯入時系統會在資料庫的Audio View上建立對應資料列
    // 因此Android的對應類別就是MediaStore.Audio
    // 透過Eclipse就可以找到下面幾個欄位資料
    // MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA
    // MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE
    // MediaStore.Audio.Media.DATA是音樂檔案所在路徑位置
    private void init_phone_music_grid() {
        System.gc();
        String[] proj = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE};
        musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                proj, null, null, null);
        count = musiccursor.getCount();
        musiclist = (ListView) findViewById(R.id.PhoneMusicList);
        musiclist.setAdapter(new MusicAdapter(getApplicationContext()));
        musiclist.setOnItemClickListener(musicgridlistener);
        mMediaPlayer = new MediaPlayer();
    }

    private AdapterView.OnItemClickListener musicgridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            System.gc();
            music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            musiccursor.moveToPosition(position);
            String filename = musiccursor.getString(music_column_index);
            try {
                if (mMediaPlayer.isPlaying()) {
                    Toast.makeText(MainActivity.this, "Player Stoped",
                            Toast.LENGTH_LONG).show();
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    return;
                }
                // 音樂檔案播放標準程序
                mMediaPlayer.setDataSource(filename);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (Exception e) {
            }
        }
    };

    public class MusicAdapter extends BaseAdapter {
        private Context mContext;

        public MusicAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            System.gc();
            TextView tv1 = new TextView(mContext.getApplicationContext());
            TextView tv2 = new TextView(mContext.getApplicationContext());
            ImageView img = new ImageView(mContext.getApplicationContext());
            // 設定圖片及上下文所需要的畫面元件
            img.setImageResource(R.mipmap.ic_launcher);
            tv1.setTextSize(12.0f);
            tv2.setTextSize(12.0f);
            String id = null;

            music_column_index = musiccursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            musiccursor.moveToPosition(position);
            id = musiccursor.getString(music_column_index);
            tv1.setText(id);
            music_column_index = musiccursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            musiccursor.moveToPosition(position);
            id = "Size(MB):"
                    + String.format("%,5.2f", Double
                    .parseDouble(musiccursor
                            .getString(music_column_index)) / 1024);
            tv2.setText(id);

            // 產生兩個線性佈局物件第一個是水平方向佈局
            // 左邊加入圖片右邊加入第二個線性佈局方向是垂直方向佈局
            // 最後加入文字元件到第二個線性佈局內
            LinearLayout layout = new LinearLayout(
                    mContext.getApplicationContext());
            LinearLayout innerLayout = new LinearLayout(
                    mContext.getApplicationContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.addView(tv1);
            innerLayout.addView(tv2);
            layout.addView(img);
            layout.addView(innerLayout);
            return layout;
        }
    }
}