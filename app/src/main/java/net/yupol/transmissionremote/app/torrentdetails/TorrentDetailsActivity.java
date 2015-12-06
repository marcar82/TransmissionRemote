package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.request.TorrentSetRequest;

public class TorrentDetailsActivity extends BaseSpiceActivity implements SaveChangesDialogFragment.SaveDiscardListener {

    public static final String EXTRA_NAME_TORRENT = "extra_key_torrent";

    private static String TAG_SAVE_CHANGES_DIALOG = "tag_save_changes_dialog";

    private static String KEY_OPTIONS_CHANGE_REQUEST = "key_options_request";

    private Torrent torrent;
    private TorrentSetRequest saveChangesRequest;
    private TorrentDetailsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.torrent_details_layout);

        torrent = getIntent().getParcelableExtra(EXTRA_NAME_TORRENT);

        setupActionBar();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new TorrentDetailsPagerAdapter(this, getSupportFragmentManager(), torrent);
        pager.setAdapter(pagerAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_OPTIONS_CHANGE_REQUEST, saveChangesRequest);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        saveChangesRequest = savedInstanceState.getParcelable(KEY_OPTIONS_CHANGE_REQUEST);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ActionBarTitleAppearance);
        toolbar.setSubtitleTextAppearance(this, R.style.ActionBarSubTitleAppearance);

        toolbar.setTitle(R.string.torrent_details);
        toolbar.setSubtitle(torrent.getName());

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {

        OptionsPageFragment optionsPage = (OptionsPageFragment) pagerAdapter.getFragment(OptionsPageFragment.class);
        TorrentSetRequest.Builder saveChangesRequestBuilder = optionsPage.getSaveOptionsRequestBuilder();

        if (saveChangesRequestBuilder == null || !saveChangesRequestBuilder.isChanged()) {
            super.onBackPressed();
            return;
        }

        saveChangesRequest = saveChangesRequestBuilder.build();
        new SaveChangesDialogFragment().show(getFragmentManager(), TAG_SAVE_CHANGES_DIALOG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSavePressed() {
        getTransportManager().doRequest(saveChangesRequest, null);
        super.onBackPressed();
    }

    @Override
    public void onDiscardPressed() {
        super.onBackPressed();
    }
}