package ir.tdaapp.tooka.adapters;

import android.content.Context;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ir.tdaapp.tooka.R;
import ir.tdaapp.tooka.models.Coin;
import ir.tdaapp.tooka.util.api.RetrofitClient;

public class TookaSpinnerAdapter extends BaseAdapter implements Filterable {

  List<Coin> list;
  Context context;
  LayoutInflater inflater;

  private ArrayList<String> fullList;
  private ArrayList<String> mOriginalValues;
  private ArrayFilter mFilter;

  public TookaSpinnerAdapter(Context context, List<Coin> list) {
    this.list = list;
    this.context = context;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    convertView = inflater.inflate(R.layout.list_item, null);
    ImageView icon = convertView.findViewById(R.id.image);
    TextView names = convertView.findViewById(R.id.text);
    String imageUrl = RetrofitClient.COIN_IMAGES + list.get(position).getIcon();
    Glide.with(context)
      .load(imageUrl)
      .into(icon);
    names.setText(list.get(position).toString());
    return convertView;
  }

  @Override
  public Filter getFilter() {
    if (mFilter == null) {
      mFilter = new ArrayFilter();
    }
    return mFilter;
  }

  private class ArrayFilter extends Filter {
    private Object lock;

    @Override
    protected FilterResults performFiltering(CharSequence prefix) {
      FilterResults results = new FilterResults();

      if (mOriginalValues == null) {
        synchronized (lock) {
          mOriginalValues = new ArrayList<String>(fullList);
        }
      }

      if (prefix == null || prefix.length() == 0) {
        synchronized (lock) {
          ArrayList<String> list = new ArrayList<String>(mOriginalValues);
          results.values = list;
          results.count = list.size();
        }
      } else {
        final String prefixString = prefix.toString().toLowerCase();

        ArrayList<String> values = mOriginalValues;
        int count = values.size();

        ArrayList<String> newValues = new ArrayList<String>(count);

        for (int i = 0; i < count; i++) {
          String item = values.get(i);
          if (item.toLowerCase().contains(prefixString)) {
            newValues.add(item);
          }

        }

        results.values = newValues;
        results.count = newValues.size();
      }

      return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

      if (results.values != null) {
        fullList = (ArrayList<String>) results.values;
      } else {
        fullList = new ArrayList<String>();
      }
      if (results.count > 0) {
        notifyDataSetChanged();
      } else {
        notifyDataSetInvalidated();
      }
    }
  }
}
