package com.estimote.examples.demos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.estimote.examples.demos.R;
import com.estimote.sdk.connection.scanner.ConfigurableDevicesScanner;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Displays basic information about beacon.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class ConfigurableDevicesListAdapter extends BaseAdapter {

  private ArrayList<ConfigurableDevicesScanner.ScanResultItem> devices;
  private LayoutInflater inflater;

  public ConfigurableDevicesListAdapter(Context context) {
    this.inflater = LayoutInflater.from(context);
    this.devices = new ArrayList<>();
  }

  public void replaceWith(Collection<ConfigurableDevicesScanner.ScanResultItem> newDevices) {
    this.devices.clear();
    this.devices.addAll(newDevices);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return devices.size();
  }

  @Override
  public ConfigurableDevicesScanner.ScanResultItem getItem(int position) {
    return devices.get(position);
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

  private void bind(ConfigurableDevicesScanner.ScanResultItem scanResultItem, View view) {
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.idTextView.setText("ID: " + scanResultItem.device.deviceId.toHexString());
    holder.macTextView.setText(String.format("MAC: %s", scanResultItem.device.macAddress.toStandardString()));
    holder.signalTextView.setText(String.format("Signal: %ddBm", scanResultItem.rssi));
    holder.typeTextView.setText("Type: " + scanResultItem.device.type);
  }

  private View inflateIfRequired(View view, int position, ViewGroup parent) {
    if (view == null) {
      view = inflater.inflate(R.layout.device_item, null);
      view.setTag(new ViewHolder(view));
    }
    return view;
  }

  static class ViewHolder {
    final TextView idTextView;
    final TextView macTextView;
    final TextView typeTextView;
    final TextView signalTextView;

    ViewHolder(View view) {
      macTextView = (TextView) view.findViewWithTag("mac");
      idTextView = (TextView) view.findViewWithTag("identifier");
      typeTextView = (TextView) view.findViewWithTag("type");
      signalTextView = (TextView) view.findViewWithTag("signal");
    }
  }
}
