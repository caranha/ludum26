package org.castelodelego.ludum26;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements WebpageCaller {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useAccelerometer = false;
        cfg.useCompass = false;

        initialize(new ludum26entry(this), cfg);
    }
    

	@Override
	public void callWebpage(String address) {
		android.net.Uri uri = android.net.Uri.parse(address);
    	startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
	}
}