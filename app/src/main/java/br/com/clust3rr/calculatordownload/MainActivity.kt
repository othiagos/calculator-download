package br.com.clust3rr.calculatordownload

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.clust3rr.calculatordownload.databinding.ActivityMainBinding
import java.lang.Math.pow
import kotlin.math.exp
import kotlin.math.floor

class MainActivity : AppCompatActivity(), TextWatcher, SeekBar.OnSeekBarChangeListener {
    private lateinit var listFileSizeMeasurementType: Array<String>
    private lateinit var listConnectionSpeedMeasurementType: Array<String>

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        listFileSizeMeasurementType = arrayOf(
            getString(R.string.size_si_byte),

            getString(R.string.size_si_kilo_byte),
            getString(R.string.size_si_mega_byte),
            getString(R.string.size_si_giga_byte),
            getString(R.string.size_si_tera_byte),

            getString(R.string.size_iec_kilo_byte),
            getString(R.string.size_iec_mega_byte),
            getString(R.string.size_iec_giga_byte),
            getString(R.string.size_iec_tera_byte)
        )

        listConnectionSpeedMeasurementType = arrayOf(
            getString(R.string.speed_bit_s),
            getString(R.string.speed_kilo_bit_s),
            getString(R.string.speed_mega_bit_s),
            getString(R.string.speed_giga_bit_s),

            getString(R.string.speed_byte_s),
            getString(R.string.speed_kilo_byte_s),
            getString(R.string.speed_mega_byte_s),
            getString(R.string.speed_giga_byte_s)
        )

        binding.marginErrorSlider.progress = DEFAULT_ERROR_MARGIN
        binding.errorMarginDisplay.text = getString(R.string.errorMargin, DEFAULT_ERROR_MARGIN)

        binding.inputFileSize.addTextChangedListener(this)
        binding.inputConnectionSpeed.addTextChangedListener(this)
        binding.marginErrorSlider.setOnSeekBarChangeListener(this)

        binding.buttonFileSizeList.setOnClickListener { buttonFileOnClick() }
        binding.buttonConnectionSpeedList.setOnClickListener { buttonConnectionOnClick() }

        // setup default selection for measurement types
        binding.buttonFileSizeList.text = getString(R.string.size_si_giga_byte)
        binding.buttonConnectionSpeedList.text = getString(R.string.speed_mega_byte_s)

