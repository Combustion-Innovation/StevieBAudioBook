package com.steviebaudiobook.steviebaudiobook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageMarkers extends LinearLayout {
	
	Communicator comm;
	Context c;
	int totalPages, currentPage;
	ImageView greyDot, whiteDot;
	LinearLayout container;
	
	@SuppressLint("NewApi")
	public PageMarkers(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		this.c = context;
	}

	public PageMarkers(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.c = context;
	}

	public PageMarkers(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.c = context;
		
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public void makeView(int currentPage) {
		
		
		this.removeAllViews();
		
		this.currentPage = currentPage;
		
		this.container = new LinearLayout(c);
		this.addView(container);
		
		this.container.setOrientation(LinearLayout.HORIZONTAL);
		this.container.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		this.container.setGravity(Gravity.CENTER);
		
		
		for(int i=0; i<totalPages; i++) {
			
			
			ImageView im = new ImageView(c);
			this.container.addView(im);
			im.setLayoutParams(new LayoutParams(10, 10));
			LinearLayout.LayoutParams margins = (LinearLayout.LayoutParams)im.getLayoutParams();
			margins.setMargins(0, 0, 10, 0);
			im.setLayoutParams(margins);
			
			
			if(i == currentPage) {
				im.setBackgroundResource(R.drawable.pagemarkerwhite);
			}
			else {
				im.setBackgroundResource(R.drawable.pagemarkergrey);
			}
			Log.d("imagedone", Integer.toString(i));
		}
		
		
		if(this.currentPage > totalPages -1)
		{
			this.currentPage = totalPages;
		}
		
	}
	
	public void setComm(Communicator comm) {
		this.comm = comm;
	}
	
	
	
	
	
	public interface Communicator {
		
	}
	
	public void decreaseTotal() {
		this.totalPages -= 1;
	}
	
	

}
