/**
 * --------------------------------------------------------
 * This demo was created using amCharts V4 preview release.
 *
 * V4 is the latest installement in amCharts data viz
 * library family, to be released in the first half of
 * 2018.
 *
 * For more information and documentation visit:
 * https://www.amcharts.com/docs/v4/
 * --------------------------------------------------------
 */

// Create map instance
var chart = am4core.create("bigMap", am4maps.MapChart);

// Set map definition
chart.geodata = am4geodata_worldLow;

// Set projection
chart.projection = new am4maps.projections.Miller();

// Create map polygon series
var polygonSeries = chart.series.push(new am4maps.MapPolygonSeries());

// Make map load polygon (like country names) data from GeoJSON
polygonSeries.useGeodata = true;

// Add some custom data
polygonSeries.data = []

// Configure series
var polygonTemplate = polygonSeries.mapPolygons.template;
polygonTemplate.tooltipText = "{name}";
polygonTemplate.fill = am4core.color("rgb(255, 228, 241)");
polygonTemplate.propertyFields.fill = "color";
polygonTemplate.events.on("hit", function (ev) {
    var data = ev.target.dataItem.dataContext;

    var info = document.getElementById("info");

    var name = document.getElementById("country-name");
    var flag = document.getElementById("flag");

    var cases = document.getElementById("total-cases");
    var newCases = document.getElementById("total-cases-last-24-hours");

    var deaths = document.getElementById("total-deaths");
    var deathsPercentage = document.getElementById("total-deaths-percentage");
    var newDeaths = document.getElementById("total-deaths-last-24-hours");

    var recovered = document.getElementById("total-recovered");
    var recoveredPercentage = document.getElementById("total-recovered-percentage");

    if (data.name) {
        name.innerText = data.name + " ";
    } else {
        name.innerText = "NaN"
    }

    if (data.id) {
        flag.innerHTML = '<i class="flagstrap-icon flagstrap-' + (data.id).toLowerCase() + '" style="vertical-align:middle;"></i>';
    } else {
        flag.innerText = "NaN"
    }
    

    if (data.cases) {
        cases.innerText = data.cases;
    } else {
        cases.innerText = "NaN"
    }
    if (data.newCases) {
        newCases.innerText = data.newCases;
    } else {
        newCases.innerText = "NaN"
    }

    if (data.deaths) {
        deaths.innerText = data.deaths;
    } else {
        deaths.innerText = "NaN"
    }
    if (data.deathsPercentage) {
        deathsPercentage.innerText = data.deathsPercentage + "%";
    } else {
        deathsPercentage.innerText = "NaN"
    }
    if (data.newDeaths) {
        newDeaths.innerText = data.newDeaths;
    } else {
        newDeaths.innerText = "NaN"
    }

    
    if (data.population) {
        recovered.innerText = data.population;
    } else {
        recovered.innerText = "NaN"
    }
    if (data.populationpercentage) {
        recoveredPercentage.innerText = data.populationpercentage + "% Infected";
    } else {
        recoveredPercentage.innerText = "NaN"
    }
});

// Create hover state and set alternative fill color
var hs = polygonTemplate.states.create("hover");
hs.properties.fill = am4core.color("#222229");

// Remove Antarctica
polygonSeries.exclude = ["AQ"];

// Add zoom control
chart.zoomControl = new am4maps.ZoomControl();