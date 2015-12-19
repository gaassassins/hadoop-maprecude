import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;


public class AddToTable {

    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();

        //********Two***********\\
        String [] pages = {"Elephant", "Zebra", "Hippopotamus", "Goat"};
        String Shop_table = "zagon2";

        //********Three*********\\
        //String [] pages = {"Сow", "Turkey", "Dog", "Horse"};
        //String Shop_table = "zagon1";

        HTable htable = new HTable(conf, Shop_table);

        int totalRecords = 15;
        int maxID = 4;
        Random rand = new Random();
        System.out.println("Recording");
        for (int i= 0; i < totalRecords; i++)
        {
            int userID = rand.nextInt(maxID) + 1;
            byte [] rowkey = Bytes.add(Bytes.toBytes(userID), Bytes.toBytes(i));
            String randomPage = pages[rand.nextInt(pages.length)];
            Put put = new Put(rowkey);
            put.addColumn(Bytes.toBytes("type"), Bytes.toBytes("sales"), Bytes.toBytes(randomPage));
            htable.put(put);
        }
        htable.flushCommits();
        htable.close();
        System.out.println("done");
    }
}