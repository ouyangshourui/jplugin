package net.jplugin.core.das.route.api;

import java.util.Map;
import java.util.Properties;

import net.jplugin.common.kits.StringKit;
import net.jplugin.core.das.api.DataSourceFactory;
import net.jplugin.core.das.route.impl.TsAlgmManager;

/**
 * <pre>
 * table-num=2
 * 
 * tb-0-name=tb-route
 * tb-0-algm=hash
 * tb-0-splits=2
 * tb-0-key=keycol
 * 
 * tb-1-name=tb-route2
 * tb-1-algm=hash
 * tb-1-splits=4
 * tb-1-key=keycol
 * 
 * data-source-num=2
 * 
 * ds-0-name=ds1
 * ds-0-weight=50
 * 
 * ds-1-name=ds2
 * ds-1-weight=50
 * 
 * </pre>
 * @author Administrator
 *
 */
public class RouterDataSourceConfig {
	private static final Object PROP_DATA_SOURCE_NUM = "data-source-num";
	private static final String DSC_PREFIX = "ds-";
	private static final String DS_NAME = "name";
	private static final String DS_WEIGHT = "weight";
	private static final Object PROP_TABLE_NUM = "table-num";
	private static final String TABLE_ALGM = "algm";
	private static final String TABLE_NAME = "name";
	private static final String TABLE_PREFIX = "tb-";
	private static final String TABLE_SPLITS = "splits";
	private static final String TABLE_KEY = "key";
	private static final String CREATION_SQL = "creation-sql";
	
	public static class DataSourceConfig{
		String dataSourceName;
		int weight;
		public String getDataSrouceCfgName() {
			return dataSourceName;
		}

		public int getWeight() {
			return weight;
		}
	}
	public static class TableConfig{
		String tableName;
		String splitAlgm;
		int splits;
		String keyField;
		String creationSql;
		public String getTableName() {
			return tableName;
		}
		public String getSplitAlgm() {
			return splitAlgm;
		}
		public int getSplits() {
			return splits;
		}
		public String getKeyField() {
			return this.keyField;
		}
		public String getCreationSql() {
			return creationSql;
		}
		
		
	}
	private DataSourceConfig[] dataSourceCfgs;
	private TableConfig[] tableConfigs;
	
	
	
	public DataSourceConfig[] getDataSourceConfig() {
		return dataSourceCfgs;
	}

	public TableConfig[] getTableConfig() {
		return tableConfigs;
	}
	
	public TableConfig findTableConfig(String tableBaseName) {
		for (TableConfig o:this.tableConfigs){
			if (tableBaseName.equalsIgnoreCase(o.getTableName())){
				return o;
			}
		}
		return null;
	}

	String trim(String s){
		if (s==null) return s;
		else return s.trim();
	}
	
	public void valid(){
		for (TableConfig o:this.tableConfigs){
			if (!TsAlgmManager.exists(o.splitAlgm)){
				throw new RuntimeException("router datasource error, algm not exists:"+o.splitAlgm);
			}
		}
		for (DataSourceConfig o:this.dataSourceCfgs){
			DataSourceFactory.getDataSource(o.dataSourceName);
		}
	}
	
	public void fromProperties(Map<String,String> prop){
		String temp = trim((String) prop.get(PROP_DATA_SOURCE_NUM));
		if (StringKit.isNull(temp))
			throw new RuntimeException(PROP_DATA_SOURCE_NUM +" not configed");
		int dataSourceNum = Integer.parseInt(temp);
		
		temp = trim((String) prop.get(PROP_TABLE_NUM));
		if (StringKit.isNull(temp))
			throw new RuntimeException(PROP_TABLE_NUM +" not configed");
		int tableNum = Integer.parseInt(temp);
		
		
		tableConfigs =  new TableConfig[tableNum];
		for (int i=0;i<tableNum;i++){
			String tableName = trim(prop.get(TABLE_PREFIX+i+"-"+TABLE_NAME));
			String algm = trim(prop.get(TABLE_PREFIX+i+"-"+TABLE_ALGM));
			String splits = trim(prop.get(TABLE_PREFIX+i+"-"+TABLE_SPLITS));
			String keyField = trim(prop.get(TABLE_PREFIX+i+"-"+TABLE_KEY));
			String creationSql =  trim(prop.get(TABLE_PREFIX+i+"-"+CREATION_SQL));
			
			if (StringKit.isNull(tableName))
				throw new RuntimeException("router datasource error,"+TABLE_PREFIX+i+"-"+TABLE_NAME+"  not found.");
			if (StringKit.isNull(algm))
				throw new RuntimeException("router datasource error,"+TABLE_PREFIX+i+"-"+TABLE_ALGM+"  not found");
			if (StringKit.isNull(splits))
				splits = "0";
//				throw new RuntimeException("router datasource error,"+TABLE_PREFIX+i+"-"+TABLE_SPLITS+"  not found");
			if (StringKit.isNull(keyField))
				throw new RuntimeException("router datasource error,"+TABLE_PREFIX+i+"-"+TABLE_KEY+"  not found");
			
			TableConfig tc = new TableConfig();
			tc.splitAlgm = algm;
			tc.tableName = tableName;
			tc.splits = Integer.parseInt(splits);
			tc.keyField = keyField;
			tc.creationSql = creationSql;
			tableConfigs[i] = tc;
		}
		
		dataSourceCfgs = new DataSourceConfig[dataSourceNum];
		for (int i=0;i<dataSourceNum;i++){
			String name = trim(prop.get(DSC_PREFIX+i+"-"+DS_NAME));
			String snum = trim(prop.get(DSC_PREFIX+i+"-"+DS_WEIGHT));
			if (StringKit.isNull(name))
				throw new RuntimeException("router datasource error,"+DSC_PREFIX+i+"-"+DS_NAME+"  not found.");
			if (StringKit.isNull(snum)){
				if (snum == null) snum="0";
			}
			
			int inum = Integer.parseInt(snum);
			DataSourceConfig dsc = new DataSourceConfig();
			dsc.dataSourceName = name;
			
			dsc.weight = inum;
			dataSourceCfgs[i] = dsc;
		}

//		int sum=0;
//		for (DataSourceConfig o:dataSourceCfgs){
//			sum +=o.weight;
//		}
//		if (sum!=100){
//			throw new RuntimeException("The sum of weight must be 100. now is "+sum);
//		}
	}


}
