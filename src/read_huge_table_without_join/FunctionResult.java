package read_huge_table_without_join;

class FunctionResult {

    public static final int SUCCESS = 0;
    public static final int FAIL = -1;
    public static final int NOT_INITIALIZED = -123;

// constructors
    public FunctionResult() {
    }

    public FunctionResult(int result_code) {
        this(result_code, "");
    }

    public FunctionResult(int result_code, String message) {
        this.result_code = result_code;
        this.result_message = message;
    }

    /**
     * result_message can be optionally set (more useful for error cases, but
     * can be sometimes used for successful cases just as well)
     */
    protected String result_message = "uninitialized instance of FunctionResult. Did you forget to use FunctionResult.set()? ";

    /**
     * result_code convention: 0=success, anything else = failure
     */
    protected int result_code = FunctionResult.NOT_INITIALIZED;

    //
    // I guess having arbitrary name=value pairs in FunctionResult would make it ugly:
    //    - no more visibility what is actually returned
    //    - no compile-time check
    //    - all kinds of type violatoins etc..
    // Let's inherit from this class (FunctionResult) and add whatever new members we need
    // in derived classes (if needed) to avoid the above problems.
    //
    //private HashMap<String, Object> other_name_value_pairs = new HashMap<String,Object>();
    /**
     * Usage example: if (functionResult.succeed() == true)
     */
    public boolean succeed() {
        return result_code == FunctionResult.SUCCESS;
    }

    /**
     * Getter function.. just a synonym for succeed()
     */
    public boolean is_successful() {
        return succeed();
    }

    /**
     * just a synonym for "not succeed()"
     */
    public boolean failed() {
        return !succeed();
    }

    /**
     * Get string result_message.
     */
    public String get_message() {
        return result_message;
    }

    /**
     * You don't have to provide result_message when you instantiate
     * FunctionResult. Instead you can use set_message() later.
     */
    public FunctionResult set_message(String message) {
        this.result_message = message;
        return this;
    }

    public int get_code() {
        return result_code;
    }

    public FunctionResult set(int result_code, String result_message) {
        this.result_code = result_code;
        this.result_message = result_message;
        return this;
    }

    public FunctionResult set(int result_code) {
        set(result_code, "");
        return this;
    }

    public FunctionResult set_success(String result_message) {
        set(FunctionResult.SUCCESS, result_message);
        return this;
    }

    public FunctionResult set_success() {
        set(FunctionResult.SUCCESS, "");
        return this;
    }

    public FunctionResult set_fail(String error_message) {
        set(FunctionResult.FAIL, error_message);
        return this;
    }

    public FunctionResult set_fail() {
        return set_fail("");
    }
}
