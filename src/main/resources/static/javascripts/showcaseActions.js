document.addEventListener('click', e => {
    let target = e.target.id;
    if (target.includes("upvoteButton")) {

        // get the button id number, this will match with the actual ticker id number
        let idNumber = target.substring(target.indexOf('n') + 1);
        let ticker = document.getElementById('uptickCount' + idNumber);
        let count = parseInt(ticker.innerHTML);
        ticker.innerHTML = count + 1;

        // call patch endpoint on ticker object to update count
        putUpdateTickerCount(e, count + 1);
    }

    function putUpdateTickerCount(e, count) {
        let id = e.target.getAttribute('data-id');
        let data = {count: count};

        fetch(`/ogcereal/showcase/${id}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        }).then(response => console.log(response.statusText));
    }
});