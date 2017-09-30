/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read_all_huge_tables_without_join;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// class HistoricalDBReader handles single db file (and single table from that file)
//
public class HistoricalDBReader implements Comparable<HistoricalDBReader> {

    private Connection db_connection;
    public String db_filename;
    public String symbol;
    Statement stmt;
    ResultSet rs;

    public HistoricalDBReader(String db_filename_param) {
        db_filename = db_filename_param;
        stmt = null;
        rs = null;

        // extract symbol from given full db filename
        Path path = Paths.get(db_filename_param);
        symbol = path.getFileName().toString();
        int pos = symbol.lastIndexOf(".");
        if (pos > 0) {
            symbol = symbol.substring(0, pos);
        }
    }

    public FunctionResultWithDBConnection _open_db(String db_filename) {

        try {
            Class.forName("org.sqlite.JDBC");
            db_connection = DriverManager.getConnection("jdbc:sqlite:" + db_filename);
            // System.out.println("SymbolStore::open_db(): db successfully opened: " + db_filename);

        } catch (Exception ex) {
            String error_message = Aid.getExceptionDetails("Exception cought while in symbol_store.open_db(): ", ex);
            System.out.println(error_message);
            return new FunctionResultWithDBConnection(FunctionResult.FAIL, error_message);
        }

        FunctionResultWithDBConnection result = new FunctionResultWithDBConnection(FunctionResult.SUCCESS);
        result.db_connection = db_connection;
        return result;
    }

    //
    // Function open_and_select() 
    // opend DB and execute "select" query on it.
    // Function returns FunctionResult, of cause! ;)
    //
    public FunctionResult open_and_select() {
        return open_and_select(0, 0);
    }

    public FunctionResult open_and_select(long from_epoch_s) {
        return open_and_select(from_epoch_s, 0);
    }

    public FunctionResult open_and_select(long from_epoch_s, long to_epoch_s) {
        if (stmt != null) {
            // error: you can't call open_and_select() multiple times
            System.out.println("Error: failed to open db");
        }

        FunctionResultWithDBConnection open_db_result = _open_db(db_filename);

        if (open_db_result.succeed() != true) {
            System.out.println("Error: failed to open db");
        }

        // build query "where" part 
        String where = "where 1=1";
        if (from_epoch_s != 0) {
            where += " and epoch_s >= " + from_epoch_s;
        }
        if (to_epoch_s != 0) {
            where += " and epoch_s <= " + to_epoch_s;
        }

        db_connection = open_db_result.db_connection;
        try {
            stmt = db_connection.createStatement();
            String sql = "select * from historical_data " + where + " order by epoch_s;";

            rs = stmt.executeQuery(sql);
            return rs.next() ? new FunctionResult(FunctionResult.SUCCESS) : new FunctionResult(FunctionResult.FAIL, "empty result set");

        } catch (SQLException ex) {
            String error_message = Aid.getExceptionDetails("SymbolStore::find_by_json(): Exception details: ", ex);
            System.out.println(error_message);
            return new FunctionResult(FunctionResult.FAIL, error_message);
        } catch (Exception ex) {
            String error_message = Aid.getExceptionDetails("SymbolStore::find_by_json(): UNEXPECTED Exception details: ", ex);
            System.out.println(error_message);
            return new FunctionResult(FunctionResult.FAIL, error_message);
        }
//        finally {
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//                if (stmt != null) {
//                    stmt.close();
//                }
//            } catch (SQLException ex) {
//                String error_message = getExceptionDetails(ex);
//                System.out.println(error_message);
//                return new FunctionResult(FunctionResult.FAIL, error_message); // new FunctionResultWithContractDetails(FunctionResult.FAIL, error_message);
//            }
//        }
    }

    // Let's define "compareTo()" to make it sortable by "Colelctions.sort()":
    // https://stackoverflow.com/questions/4066538/sort-an-arraylist-based-on-an-object-field
    // -> https://stackoverflow.com/a/4066659/7022062
    // defind compareTo(o) and then just Collections.sort(objList);
    @Override
    public int compareTo(HistoricalDBReader o) {
        try {
            return this.rs.getInt("epoch_s") - o.rs.getInt("epoch_s");
        } catch (SQLException ex) {
            String error_message = Aid.getExceptionDetails("SymbolStore::find_by_json(): Exception details: ", ex);
            System.out.println(error_message);
            return -1; //new FunctionResult(FunctionResult.FAIL, error_message);
        } catch (Exception ex) {
            String error_message = Aid.getExceptionDetails("SymbolStore::find_by_json(): UNEXPECTED Exception details: ", ex);
            System.out.println(error_message);
            return -1; //new FunctionResult(FunctionResult.FAIL, error_message);
        }
    }

    // Don't forget to release resources when you're done with this DB reader
    public void clean_up() {

        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException ex) {
            String error_message = Aid.getExceptionDetails(ex);
            System.out.println(error_message);
            return; // new FunctionResult(FunctionResult.FAIL, error_message); // new FunctionResultWithContractDetails(FunctionResult.FAIL, error_message);
        }

    }
}
