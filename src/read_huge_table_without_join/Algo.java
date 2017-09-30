/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read_huge_table_without_join;

class Algo implements IHistoricalDataProcessor {

    public void on_bar(String symbol, long bar_epoch_s) {
        System.out.println("Algo.on_bar(): bar_epoch_s=" + bar_epoch_s + ", sym=" + symbol);
    }

    public void on_tick(String symbol, long tick_epoch_s) {
        System.out.println("Algo.on_bar(): tick_epoch_s=" + tick_epoch_s + ", sym=" + symbol);
    }

}
