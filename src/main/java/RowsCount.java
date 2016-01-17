import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;




public class RowsCount {

    static class Mapper1 extends TableMapper<ImmutableBytesWritable, IntWritable> {

        private static final IntWritable one = new IntWritable(1);
        private static final IntWritable neg_one= new IntWritable(-1);

        @Override
        public void map(ImmutableBytesWritable row, Result values, Context context) throws IOException, InterruptedException {
            ImmutableBytesWritable userKey = new ImmutableBytesWritable(row.get(), 0, Bytes.SIZEOF_INT);
            TableSplit currentSplit = (TableSplit)context.getInputSplit();
            byte[] tableName = currentSplit.getTableName();

            String table = Bytes.toString(tableName);

            if (table.equals("zoo1"))
                context.write(userKey, one);
            else
                context.write(userKey, neg_one);

            /*zoo1*/
            /*
            id: 1  animals: 17
            id: 2  animals: 18
            id: 3  animals: 26
            id: 4  animals: 17
            id: 5  animals: 14
            id: 6  animals: 11
             */


            /*zoo2*/
            /*
            id: 1  animals: 9
            id: 2  animals: 7
            id: 4  animals: 7
             */
        }
    }

    public static class Reducer1 extends TableReducer<ImmutableBytesWritable, IntWritable, ImmutableBytesWritable> {
        public void reduce(ImmutableBytesWritable key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            boolean flag = true;
            int elements = 0;
            for (IntWritable val : values) {
                elements += val.get();
                int v = val.get();
                if (v == 1)
                     flag = false;
            }

            if (flag)
            {
                Put put = new Put(key.get());
                put.addColumn(Bytes.toBytes("id"), Bytes.toBytes("sales"), Bytes.toBytes(Math.abs(elements)));
                System.out.println("id: " + Bytes.toInt(key.get()) + "  animals: " + Math.abs(elements));
                context.write(key, put);}
        }

    }

    public static void main(String[] args) throws Exception {
        List scans = new ArrayList();
        Configuration conf = HBaseConfiguration.create();
        Job job = Job.getInstance(conf, "Hbase_RowsCount");
        job.setJarByClass(RowsCount.class);

        String columns = "type";
        String table1 = "zoo1";
        String table2 = "zoo2";

         Scan scan = new Scan();
         scan.addFamily(Bytes.toBytes(columns));
         scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, table1.getBytes());
         scan.setFilter(new FirstKeyOnlyFilter());
         scans.add(scan);

        Scan scan2 = new Scan();
        scan2.addFamily(Bytes.toBytes(columns));
        scan2.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, table2.getBytes());
        scan2.setFilter(new FirstKeyOnlyFilter());
        scans.add(scan2);


        TableMapReduceUtil.initTableMapperJob(scans, Mapper1.class, ImmutableBytesWritable.class, IntWritable.class, job);
        TableMapReduceUtil.initTableReducerJob("resultfor", Reducer1.class, job);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}