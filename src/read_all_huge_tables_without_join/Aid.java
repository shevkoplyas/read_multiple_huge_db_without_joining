/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read_all_huge_tables_without_join;

/**
 *
 * @author dmitry
 */
public class Aid {

    public static String getExceptionDetails(String describe_the_failed_operation) {
        return (describe_the_failed_operation == "" ? "" : describe_the_failed_operation + "\n");
    }

    public static String getExceptionDetails(String describe_the_failed_operation, Exception ex) {
        return (describe_the_failed_operation == "" ? "" : describe_the_failed_operation + "\n")
                + " Exception class: '" + ex.getClass().toString().replace("class ", "") + "'\n"
                + " Exception message: '" + ex.getMessage() + "'\n"
                + " Exception backtrace: <todo later>";
    }

    public static String getExceptionDetails(Exception ex) {
        return " Exception class: '" + ex.getClass().toString().replace("class ", "") + "'\n"
                + " Exception message: '" + ex.getMessage() + "'\n"
                + " Exception backtrace: <todo later>";
    }
}
