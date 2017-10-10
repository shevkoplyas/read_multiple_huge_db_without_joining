package read_all_huge_tables_without_join;

public interface IHistoricalDataProcessor {

// https://stackoverflow.com/a/43192755/7022062
    void on_bar(String symbol, long bar_epoch_s);

    void on_tick(String symbol, long tick_epoch_s);
}
