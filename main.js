var container = document.getElementById("eventlist");
var button = document.getElementById('btn');
var pageCounter = 0;

    button.addEventListener("click", function () {
        $(document).ready(function() {
            $.ajax({
                url:"https://api.helsingborg.se/event/json/wp/v2/event",
                headers: {"Accept": "application/json"}
            })
            .done(function(data){
                list = $('events');
                for (i = 0; i < data.length; i++) {
                    html = '';
                    html = '<li id="event_' + i + '">' + data[i]['name'] + '</li>';
                    list.append(html);
                    renderHTML(data[i]['title'].plain_text + data[i]['date'] + "<br>")
                }
                console.log(data);
                button.style.visibility = "hidden"
            })
        });
    });

function renderHTML(data) {
    container.insertAdjacentHTML('beforeend', data);
}
