package com.example.mobileoffloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//  References
//  https://www.android.com/
//  https://developer.android.com/guide
//  https://developer.android.com/training/camera/photobasics
//  https://developer.android.com/develop/ui/views/components/spinner
//  https://www.tutlane.com/tutorial/android/android-spinner-dropdown-list-with-examples
public class UploadActivity extends AppCompatActivity {
    private String BASE_URL_SERVER_1 = APIContract.BASE_URL_SERVER_1;
    private String BASE_URL_SERVER_2 = APIContract.BASE_URL_SERVER_2;
    private String BASE_URL_SERVER_3 = APIContract.BASE_URL_SERVER_3;
    private String BASE_URL_SERVER_4 = APIContract.BASE_URL_SERVER_4;

    private Button upload;

    private String imageNameValue = MainActivity.getImageName();
//    private String imageBase64 = MainActivity.getImageBase();
    private Bitmap imageBitmap = MainActivity.getImageBitmap();
//    private byte[] imageByteArray = MainActivity.getImageByteArray();

    ArrayList<String> predictedList = new ArrayList<String>();
    private List<Integer> countArray = new ArrayList<Integer>(Collections.nCopies(10, 0));

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_page);
        upload = findViewById(R.id.uploadBtn);
        textView  = findViewById(R.id.resultView);
        upload.setOnClickListener(view -> {
            textView.setText("Identifying Digit" );


            Bitmap[] bitmapImages = splitBitmap(imageBitmap);

            ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
            bitmapImages[0].compress(Bitmap.CompressFormat.PNG, 25, byteArrayOutputStream1);
            byte[] byteArray1 = byteArrayOutputStream1.toByteArray();
            String encoded1 = Base64.encodeToString(byteArray1, Base64.DEFAULT);


            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            bitmapImages[1].compress(Bitmap.CompressFormat.PNG, 25, byteArrayOutputStream2);
            byte[] byteArray2 = byteArrayOutputStream2.toByteArray();
            String encoded2 = Base64.encodeToString(byteArray2, Base64.DEFAULT);


            ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
            bitmapImages[2].compress(Bitmap.CompressFormat.PNG, 25, byteArrayOutputStream3);
            byte[] byteArray3 = byteArrayOutputStream3.toByteArray();
            String encoded3 = Base64.encodeToString(byteArray3, Base64.DEFAULT);


            ByteArrayOutputStream byteArrayOutputStream4 = new ByteArrayOutputStream();
            bitmapImages[3].compress(Bitmap.CompressFormat.PNG, 25, byteArrayOutputStream4);
            byte[] byteArray4 = byteArrayOutputStream4.toByteArray();
            String encoded4 = Base64.encodeToString(byteArray4, Base64.DEFAULT);

            predictedList.clear();
            sendRequest(BASE_URL_SERVER_1, encoded1);
            sendRequest(BASE_URL_SERVER_2, encoded2);
            sendRequest(BASE_URL_SERVER_3, encoded3);
            sendRequest(BASE_URL_SERVER_4, encoded4);
        });
    }

    public Bitmap[] splitBitmap(Bitmap src) {
        Bitmap[] divided = new Bitmap[4];
        divided[0] = Bitmap.createBitmap(
                src,
                0, src.getHeight() / 2,
                src.getWidth() / 2, src.getHeight() / 2
        );
        divided[1] = Bitmap.createBitmap(
                src,
                0, 0,
                src.getWidth() / 2, src.getHeight() / 2
        );
        divided[2] = Bitmap.createBitmap(
                src,
                src.getWidth() / 2, src.getHeight() / 2,
                src.getWidth() / 2, src.getHeight() / 2
        );
        divided[3] = Bitmap.createBitmap(
                src,
                src.getWidth() / 2, 0,
                src.getWidth() / 2, src.getHeight() / 2
        );
        return divided;
    }

    private void sendRequest(String serverUrl, String imageBase64) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formbody = new FormBody.Builder().add("imageName",imageNameValue).add("imageBase",imageBase64).build();
        Request request = new Request.Builder().url(serverUrl).post(formbody).build();
//        Response response = client.newCall(request).execute();
//        String responsString = response.body().string();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body().string();
                System.out.println(resStr);
//                String result = "";
//                try {
//                    JSONObject jsonResponse = new JSONObject(resStr);
//                    result = jsonResponse.getString("result");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                String finalMessage = result;
                String finalMessage = resStr;

                predictedList.add(finalMessage);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        Toast.makeText(UploadActivity.this, finalMessage, Toast.LENGTH_SHORT).show();
                        if(predictedList.size() == 4){
//                            System.out.println("predicted list" + predictedList);
                            predictedList.removeAll(Arrays.asList("", null));
//                            Collections.sort(predictedList);

                            for(int i = 0;i<predictedList.size();i++){
                                int temp = Integer.parseInt(predictedList.get(i));
                                countArray.set(temp, countArray.get(temp) + 1);
                            }

//                            System.out.println("counts" + countArray);

                            int predictedResult = 0;
                            int maxCount = 0;
                            for(int i = 0; i<countArray.size();i++){
                                if(countArray.get(i)>maxCount){
                                    predictedResult = i;
                                    maxCount = countArray.get(i);
                                }
                            }

                            textView.setText("Result: " + predictedResult);
                            createDirectoryAndSaveFile(imageNameValue, imageBitmap, Integer.toString(predictedResult));
                        }

//                        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                    }
                });

            }
        });

    }

    private void createDirectoryAndSaveFile(String fileName, Bitmap imageToSave, String modelResult) {

        File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imagesFolder = new File(pictureFolder, "DigitClassifier");
//        File direct = new File(dir);
        if (!imagesFolder.exists()) {
//            File imageDirectory = new File("/"+modelResult);
            imagesFolder.mkdirs();
        }
        File resultFolder = new File(imagesFolder, modelResult);
        if(!resultFolder.exists()){
            resultFolder.mkdirs();
        }
        File file = new File(resultFolder, fileName);
//        if (file.exists()) {
//            file.delete();
//        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.PNG, 50, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}