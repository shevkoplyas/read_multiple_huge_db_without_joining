package read_huge_table_without_join;

import java.sql.Connection;

class FunctionResultWithDBConnection extends FunctionResult {

    public FunctionResultWithDBConnection() {

    }

    public FunctionResultWithDBConnection(int result_code) {
        this(result_code, "");
    }

    public FunctionResultWithDBConnection(int result_code, String message) {
        this.result_code = result_code;
        this.result_message = message;
    }

    public Connection db_connection;
}
