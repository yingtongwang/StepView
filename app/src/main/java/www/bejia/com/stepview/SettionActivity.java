package www.bejia.com.stepview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Author   : Wangyingbao
 * <p>
 * Date     : 2016/11/5 11:22
 * <p>
 * Email    : 1551757778@qq.com
 * <p>
 * Describe :
 */

public class SettionActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settion);


        sharedPreferences = this.getSharedPreferences(Keys.SPNAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /**
         * 加载默认的目标bushu
         */
        int step = sharedPreferences.getInt(Keys.ALLSTEP, 1000);

        editText = (EditText) findViewById(R.id.editText);
        editText.setText(step + "");
    }

    /**
     * 创建菜单
     *
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                editor.putInt(Keys.ALLSTEP, Integer.valueOf(editText.getText().toString()));
                editor.commit();
                setResult(MainActivity.REQUESTCODE);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
