package app.filter
//import FilterRowsWithGivenColumns.Companion.createSubmittableJob
//import com.google.common.base.Splitter
//import com.google.common.primitives.Longs
//import org.apache.hadoop.conf.Configuration
//import org.apache.hadoop.fs.Path
//import org.apache.hadoop.hbase.HBaseConfiguration
//import org.apache.hadoop.hbase.client.Connection
//import org.apache.hadoop.hbase.client.ConnectionFactory
//import org.apache.hadoop.hbase.client.Result
//import org.apache.hadoop.hbase.client.Scan
//import org.apache.hadoop.hbase.filter.*
//import org.apache.hadoop.hbase.io.ImmutableBytesWritable
//import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil
//
//import org.apache.hadoop.hbase.mapreduce.TableMapper
//import org.apache.hadoop.hbase.util.Bytes
//import org.apache.hadoop.io.Text
//import org.apache.hadoop.mapreduce.Job
//import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
//import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
//import java.io.IOException
//import java.util.*
//import kotlin.system.exitProcess
//
//
//class FilterRowsWithGivenColumns {
//
//    companion object {
//        val NAME = "FilterRowsWithGivenColumns"
//        @Throws(IOException::class)
//        fun createSubmittableJob(conf: Configuration, args: Array<String>): Job? {
//            val tableName = "k2hb:ingest"
//            val family = "topic"
//            val columns = System.getenv("HTME_COLUMNS_EXPORTED")
//
//            var columnsSet: Iterable<String?> = HashSet()
//            columnsSet = Splitter.on(',')
//                    .split(columns)
//            val qualifierFilters: MutableList<Filter> = ArrayList<Filter>()
//
//            for (qual in columnsSet) {
//                qualifierFilters.add(QualifierFilter(CompareFilter.CompareOp.EQUAL,
//                        BinaryComparator(Bytes.toBytes(qual))))
//            }
//
//            val skipFilter: Filter = SkipFilter(FilterList(FilterList.Operator.MUST_PASS_ALL, qualifierFilters))
//            val scan = Scan()
//            scan.addFamily(Bytes.toBytes(family))
//            scan.cacheBlocks = false
//            scan.caching = 1000
//            scan.filter = skipFilter
//            val job = Job.getInstance(conf, NAME + "_" + tableName)
//            job.setJarByClass(FilterRowsWithGivenColumns::class.java)
//            TableMapReduceUtil.initTableMapperJob(tableName, scan,
//                    FilterRowsWithGivenColumns.FilterRowsWithGivenColumnsMapper::class.java, ImmutableBytesWritable::class.java, kotlin.Result::class.java, job)
//            job.numReduceTasks = 0
//            job.mapOutputKeyClass = Text::class.java
//            job.mapOutputValueClass = Text::class.java
//            job.outputKeyClass = Text::class.java
//            job.outputValueClass = Text::class.java
//            job.outputFormatClass = TextOutputFormat::class.java
//            FileOutputFormat.setOutputPath(job, Path("/home/ssm-user/mapred-output/"))
//            return job
//        }
//
//    }
//
//    /**
//     * Mapper that runs the count.
//     */
//    internal class FilterRowsWithGivenColumnsMapper : TableMapper<Text?, Text?>() {
//        var empty: Text = Text("")
//        var key_to_write: Text = Text()
//        var maxTs = Long.MAX_VALUE
//        var minTs = Long.MIN_VALUE
//
//        /** Counter enumeration to count the actual rows.  */
//        enum class Counters {
//            MATCHING_ROWS, OUTSIDE_TIME_RANGE
//        }
//
//        @Throws(IOException::class, InterruptedException::class)
//        override fun setup(context: Context) {
//            maxTs = context.configuration.getLong("maxts", Long.MAX_VALUE)
//            minTs = context.configuration.getLong("mints", Long.MIN_VALUE)
//        }
//
//        @Throws(IOException::class)
//        override fun map(row: ImmutableBytesWritable, values: Result,
//                         context: Context) {
//            try {
//                val tsArray = LongArray(values.size())
//                var i = 0
//                for (value in values.listCells()) {
//                    tsArray[i] = value.timestamp
//                    i++
//                }
//                if (Longs.min(*tsArray) < minTs || Longs.max(*tsArray) > maxTs) {
//                    context.getCounter(Counters.OUTSIDE_TIME_RANGE).increment(1)
//                    return
//                } else {
//                    context.getCounter(Counters.MATCHING_ROWS).increment(1)
//                    key_to_write.set(row.get())
//                    context.write(key_to_write, empty)
//                }
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//
//}
//
///**
// * Main entry point.
// *
// * @param args The command line parameters.
// * @throws Exception When running the job fails.
// */
//@Throws(Exception::class)
//fun main(args: Array<String>) {
//
//    val conf = HBaseConfiguration.create().apply {
//        this.set("hbase.zookeeper.quorum", System.getenv("HBASE_MASTER_URL"))
//        this.setInt("hbase.zookeeper.port", 2181)
//    }
//
//    println("Starting job")
//    val job = createSubmittableJob(conf, arrayOf())
//
//    println("Job Started")
//    val tick = Date()
//
//    var success = false
//    try {
//        success = job!!.waitForCompletion(true)
//        val tock = Date()
//        println("Finished job. Took ${(tock.time - tick.time) / 1000} seconds")
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//    exitProcess(if (success) 0 else 1)
//}
