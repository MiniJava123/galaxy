use bi;
drop table if exists ${tableName};
CREATE TABLE ${tableName}(
${columnInfo}
) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\005' STORED AS INPUTFORMAT "com.hadoop.mapred.DeprecatedLzoTextInputFormat" OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat";

