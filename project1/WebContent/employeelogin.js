/**
 * Handle the data returned by EmployeeLoginServlet
 * @param resultDataString jsonObject
 */
function handleEmployeeLoginResult(resultDataString) {
	console.log(resultDataString);
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
        window.location.replace("employeeIndex.html");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitEmployeeLoginForm(formSubmitEvent) {
    console.log("submit login form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/employeelogin",
        // Serialize the login form to the data sent by POST request
        jQuery("#employee_login_form").serialize(),
        (resultDataString) => handleEmployeeLoginResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#employee_login_form").submit((event) => submitEmployeeLoginForm(event));