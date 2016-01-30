use bi;
CREATE ${tableType} TABLE ${tableName}(
${columnInfo}
) ROW FORMAT SERDE "${serdeInfo}"
	WITH SERDEPROPERTIES (${serdePropInfo})
  STORED AS INPUTFORMAT "${inputFormat}"
            OUTPUTFORMAT "${outputFormat}"
  ${locationDdl};