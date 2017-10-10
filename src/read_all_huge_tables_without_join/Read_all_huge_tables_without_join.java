/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read_all_huge_tables_without_join;

//public class Read_all_huge_tables_without_join {
//
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        // TODO code application logic here
//    }
//    
//}
import java.sql.*;
import java.util.*;  // for ArrayList, List
import java.io.IOException;

// https://stackoverflow.com/questions/2102952/listing-files-in-a-directory-matching-a-pattern-in-java
// -> https://stackoverflow.com/a/26681423/7022062
import java.nio.*;
import java.nio.file.*;
import java.io.File;


import java.util.*;  // for ArrayList, List

public class Read_all_huge_tables_without_join {

    public static void main(String[] args) {
        Read_all_huge_tables_without_join asdf = new Read_all_huge_tables_without_join();
        asdf.main2();
    }

    public void main2() {

        // algo instance will get on_bar() callbacks from the HistoricalDataPlayer
        Algo algo = new Algo();

        // path to the folder with all the sqlite3 db files
        String historical_data_folder = "./fake_historical_bars_databases";

        // 1st example: replay all available data
        System.out.println("1st example: replay all available data");
        HistoricalDataPlayer hdp = new HistoricalDataPlayer(algo);
        hdp.historical_data_replay(historical_data_folder);

        // 2nd example: replay historical bars only from given time range
        long from_epoch_s = 1231;
        long to_epoch_s = 1233;
        System.out.println("\n2nd example: replay historical bars only from given time range [" + from_epoch_s + ", " + to_epoch_s + "]");
        hdp = new HistoricalDataPlayer(algo);
        hdp.historical_data_replay(historical_data_folder, from_epoch_s, to_epoch_s);
    }

}
