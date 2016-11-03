package com.estimote.bulkupdater.presentation;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.estimote.bulkupdater.R;
import com.estimote.bulkupdater.model.UpdateStatus;
import com.estimote.sdk.DeviceId;
import com.estimote.sdk.connection.scanner.BulkUpdater;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Adapter for managing list of devices.
 * @author Pawel Dylag (pawel.dylag@estimote.com)
 */
public class DevicesUpdateAdapter extends RecyclerView.Adapter<DevicesUpdateAdapter.UpdateViewHolder> {

  /** Tag for logging */
  private final static String TAG = DevicesUpdateAdapter.class.getSimpleName();
  /** Main dataset with devices and their statuses */
  private List<UpdateStatus> dataset = new ArrayList<>();

  /**
   * Class for item view and layout bindings
   */
  public static class UpdateViewHolder extends RecyclerView.ViewHolder {

    public TextView textViewDeviceId, textViewMessage, textViewStatus;

    public UpdateViewHolder(View itemView) {
      super(itemView);
      textViewDeviceId =  (TextView) itemView.findViewById(R.id.textViewDevice);
      textViewMessage =  (TextView) itemView.findViewById(R.id.textViewMessage);
      textViewStatus =  (TextView) itemView.findViewById(R.id.textViewStatus);
    }
  }


  // METHODS

  @Override
  public DevicesUpdateAdapter.UpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // create a new view
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.recycler_item, parent, false);
    return new UpdateViewHolder(v);
  }


  @Override
  public void onBindViewHolder(DevicesUpdateAdapter.UpdateViewHolder holder, int position) {
    UpdateStatus d = dataset.get(position);
    holder.textViewStatus.setText(d.status.toString());
    holder.textViewDeviceId.setText(d.deviceId.toHexString());
    holder.textViewMessage.setText(d.message);
  }

  @Override
  public int getItemCount() {
    return dataset.size();
  }

  public void setDataset(List<UpdateStatus> dataset) {
    if (dataset == null) {
      Log.d(TAG, "dataset cannot be null");
      return;
    }
    this.dataset = dataset;
    notifyDataSetChanged();
  }

  public void changeStatus(DeviceId id, BulkUpdater.Status status, String message) {
    for (int i = 0; i < dataset.size(); i++) {
      UpdateStatus us = dataset.get(i);
      if (us.deviceId.equals(id)) {
        us.message = message;
        us.status = status;
        notifyItemChanged(i);
        return;
      }
    }
  }

}
