var container = document.getElementById("eventlist");
var button = document.getElementById('btn');

    button.addEventListener("click", function () {
        $(document).ready(function() {
            $.ajax({
                url:"https://api.helsingborg.se/event/json/wp/v2/event",
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
            var row = `<tr>
            <td>${data[i]['title'].plain_text}</td>
            <td>${data[i]['occasions'][0].start_date}</td>
            <td>${data[i]['id']}</td>
            </tr>`
            table.innerHTML += row
        }
    }

