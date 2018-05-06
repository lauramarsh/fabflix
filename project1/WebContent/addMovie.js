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
	var movies = document.getElementsByClassName("movieId");
	var quants = document.getElementsByClassName("quantInput");
	var index = result.getAttribute("name");
	var curMovie = movies[index].value;
	var newQuant = quants[index].value;
	
    console.log("click");
    console.log(curMovie);
    console.log(newQuant);
    
    if (parseInt(newQuant) > 0) {
    	$.ajax({
        	method: "GET", 
        	url: "/project1/editMovie", 
        	data: {"movie-id": curMovie, "movie-quant": newQuant}
        });
        
        window.location.reload(true);
    } 
    else {
    	console.log
    	window.alert("Please enter a valid number (more than 0)!");
    	window.location.reload(false);
    }
    
    
}

function handleDelete(result) {
	console.log(result)
	var movies = document.getElementsByClassName("movieId");	
	var index = result.getAttribute("name");
	
	console.log("click");
    console.log(movies[index].value);
    
    
    $.ajax({
    	method: "GET", 
    	url: "/project1/deleteMovie", 
    	data: {"movie-id": movies[index].value}
    });
    
    window.location.reload(true);
}