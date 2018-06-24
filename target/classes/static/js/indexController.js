var app = angular.module("trucksController",[]);

app.controller("trucksController",function($scope,$http , $q  ){

		var done = false; 
		var truckArr = [];
		var location ;
		
		function addToArr(truck , len)
		{
			truckArr.push(truck);
			
			if (truckArr.length === len)
			{
				showActiveTrucks(truckArr);
			}
			
		}
		
		function getLocation(t , callback , len)
		{
		   	return $http.get('http://localhost:8080/viewCurrentLocation/' + t.id).then(function(response) {
				t.longitude = response.data.lon;
				t.latitude = response.data.lat;
				callback(t , len);
				return response.data;
			});
		}
		
		$scope.getActiveTrucks = function()
		{
			$http.get("http://localhost:8080/getActiveTrucks")
			
			.then(function(response) {
				$scope.trucks = response.data;

				for (var i = 0 ; i<$scope.trucks.length ; i++)
				{
					location = getLocation($scope.trucks[i] , addToArr ,$scope.trucks.length );
				}
			});
		}
		
		$scope.getTruckData = function(truckId)
		{
			$http.get("http://localhost:8080/viewTruck/"+truckId)
			
			.then(function(response) {
			
			$scope.selectedTruck = response.data;
			showSpecificTruck($scope.selectedTruck.id);
			});
		}
	
	
});
		