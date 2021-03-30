const cloudIcon = '<img src="images/cloud.png" height=50 width=50></img>'
const rainIcon = '<img src="images/rain.png" height=50 width=50></img>'

// Kartlägger tid till väder
var timeToWeather = new Map();

// Kartlägger dagar där det regnar
var rainToDay = new Map();
var table = document.getElementById('eventTable')

/*
* metoden får som inparameter evenemangen som hämtas från Helsingborg stads API
* och gör ett anrop till SMHIs väder API för hämta vädret i skåne
* väder datan kombineras med evenemang datan för att få fram rekommenderade evenemang för varje dag
*/
function displayRecommendedEvents(events) {

  // Asynkront ajax anrop till SMHIs väder API
  $.ajax({
    url: "http://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/14/lat/56/data.json",
    headers: { "Accept": "application/json" }
  })
    .done(function (weatherData) {
      console.log(events);
      console.log(weatherData)
      for (let i = 0; i < weatherData.timeSeries.length; i++) {
        // Lägger tid som nyckel och vädret som värde
        timeToWeather.set(weatherData.timeSeries[i].validTime.substring(0, 10), weatherData.timeSeries[i].parameters[18].values[0])
        if (weatherData.timeSeries[i].parameters[18].values[0] >= 7) {
          // Lägger dag som nyckel och regn som värde
          rainToDay.set(weatherData.timeSeries[i].validTime.substring(0, 10), 'rain');
        }
      }

      for (let i = 0; i < events.length; i++) {
        if (events[i].occasions != null) {
          for (let j = 0; j < events[i].occasions.length; j++) {
            // Kollar om det finns en evenemang ett visst datum
            if (timeToWeather.has(events[i].occasions[j].start_date.substring(0, 10))) {

              // checkdata kollar om det regnar eller inte en viss dag
              var checkdata = rainToDay.get(events[i].occasions[j].start_date.substring(0, 10));

              // Om det inte regnar en viss dag visas utomhusevenemang som äger rum den dagen
              if ((checkdata != 'rain') && (events[i].event_categories != null) && (checkForOutsideCategories(events[i].event_categories) == true)) {
                addTableRow(events[i]['title'].plain_text + " " + events[i]['id'], events[i]['occasions'][j].start_date, "Utomhus", cloudIcon)
                console.log(events[i].occasions[j].start_date + " perfect day for outside event");

              // Om det regnar visas inomhusevenemang den dagen
              } else if ((checkdata == 'rain') && (events[i].event_categories != null) && (checkForInsideCategories(events[i].event_categories) == true)) {
                addTableRow(events[i]['title'].plain_text + " " + events[i]['id'], events[i]['occasions'][j].start_date, "Inomhus", rainIcon)
                console.log(events[i].occasions[j].start_date + " perfect day for inside event");
              }
            }
          }
        }
      }
    })
}


function addTableRow(title, date, place, weather) {
  var row =
    `<tr>
            <td>${title}</td>
            <td>${date}</td>
            <td>${place}</td>
            <td>${weather}</td>
            </tr>`

  table.innerHTML += row
}


$("#btnEvents").click(function () {
  $.ajax({
    url: "https://api.helsingborg.se/event/json/wp/v2/event?per_page=100",
    headers: { "Accept": "application/json" }
  })
    .done(function (data) {
      displayEvents(data)
    })
})

$("#btnRecommended").click(function () {
  $.ajax({
    url: "https://api.helsingborg.se/event/json/wp/v2/event?per_page=100",
    headers: { "Accept": "application/json" }
  })
    .done(function (data) {
      table.innerHTML = ''
      displayRecommendedEvents(data);
    })
})

/*
* Kollar om evenemanget innehåller kategorier som indikerar att evenemanget sker utomhus
*/
function checkForOutsideCategories(category) {
  return category.indexOf("Utomhus") >= 0 || category.indexOf("Natur") >= 0 || category.indexOf("Utflykter") >= 0 || category.indexOf("Aktiviteter") >= 0 &&
    category.indexOf("Digitalt") < 0 && category.indexOf("Spel och e-sport") < 0 && category.indexOf("Spel och teknik") < 0 && category.indexOf("Film") < 0
}
/*
* Kollar om evenemanget innehåller kategorier som indikerar att evenemanget sker inomhus
*/
function checkForInsideCategories(category) {
  return category.indexOf("Inomhus") >= 0 || category.indexOf("Konsert") >= 0 || category.indexOf("Digitalt") >= 0 || category.indexOf("Digitalt skapande") >= 0 ||
    category.indexOf("Spel och e-sport") >= 0 || category.indexOf("Konst") >= 0 || category.indexOf("Film") >= 0
}

/*
* Metoden innehåller en inparameter data som innehåller alla evenemang från Helsingborg stads API
* Datan läggs till i tabellen som innehåller evenemangen.
*/
function displayEvents(data) {
  var table = document.getElementById('eventTable')
  table.innerHTML = ''
  for (var i = 0; i < data.length; i++) {
    if (data[i].occasions != null) {
      for (var j = 0; j < data[i].occasions.length; j++) {
        if (data[i]['location'] != null) {
          addTableRow(data[i]['title'].plain_text, data[i]['occasions'][j].start_date, data[i]['location'].city)
        } else {
          addTableRow(data[i]['title'].plain_text, data[i]['occasions'][j].start_date)
        }
      }
    }
  }
}

function sortTable(n) {
  var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
  table = document.getElementById("eventTable");
  switching = true;
  dir = "asc";
  while (switching) {
    switching = false;
    rows = table.rows;
    for (i = 1; i < (rows.length - 1); i++) {
      shouldSwitch = false;
      x = rows[i].getElementsByTagName("TD")[n];
      y = rows[i + 1].getElementsByTagName("TD")[n];
      if (dir == "asc") {
        if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
          shouldSwitch = true;
          break;
        }
      } else if (dir == "desc") {
        if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
          shouldSwitch = true;
          break;
        }
      }
    }
    if (shouldSwitch) {
      rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
      switching = true;
      switchcount++;
    } else {
      if (switchcount == 0 && dir == "asc") {
        dir = "desc";
        switching = true;
      }
    }
  }
}

