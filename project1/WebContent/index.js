if (typeof cache === 'undefined') {
    var cache = {}
}
function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	console.log("sending AJAX request to backend Java Servlet")
	
	// TODO: if you want to check past query results first, you can do it here
	if (query in cache)
	{
	    console.log("Using cache for autocomplete.")
	    doneCallback( { suggestions: cache[query]} );
	}
	else
	{
		console.log("Using ajax for autocomplete")
		
		// sending the HTTP GET request to the Java Servlet
		// with the query data
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			"url": "autocomplete?query=" + escape(query),
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		})
	}
}

/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	// parse the string into JSON
	var jsonData = JSON.parse(data);
	console.log(jsonData)
	
	// TODO: if you want to cache the result into a global variable you can do it here
	//add query to cache 
	cache[query] = jsonData

	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	
	jQuery.ajax({
	     type: 'GET',
	     success:function() {
	         window.location.href = "/project1/moviepage?movie=" + encodeURIComponent(suggestion["value"]);
	     }
	    
	})
	
	console.log("you selected " + suggestion["value"])
	var url = suggestion["value"]
	console.log(url)
	 
}

/*
 * This statement binds the autocomplete library with the input box element and 
 *   sets necessary parameters of the library.
 * 
 * The library documentation can be find here: 
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 * 
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // set the groupby name in the response json data field
    //groupBy: "category",
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    minChars:3,
    
});


/*
 * do normal full text search if no suggestion is selected 
 */
function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	// TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#autocomplete').val())
	}
})
