function SendForm() {
    var name = $("#name").val();
    var descr = $("#description").val();
    var startTime = $("#startDate").val();
    var endTime = $("#endDate").val();
    var location = $("#standort").val();
    var bandId = $("#bandname").val();
    
    var reqWrapper = new Object();
    reqWrapper.band = new Object();
    reqWrapper.band.id = bandId;

    reqWrapper.appearanceRequest = new Object();
    reqWrapper.appearanceRequest.name = name;
    reqWrapper.appearanceRequest.accpeted = 0;
    reqWrapper.appearanceRequest.description = descr;
    reqWrapper.appearanceRequest.startTime = startTime + ":00";
    reqWrapper.appearanceRequest.endTime = endTime + ":00";
    reqWrapper.appearanceRequest.location = new Object();
    reqWrapper.appearanceRequest.location.id = location;

    var query = "http://localhost:8081/BandManagement_Webserver/rest/bands/appearanceRequests";
    var request = $.ajax({
        method: "POST",
        type: "POST",
        url: query,
        data: JSON.stringify(reqWrapper),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            alert("Anfrage abgeschickt!");
        }
    });
}




jQuery(window).load(function () {
    var query = "http://localhost:8081/BandManagement_Webserver/rest/locations";
    var request = $.ajax({
        method: "GET",
        type: "GET",
        url: query,
        dataType: "json"
    }).done(function (data) {
        data.forEach(function (s) {
            addLocation(s);
        });
    });

    var query = "http://localhost:8081/BandManagement_Webserver/rest/bands";
    var request = $.ajax({
        method: "GET",
        type: "GET",
        url: query,
        dataType: "json"
    }).done(function (data) {
        data.forEach(function (s) {
            addBand(s);
        });
    });
  }
);

function addLocation(loc) {
    $("#standort").append("<option value='" + loc.id + "'>" + loc.name + "</option>");
}

function addBand(b) {
    $("#bandname").append("<option value='" + b.id + "'>" + b.name + "</option>");
}