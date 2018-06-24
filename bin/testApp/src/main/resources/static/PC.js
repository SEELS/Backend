var latitude = 37.772, longitude = -122.214, x = 0;
var flightPath, marker;
var done = false ;

function changeMarkerPosition(marker, lat, lng) {
	var latlng = new google.maps.LatLng(lat, lng);
	marker.setPosition(latlng);
}

function getPosition() {

	$.get("http://localhost:8080/getCurrentLocation", function(response) {
		data = response;
		latitude = data.lat;
		longitude = data.lon;
		console.log(latitude + " " + longitude);
	});
}

function updatePath(flightPath , marker) {
	// get existing path
	var path = flightPath.getPath();
	// add new point
	getPosition();
	path.push(new google.maps.LatLng(latitude, longitude));
	// Update Path
	flightPath.setPath(path);

	console.log(latitude + " " + longitude);

	changeMarkerPosition(marker, latitude, longitude);
}

function initMap() {
	var map = new google.maps.Map(document.getElementById('map'), {
		zoom : 3,
		center : {
			lat : 0,
			lng : -180
		},
		mapTypeId : 'terrain'
	});

	// Init path from source
	var flightPlanCoordinates = [];
	var i = 0;
	flightPath = new google.maps.Polyline({
		path : flightPlanCoordinates,
		geodesic : true,
		strokeColor : '#FF0000',
		strokeOpacity : 1.0,
		strokeWeight : 2
	});
	flightPlanCoordinates.push({
		lat : latitude,
		lng : longitude
	});
	marker = new google.maps.Marker({
		position : {
			lat : latitude,
			lng : longitude
		},
		map : map,
		title : 'Hello World!'
	});
	flightPath.setMap(map);
	
	done = true;
	alert(done);
	
	if (done)
		var myVar = setInterval(function(){updatePath(flightPath , marker)}, 1000);


}

