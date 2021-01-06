var container = document.getElementById("eventlist");
var button = document.getElementById('btn');

    button.addEventListener("click", function () {
        $(document).ready(function() {
            $.ajax({
                url:"https://api.helsingborg.se/event/json/wp/v2/event?per_page=50",
                headers: {"Accept": "application/json"}
            })
            .done(function(data){
                myFunction(data);
                var events = $('#eventlist');
                for (i = 0; i < data.length; i++) {
                    html = '<li id="event_' + i + '">' + '<a>' + data[i]['title'].plain_text + '</a>' + '</li>';
                    events.append(html);
              }
                console.log(data);
                button.style.visibility = "hidden"
            })
        });
    });
    function myFunction(data) {

        var table = document.getElementById('myTable')
        for(var i = 0; i < data.length; i++) {
            if(data[i].occasions != null) {
                for(var j = 0; j < data[i].occasions.length; j++) {
                    var row = `<tr>
                    <td>${data[i]['title'].plain_text}</td>
                    <td>${data[i]['occasions'][j].start_date}</td>
                    <td>${data[i]['id']}</td>
                    </tr>`
                    table.innerHTML += row
                }
            }
        }
    }

