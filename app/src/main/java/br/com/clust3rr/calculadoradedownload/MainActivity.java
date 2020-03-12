package br.com.clust3rr.calculadoradedownload;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CharSequence[] mCharSequenceFileType, mCharSequenceConnectionType;
    private EditText mFileSize, mConnectionSpeed;
    private Button mBtnFile, mBtnConnection;
    private TextView mResult, mTax;
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFileSize = findViewById(R.id.textInputEditFileSize);
        mConnectionSpeed = findViewById(R.id.textInputEditConnectionSpeed);
        mBtnFile = findViewById(R.id.buttonFile);
        mBtnConnection = findViewById(R.id.buttonConection);
        mSeekBar = findViewById(R.id.seekBar);
        mResult = findViewById(R.id.result);
        mTax = findViewById(R.id.tax);

        mCharSequenceFileType = new CharSequence[]{
                getText(R.string.kilobyte).toString(),
                getText(R.string.megabyte).toString(),
                getText(R.string.gigabyte).toString(),
                getText(R.string.terabyte).toString()};

        mCharSequenceConnectionType = new CharSequence[]{
                getText(R.string.kbps).toString(),
                getText(R.string.mbps).toString(),
                getText(R.string.gbps).toString()};

        mFileSize.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String fs = mFileSize.getText().toString();
                String cs = mConnectionSpeed.getText().toString();
                checkFields(fs, cs);
                return false;
            }
        });

        mConnectionSpeed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String fs = mFileSize.getText().toString();
                String cs = mConnectionSpeed.getText().toString();
                checkFields(fs, cs);
                return false;
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String tax = " " + progress + " %";
                mTax.setText(tax);
                String fs = mFileSize.getText().toString();
                String cs = mConnectionSpeed.getText().toString();
                checkFields(fs, cs);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        mTax.setText(" " + mSeekBar.getProgress() + " %");
    }

    private void checkFields(String fs, String cs) {
        if (fs.equals("") || cs.equals("")) {
            mResult.setText("");
        } else {
            result(fs, cs);
        }
    }

    public void buttonFileOnClick(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.fileSize);
        int position = 0;
        for (int i = 0; !(mCharSequenceFileType[i].equals(mBtnFile.getText())); i++)
            position = i + 1;

        dialog.setSingleChoiceItems(mCharSequenceFileType, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBtnFile.setText(mCharSequenceFileType[which]);
                String fs = mFileSize.getText().toString();
                String cs = mConnectionSpeed.getText().toString();
                checkFields(fs, cs);
                dialog.cancel();
            }
        }).create().show();

    }

    public void buttonConnectionOnClick(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.connectionSpeed);
        int position = 0;
        for (int i = 0; !(mCharSequenceConnectionType[i].equals(mBtnConnection.getText())); i++)
            position = i + 1;

        dialog.setSingleChoiceItems(mCharSequenceConnectionType, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBtnConnection.setText(mCharSequenceConnectionType[which]);
                String fs = mFileSize.getText().toString();
                String cs = mConnectionSpeed.getText().toString();
                checkFields(fs, cs);
                dialog.cancel();
            }
        }).create().show();

    }

    private void result(String size, String speed) {
        double fileSize = Double.parseDouble(size);
        double connectionSpeed = Double.parseDouble(speed);
        double fileSizeKbytes = 0;
        double connectionSpeedKbytes = 0;
        double timeInSeconds;
        double milliseconds;
        double tax = (double) mSeekBar.getProgress() / 100 + 1;

        /*         Bytes -> Gbytes
         * Tbytes  1 * 1024 * 1024 * 1024 * 1024
         * Gbytes  1 * 1024 * 1024 * 1024
         * Mbytes  1 * 1024 * 1024
         * Kbytes  1 * 1024
         * Bytes   1
         *
         *         Gbytes -> bytes
         * Gbytes  1
         * Mbytes  1 / 1024
         * Kbytes  1 / 1024 / 1024
         * Bytes   1 / 1024 / 1024 / 1024
         */

        CharSequence fileText = mBtnFile.getText();
        if (mCharSequenceFileType[0].equals(fileText)) {
            fileSizeKbytes = fileSize;
        } else if (mCharSequenceFileType[1].equals(fileText)) {
            fileSizeKbytes = fileSize * 1024;
        } else if (mCharSequenceFileType[2].equals(fileText)) {
            fileSizeKbytes = fileSize * 1024 * 1024;
        } else if (mCharSequenceFileType[3].equals(fileText)) {
            fileSizeKbytes = fileSize * 1024 * 1024 * 1024;
        }

        CharSequence connectionText = mBtnConnection.getText();
        if (mCharSequenceConnectionType[0].equals(connectionText)) {
            connectionSpeedKbytes = connectionSpeed / 8;
        } else if (mCharSequenceConnectionType[1].equals(connectionText)) {
            connectionSpeedKbytes = connectionSpeed * 125;
        } else if (mCharSequenceConnectionType[2].equals(connectionText)) {
            connectionSpeedKbytes = connectionSpeed * 125 * 1000;
        }

        timeInSeconds = fileSizeKbytes / connectionSpeedKbytes * tax;
        milliseconds = timeInSeconds * 1000;
        mResult.setText(getTime(milliseconds));

    }

    private String getTime(Double milliseconds) {
        double seconds;
        double minutes;
        double hours;
        double days;

        String time;

        seconds = (milliseconds / 1000);
        minutes = (milliseconds / 60000);
        hours = (milliseconds / 3600000);
        days = (milliseconds / 86400000);

        /* seconds 1000        = ms * 1000
         * minutes 60000       = ms * 1000 * 60
         * hours   3600000‬     = ms * 1000 * 60 * 60
         * days    86400000‬    = ms * 1000 * 60 * 60 * 24
         */

        String day = getText(R.string.days).toString();
        String hour = getText(R.string.hours).toString();
        String minute = getText(R.string.minutes).toString();
        String second = getText(R.string.seconds).toString();

        if (days >= 1) {
            time = (int) Math.floor(days) + " " + day + " " +
                    (int) Math.floor(days * 24 % 24) + " " + hour + " " +
                    (int) Math.floor(days * 24 * 60 % 60) + " " + minute + " " +
                    (int) Math.floor(days * 24 * 60 * 60 % 60) + " " + second;
        } else if (hours >= 1) {
            time = (int) Math.floor(hours) + " " + hour + " " +
                    (int) Math.floor(hours * 60 % 60) + " " + minute + " " +
                    (int) Math.floor(hours * 60 * 60 % 60) + " " + second;
        } else if (minutes >= 1) {
            time = (int) Math.floor(minutes) + " " + minute + " " +
                    (int) Math.floor(minutes * 60 % 60) + " " + second;
        } else if (seconds >= 1) {
            time = (int) Math.floor(seconds) + " " + second;
        } else {
            time = getText(R.string.lowTime).toString();
        }
        return time;
    }

}
