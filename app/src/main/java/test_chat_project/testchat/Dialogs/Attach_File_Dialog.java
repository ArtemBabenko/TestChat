package test_chat_project.testchat.Dialogs;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import test_chat_project.testchat.Chat_Room;
import test_chat_project.testchat.R;

import static android.app.Activity.RESULT_OK;
import static test_chat_project.testchat.Chat_Room.btn_add_image;
import static test_chat_project.testchat.Chat_Room.btn_send_msg;
import static test_chat_project.testchat.Chat_Room.imageURI;
import static test_chat_project.testchat.Chat_Room.input_msg;
import static test_chat_project.testchat.Chat_Room.mIdImage;

public class Attach_File_Dialog extends DialogFragment implements View.OnClickListener {

    final String LOG_TAG = "myLogs";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri filepath;
    private StorageReference storageRefrence;
    private String pictureImagePath = "";
    private ImageView imgImage;
    private ImageView imgCamera;
    private ImageView imgFile;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageRefrence = FirebaseStorage.getInstance().getReference();
        setStyle(STYLE_NO_TITLE, 0);
        setCancelable(false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attach_file_dialog, null);
        v.findViewById(R.id.attach_cancel).setOnClickListener(this);
        imgImage = (ImageView) v.findViewById(R.id.attach_image_gallery);
        imgImage.setOnClickListener(this);
        imgCamera = (ImageView) v.findViewById(R.id.attach_image_camera);
        imgCamera.setOnClickListener(this);
        imgFile = (ImageView) v.findViewById(R.id.attach_image_file);
        imgFile.setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.attach_image_gallery) {
            callGalary();
        } else if (v.getId() == R.id.attach_image_camera) {
            Toast.makeText(getActivity(), "In developing", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.attach_image_file) {
            Toast.makeText(getActivity(), "In developing", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.attach_cancel){
            dismiss();
        }
    }

    public void callGalary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);
    }

    private void callCamera() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        Log.d("LOGGED", "imageFileName :  "+ imageFileName);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;

        File file = new File(pictureImagePath);

        Uri outputFileUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        cameraIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Log.d("LOGGED", "pictureImagePath :  "+ pictureImagePath);
        Log.d("LOGGED", "outputFileUri :  "+ outputFileUri);

        startActivityForResult(Intent.createChooser(cameraIntent, "Select an Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            if (filepath != null) {

                final ProgressDialog progresDialog = new ProgressDialog(getActivity());
                progresDialog.setTitle("Uploading");
                progresDialog.show();

                StorageReference riversRef = storageRefrence.child("Images").child(filepath.getLastPathSegment());

                riversRef.putFile(filepath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progresDialog.dismiss();
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                imageURI = String.valueOf(downloadUri);
                                mIdImage = "3";
                                btn_add_image.setImageResource(R.mipmap.ic_clippy_full);
                                input_msg.setHint("Caption");
//                                Picasso.with(getApplicationContext()).load(downloadUri).fit().centerCrop().into(testImg);
                                Toast.makeText(getActivity(), "File Uploaded", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progresDialog.dismiss();
                                Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progresDialog.setMessage((int) progress + "% Uploaded...");
                        if(progress == 100){
                           dismiss();
                        }
                    }
                });
            } else {
                //Error Toast
            }
        }

    }


    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 1: onCancel");
    }

}
