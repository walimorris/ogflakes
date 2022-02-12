const UPTICK = 'uptick';
const UPTICK_COUNT = 'uptickCount';

document.addEventListener('click', e => {
    let targetClass = e.target.className;
    if (targetClass !== undefined && targetClass.includes(UPTICK)) {

        // Get the parent uptick button id value from uptick image
        let targetParent = e.target.parentElement;
        let idNumber = targetParent.id.substring(targetParent.id.indexOf('n') + 1);
        let ticker = document.getElementById(UPTICK_COUNT + idNumber);
        let count = parseInt(ticker.innerHTML);
        ticker.innerHTML = count + 1;

        // update ticker count
        putUpdateTickerCount(targetParent, count + 1);
    }

    /**
     * Get the data-id attribute from uptickCounter button element and updates
     * count in repository.
     *
     * @param element uptick button
     * @param count   the new count
     */
    function putUpdateTickerCount(element, count) {
        let id = element.getAttribute('data-id');
        let data = {count: count};

        if (data) {
            fetch(`/ogcereal/showcase/${id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            }).then(response => console.log(response.statusText));
        } else {
            console.log("Important: unable to update ticker-count without object id.");
        }
    }
});