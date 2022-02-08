import { Dropzone } from "dropzone";

console.log('inside dropzone js');

let myDropZone = new Dropzone("#dropzone");
myDropZone.on("addedfile", file => {
    console.log(`file added ${file.name}`);
})

console.log("inside imagedrops.js");