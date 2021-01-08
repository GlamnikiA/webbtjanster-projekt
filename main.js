var container = document.getElementById("eventlist");
var button = document.getElementById('button');
var dates = [];

    function fetchAndUpdateInfo(details) {
            $.ajax({
                url:"http://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/14/lat/56/data.json",
                headers: {"Accept": "application/json"}
            })
            .done(function(data) {
              var table = document.getElementById('myTable')
              table.innerHTML = ''
              var timeweather = new Map();
              for(let i = 0; i < data.timeSeries.length; i++) {
                var da = new Date()
                timeweather.set(data.timeSeries[i].validTime.substring(0, 10), data.timeSeries[i].parameters[18].values[0])
                console.log(data.timeSeries[i].validTime.substring(0, 10));
              }
              console.log(timeweather.get(data.timeSeries[12].validTime.substring(0,10)));
              console.log(data);
              
              for(let i = 0; i < details.length; i++) {
                if(details[i].occasions != null) {
                  for(let j = 0; j < details[i].occasions.length; j++) {
                    if(timeweather.has(details[i].occasions[j].start_date.substring(0, 10))) {
                      var weather = timeweather.get(details[i].occasions[j].start_date.substring(0, 10));
                      if(weather > 7) {
                        console.log(details[i].occasions[j].start_date + " rain");
                      } else {
                        console.log(details[i].occasions[j].start_date + " not rain");
                      }
                    }
                  }
                }
              }
            })
    }

    $("#button2").click(function() {
        $.get("https://api.helsingborg.se/event/json/wp/v2/event?per_page=100", function(data) {

            fetchAndUpdateInfo(data);
        })
    })

      $(document).ready(function() {
          $("#button").click(function() {
               $.get("https://api.helsingborg.se/event/json/wp/v2/event?per_page=100", function(data) {
                myFunction(data);
                var events = $('#eventlist');
               for (i = 0; i < data.length; i++) {
                   html = '<li id="event_' + i + '">' + '<a>' + data[i]['title'].plain_text + '</a>' + '</li>';
                   events.append(html);
             }
              console.log(data);
              button.style.visibility = "hidden"
              }) 
          })
       });

    function myFunction(data) {
        var table = document.getElementById('myTable')
        table.innerHTML = ''
        var image='<img src="images/cloud.png" height=50 width=50></img>'
        for(var i = 0; i < data.length; i++) {
            if(data[i].occasions != null) {
                 for(var j = 0; j < data[i].occasions.length; j++) {
                   var d = new Date(data[i]['occasions'][j].start_date);
                   console.log(d);
                   dates[i] = d.getFullYear() + "/" + (d.getMonth() + 1) + "/" + d.getDate();
                   dates[j] = d.getFullYear() + "/" + (d.getMonth() + 1) + "/" + d.getDate();
                   console.log(dates[i]);
                    var row = `<tr>
                    <td>${data[i]['title'].plain_text}</td>
                    <td>${data[i]['occasions'][j].start_date}</td>
                    <td>${data[i]['id']}</td>
                    <td>${image}</td>
                    </tr>`
                    table.innerHTML += row
                } 
            }
        }
        console.log(dates);
    }
    function sortTable(n) {
        var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
        table = document.getElementById("myTable");
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
            switchcount ++;
          } else {
            if (switchcount == 0 && dir == "asc") {
              dir = "desc";
              switching = true;
            }
          }
        }
      }

