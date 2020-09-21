package app.load

import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.InputSplit
import org.apache.hadoop.mapreduce.RecordReader
import org.apache.hadoop.mapreduce.TaskAttemptContext
import org.apache.hadoop.mapreduce.lib.input.FileSplit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.util.zip.GZIPInputStream

class UcRecordReader: RecordReader<LongWritable, Text>() {

    private var input: BufferedReader? = null
    private var value: Text? = null

    override fun initialize(split: InputSplit, context: TaskAttemptContext) {
        split.locations.forEach {
            logger.info("LOCATION: '$it'")
        }
        val file = (split as FileSplit).path
        val fs = file.getFileSystem(context.configuration)
        input = BufferedReader(InputStreamReader(GZIPInputStream(fs.open(file))))
    }

    override fun close() = IOUtils.closeStream(input)


    override fun nextKeyValue(): Boolean {
        val line = input?.readLine()
        return if (line != null) {
            value = Text(line)
            true
        }
        else {
            false
        }
    }

    override fun getCurrentKey(): LongWritable = LongWritable()

    override fun getCurrentValue(): Text? = value

    override fun getProgress(): Float = .5f

    companion object {
        val logger: Logger = LoggerFactory.getLogger(UcRecordReader::class.java)
    }
}