        updateResult()
    }

    private fun updateResult() {
        val fs = binding.inputFileSize.text.toString()
        val cs = binding.inputConnectionSpeed.text.toString()
        val mr = binding.marginErrorSlider.progress

        binding.timeResult.text = if(fs.isBlank() || cs.isBlank())
            getString(R.string.noTime)
        else
            calculateTimeToDownload(fs, cs, mr) ?: "Error"
    }

    private fun buttonFileOnClick() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.fileSize)
        var position = 0
        var i = 0
        while (listFileSizeMeasurementType[i] != binding.buttonFileSizeList.text.toString()) {
            position = i + 1
            i++
        }

        dialog.setSingleChoiceItems(
            listFileSizeMeasurementType,
            position
        ) { mDialog: DialogInterface, which: Int ->
            binding.buttonFileSizeList.text = listFileSizeMeasurementType[which]
            updateResult()
            mDialog.cancel()
        }.create().show()
    }

    private fun buttonConnectionOnClick() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.connectionSpeed)
        var position = 0
        var i = 0
        while (listConnectionSpeedMeasurementType[i] != binding.buttonConnectionSpeedList.text.toString()) {
            position = i + 1
            i++
        }

        dialog.setSingleChoiceItems(
            listConnectionSpeedMeasurementType,
            position
        ) { mDialog: DialogInterface, which: Int ->
            binding.buttonConnectionSpeedList.text = listConnectionSpeedMeasurementType[which]
            updateResult()
            mDialog.cancel()
        }.create().show()
    }

    private fun calculateTimeToDownload(size: String, speed: String, margin: Int): String? {
        var fileSize = 0.0
        var connectionSpeed = 0.0

        try {
            fileSize = size.toDouble()
            connectionSpeed = speed.toDouble()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return null
        }

        // TODO: Move to a new object
        val fileSizeInBits: Double = fileSize * when(binding.buttonFileSizeList.text.toString()){
            getString(R.string.size_si_byte) -> SIZE_BYTE
            getString(R.string.size_si_kilo_byte) -> SIZE_SI_KILO_BYTE * SIZE_BYTE
            getString(R.string.size_si_mega_byte) -> SIZE_SI_MEGA_BYTE * SIZE_BYTE
            getString(R.string.size_si_giga_byte) -> SIZE_SI_GIGA_BYTE * SIZE_BYTE
            getString(R.string.size_si_tera_byte) -> SIZE_SI_TERA_BYTE * SIZE_BYTE
            getString(R.string.size_iec_kilo_byte) -> SIZE_IEC_KILO_BYTE * SIZE_BYTE
            getString(R.string.size_iec_mega_byte) -> SIZE_IEC_MEGA_BYTE * SIZE_BYTE
            getString(R.string.size_iec_giga_byte) -> SIZE_IEC_GIGA_BYTE * SIZE_BYTE
            getString(R.string.size_iec_tera_byte) -> SIZE_IEC_TERA_BYTE * SIZE_BYTE
            else -> { 0.0 }
        }

        // TODO: Move to a new object
        val connectionSpeedInBits: Double = connectionSpeed * when(binding.buttonConnectionSpeedList.text.toString()){
            getString(R.string.speed_bit_s) -> SPEED_BIT_S
            getString(R.string.speed_byte_s) -> SPEED_BYTE_S
            getString(R.string.speed_kilo_bit_s) -> SPEED_KILO_BIT_S
            getString(R.string.speed_mega_bit_s) -> SPEED_MEGA_BIT_S
            getString(R.string.speed_giga_bit_s) -> SPEED_GIGA_BIT_S
            getString(R.string.speed_kilo_byte_s) -> SPEED_KILO_BYTE_S
            getString(R.string.speed_mega_byte_s) -> SPEED_MEGA_BYTE_S
            getString(R.string.speed_giga_byte_s) -> SPEED_GIGA_BYTE_S
            else -> { 0.0 }
        }

        return calculateTimeToDownload(fileSizeInBits, connectionSpeedInBits, margin)
    }

    private fun calculateTimeToDownload(
        fileSize: Bits,
        connectionSpeed: Bits,
        margin: Int) : String {

        if(margin < 0 || margin > 100)
            return "Error"

        val _margin = (1.0 + (margin.toDouble() / 100))
        val timeInSeconds: Seconds = ((fileSize) / connectionSpeed) * _margin
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
        } else if (timeInSeconds >= TIME_SECONDS) {
            floor(timeInSeconds).toInt().toString() + "" + second
        } else getString(R.string.noTime)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        updateResult()
    }
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        binding.errorMarginDisplay. text = getString(R.string.errorMargin, progress)
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        updateResult()
    }

    companion object {
        private const val DEFAULT_ERROR_MARGIN = 5

        // FILE SIZE
        private const val SIZE_BIT = 1.0
        private const val SIZE_BYTE = 8.0

        // SI -> 1KB = 10^3
        private const val SIZE_SI_KILO_BYTE : Double = 1000.0 // 10^3 = 1.000
        private const val SIZE_SI_MEGA_BYTE : Double = 1000000.0 // 10^6 = 1.000.000
        private const val SIZE_SI_GIGA_BYTE : Double = 1000000000.0 // 10^9 = 1.000.000
        private const val SIZE_SI_TERA_BYTE : Double = 1000000000000.0 // 10^12 = 1.000.000

        // IEC 600274-2 -> 1KiB = 2^10
        private const val SIZE_IEC_KILO_BYTE : Double = 1024.0 // 2^10 = 1.024
        private const val SIZE_IEC_MEGA_BYTE : Double = 1048576.0 // 2^20 = 1.048.576
        private const val SIZE_IEC_GIGA_BYTE : Double = 1073741824.0 // 2^30 = 1.073.741.824
        private const val SIZE_IEC_TERA_BYTE : Double = 1099511627776.0 // 2^40 = 1.099.511.627.776

        // CONNECTION SPEED
        private const val SPEED_BIT_S = SIZE_BIT
        private const val SPEED_KILO_BIT_S = SIZE_SI_KILO_BYTE
        private const val SPEED_MEGA_BIT_S = SIZE_SI_MEGA_BYTE
        private const val SPEED_GIGA_BIT_S = SIZE_SI_GIGA_BYTE

        private const val SPEED_BYTE_S = SIZE_BYTE
        private const val SPEED_KILO_BYTE_S = SIZE_SI_KILO_BYTE * SIZE_BYTE
        private const val SPEED_MEGA_BYTE_S = SIZE_SI_MEGA_BYTE * SIZE_BYTE
        private const val SPEED_GIGA_BYTE_S = SIZE_SI_GIGA_BYTE * SIZE_BYTE

        // TIME
        private const val TIME_SECONDS = 1.0
        private const val TIME_MINUTE = TIME_SECONDS * 60
        private const val TIME_HOUR = TIME_MINUTE * 60
        private const val TIME_DAY = TIME_HOUR * 24
        private const val TIME_YEAR = TIME_DAY * 365
    }
}

typealias Bits = Double
typealias Bytes = Double
typealias Seconds = Double