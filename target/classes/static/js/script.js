var map ;
var markers=[];
var flightPath=[];
var flightPlanCoordinates = [];		// 2D Array -> array of flightPlanCoordinates for each truck
var longitude = [] ;
var latitude = [];

// Sets the map on all markers in the array.
function setMapOnAll(map) {
   for (var i = 0; i < markers.length; i++) {
      markers[i].setMap(map);
   }
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
	setMapOnAll(null);
}

function addMarker(location , truckId) {
	
	flightPlanCoordinates[truckId] = [];
	flightPlanCoordinates[truckId].push(location);

	flightPath[truckId] = new google.maps.Polyline({
          path: flightPlanCoordinates[truckId],
          geodesic: true,
          strokeColor: '#FF0000',
          strokeOpacity: 1.0,
          strokeWeight: 2
        });
	
	markers[truckId] = new google.maps.Marker({
          position: location,
		  map: map
    });	
    flightPath[truckId].setMap(map);
	markers[truckId].setPosition(location);

}


function changeMarkerPosition(marker, lat, lng) {
	var latlng = new google.maps.LatLng(lat, lng);
	marker.setPosition(latlng);
}

function initMap() {
	
	markers=[];
	flightPath=[];
	flightPlanCoordinates = [];		// 2D Array -> array of flightPlanCoordinates for each truck
	longitude = [] ;
	latitude = [];
	
	var mapOptions = {
		center: new google.maps.LatLng(30.06263, 31.24967),
		zoom: 3,
		mapTypeId : 'terrain'
	}
	map = new google.maps.Map(document.getElementById("map"), mapOptions);
}


function getPosition(truckId) {

	$.get("http://localhost:8080/viewCurrentLocation/"+truckId, function(response) {
		data = response;
		latitude[truckId] = data.lat;
		longitude[truckId] = data.lon;
	});
}

function updatePath(truckId) {
	
	// get existing path
	var path = flightPath[truckId].getPath();
	
	// add new point
	getPosition(truckId);
	path.push(new google.maps.LatLng(latitude[truckId] , longitude[truckId]));
	
	// Update Path	
	flightPath[truckId].setPath(path);

	changeMarkerPosition(markers[truckId], latitude[truckId], longitude[truckId]);
}

function showActiveTrucks(trucks)
{
	initMap();
 	for( i=0; i<trucks.length ; i++)
	{
		addMarker(new google.maps.LatLng(trucks[i].latitude , trucks[i].longitude) ,trucks[i].id);
		longitude[trucks[i].id] = trucks[i].longitude;
		latitude[trucks[i].id] = trucks[i].latitude;
	}
		
	for (i=0 ; i<trucks.length; i++)
	{
		var x = trucks[i].id ;
		var myVar = setInterval(function(){updatePath(x, flightPath[x])}, 1000);
	}

}

function showSpecificTruck(truckId)
{
	clearMarkers();
	markers[truckId].setMap(map);
}
