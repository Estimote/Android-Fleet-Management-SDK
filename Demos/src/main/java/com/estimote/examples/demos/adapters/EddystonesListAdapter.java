package com.estimote.examples.demos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.estimote.examples.demos.R;
import com.estimote.sdk.Utils;
import com.estimote.sdk.eddystone.Eddystone;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Displays basic information about nearable.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class EddystonesListAdapter extends BaseAdapter {

  private ArrayList<Eddystone> eddystones;
  private LayoutInflater inflater;

  public EddystonesListAdapter(Context context) {
    this.inflater = LayoutInflater.from(context);
    this.eddystones = new ArrayList<>();
  }

  public void replaceWith(Collection<Eddystone> newEddystones) {
    this.eddystones.clear();
    this.eddystones.addAll(newEddystones);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return eddystones.size();
  }

  @Override
  public Eddystone getItem(int position) {
    return eddystones.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    view = inflateIfRequired(view, position, parent);
    bind(getItem(position), view);
    return view;
  }

  private void bind(Eddystone eddystone, View view) {
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.macTextView.setText(String.format("MAC: %s (%.2fm)", eddystone.macAddress, Utils.computeAccuracy(eddystone)));
    holder.rssiTextView.setText("RSSI: " + eddystone.rssi);
    holder.eddystoneNamespaceTextView.setText("Namespace: " + (eddystone.namespace == null ? "-" : eddystone.namespace));
    holder.eddystoneInstanceIdTextView.setText("Instance ID: " + (eddystone.instance == null ? "-" : eddystone.instance));
    holder.eddystoneUrlTextView.setText("URL: " + (eddystone.url == null ? "-" : eddystone.url));
  }

  private View inflateIfRequired(View view, int position, ViewGroup parent) {
    if (view == null) {
      view = inflater.inflate(R.layout.eddystone_item, null);
      view.setTag(new ViewHolder(view));
    }
    return view;
  }

  static class ViewHolder {
    final TextView macTextView;
    final TextView rssiTextView;
    final TextView eddystoneNamespaceTextView;
    final TextView eddystoneInstanceIdTextView;
    final TextView eddystoneUrlTextView;

    ViewHolder(View view) {
      macTextView = (TextView) view.findViewWithTag("mac");
      rssiTextView = (TextView) view.findViewWithTag("rssi");
      eddystoneNamespaceTextView = (TextView) view.findViewWithTag("eddystone_namespace");
      eddystoneInstanceIdTextView = (TextView) view.findViewWithTag("eddystone_instance_id");
      eddystoneUrlTextView = (TextView) view.findViewWithTag("eddystone_url");
    }
  }
}
