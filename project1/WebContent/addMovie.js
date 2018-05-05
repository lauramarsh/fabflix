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

function handleEdit(result) {
	console.log(result)
	var newQuant = document.getElementById('inputQuant').value;
	var movieId = document.getElementById('inputMovieID').value;
	
    console.log("click");
    console.log(movieId);
    console.log(newQuant);
    
    
    $.ajax({
    	method: "GET", 
    	url: "/project1/editMovie", 
    	data: {"movie-id": movieId, "movie-quant": newQuant}
    });
    
    window.location.reload(true);
}

function handleDelete(result) {
	console.log(result)
	var movieId = document.getElementById('inputMovieID').value;
	
    console.log("click");
    console.log(movieId);
    
    
    $.ajax({
    	method: "GET", 
    	url: "/project1/deleteMovie", 
    	data: {"movie-id": movieId}
    });
    
    window.location.reload(true);
}