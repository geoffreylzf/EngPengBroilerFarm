package my.com.engpeng.engpeng.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import my.com.engpeng.engpeng.R;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Admin on 20/1/2018.
 */

public class UIUtils {

    public static Dialog getProgressDialog(Context context){
        Dialog progressDialog = new Dialog(context, android.R.style.Theme_Black);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.colorTranslucent);
        progressDialog.setContentView(view);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static AlertDialog getMessageDialog(Context context, String title, String msg){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return  alertDialog;
    }

    public static void showToastMessage(Context context, String message) {
        Toast toast = new Toast(context);
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void vibrate(Context activity) {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(500);
        }
    }
}
