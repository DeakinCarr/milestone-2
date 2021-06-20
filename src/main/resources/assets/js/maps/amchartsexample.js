// Set the default theme for the library components to use
am4core.useTheme(am4themes_animated);

// set the target to modify and the amcharts object that it represents
var chart = am4core.create("chartdiv", am4maps.MapChart);
chart.hiddenState.properties.opacity = 0; // this creates initial fade-in

// Creates basic country layout using the miller map model
chart.geodata = am4geodata_worldLow;
chart.projection = new am4maps.projections.Miller();

// Just creates a heading in the chart
var title = chart.chartContainer.createChild(am4core.Label);
title.text = "Coronavirus Concentration";
title.fontSize = 20;
title.paddingTop = 30;
title.align = "center";
title.zIndex = 100;

// Adds filler data in the event the backend query fails or something
var polygonSeries = chart.series.push(new am4maps.MapPolygonSeries());
// Create a copy of the current layout
var polygonTemplate = polygonSeries.mapPolygons.template;
// Create a tooltip with a unique structure
polygonTemplate.tooltipText = "{name}: {value.value.formatNumber('#.0')}";
// Create a heatmap targeting the copied layout and layer it across the base layout
polygonSeries.heatRules.push({
  property: "fill",
  target: polygonSeries.mapPolygons.template,
  min: am4core.color("#f58a97"),
  max: am4core.color("#f00020")
});
// Aligns the countries with the geographical lat and long values from the backend
polygonSeries.useGeodata = true;

// Create the legend along the bottom of what numbers are represetative of what colours i.e. the colour key
var heatLegend = chart.chartContainer.createChild(am4maps.HeatLegend);
heatLegend.valign = "bottom";
heatLegend.align = "left";
heatLegend.width = am4core.percent(100);
heatLegend.series = polygonSeries;
heatLegend.orientation = "horizontal";
heatLegend.padding(20, 20, 20, 20);
heatLegend.valueAxis.renderer.labels.template.fontSize = 10;
heatLegend.valueAxis.renderer.minGridDistance = 40;

// Create the on-hover event that shades in countries and pops the child event
polygonSeries.mapPolygons.template.events.on("over", event => {
  handleHover(event.target);
});
// Likewise as above but for clicking
polygonSeries.mapPolygons.template.events.on("hit", event => {
  handleHover(event.target);
});

// Aforementioned child event popped from the user hovering
function handleHover(mapPolygon) {
  // If there is actually something being hovered over, show a tooltip for it. otherwise, hide all tooltips
  if (!isNaN(mapPolygon.dataItem.value)) {
    heatLegend.valueAxis.showTooltipAt(mapPolygon.dataItem.value);
  } else {
    heatLegend.valueAxis.hideTooltip();
  }
}

polygonSeries.mapPolygons.template.strokeOpacity = 0.4;

// When the mouse moves off of a country, hide all tooltips that are around. 
// There shouldnt be any, but just in case.
polygonSeries.mapPolygons.template.events.on("out", event => {
  heatLegend.valueAxis.hideTooltip();
});

// Zoom controls blah blah blah
chart.zoomControl = new am4maps.ZoomControl();
chart.zoomControl.valign = "top";