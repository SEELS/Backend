 // This example creates a 2-pixel-wide red polyline showing the path of William
      // Kingsford Smith's first trans-Pacific flight between Oakland, CA, and
      // Brisbane, Australia.
var app = angular.module('mobile', []);

app.controller('MobileController', function($scope, $http, $location) {

	var config = {
            headers : {
                'Content-Type': 'application/json;charset=utf-8;'
            }
    }

var x = document.getElementById("demo");

$scope.getLocation=function() {
	 if (navigator.geolocation) {
	        navigator.geolocation.watchPosition(showPosition);
	    } else {
	        x.innerHTML = "Geolocation is not supported by this browser.";
	    }
}


function showPosition(position) {
    var latlon = position.coords.latitude + "," + position.coords.longitude;
    x.innerHTML = position.coords.latitude + "," + position.coords.longitude;
    var url = "/saveLocation/"+position.coords.latitude+"/"+position.coords.longitude;

	console.log(url);
	
	$http.get(url,config).then(function (sendLocation) {
    	$scope.boolean = sendLocation.data;
    	 }, function (sendLocation) {
             $scope.boolean = sendLocation.data;
             		 //document.write(sendLocation);

         });
}
// To use this code on your website, get a free API key from Google.
// Read more at: https://www.w3schools.com/graphics/google_maps_basic.asp

function showError(error) {
    switch(error.code) {
        case error.PERMISSION_DENIED:
            x.innerHTML = "User denied the request for Geolocation."
            break;
        case error.POSITION_UNAVAILABLE:
            x.innerHTML = "Location information is unavailable."
            break;
        case error.TIMEOUT:
            x.innerHTML = "The request to get user location timed out."
            break;
        case error.UNKNOWN_ERROR:
            x.innerHTML = "An unknown error occurred."
            break;
    }
}
});