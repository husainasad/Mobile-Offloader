## Mobile-Offloader
Mobile Offloader is a basic android application integrated with four flask servers working as edge computing devices.<br>
The user can use the application to click images of handwritten digits. <br>
Before uploading, the application splits the image into four parts and uploads each part to a different server.<br>
The servers receive the part-image array in a post request.<br>
The image array is then converted back into the original image.<br>
The servers use CNN model to classify the part-image and return the classification result.<br>
The android application classifies the original image based on the results of servers on each part-image, and saves the image in the classified folder.<br>
