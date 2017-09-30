/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read_huge_table_without_join;

//public class Read_huge_table_without_join {
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
/*
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
 */

import java.util.*;  // for ArrayList, List

public class Read_huge_table_without_join {

    public static void main(String[] args) {
        Read_huge_table_without_join asdf = new Read_huge_table_without_join();
        asdf.main2();
    }

    public void main2() {
        Path path = FileSystems.getDefault().getPath(".");

        System.out.println("path to pwd: "+path.toUri().toString());
        Algo algo = new Algo();

        String historical_data_folder = "./fake_historical_bars_databases";
        long from_epoch_s = 1222;
        long to_epoch_s = 1333;
        HistoricalDataPlayer hdbr = new HistoricalDataPlayer(algo);
        hdbr.historical_data_replay(historical_data_folder, from_epoch_s, to_epoch_s);
    }

}
