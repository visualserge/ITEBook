package itebook.serge.com.itebook;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;

public class BookActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;
    // Progress dialog type (0 - for Horizontal progress bar)
    private static final int progress_bar_type = 0; 
    // REST web service to access
    //private static String uri = "http://it-ebooks-api.info/v1/search/php%20mysql";
    // Declare adapter

    private Book book;
    private String id;
    
    
    //Contents
    private ImageView bookImage;
    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView bookDescription;
    
    private TextView bookPublisher;
    private TextView bookISBN;
    private TextView bookPages;
    private TextView bookYear;
    
    @SuppressWarnings("unused")
	private Button btnDescription;
	private Button btnDownload;
    private Button btnClose;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);
		
		//Get the bundle
	    Bundle bundle = getIntent().getExtras();
	    //Extract the data�
	    id = bundle.getString("bookid"); 

	    bookImage = (ImageView)findViewById(R.id.imgBookImage);
	    
	    bookTitle = (TextView)findViewById(R.id.txtTitle);
	    bookAuthor = (TextView)findViewById(R.id.txtAuthor);

	    bookPublisher = (TextView)findViewById(R.id.txtPublisher);
	    bookISBN = (TextView)findViewById(R.id.txtISBN);
	    bookPages = (TextView)findViewById(R.id.txtPages);
	    bookYear = (TextView)findViewById(R.id.txtYear);
	    bookDescription = (TextView)findViewById(R.id.txtDescription);
	    
	    btnDownload = (Button)findViewById(R.id.btnDownload);
	    btnClose = (Button)findViewById(R.id.btnExit);
	    
	    bookImage.setVisibility(View.INVISIBLE);
	    btnDownload.setVisibility(View.INVISIBLE);
	    btnClose.setVisibility(View.INVISIBLE);

	    bookDescription.setMovementMethod(new ScrollingMovementMethod());
	    btnClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	    
	    btnDownload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Download(book.getDownload());
			}
		});
	    
	    new MakeHttpRequest().execute("http://it-ebooks-api.info/v1/book/"+id); 
	}
	
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case progress_bar_type: //Set this to 0
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading details...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
            return pDialog;
        default:
            return null;
        }
    }
    
    
    private void Download(String url){
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW, 
			     Uri.parse(url));
			startActivity(intent);
    }
	
    protected class MakeHttpRequest extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @SuppressWarnings("deprecation")
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }
        /**
         * Reading file in background thread
         * */
        @Override
    protected String doInBackground(String... uri) {
        	  String _uri = uri[0];
              int total = 0;
              
              
              try 
              {           
	                // Send GET request to <service>/json/<plate>
	                DefaultHttpClient httpClient = new DefaultHttpClient();
	                HttpGet request = new HttpGet(_uri);
	                request.setHeader("Accept", "application/json");
	                request.setHeader("Content-type", "application/json");
	                HttpResponse response = httpClient.execute(request);         
	                HttpEntity responseEntity = response.getEntity();
	         
	                // Read response data into buffer
	                int contentLength = (int)responseEntity.getContentLength();
	                char[] buffer = new char[(int)responseEntity.getContentLength()];
	                InputStream stream = responseEntity.getContent();
	                InputStreamReader reader = new InputStreamReader(stream);

	                int hasRead = 0;
	                while (hasRead < contentLength)
	                    hasRead += reader.read(buffer, hasRead, contentLength-hasRead);
	                stream.close();
	         
	                // In order to use this code, the REST web service 
	                // must have a Wrapped BodyStyle.
	                
	                      JSONObject obj = new JSONObject(new String(buffer));  
		   	          	  book = new Book();
		   	          	 
		   	          	  book.setImage(obj.getString("Image"));
		   	          	  book.setTitle(obj.getString("Title"));
		   	          	  book.setPublisher(obj.getString("Publisher"));
		   	          	  book.setIsbn(obj.getString("ISBN"));
		   	          	  book.setAuthor(obj.getString("Author"));
		   	          	  book.setYear(obj.getString("Year"));
		   	          	  book.setPage(obj.getString("Page"));
		   	          	  book.setDescription(obj.getString("Description"));
		   	          	  book.setDownload(obj.getString("Download"));

		   	              total += 1;
		   	              publishProgress(""+ (int) ((total*100) / 1));
		   	              Thread.sleep(1);
		   	            
 
                }
                catch (Exception e) 
	            {
                	Log.e("Error: ", e.getMessage());
	            }   
              
              
           return null;
        }
        /**
         * Updating progress bar
         * */
     protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
     }
 
        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @SuppressWarnings("deprecation")
	protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was read
            dismissDialog(progress_bar_type); 
            
            //display results here
             Picasso.with(getApplicationContext()).load(book.getImage()).placeholder(R.drawable.loading).into(bookImage);
             bookTitle.setText(book.getTitle());
             bookAuthor.setText("Author: "+book.getAuthor());
             bookDescription.setText(book.getDescription());
             
             bookPublisher.setText("Publisher: "+book.getPublisher());
             bookISBN.setText("ISBN: "+book.getIsbn());
             bookYear.setText("Year: "+book.getYear());
             bookPages.setText("Pages: "+book.getPage());

             //Then show the controls
             bookImage.setVisibility(View.VISIBLE);
             btnDownload.setVisibility(View.VISIBLE);
     	     btnClose.setVisibility(View.VISIBLE);
            
    }
 }
}

