const cloudIcon = '<img src="images/cloud.png" height=50 width=50></img>'
const rainIcon = '<img src="images/rain.png" height=50 width=50></img>'
var table = document.getElementById('eventTable')


function addTableRow(title, date, city, address, weather) {
  var row =
    `<tr>
            <td>${title}</td>
            <td>${date}</td>
            <td>${city}</td>
            <td>${address}</td>
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
      console.log(data)
      displayEvents(data)
        })

        $.ajax({
          url: "http://localhost:4999/api/v1/events/1873361",
          headers: { "Accept": "application/json" }
        })
          .done(function (data) {
            console.log(data)
              })
})


// Makes an API request to our server to fetch recommended events then puts it in the table.
$("#btnRecommended").click(function () {
  $.ajax({
    url: "http://localhost:4999/api/v1/events",
    headers: { "Accept": "application/json" }
  })
    .done(function (data) {
      table.innerHTML = ''
      document.getElementById('väder').innerHTML = 'väder';
      console.log(data);
      for(let i = 0; i < data.length; i++) {
        var weather = data[i]['weather'];
        if(weather == 'bad') {
          addTableRow(data[i]['title'], data[i]['date'], "Inomhus", data[i]['location'], rainIcon)
        } else {
          addTableRow(data[i]['title'], data[i]['date'], "Utomhus", "Undefined", cloudIcon)
        }
      }
    })
})



// Displays events based on the data fetched from the APIs, by inserting it in the table.
function displayEvents(data) {
  var table = document.getElementById('eventTable')
  table.innerHTML = ''
  document.getElementById('väder').innerHTML = 'id';
  for (var i = 0; i < data.length; i++) {
    if (data[i].occasions != null) {
      for (var j = 0; j < data[i].occasions.length; j++) {
        if (data[i]['location'] != null) {
          addTableRow(data[i]['title'].plain_text, data[i]['occasions'][j].start_date, data[i]['location'].city,  data[i]['location'].formatted_address, data[i]['id'])
        } else {
          addTableRow(data[i]['title'].plain_text, data[i]['occasions'][j].start_date, "Ej specifierad", "Ej specifierad", data[i]['id'])
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

