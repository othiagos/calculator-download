package br.com.clust3rr.calculatordownload

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.clust3rr.calculatordownload.databinding.ActivityMainBinding
import kotlin.math.floor

class MainActivity : AppCompatActivity(), TextWatcher {
    private lateinit var mCharSequenceFileType: Array<String>
    private lateinit var mCharSequenceConnectionType: Array<String>

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding.marginErrorSlider.progress = DEFAULT_MARGIN
        binding.errorMarginDisplay.text = getString(R.string.errorMargin, DEFAULT_MARGIN)

        mCharSequenceFileType = arrayOf(
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
        )

        mCharSequenceConnectionType = arrayOf(
            getString(R.string.vel_bit_s),
            getString(R.string.vel_byte_s),
            getString(R.string.vel_Kb_s),
            getString(R.string.vel_KB_s),
            getString(R.string.vel_Mb_s),
            getString(R.string.vel_MB_s),
            getString(R.string.vel_Gb_s),
            getString(R.string.vel_GB_s)
        )

        binding.textInputFileSize.addTextChangedListener(this)
        binding.textInputConnectionSpeed.addTextChangedListener(this)
        binding.marginErrorSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.errorMarginDisplay. text = getString(R.string.errorMargin, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateResult()
            }
        })

        binding.buttonFileSizeList.setOnClickListener { buttonFileOnClick() }
        binding.buttonSpeedList.setOnClickListener { buttonConnectionOnClick() }

        updateResult()
    }

    private fun updateResult() {
        val fs = binding.textInputFileSize.text.toString()
        val cs =  binding.textInputConnectionSpeed.text.toString()
        val mr = binding.marginErrorSlider.progress

        binding.timeResult.text = if(fs.isBlank() || cs.isBlank())
            getString(R.string.noTime)
        else
            calculateTimeToDownload(fs, cs, mr)
    }

    private fun buttonFileOnClick() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.fileSize)
        var position = 0
        var i = 0
        while (mCharSequenceFileType[i] != binding.buttonFileSizeList.text.toString()) {
            position = i + 1
            i++
        }

        dialog.setSingleChoiceItems(
            mCharSequenceFileType,
            position
        ) { mDialog: DialogInterface, which: Int ->
            binding.buttonFileSizeList.text = mCharSequenceFileType[which]
            updateResult()
            mDialog.cancel()
        }.create().show()
    }

    private fun buttonConnectionOnClick() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.connectionSpeed)
        var position = 0
        var i = 0
        while (mCharSequenceConnectionType[i] != binding.buttonSpeedList.text.toString()) {
            position = i + 1
            i++
        }

        dialog.setSingleChoiceItems(
            mCharSequenceConnectionType,
            position
        ) { mDialog: DialogInterface, which: Int ->
            binding.buttonSpeedList.text = mCharSequenceConnectionType[which]
            updateResult()
            mDialog.cancel()
        }.create().show()
    }

    private fun calculateTimeToDownload(size: String, speed: String, margin: Int): String {
        var fileSize = 0.0
        var connectionSpeed = 0.0

        try {
            fileSize = size.toDouble()
            connectionSpeed = speed.toDouble()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        var fileSizeBits = 0.0
        var connectionSpeedBits = 0.0

        when(binding.buttonFileSizeList.text.toString()){
            mCharSequenceFileType[0] -> fileSizeBits = fileSize * SIZE_BIT
            mCharSequenceFileType[1] -> fileSizeBits = fileSize * SIZE_BYTE
            mCharSequenceFileType[2] -> fileSizeBits = fileSize * SIZE_KB
            mCharSequenceFileType[3] -> fileSizeBits = fileSize * SIZE_MB
            mCharSequenceFileType[4] -> fileSizeBits = fileSize * SIZE_GB
            mCharSequenceFileType[5] -> fileSizeBits = fileSize * SIZE_TB
            mCharSequenceFileType[6] -> fileSizeBits = fileSize * SIZE_Kb
            mCharSequenceFileType[7] -> fileSizeBits = fileSize * SIZE_Mb
            mCharSequenceFileType[8] -> fileSizeBits = fileSize * SIZE_Gb
            mCharSequenceFileType[9] -> fileSizeBits = fileSize * SIZE_Tb
            mCharSequenceFileType[10] -> fileSizeBits = fileSize * SIZE_KiB
            mCharSequenceFileType[11] -> fileSizeBits = fileSize * SIZE_MiB
            mCharSequenceFileType[12] -> fileSizeBits = fileSize * SIZE_GiB
            mCharSequenceFileType[13] -> fileSizeBits = fileSize * SIZE_TiB
        }

        when(binding.buttonSpeedList.text.toString()){
            mCharSequenceConnectionType[0] -> connectionSpeedBits = connectionSpeed * VEL_bit_S
            mCharSequenceConnectionType[1] -> connectionSpeedBits = connectionSpeed * VEL_byte_S
            mCharSequenceConnectionType[2] -> connectionSpeedBits = connectionSpeed * VEL_Kb_S
            mCharSequenceConnectionType[3] -> connectionSpeedBits = connectionSpeed * VEL_KB_S
            mCharSequenceConnectionType[4] -> connectionSpeedBits = connectionSpeed * VEL_Mb_S
            mCharSequenceConnectionType[5] -> connectionSpeedBits = connectionSpeed * VEL_MB_S
            mCharSequenceConnectionType[6] -> connectionSpeedBits = connectionSpeed * VEL_Gb_S
            mCharSequenceConnectionType[7] -> connectionSpeedBits = connectionSpeed * VEL_GB_S
        }

        val timeInSeconds = fileSizeBits / (connectionSpeedBits * (1 - (margin.toDouble() / 100)))
        return timeToSting(timeInSeconds)
    }

    private fun timeToSting(timeInSeconds: Double): String {
        val minutes = (timeInSeconds / TIME_MINUTE)
        val hours = (timeInSeconds / TIME_HOUR)
        val days = (timeInSeconds / TIME_DAY)

        val day = getString(R.string.day)
        val hour = getString(R.string.hour)
        val minute = getString(R.string.minute)
        val second = getString(R.string.second)

        return if (timeInSeconds > TIME_YEAR) {
            getString(R.string.highTime)
        } else if (timeInSeconds < 1) {
            getString(R.string.lowTime)
        } else if (days >= 1) {
            floor(days).toInt()
                .toString() + "" + day + " " + floor(days * 24 % 24)
                .toInt() + "" + hour + " " + floor(days * 24 * 60 % 60)
                .toInt() + "" + minute + " " + floor(days * 24 * 60 * 60 % 60)
                .toInt() + "" + second
        } else if (hours >= 1) {
            floor(hours).toInt()
                .toString() + "" + hour + " " + floor(hours * 60 % 60)
                .toInt() + "" + minute + " " + floor(hours * 60 * 60 % 60)
                .toInt() + "" + second
        } else if (minutes >= 1) {
            floor(minutes).toInt().toString() + "" + minute + " " + floor(
                minutes * 60 % 60
            ).toInt() + "" + second
        } else if (timeInSeconds >= TIME_S) {
            floor(timeInSeconds).toInt().toString() + "" + second
        } else getString(R.string.noTime)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        updateResult()
    }

    companion object {
        private const val DEFAULT_MARGIN = 5
        private const val SIZE_BIT = 1.0
        private const val SIZE_BYTE = SIZE_BIT * 8
        private const val SIZE_KB = 1000 * SIZE_BYTE
        private const val SIZE_MB = 1000 * SIZE_KB
        private const val SIZE_GB = 1000 * SIZE_MB
        private const val SIZE_TB = 1000 * SIZE_GB
        private const val SIZE_Kb = 1000 * SIZE_BIT
        private const val SIZE_Mb = 1000 * SIZE_Kb
        private const val SIZE_Gb = 1000 * SIZE_Mb
        private const val SIZE_Tb = 1000 * SIZE_Gb
        private const val SIZE_KiB = 1024 * SIZE_BIT
        private const val SIZE_MiB = 1024 * SIZE_KiB
        private const val SIZE_GiB = 1024 * SIZE_MiB
        private const val SIZE_TiB = 1024 * SIZE_GiB
        private const val VEL_bit_S = SIZE_BIT
        private const val VEL_byte_S = SIZE_BYTE

        private const val VEL_Kb_S = SIZE_Kb
        private const val VEL_KB_S = SIZE_KB
        private const val VEL_Mb_S = SIZE_Mb
        private const val VEL_MB_S = SIZE_MB
        private const val VEL_Gb_S = SIZE_Gb
        private const val VEL_GB_S = SIZE_GB

        private const val TIME_S = 1.0
        private const val TIME_MINUTE = TIME_S * 60
        private const val TIME_HOUR = TIME_MINUTE * 60
        private const val TIME_DAY = TIME_HOUR * 24
        private const val TIME_YEAR = TIME_DAY * 365
    }
}