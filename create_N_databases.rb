#!/usr/bin/env ruby

# this script will create bunch sqlite3 files under ./fake_historical_bars_databases folder
# (see "CREATE TABLE" statement somewhere down below)
#

# gem install sqlite3
# https://github.com/sparklemotion/sqlite3-ruby
require "sqlite3"

# for FileUtils.mkdir_p
require 'fileutils'

require 'rubygems'
#require 'pry'

number_of_databases_to_create = 333
number_of_datapoints = 33

# ----------------------------------- don't edit below this line ----------

result_dir = './fake_historical_bars_databases'
FileUtils.mkdir_p(result_dir) unless Dir.exists?(result_dir)
input_filename_db_prefix="#{result_dir}/symbol_"
db_objects = []
db_names = []

# create bunch of DB's on filesystem
number_of_databases_to_create.times{|i|
    input_filename_db = "#{input_filename_db_prefix}#{i}.db"
     db_objects << SQLite3::Database.new(input_filename_db)
     db_names << input_filename_db
}

# create tables inside each db
db_objects.each{|db_object|
    sql = <<-SQL
      CREATE TABLE IF NOT EXISTS historical_data(epoch_s int PRIMARY KEY,open real,high real,low real,close real,volume int,barCount int,WAP real)
    SQL

    result_rows = db_object.execute(sql)
    # check result (should be empty array)
    raise "error: something went wrong while trying to create table" if result_rows.class != Array && result_rows.size != 0
}

# fill up tables with bogus data
db_objects.each{|db_object|
    number_of_datapoints.times{
    # Note: this might get error:
    # UNIQUE constraint failed: historical_data.epoch_s (SQLite3::ConstraintException)
    sql = <<-SQL
      INSERT INTO historical_data (epoch_s, open, high, low, close, volume, barCount, WAP) values
                                  (#{rand(1000..2000)}, #{rand(1..100)}, #{rand(1..100)}, #{rand(1..100)}, #{rand(1..100)}, #{rand(1..100)}, #{rand(1..100)}, #{rand(1..100)} )
    SQL
    begin
	result_rows = db_object.execute(sql)
    rescue Object => boom
	# just ignore duplicate keys..
    end
    }
}

## # prototyping "ordered select from multiple databases/tables without join"
## class Java_db_statement
##     attr_reader :current_row
##     attr_accessor :db_filename
##     def initialize(rows_param, db_filename_param)
## 	@rows = rows_param
## 	@db_filename = db_filename_param
## 	@current_row_idx = 0
##     end
##     def next
## 	return false if @rows.size <= @current_row_idx
## 	@current_row = @rows[@current_row_idx]
## 	@current_row_idx += 1
## 	return @current_row
##     end
## end
## 
## # read "first" elements from all tables into array
## all_jdbst = []
## db_objects.each_index{|db_object_idx|
##     db_object = db_objects[db_object_idx]
##     db_filename = db_names[db_object_idx]
##     sql = <<-SQL
##       select
##         epoch_s, open, high, low, close, volume, barCount, WAP
##       from
##         historical_data
##       order by
##         epoch_s
##     SQL
##     rows = db_object.execute(sql)
##     jdbst = Java_db_statement.new(rows, db_filename)
## 
##     all_jdbst << jdbst if jdbst.next
## }
## 
## # sort elements by epoch_s
## loop {
##     # sort elements
##     all_jdbst.sort!{|a,b| a.current_row[0] <=> b.current_row[0]}
##     
##     # exit if no more elements to process in all_jdbst array
##     break if all_jdbst.size == 0
##     
##     loop {
##         break if all_jdbst.size == 0
## 
## 	# pop out 0-th element
## 	el_to_process = all_jdbst.shift
## 	
## 	#puts "all_jdbst.inspect: #{all_jdbst.inspect}"
## 	puts "PROCESSING: #{el_to_process.current_row[0]} #{el_to_process.db_filename}"
## 	
## 	break_to_resorting = false
## 	loop {
## 	    break unless el_to_process.next
## 	
## 	    # have more in el_to_process.next
## 	    if all_jdbst.size > 0 &&  el_to_process.current_row[0] <= all_jdbst[0].current_row[0]
## 		# and it is still smaller! process it w/o pushing back to array
## 		puts "PROCESS w/o pushing: #{el_to_process.current_row[0]}     #{el_to_process.db_filename}"
## 	    else
## 		# and it is not smaller, push back to array and resort!
## 		all_jdbst.push(el_to_process)
## 		break_to_resorting = true
## 		break
## 	    end
## 	}
## 	break if break_to_resorting
##     }
## }
##