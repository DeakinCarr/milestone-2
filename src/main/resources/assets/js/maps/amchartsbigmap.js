
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
// Create the tooltip format
polygonTemplate.tooltipText = "{name}";
//Set the fill colour for countries that dont have data. Doesn't matter as they are removed, but just in case
polygonTemplate.fill = am4core.color("rgb(255, 228, 241)");
polygonTemplate.propertyFields.fill = "color";

// Build the onclick listener used when selecting a country
polygonTemplate.events.on("hit", function (ev) {
    // Pull the info of the country thats been clicked
    var data = ev.target.dataItem.dataContext;

    // Names all the components on the page that need to be changed when the user selects a country
    var name = document.getElementById("country-name");
    var flag = document.getElementById("flag");

    var cases = document.getElementById("total-cases");
    var newCases = document.getElementById("total-cases-last-24-hours");

    var deaths = document.getElementById("total-deaths");
    var deathsPercentage = document.getElementById("total-deaths-percentage");
    var newDeaths = document.getElementById("total-deaths-last-24-hours");

    var recovered = document.getElementById("total-recovered");
    var recoveredPercentage = document.getElementById("total-recovered-percentage");

    // Basic logic behind determining what the text is set to
    if (data.name) {
        name.innerText = data.name + " ";
    } else {
        name.innerText = "NaN"
    }

    if (data.id) {
        // Additionally makes use of the flagstrap library (Not natively associated with the amcharts plugin)
        flag.innerHTML = '<i class="flagstrap-icon flagstrap-' + (data.id).toLowerCase() + '" style="vertical-align:middle;"></i>';
    } else {
        flag.innerText = "Data is unavailable for this country."
    }
    

    if (data.cases) {
        cases.innerText = data.cases;
    } else {
        cases.innerText = ""
    }
    if (data.newCases) {
        newCases.innerText = data.newCases;
    } else {
        newCases.innerText = ""
    }

    if (data.deaths) {
        deaths.innerText = data.deaths;
    } else {
        deaths.innerText = ""
    }
    if (data.deathsPercentage) {
        deathsPercentage.innerText = data.deathsPercentage + "%";
    } else {
        deathsPercentage.innerText = ""
    }
    if (data.newDeaths) {
        newDeaths.innerText = data.newDeaths;
    } else {
        newDeaths.innerText = ""
    }

    
    if (data.population) {
        recovered.innerText = data.population;
    } else {
        recovered.innerText = ""
    }
    if (data.populationpercentage) {
        recoveredPercentage.innerText = data.populationpercentage + "% Infected";
    } else {
        recoveredPercentage.innerText = ""
    }
});

// Create hover state and set alternative fill color
var hs = polygonTemplate.states.create("hover");
hs.properties.fill = am4core.color("#222229");

// Add zoom control
chart.zoomControl = new am4maps.ZoomControl();