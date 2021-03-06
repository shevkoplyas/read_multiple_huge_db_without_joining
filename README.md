# What's it all about

The goal of this 1-evening exercise is to prototype one of the core mechanisms of
algo-trading backtest engine, which supposed to read hundreds of historical bars
databases/tables in parallel sequentially and all ordered by bar's timestamp,
but wihout using "join".

The problem we're solving here is the size of the data.
For example 10 years of 1-minute bars for
S&P500 components takes about 40 GByte of sqlite3 db files (1 db file per one symbol).
And with limitted RAM we simply can't join them all and then sort. We'll have
to open all 500 DBs, then "select *" ordered by timestamp and then manipulate
the array of "cursors" in order to read all 500 db/tables strictly sequentially
all ordered in time.

As a 1st step let's generate 333 fake historical db files with bogus data by
running included ruby script "./create_N_databases.rb". Its output is stored
in form of 333 files under "fake_historical_bars_databases/" folder (I left
those generated bogus .db files as a part of the project, so you don't have to
generate new ones).

Next step is to look at the java source code:
  - class Read_all_huge_tables_without_join creates a new instance of "HistoricalDataPlayer" 
    and calls for "historical_data_replay" passing reference to algo, which will get "on_bar()"
    callbacks along with from_epoch_s, to_epoch_s values which defines the exact time interval we want
    to replay in our backtest scenario. Leaving values out and calling:

        // 1st example: replay all available data
        HistoricalDataPlayer hdbr = new HistoricalDataPlayer(algo);
        hdbr.historical_data_replay(historical_data_folder);

        // 2nd example: replay historical bars only from given time range
        long from_epoch_s = 1231;
        long to_epoch_s = 1233;
        hdbr = new HistoricalDataPlayer(algo);
        hdbr.historical_data_replay(historical_data_folder, from_epoch_s, to_epoch_s);

  - class HistoricalDBReader handles single db file (opens db, selects rows, holds the cursor (ResultSet rs) for single table from that file)

  - and "the meat" of the project is inside class HistoricalDataPlayer!
    HistoricalDataPlayer would create an array of HistoricalDBReaders like this:
            ```List<HistoricalDBReader> db_readers = new ArrayList<HistoricalDBReader>();```
    and then sort/iterate/process all the records from all the data readers  (search for: /* THE MEAT - main processing loop */ comment)

  
  
Now let's run it, here's the end of the output (only covers 2nd example):

> 2nd example: replay historical bars only from given time range [1231, 1233]
> Algo.on_bar(): bar_epoch_s=1231, sym=symbol_136
> Algo.on_bar(): bar_epoch_s=1231, sym=symbol_231
> Algo.on_bar(): bar_epoch_s=1231, sym=symbol_29
> Algo.on_bar(): bar_epoch_s=1231, sym=symbol_55
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_199
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_206
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_211
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_25
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_250
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_256
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_292
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_48
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_65
> Algo.on_bar(): bar_epoch_s=1232, sym=symbol_83
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_189
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_21
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_220
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_237
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_249
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_26
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_312
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_34
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_50
> Algo.on_bar(): bar_epoch_s=1233, sym=symbol_60

As you can see our "algo" got "on_bar()" in ascending order (epoch_s lineary growing).
And yes, in ideal world we should pass "bar" object (which does not even exist in this demo) instead of just "long epoch_s" and
more than one algo might be interested in "subscribing" to replay and yes - somewhere
along the way in the middle of replay one of algos might decide to add more symbols
into subscription symbol set, and actually bars should not go directly to algo, but there should be something
inetween, which analyze each bar timestamp and decides how many fake "heartbeat callbacks" it should send to all algos
before allowing them to see next bar etc., but all these are outside of the scope of this simple
"how to select from multiple huge DB/tables without join" example.

# How to compile / run

The project was built/tested on NetBeans 8.1 (java version "1.8.0_92").
To compile under NetBeans: don't forget to add sqlite JDBC dependency:
right mouse click on the project -> properties -> libraries -> "Add JAR/Folder" button -> browse to the lib/sqlite-jdbc-3.8.11.2.jar file.

Alternatively just use "./compile" and "./run" scripts to start this example without any IDE from command line.

# Rough scatch on the read multiple tables logic (stored to ./doc/ folder as .png image):
![alt text](https://raw.githubusercontent.com/shevkoplyas/Read_multiple_huge_DBs_without_joining/master/doc/Read_multiple_huge_DBs_without_joining.png)

                    
Cheers,             
Dmitry Shevkoplyas  
https://dimon.ca/   

ps: Here's the "create table" statement for all sqlite3 files:
      CREATE TABLE IF NOT EXISTS historical_data(epoch_s int PRIMARY KEY, open real, high real, low real, close real, volume int, barCount int, WAP real);
