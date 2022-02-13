console.log('Inside imagedrops.js');

const dropzone = document.getElementById('dropzone');
const dropzoneInput = document.getElementById('dropzone_input');
const dropzoneSelect = document.getElementById('dropzone_select');

dropzone.addEventListener('click', () => {
    dropzoneInput.click();
});

dropzoneInput.addEventListener('change', (e) => {
    if (dropzone.querySelector('#dropimage_figure')) {
        let dropImageFigure = document.getElementById('dropimage_figure');
        dropzone.removeChild(dropImageFigure);
    }
    handleFileSelection(e);
});

/**
 * Handles file upload event
 * @param e dropzone input change event
 */
function handleFileSelection(e) {
    let file = e.target.files[0];
    let fileReader = new FileReader();

    fileReader.readAsDataURL(file);
    fileReader.onloadend = function () {
        createDropzoneFigureImage(fileReader, file);
    }
}

function createDropzoneFigureImage(fileReader, file) {
    let figure = document.createElement('figure');
    figure.id = 'dropimage_figure';
    let img = document.createElement('img');
    img.id = 'dropzone_image';
    img.src = fileReader.result;

    let caption = document.createElement('figcaption');
    caption.innerText = file.name;

    figure.appendChild(img);
    figure.appendChild(caption);

    // remove upload text element if it exits and add image
    if (dropzone.querySelector('#dropzone_select') != null) {
        dropzone.removeChild(dropzoneSelect);
    }
    dropzone.appendChild(figure);
}