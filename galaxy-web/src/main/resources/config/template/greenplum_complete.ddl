CREATE TABLE ${tableName}(
${columnInfo}
) WITH (APPENDONLY=true, COMPRESSLEVEL=6, ORIENTATION=column, COMPRESSTYPE=zlib, OIDS=false) DISTRIBUTED RANDOMLY;

