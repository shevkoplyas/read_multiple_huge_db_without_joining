/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read_huge_table_without_join;

/**
 *
 * @author dmitry
 */
public interface IHistoricalDataProcessor {

// https://stackoverflow.com/a/43192755/7022062
    void on_bar(String symbol, long bar_epoch_s);

    void on_tick(String symbol, long tick_epoch_s);
}
