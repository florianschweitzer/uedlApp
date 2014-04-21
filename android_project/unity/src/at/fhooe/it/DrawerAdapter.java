package at.fhooe.it;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Adapter which contains the information which is shown in the navigation
 * drawer
 * 
 * @author Florian Schweitzer
 * 
 */
public class DrawerAdapter extends BaseExpandableListAdapter {
	/**
	 * HashMap with the items
	 */
	private HashMap<String, List<String>> m_items;

	/**
	 * The context of the adapter
	 */
	private Context m_context;

	/**
	 * Index of the current selected phase
	 */
	private int m_selGroupColor;

	/**
	 * Index of the current selected detail
	 */
	private int m_selChildColor;

	/**
	 * Index of the current selected item
	 */
	private int[] m_selectedItem = { 0, 0 };

	/**
	 * Constructor
	 * 
	 * @param _context
	 *            the context
	 * @param _items
	 *            the items which are contained in the drawer
	 */
	public DrawerAdapter(Context _context, HashMap<String, List<String>> _items) {
		m_context = _context;
		m_items = _items;
	}

	/**
	 * Set the color of the selected items
	 * 
	 * @param _selGroupColor
	 *            the current selected phase
	 * @param _selChildColor
	 *            the current selected detail
	 */
	public void setColors(int _selGroupColor, int _selChildColor) {
		m_selGroupColor = _selGroupColor;
		m_selChildColor = _selChildColor;
		notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		String key = (String) m_items.keySet().toArray()[groupPosition];
		return m_items.get(key).get(childPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition + childPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View row = ((LayoutInflater) m_context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.drawer_child_item, null);

		TextView rowTv = (TextView) row.findViewById(R.id.childItem);

		String group = (String) m_items.keySet().toArray()[groupPosition];
		String child = m_items.get(group).get(childPosition);

		if (groupPosition == m_selectedItem[0]
				&& childPosition == m_selectedItem[1]) {
			row.setBackgroundColor(m_selChildColor);
		}

		rowTv.setText(child);
		return row;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		String key = (String) m_items.keySet().toArray()[groupPosition];
		return m_items.get(key).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return (String) m_items.keySet().toArray()[groupPosition];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return m_items.keySet().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View row = ((LayoutInflater) m_context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.drawer_group_item, null);

		TextView rowTv = (TextView) row.findViewById(R.id.groupItem);
		rowTv.setTypeface(Typeface.DEFAULT_BOLD);

		String group = (String) m_items.keySet().toArray()[groupPosition];
		rowTv.setText(group);

		if (groupPosition == m_selectedItem[0]) {
			row.setBackgroundColor(m_selGroupColor);
		}

		return row;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/**
	 * Select a specific item
	 * 
	 * @param groupPosition
	 *            the index of the selected phase
	 * @param childPosition
	 *            the index of the selected detail
	 */
	public void selectItem(int groupPosition, int childPosition) {
		m_selectedItem[0] = groupPosition;
		m_selectedItem[1] = childPosition;
		notifyDataSetChanged();
	}

}
