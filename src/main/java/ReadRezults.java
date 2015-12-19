import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class ReadRezults {

    public static void main(String[] args) throws Exception {

        Configuration conf = HBaseConfiguration.create();
        String zagon1 = "table_one";
        String zagon2 = "table_two";
        String rezult = "rezult_table";
        HTable htable = new HTable(conf, rezult);

        Scan scan = new Scan();
        ResultScanner scanner = htable.getScanner(scan);
        Result r;
        while (((r = scanner.next()) != null)) {

            byte[] key = r.getRow();
            int userId = Bytes.toInt(key);

            byte[] totalValue = r.getValue(Bytes.toBytes("type"), Bytes.toBytes("sales"));
            int count = Bytes.toInt(totalValue);

            //byte[] totalValue = r.getValue(Bytes.toBytes("days"), Bytes.toBytes("sales"));
            //String count = Bytes.toString(totalValue);

            System.out.println("type: " + userId + ",  sales: " + count);
        }
        scanner.close();
        htable.close();
    }
}