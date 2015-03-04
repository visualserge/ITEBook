package itebook.serge.com.itebook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends BaseAdapter {

	private ArrayList<Book> books = new ArrayList<Book>();
	
	public BookAdapter(ArrayList<Book> books){
		this.books = books;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.books.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.books.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return this.books.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.books, parent, false);
		}
		
		Book book = this.books.get(position);

		ImageView image = (ImageView)convertView.findViewById(R.id.icon);
	    Picasso.with(parent.getContext()).load(book.getImage()).placeholder(R.drawable.loading).into(image);
	    
		TextView titleView = (TextView)convertView.findViewById(R.id.title_view);
		titleView.setText(book.getTitle());
		
	    TextView isbnView = (TextView)convertView.findViewById(R.id.isbn_view);
	    isbnView.setText("ISBN: "+ book.getIsbn());

		return convertView;
	}
}
