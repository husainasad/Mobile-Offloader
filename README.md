## Mobile-Offloader
Mobile Offloader is a basic android application integrated with four flask servers working as edge computing devices.<br>
The user can use the application to click images of handwritten digits. <br>
Before uploading, the application splits the image into four parts and uploads each part to a different server.<br>
The servers receive the part-image array in a post request.<br>
The image array is then converted back into the original image.<br>
The servers use CNN model to classify the part-image and return the classification result.<br>
The android application classifies the original image based on the results of servers on each part-image, and saves the image in the classified folder.<br>
There are four classifier model weights (one for each image quadrant). <br>
The model are trained on MNIST dataset and have ~80-85% accuracy on test data.<br>
The accuracy for quadrant-trained models is lower compared to complete image trained model (99.2%) is probably due to loss of useful features.<br>
You can load the model weights 'best1.hdf5', 'best2.hdf5', 'best3.hdf5' and 'best4.hdf5' for quadrants 1, 2, 3, and 4 respectively.<br>
You can also train the model weights using 'model.py'. <br>

### Instructions to setup
Use commands 'python server1.py',  'python server2.py', 'python server3.py', and 'python server4.py' in terminal to run the flask servers<br>
Copy the IP addresses from terminal<br>
Open 'MobileOffloader' project in android studio<br>
Replace the BASE_URL_Server_1, BASE_URL_Server_2, BASE_URL_Server_3, and BASE_URL_Server_4 string value in the file APIContract.java with the IP address from flask servers. <br>
Run the android application<br>

### Important note regarding model training and loading
Library versions used during model training should be same as the ones used during model loading, otherwise the server-side code may not work as intended. <br>
To solve this issue, re-train the model in the system where server-side code will be executed. <br>

### Important note regarding application performance
Tasks such as image splitting, encoding and sending synchronous POST requests are heavy operation tasks which reduce the application speed and may cause unexpected behaviors <br>
To mitigate this problem, all these tasks are assigned to Executor service instead of main UI thread of the application.<br>
Similarly, other resource-heavy, slow or other tasks that can be performed in the background can and should be moved to background threads via Executor service.<br>
