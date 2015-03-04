package itebook.serge.com.itebook;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Reader extends Activity implements OnItemClickListener {

	// Declare list view to be used to display data
    private ListView myListView;
    // Progress Dialog
    private ProgressDialog pDialog;
    // Progress dialog type (0 - for Horizontal progress bar)
    private static final int progress_bar_type = 0; 
    // REST web service to access
    //private static String uri = "http://it-ebooks-api.info/v1/search/php%20mysql";
    // Declare adapter
    private BookAdapter bookadapter;
    
    
    //Search functionality
    private EditText txtSearch;
    private Book book;
    private BookResults results;
    private ArrayList<Book> booklist = new ArrayList<Book>();
    
    //Pagination
    private int noOfBtns;
    private Button[] btns;
    private int firstload = 0;
    private LinearLayout ll;
    private LinearLayout.LayoutParams lp;
    private int resultsFound = 0;
    private String error;
    private String value;
    private String _page;
    
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reader);

		if(isInternetOn()){
			myListView = (ListView)findViewById(R.id.book_list);	
			myListView.setOnItemClickListener(this);
			txtSearch = (EditText) findViewById(R.id.txtSearch);	

			txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			    
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					// TODO Auto-generated method stub
 
					InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
					
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						StartSearch();
			            return true;
			        }
					
					return false;
				}
			});

			CheckBtnBackGround(0);
		}
	}
	
	 @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

    //Start searching
	 private void StartSearch(){
			if(isInternetOn()){
				 value = txtSearch.getText().toString();
				 
				 if(!value.isEmpty()){
					//Clear all first items
		 	        //then remove any existing views from previous searches...
					ClearAll();
					 
					String urlEncoded = Uri.encode("http://it-ebooks-api.info/v1/search/"+value.replace("#", "%23"), ALLOWED_URI_CHARS); 
					new MakeHttpRequest().execute(urlEncoded); 
					    
					    //then display the results
					}
					else{
						ClearAll();
						 
						Toast.makeText(Reader.this,
		     				"eBook not found!"
		     				, Toast.LENGTH_LONG).show();
					}
				}
	 }

    //Clear all the books
	 private void ClearAll(){
		    ll = (LinearLayout)findViewById(R.id.btnLay);        
	        ll.removeAllViews();
			booklist.clear();
			firstload = 1;
	 }

    //Displays buttons
	 private void Btnfooter()
	    {
	        int val = Integer.parseInt(book.getResults().getTotal()) % booklist.size();

	        val = (val == 0 ? 0 : 1);

	        noOfBtns = Integer.parseInt(book.getResults().getTotal()) / booklist.size() + val;
	        
	        
	        ll = (LinearLayout)findViewById(R.id.btnLay);
	        
	        //then remove any existing views from previous searches...
	        ll.removeAllViewsInLayout();
	        
	        btns = new Button[noOfBtns];
	         
	        for(int i=0;i<noOfBtns;i++)
	        {
	            btns[i] = new Button(this);
	            btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
	            btns[i].setText(""+(i+1));
	             
	            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            ll.addView(btns[i], lp);
	             
	            final int j = i;
	            btns[j].setOnClickListener(new OnClickListener() {
	                 
	               public void onClick(View v) 
	               {
	                    //loadList(j);
	                	//call the search
	                	//Clear all first items
                     if(isInternetOn()){
	    				booklist.clear();
	    				firstload = 0;
                        _page = String.valueOf(j+1);
	    				String urlEncoded = Uri.encode("http://it-ebooks-api.info/v1/search/"+value.replace("#", "%23")+"/page/"+ String.valueOf(j+1), ALLOWED_URI_CHARS);
	    			    new MakeHttpRequest().execute(urlEncoded); 
	    			    
	                    CheckBtnBackGround(j);
	                  }
	               }
	            });
	        }
	         
	    }
	    /**
	     * Method for Checking Button Backgrounds
	     */
	    @SuppressWarnings("deprecation")
		private void CheckBtnBackGround(int index)
	    {
	        //title.setText("Page "+(index+1)+" of "+noOfBtns);
	    	if(resultsFound > 0){
	        for(int i=0;i<noOfBtns;i++)
	        {
	            if(i==index)
	            {
	                btns[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_box));
	                btns[i].setTextColor(getResources().getColor(android.R.color.white));
	            }
	            else
	            {
	                btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
	                btns[i].setTextColor(getResources().getColor(android.R.color.black));
	            }
	        }
	      }      
	    }
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case progress_bar_type: //Set this to 0
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Searching ebook...");
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
	                
	                JSONObject books = new JSONObject(new String(buffer));  

	                resultsFound = Integer.parseInt(books.getString("Total"));
	                error = books.getString("Error");
	                
	                if(resultsFound > 0)
	                {
	                	JSONArray arr = books.getJSONArray("Books");
	 	               
		                for(int j = 0; j < arr.length(); j++)
		   	            {
		   	          	  JSONObject obj = arr.getJSONObject(j);
		   	          	  book = new Book();
		   	          	  results = new BookResults();
		   	          	  
		   	          	  results.setTotal(books.getString("Total"));
		   	          	  results.setPage(books.getString("Page"));
		   	          	  results.setErrors(books.getString("Error"));                                                                                                                                                                                                                           
		   	          	  results.setTime(books.getString("Time"));
		   	          	  
		   	          	  book.setId(obj.getString("ID"));
		   	          	  book.setTitle(obj.getString("Title"));
		   	              book.setDescription(obj.getString("Description"));
		   	              book.setImage(obj.getString("Image")); 
		   	              book.setIsbn(obj.getString("isbn"));
		   	              book.setResults(results);
		   	              
		   	              booklist.add(book);
		   	              bookadapter = new BookAdapter(booklist);
		   	              
		   	              total += booklist.size();
		   	              publishProgress(""+ ((total*100) / arr.length()));
		   	              Thread.sleep(10);
		   	            }
	                  }   
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
            myListView.setAdapter(bookadapter);      

            if(resultsFound > 0){
                if(firstload == 1){
               	    Btnfooter();
                    CheckBtnBackGround(0);

                    //By default, if there is a result found, set page into 1 first.
                    _page = "1";
               }
                //display searched result.
                //I removed the line of code "book.getResults().getPage()" due to wrong result and unreliable result of Page
                //field from the API, I set instead the page from exact button text so I have an accurate result for page number.

                Toast.makeText(Reader.this,
        				"Page "+ _page +" of "+ noOfBtns
        				, Toast.LENGTH_LONG).show();
            	Log.d("Request query execution time (seconds)", book.getResults().getTime());
            }
            else{
            	Toast.makeText(Reader.this,
        				"eBook not found!"
        				, Toast.LENGTH_LONG).show();
            	//Log the search results if any, such as errors etc.
                Log.d("Error code/description (Note: request success code = 0)", error);    
            }
         
    }
 }
    
    
    
    
    
    @SuppressWarnings("static-access")
	public final boolean isInternetOn() {
        
        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
         
           // Check for network connections
            if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                 connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                 connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                 connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
                
                // if connected with Internet
                return true;
                 
            } else if ( 
              connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
              connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
               
                Toast.makeText(Reader.this, " Not Connected to internet. ", Toast.LENGTH_LONG).show();
                return false;
            }
          return false;
        }
    
    
    
    
    
    
    

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
              if(isInternetOn()){
		            Book selectedFromList = (Book) (bookadapter.getItem(arg2));

		            // custom dialog
					//final Dialog dialog = new Dialog(context);
					//dialog.setContentView(R.layout.custom);
					//dialog.setTitle("Description");
		 
					// set the custom dialog components - text, image and button
					//TextView text = (TextView) dialog.findViewById(R.id.txtDescription);

					//text.setText(selectedFromList.getDescription());
		 
					//Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
					// if button is clicked, close the custom dialog
					//dialogButton.setOnClickListener(new OnClickListener() {
						//@Override
						//public void onClick(View v) {
							//dialog.dismiss();
						//}
					//});
		 
					//dialog.show();
						
						//finish();
						Intent intent = new Intent(this, BookActivity.class);
						
						//Create the bundle
						  Bundle bundle = new Bundle();
						  //Add your data to bundle
						  bundle.putString("bookid", selectedFromList.getId());  
						  //Add the bundle to the intent
						  intent.putExtras(bundle);
						
						super.startActivity(intent);
				
	               }
	      }
}
