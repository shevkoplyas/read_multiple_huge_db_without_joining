/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read_all_huge_tables_without_join;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// class HistoricalDBReader handles single db file (and single table from that file)
//
public class HistoricalDataPlayer {

    private IHistoricalDataProcessor historical_data_processor;

    public HistoricalDataPlayer(IHistoricalDataProcessor historical_data_processor_param) {
        historical_data_processor = historical_data_processor_param;
    }

    // historical_data_folder - comes from ./config/xxx.json
    //
    public void historical_data_replay(String historical_data_folder, long from_epoch_s, long to_epoch_s) {

        // get list of *.db files
        List<File> files = get_files_by_mask(historical_data_folder);

        // create empty array of "HistoricalDBReader" (one per each db)
        List<HistoricalDBReader> db_readers = new ArrayList<HistoricalDBReader>();

        // and create DBReaders instance for each db
        for (File ifile : files) {
            String filename = historical_data_folder + "/" + ifile.getName();
            //System.out.print(filename);  // print list of files on the screen (just for debug)
            HistoricalDBReader db_reader = new HistoricalDBReader(filename);
            FunctionResult open_and_select_result = db_reader.open_and_select(from_epoch_s, to_epoch_s);
            if (open_and_select_result.succeed()) {
                db_readers.add(db_reader); // we only collect db_readers which have at least 1 row to read
            }
        }
        
        /* THE MEAT - main processing loop */
        try {

            // sort array according to: db_reader.rs.getInt("epoch_s")
            do {
                // check if array is not yet empty
                if (db_readers.size() == 0) {
                    break;
                }

                // sort elements according to "epoch_s" field
                Collections.sort(db_readers);

                // pop 0-th element from array
                HistoricalDBReader poped = db_readers.remove(0);

                // processing element
                // System.out.println("processing          " + poped.rs.getInt("epoch_s"));
                historical_data_processor.on_bar(poped.symbol, poped.rs.getInt("epoch_s"));

                while (poped.rs.next()) {

                    // have next row from poped element, make sure it is <= then next in line (new 0-th element of array)
                    if (db_readers.size() == 0
                            || poped.rs.getInt("epoch_s") <= db_readers.get(0).rs.getInt("epoch_s")) {        // db_readers.size() > 0 &&   <-- this condition is useless here, since previous check was "== 0" (by OR) so obviously it is ">0" if we're here...
                        // System.out.println("processing w/o pushing " + poped.rs.getInt("epoch_s"));
                        historical_data_processor.on_bar(poped.symbol, poped.rs.getInt("epoch_s"));

                        continue;
                    }

                    db_readers.add(poped); // adding poped back and moving to re-sort array (by breaking out)
                    break;
                }

            } while (true);

        } catch (SQLException ex) {
            String error_message = Aid.getExceptionDetails("SymbolStore::find_by_json(): Exception details: ", ex);
            System.out.println(error_message);
            return;// new FunctionResult(FunctionResult.FAIL, error_message);
        } catch (Exception ex) {
            String error_message = Aid.getExceptionDetails("SymbolStore::find_by_json(): UNEXPECTED Exception details: ", ex);
            System.out.println(error_message);
            return;// new FunctionResult(FunctionResult.FAIL, error_message);
        }

    }

    // let's overload historical_data_replay just for convenience (to mimic default from/to args)
    public void historical_data_replay(String historical_data_folder) {
        historical_data_replay(historical_data_folder, 0, 0);
    }

    // delete this when class moved to "speculant" since this method is in Aid:
    public List<File> get_files_by_mask(String path_to_dir) {
        Path dir = Paths.get(path_to_dir);
        List<File> files = new ArrayList<>();
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{java,class,jar}")) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{db}")) {
            for (Path entry : stream) {
                files.add(entry.toFile());
            }
            return files;
        } catch (IOException x) {
            throw new RuntimeException(String.format("error reading folder %s: %s",
                    dir,
                    x.getMessage()),
                    x);
        }

    }

}
