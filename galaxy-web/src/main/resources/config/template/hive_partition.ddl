use bi;
CREATE ${tableType} TABLE ${tableName}(
${columnInfo}
) PARTITIONED BY (
    ${partitionColInfo}
) ROW FORMAT SERDE "${serdeInfo}"
	WITH SERDEPROPERTIES (${serdePropInfo})
  STORED AS INPUTFORMAT "${inputFormat}"
            OUTPUTFORMAT "${outputFormat}"
  ${locationDdl};