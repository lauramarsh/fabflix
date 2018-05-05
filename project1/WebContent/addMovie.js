/**
 * 
 */
function handleAdd(result) {
	
	console.log(result)
	var movieId = result.getAttribute("value");
	
    console.log("click");
    console.log(movieId);
    
    $.ajax({
    	method: "GET", 
    	url: "/project1/addMovie", 
    	data: {"movie-id": $(result).attr("value")}
    });
    
    window.alert("Movie has been added to you cart!");
   
}