package net.kidfeng.android.osbuildreader;

import java.lang.reflect.Field;

import net.kidfeng.android.osbuildreader.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends Activity {
	private static final int ABOUT_DIALOG = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView lv = (ListView)findViewById(R.id.ConstantList);
        lv.setAdapter(new ConstantAdapter());
        setAnimation(lv);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    	case ABOUT_DIALOG:
    		return createAboutDialog();
    	default:
        	return super.onCreateDialog(id);
    	}
    }
    
    private Dialog createAboutDialog(){
    	Builder builder = new Builder(this);
    	builder.setTitle(R.string.about_title);
    	View view = getLayoutInflater().inflate(R.layout.about_dialog, null);
    	builder.setView(view);
    	builder.setPositiveButton(R.string.more_app, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					ApplicationInfo info = getPackageManager().getApplicationInfo("com.android.vending", PackageManager.GET_META_DATA);
					if(info != null) {
						Intent intent = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("market://search?q=pub:\"Kid.F\""));
						startActivity(intent);
						return;
					} else {
						Toast.makeText(Home.this, R.string.no_market, Toast.LENGTH_LONG);
					}
				} catch (NameNotFoundException e) {/*do nothing*/}
			}
		});
    	return builder.create();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.home, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.about:
    		showDialog(ABOUT_DIALOG);
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    
    private class ConstantAdapter extends BaseAdapter {
    	private  final Field[] fields = android.os.Build.class.getDeclaredFields();

		@Override
		public int getCount() {
			return fields.length;
		}

		@Override
		public Object getItem(int position) {
			return fields[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView nameTextView, valueTextView;
			
			if(convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item, null);
				nameTextView = (TextView)convertView.findViewById(R.id.ConstantName);
				valueTextView = (TextView)convertView.findViewById(R.id.ConstantValue);
				
				Holder holder = new Holder();
				holder.name = nameTextView;
				holder.value = valueTextView;
				convertView.setTag(holder);
			} else {
				Holder holder = (Holder)convertView.getTag();
				nameTextView = holder.name;
				valueTextView = holder.value;
			}
			Field field = fields[position];
			field.setAccessible(true);
			String name = field.getName();
			nameTextView.setText("Build."+ name + " = ");
			try {
				valueTextView.setText(field.get(name).toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return convertView;
		}
    }
    
	private static class Holder {
		TextView name;
		TextView value;
	}
	
	private static void setAnimation(ListView listView){
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(150);
        set.addAnimation(animation);
		
		LayoutAnimationController controller = new LayoutAnimationController(animation);
		listView.setLayoutAnimation(controller);
	}
}