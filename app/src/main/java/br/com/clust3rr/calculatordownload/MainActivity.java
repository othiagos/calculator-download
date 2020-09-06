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

    private static final double SIZE_BIT = 1;
    private EditText mFileSize, mConnectionSpeed;
    private Button mBtnFile, mBtnConnection;
    private TextView mResult, mTax;
    private static final int DEFAULT_MARGIN = 5;
    private SeekBar mMarginRate;
    private static final double SIZE_BYTE = SIZE_BIT * 8;
    private static final double SIZE_KB = 1000d * SIZE_BYTE;
    private static final double SIZE_MB = 1000 * SIZE_KB;
    private static final double SIZE_GB = 1000 * SIZE_MB;
    private static final double SIZE_TB = 1000 * SIZE_GB;
    private static final double SIZE_Kb = 1000 * SIZE_BIT;
    private static final double SIZE_Mb = 1000 * SIZE_Kb;
    private static final double SIZE_Gb = 1000 * SIZE_Mb;
    private static final double SIZE_Tb = 1000 * SIZE_Gb;
    private static final double SIZE_KiB = 1024 * SIZE_BIT;
    private static final double SIZE_MiB = 1024 * SIZE_KiB;
    private static final double SIZE_GiB = 1024 * SIZE_MiB;
    private static final double SIZE_TiB = 1024 * SIZE_GiB;
    private static final double VEL_bit_S = SIZE_BIT;
    private static final double VEL_byte_S = SIZE_BYTE;
    private static final double VEL_Kb_S = SIZE_Kb;
    private static final double VEL_KB_S = SIZE_KB;
    private static final double VEL_Mb_S = SIZE_Mb;
    private static final double VEL_MB_S = SIZE_MB;
    private static final double VEL_Gb_S = SIZE_Gb;
    private static final double VEL_GB_S = SIZE_GB;
    private static final double TIME_S = 1;
    private static final double TIME_MINUTE = TIME_S * 60;
    private static final double TIME_HOUR = TIME_MINUTE * 60;
    private static final double TIME_DAY = TIME_HOUR * 24;
    private static final double TIME_YEAR = TIME_DAY * 365;
    private String[] mCharSequenceFileType, mCharSequenceConnectionType;

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
        mCharSequenceFileType = new String[]{
                getString(R.string.size_bits),
                getString(R.string.size_bytes),
                getString(R.string.size_KB),
                getString(R.string.size_MB),
                getString(R.string.size_GB),
                getString(R.string.size_TB),
                getString(R.string.size_Kb),
                getString(R.string.size_Mb),
                getString(R.string.size_Gb),
                getString(R.string.size_Tb),
                getString(R.string.size_KiB),
                getString(R.string.size_MiB),
                getString(R.string.size_GiB),
                getString(R.string.size_TiB)
        };

        mCharSequenceConnectionType = new String[]{
                getString(R.string.vel_bit_s),
                getString(R.string.vel_byte_s),
                getString(R.string.vel_Kb_s),
                getString(R.string.vel_KB_s),
                getString(R.string.vel_Mb_s),
                getString(R.string.vel_MB_s),
                getString(R.string.vel_Gb_s),
                getString(R.string.vel_GB_s)
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

            if (fs.equals("") || cs.equals(""))
                mResult.setText(getString(R.string.noTime));
            else
                mResult.setText(calculateTimeToDownload(fs, cs, mr));

        }

    }

    public void buttonFileOnClick(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.fileSize);
        int position = 0;
        for (int i = 0; !(mCharSequenceFileType[i].equals(mBtnFile.getText().toString())); i++)
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
        for (int i = 0; !(mCharSequenceConnectionType[i].equals(mBtnConnection.getText().toString())); i++)
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

    private String calculateTimeToDownload(String size, String speed, int margin) {

        double fileSize = 0, connectionSpeed = 0;

        try {
            fileSize = Double.parseDouble(size);
            connectionSpeed = Double.parseDouble(speed);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        double fileSize_bit = 0, connectionSpeed_bit = 0, timeInSeconds;

        String fileText = mBtnFile.getText().toString();

        if (mCharSequenceFileType[0].equals(fileText))
            fileSize_bit = fileSize * SIZE_BIT;
        else if (mCharSequenceFileType[1].equals(fileText))
            fileSize_bit = fileSize * SIZE_BYTE;
        else if (mCharSequenceFileType[2].equals(fileText))
            fileSize_bit = fileSize * SIZE_KB;
        else if (mCharSequenceFileType[3].equals(fileText))
            fileSize_bit = fileSize * SIZE_MB;
        else if (mCharSequenceFileType[4].equals(fileText))
            fileSize_bit = fileSize * SIZE_GB;
        else if (mCharSequenceFileType[5].equals(fileText))
            fileSize_bit = fileSize * SIZE_TB;
        else if (mCharSequenceFileType[6].equals(fileText))
            fileSize_bit = fileSize * SIZE_Kb;
        else if (mCharSequenceFileType[7].equals(fileText))
            fileSize_bit = fileSize * SIZE_Mb;
        else if (mCharSequenceFileType[8].equals(fileText))
            fileSize_bit = fileSize * SIZE_Gb;
        else if (mCharSequenceFileType[9].equals(fileText))
            fileSize_bit = fileSize * SIZE_Tb;
        else if (mCharSequenceFileType[10].equals(fileText))
            fileSize_bit = fileSize * SIZE_KiB;
        else if (mCharSequenceFileType[11].equals(fileText))
            fileSize_bit = fileSize * SIZE_MiB;
        else if (mCharSequenceFileType[12].equals(fileText))
            fileSize_bit = fileSize * SIZE_GiB;
        else if (mCharSequenceFileType[13].equals(fileText))
            fileSize_bit = fileSize * SIZE_TiB;

        String connectionText = mBtnConnection.getText().toString();

        if (mCharSequenceConnectionType[0].equals(connectionText))
            connectionSpeed_bit = connectionSpeed * VEL_bit_S;
        else if (mCharSequenceConnectionType[2].equals(connectionText))
            connectionSpeed_bit = connectionSpeed * VEL_byte_S;
        else if (mCharSequenceConnectionType[3].equals(connectionText))
            connectionSpeed_bit = connectionSpeed * VEL_Kb_S;
        else if (mCharSequenceConnectionType[4].equals(connectionText))
            connectionSpeed_bit = connectionSpeed * VEL_KB_S;
        else if (mCharSequenceConnectionType[5].equals(connectionText))
            connectionSpeed_bit = connectionSpeed * VEL_Mb_S;
        else if (mCharSequenceConnectionType[6].equals(connectionText))
            connectionSpeed_bit = connectionSpeed * VEL_MB_S;
        else if (mCharSequenceConnectionType[7].equals(connectionText))
            connectionSpeed_bit = connectionSpeed * VEL_Gb_S;
        else if (mCharSequenceConnectionType[8].equals(connectionText))
            connectionSpeed_bit = connectionSpeed * VEL_GB_S;

        timeInSeconds = fileSize_bit / (connectionSpeed_bit * (1 - ((double) margin / 100)));
        return timeToSting(timeInSeconds);
    }

    private String timeToSting(Double timeInSeconds) {

        double minutes = (timeInSeconds / TIME_MINUTE);
        double hours = (timeInSeconds / TIME_HOUR);
        double days = (timeInSeconds / TIME_DAY);

        String day = getString(R.string.day);
        String hour = getString(R.string.hour);
        String minute = getString(R.string.minute);
        String second = getString(R.string.second);

        if (timeInSeconds > TIME_YEAR) {
            return getString(R.string.highTime);
        } else if (timeInSeconds < 1) {
            return getString(R.string.lowTime);
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
        } else if (timeInSeconds >= TIME_S) {
            return (int) Math.floor(TIME_S) + "" + second;
        } else return getString(R.string.noTime);

    }
}