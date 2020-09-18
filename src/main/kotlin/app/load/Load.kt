package app.load

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.KeyValue
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.util.GenericOptionsParser

fun main(args: Array<String>) {

    val arguments = Configuration().let { GenericOptionsParser(it, args).remainingArgs }

    val conf = HBaseConfiguration.create()

    val job = Job.getInstance(conf, "hbase bulk import").apply {
        setJarByClass(UcMapper::class.java)
        mapperClass = UcMapper::class.java
        mapOutputKeyClass = ImmutableBytesWritable::class.java
        mapOutputValueClass = KeyValue::class.java
        inputFormatClass = TextInputFormat::class.java
    }

    val hTable = HTable(conf, "epl")
    HFileOutputFormat.configureIncrementalLoad(job, hTable)

    FileInputFormat.addInputPath(job, Path(arguments[0]))
    FileOutputFormat.setOutputPath(job, Path(arguments[1]))
    job.waitForCompletion(true)
}
