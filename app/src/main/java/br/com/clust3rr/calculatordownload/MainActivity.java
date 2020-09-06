package br.com.clust3rr.calculatordownload;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private CharSequence[] mCharSequenceFileType, mCharSequenceConnectionType;
    private EditText mFileSize, mConnectionSpeed;
    private Button mBtnFile, mBtnConnection;
    private TextView mResult, mTax;
    private static final int DEFAULT_MARGIN = 5;
    private static final double YEAR_MILLIS = 31536000000d;
    private SeekBar mMarginRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFileSize = findViewById(R.id.textInputEditFileSize);
        mConnectionSpeed = findViewById(R.id.textInputEditConnectionSpeed);
        mBtnFile = findViewById(R.id.buttonFile);
        mBtnConnection = findViewById(R.id.buttonConection);
        mMarginRate = findViewById(R.id.seekBar);
        mResult = findViewById(R.id.result);
        mTax = findViewById(R.id.error_margin);

        mMarginRate.setProgress(DEFAULT_MARGIN);
        mTax.setText(getString(R.string.errorMargin, DEFAULT_MARGIN));

        //
        mCharSequenceFileType = new CharSequence[]{
                getString(R.string.kilobyte),
                getString(R.string.megabyte),
                getString(R.string.gigabyte),
                getString(R.string.terabyte)
        };

        mCharSequenceConnectionType = new CharSequence[]{
                getString(R.string.kbps),
                getString(R.string.mbps),
                getString(R.string.gbps)
        };

        mFileSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateResult();
            }
        });

        mConnectionSpeed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateResult();
            }
        });

        mMarginRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTax.setText(getString(R.string.errorMargin, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateResult();
            }
        });

        updateResult();
    }

    private void updateResult() {
        if (mFileSize != null && mConnectionSpeed != null && mResult != null) {
            String fs = mFileSize.getText().toString();
            String cs = mConnectionSpeed.getText().toString();
            int mr = mMarginRate.getProgress();

            if (fs.equals("") || cs.equals("")) {
                mResult.setText(getString(R.string.noTime));
            } else {
                mResult.setText(result(fs, cs, mr));
            }

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
                updateResult();
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
                updateResult();
                dialog.cancel();
            }
        }).create().show();
    }

    private String result(String size, String speed, int margin) {
        /*
         *         Bytes -> Gbytes
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

        double fileSize = 0;
        double connectionSpeed = 0;
        try {
            fileSize = Double.parseDouble(size);
            connectionSpeed = Double.parseDouble(speed);
        } catch (NumberFormatException ignored) {
        }
        double fileSizeKbytes = 0;
        double connectionSpeedKbytes = 0;
        double timeInSeconds;
        double milliseconds;

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

        timeInSeconds = fileSizeKbytes / (connectionSpeedKbytes * (1 - ((double) margin / 100)));
        milliseconds = timeInSeconds * 1000;
        return getTime(milliseconds);
    }

    private String getTime(Double milliseconds) {
        /*
         * seconds 1000        = ms * 1000
         * minutes 60000       = ms * 1000 * 60
         * hours   3600000‬     = ms * 1000 * 60 * 60
         * days    86400000‬    = ms * 1000 * 60 * 60 * 24
         */

        double seconds = (milliseconds / 1000);
        double minutes = (milliseconds / 60000);
        double hours = (milliseconds / 3600000);
        double days = (milliseconds / 86400000);

        String day = getString(R.string.day);
        String hour = getString(R.string.hour);
        String minute = getString(R.string.minute);
        String second = getString(R.string.second);

        if (milliseconds > YEAR_MILLIS) {
            return getString(R.string.highTime);
        } else if (days >= 1) {
            return (int) Math.floor(days) + "" + day + " " +
                    (int) Math.floor(days * 24 % 24) + "" + hour + " " +
                    (int) Math.floor(days * 24 * 60 % 60) + "" + minute + " " +
                    (int) Math.floor(days * 24 * 60 * 60 % 60) + "" + second;
        } else if (hours >= 1) {
            return (int) Math.floor(hours) + "" + hour + " " +
                    (int) Math.floor(hours * 60 % 60) + "" + minute + " " +
                    (int) Math.floor(hours * 60 * 60 % 60) + "" + second;
        } else if (minutes >= 1) {
            return (int) Math.floor(minutes) + "" + minute + " " +
                    (int) Math.floor(minutes * 60 % 60) + "" + second;
        } else if (seconds >= 1) {
            return (int) Math.floor(seconds) + "" + second;
        } else {
            return getString(R.string.lowTime);
        }
    }
}