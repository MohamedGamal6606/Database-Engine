package StarterCode;


/** * @author Wael Abouelsaadat */ 

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import StarterCode.Table;

public class DBApp {
	
	
	static  String filePath = "C:\\Users\\User\\workspace\\Database-Engine\\src\\StarterCode\\resources\\metadata.csv";
	static Hashtable<String,Table> database = new Hashtable(); 


	public DBApp( ) throws Exception{
		Page.readConfig("C:\\Users\\User\\workspace\\Database-Engine\\src\\StarterCode\\resources\\DBApp.config");
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        String line = br.readLine();
        while (line != null) 
        {
            writer.write(" ");
            line = br.readLine();
        }
        br.close();
	}

	// this does whatever initialization you would like 
	// or leave it empty if there is no code you want to 
	// execute at application startup 
	public void init( ){
		
		
	}
	

	// following method creates one table only
	// strClusteringKeyColumn is the name of the column that will be the primary
	// key and the clustering column as well. The data type of that column will
	// be passed in htblColNameType
	// htblColNameValue will have the column name as key and the data 
	// type as value
	public void createTable(String strTableName, 
							String strClusteringKeyColumn,  
							Hashtable<String,String> htblColNameType) throws DBAppException{		
		Table newTable = new Table(strTableName,strClusteringKeyColumn);
		database.put(strTableName, newTable);
		//create csv file 
		writeToCSV(strTableName,strClusteringKeyColumn,htblColNameType);
		//throw new DBAppException("not implemented yet");
	}
	
	
	
	
	
	
	
	
	public static void writeToCSV(String tableName,String Ckey, Hashtable<String, String> data) {
        try {
            
        	
        	
        	BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,true));
            // Write data to the CSV file
            writeRow(writer,tableName,Ckey, data);


            writer.close();
            System.out.println("Data has been written to " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the CSV file: " + e.getMessage());
        }
    }

    public static void writeRow(BufferedWriter writer,String tableName,String Ckey, Hashtable<String, String>data) throws IOException {
    	
        for(String key: data.keySet()){
            writer.write(tableName);
            writer.write(",");
            writer.write(key);
            writer.write(",");
            writer.write(data.get(key));
            writer.write(",");
            if(key.equals(Ckey)){
                writer.write("True");
                writer.write(",");
            }
            else{
                writer.write("False");
                writer.write(",");
            }
            writer.write("null");
            writer.write(",");
            writer.write("null");
            writer.newLine();
        }
        
    }


	// following method creates a B+tree index 
	public void createIndex(String   strTableName,
							String   strColName,
							String   strIndexName) throws Exception{
		
		Table t = database.get(strTableName);
		//Check if index name is used
		if(t.index.size()>0){
			if(t.index.contains(strIndexName))
				throw new DBAppException("Index Name is already used!!");
		}
		
		BTree b = new BTree();
		//Fill BTree
		t.fill(b,strColName);
		
		//Insert Index in table
		t.index.put(strIndexName, b);
		
		
		//updateCSV method
		updateCSV(strTableName,strColName,strIndexName);
		
		
		//throw new DBAppException("not implemented yet");
	}
	
	
	public static void updateCSV(String tableName,String colName, String indexName) throws IOException {
        try {
            List<String[]> rows = readCSV(filePath);

            boolean updated = false;
            for (String[] row : rows) {
                if (row.length >= 2 && row[1].equals(colName)) { // Check if column name matches
                    row[4] = indexName; // Update index name
                    row[5] = "B+tree"; // Update index type
                    updated = true;
                }
            }

            if (updated) {
                updateRows(rows);
                System.out.println("Index name and index type updated successfully.");
            } else {
                System.out.println("No rows found with column name '" + colName + "'.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }


	
    private static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                rows.add(data);
            }
        }
        return rows;
    }

    private static void updateRows( List<String[]> rows) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }


	// following method inserts one row only. 
	// htblColNameValue must include a value for the primary key
	public void insertIntoTable(String strTableName, 
								Hashtable<String,Object>  htblColNameValue) throws Exception{
	
		Table getTable = database.get(strTableName);
		
		
		getTable.insertToTable1(htblColNameValue);
		//UPDATE INDICES
		
		
	}


	// following method updates one row only
	// htblColNameValue holds the key and new value 
	// htblColNameValue will not include clustering key as column name
	// strClusteringKeyValue is the value to look for to find the row to update.
	public void updateTable(String strTableName, 
							String strClusteringKeyValue,
							Hashtable<String,Object> htblColNameValue   )  throws Exception{
		
		
		Table getTable = database.get(strTableName);
		getTable.updateTableValues(strClusteringKeyValue,htblColNameValue);
		//UPDATE INDICES
		
		
		
	}


	// following method could be used to delete one or more rows.
	// htblColNameValue holds the key and value. This will be used in search 
	// to identify which rows/tuples to delete. 	
	// htblColNameValue enteries are ANDED together
	public void deleteFromTable(String strTableName, 
								Hashtable<String,Object> htblColNameValue) throws Exception{
		
		Table getTable = database.get(strTableName);
		getTable.deleteValuesFromTable1(strTableName,htblColNameValue);
		//UPDATE INDICES
		
		
		
	}

	//arrSQLTerms = an array of SQL Terms where
	//arr[0] is the first query to perform operations and
	//arr[1] is the second query to perform operations and
	//they both consists of an SQL Term that has table name,col name,operator and object value
	//strarrOperators = contains the operators that will be performed between the 
	// 2 queries
	public Iterator selectFromTable(SQLTerm[] arrSQLTerms, 
									String[]  strarrOperators) throws DBAppException, Exception{
		Vector<Tuple> res = new Vector();
		
		checkTableName(arrSQLTerms);
		
		if(strarrOperators.length==0){
			Table t = database.get(arrSQLTerms[0]._strTableName);
			if(arrSQLTerms[0]._strOperator == null){
				
				for (int i = 0; i < strarrOperators.length; i++) {
					Page p = Page.loadPage(t.pageFiles.get(i));
					for (int j = 0; j < p.tuples.size(); j++) {
						res.add(p.tuples.get(j));
					}
				}
				
				
			}
			else{
				res = this.Queries(arrSQLTerms[0]);
			}
			return res.iterator();
			
		}
		Stack<Vector<Tuple>> s = new Stack();
		Stack<String> op = new Stack();
		
		for (int i = 0; i < arrSQLTerms.length-1; i=i+2) {
			int j = 0;
			//check if stack is empty
			//if yes then push both operands and operator(AND/OR/XOR)
			//else check operands if the precedence of the operator is greater
			//than the one in the stack then push
			//else pop and apply operator
			//then push and check again
			
			//WE CANT DO IT SIMULTANOUSLY SINCE WE MIGHT GET A HIGHER PRECEDENCE LATER
			//XOR OR XOR AND
			//SIMUL WILL DO XOR & OR WHILE WE WANT XOR & AND First 
			if(s.isEmpty()&&op.isEmpty()){
				s.push(this.Queries(arrSQLTerms[i]));
				s.push(this.Queries(arrSQLTerms[i+1]));
				op.push(strarrOperators[j]);
			}else{
				if(precedence(strarrOperators[j])>=precedence(op.peek())){
					s.push(this.Queries(arrSQLTerms[i]));
					s.push(this.Queries(arrSQLTerms[i+1]));
					op.push(strarrOperators[j]);
				}
				else{
					Stack<Vector<Tuple>> t1 = new Stack();
					Stack<String> t2 = new Stack();
					t1.push(s.pop());
					t1.push(s.pop());
					t2.push(op.pop());
					s.push(this.Queries(arrSQLTerms[i]));
					i--;
					op.push(strarrOperators[j]);
					s.push(t1.pop());
					s.push(t1.pop());
					op.push(t2.pop());
				}
				
				
			}
			
			
			j++;
		}
			
		while(!op.isEmpty()){
			s.push(logic(s.pop(),s.pop(),op.pop()));
		}
	
		if(s.size()==1){
			res = s.pop();
		}else{
			throw new DBAppException("Wrong");
		}	
			

		
		
		
			//Do the operations from strarrOperators
		return res.iterator();
		
	}

	public static int precedence(String opp){
		int res = 0;
		if(opp.equals("AND")){
			res = 3;
		}
		
		if(opp.equals("OR")){
			res = 2;
		}
		if(opp.equals("XOR")){
			res = 1;
		}
		return res;
	}
	
	
	
	public void checkTableName(SQLTerm[] arrSQLTerms) throws DBAppException {
		if(arrSQLTerms.length>0){
			String name = arrSQLTerms[0]._strTableName;
			for (int i = 0; i < arrSQLTerms.length; i++) {
				if(!arrSQLTerms[i]._strTableName.equals(name)){
					throw new DBAppException("JOINS are not implemented!");
				}
			}
		}
	}

	//where clause
	public Vector<Tuple> Queries(SQLTerm arrSQLTerms) throws Exception{
		
		Vector<Tuple> res = new Vector();
		BufferedReader br = new BufferedReader(new FileReader(DBApp.filePath));
		String line = br.readLine();
		Vector<String> pageNames = new Vector();
		Table t = database.get(arrSQLTerms._strTableName);
		Hashtable<String,Object> temp1 = new Hashtable();
		temp1.put(arrSQLTerms._strColumnName, arrSQLTerms._objValue);
		Tuple temp2 = new Tuple(temp1,t.Ckey);
		boolean flag = false;
		while (line != null) 
		{
			String[] sp = line.split(",");
			if(sp[0].equals(arrSQLTerms._strTableName))
			{
				if(sp[1].equals(arrSQLTerms._strColumnName))
				{
					if(sp[5].equals("B+tree"))
					{
						
						BTree b = database.get(arrSQLTerms._strTableName).index.get(sp[4]);
						pageNames = (Vector<String>) b.search((Comparable) arrSQLTerms._objValue);
						
						if(pageNames != null) {
							for (int i = 0; i < pageNames.size(); i++) {
								Page p = Page.loadPage(pageNames.get(i));
								//loop through tuples
								//bsearch
								for (int j = 0; j < p.tuples.size(); j++) {
									switch (arrSQLTerms._strOperator) {
									case "=":
										if (p.tuples.get(j).compareTo(temp2,
												arrSQLTerms._strColumnName) == 0)
											res.add(p.tuples.get(j));
										break;
									case "!=":
										if (p.tuples.get(j).compareTo(temp2,
												arrSQLTerms._strColumnName) != 0)
											res.add(p.tuples.get(j));
										break;
									case ">=":
										if (p.tuples.get(j).compareTo(temp2,
												arrSQLTerms._strColumnName) >= 0)
											res.add(p.tuples.get(j));
										break;
									case "<=":
										if (p.tuples.get(j).compareTo(temp2,
												arrSQLTerms._strColumnName) <= 0)
											res.add(p.tuples.get(j));
										break;
									case ">":
										if (p.tuples.get(j).compareTo(temp2,
												arrSQLTerms._strColumnName) > 0)
											res.add(p.tuples.get(j));
										break;
									case "<":
										if (p.tuples.get(j).compareTo(temp2,
												arrSQLTerms._strColumnName) < 0)
											res.add(p.tuples.get(j));
										break;
									default:
										throw new DBAppException(
												"Wrong operator");
									}

								}

							}
							flag = true;
						}
						break;
					}
				}
			}
		

		
			line = br.readLine();
		}
		br.close();
		//no index
		if(!flag){
			
			
			//get pages
			for (int i = 0; i < t.pageFiles.size(); i++) {
				Page p = Page.loadPage(t.pageFiles.get(i));
				//loop through tuples
				
				for (int j = 0; j < p.tuples.size(); j++) {
					
					switch(arrSQLTerms._strOperator){
					case "=":if(p.tuples.get(j).compareTo(temp2,arrSQLTerms._strColumnName)==0)
						res.add(p.tuples.get(j));break;
					case "!=":if(p.tuples.get(j).compareTo(temp2,arrSQLTerms._strColumnName)!=0)
						res.add(p.tuples.get(j));break;
					case ">=":if(p.tuples.get(j).compareTo(temp2,arrSQLTerms._strColumnName)>=0)
						res.add(p.tuples.get(j));break;
					case "<=":if(p.tuples.get(j).compareTo(temp2,arrSQLTerms._strColumnName)<=0)
						res.add(p.tuples.get(j));break;
					case ">":if(p.tuples.get(j).compareTo(temp2,arrSQLTerms._strColumnName)>0)
						res.add(p.tuples.get(j));break;
					case "<":if(p.tuples.get(j).compareTo(temp2,arrSQLTerms._strColumnName)<0)
						res.add(p.tuples.get(j));break;
					default: throw new DBAppException("Wrong operator");
					}
					
				}	
				
			}
			
			
			
			
			
		}
		
	
		
		
		return res;
	}
	
	
    public static Vector<Tuple> logic(Vector<Tuple> table1,Vector<Tuple> table2,String strarrOperators) throws Exception{
        Vector<Tuple> res = new Vector();
        
        System.out.println("Table 1");
        for (int i = 0; i < table1.size(); i++) {
            System.out.println(table1.get(i));
        }
        System.out.println("Table 2");
        for (int i = 0; i < table2.size(); i++) {
            System.out.println(table2.get(i));
        }
        System.out.println("end of tables");
        //OR (No duplicates)
        if(strarrOperators.equals("OR")){
            for (int i = 0; i <table1.size() ; i++) {
                Tuple t = table1.get(i);
                if(!isPresent(table2,t)){
                    res.add(t);
                }
            }
            for (int i = 0; i <table2.size() ; i++) {
                Tuple t = table2.get(i);
                if(!isPresent(res,t)){
                    res.add(t);
                }
            }
            
        }
        if(strarrOperators.equals("XOR")){
            for (int i = 0; i <table2.size() ; i++) {
                Tuple t = table2.get(i);
                if(!isPresent(table1,t)){
                    res.add(t);
                }
            }
            for (int i = 0; i <table1.size() ; i++) {
                Tuple t = table1.get(i);
                if(!isPresent(table2,t)){
                    res.add(t);
                }
            }
            
        }
        
        if(strarrOperators.equals("AND")){
            for (int i = 0; i <table1.size() ; i++) {
                Tuple t = table1.get(i);
                if(isPresent(table2,t)){
                    res.add(t);
                }
            }    
        }
        

        return res;
    }
    
public static boolean isPresent(Vector<Tuple> table1,Tuple t){
        
        for (int i = 0; i < table1.size(); i++) {
            if(table1.get(i).compareTo(t)==0){
                return true;
            }
                
        }
        
        return false;
    }
	
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main( String[] args ){
	
	try{
			
			
		
			String strTableName = "Student";
			DBApp	dbApp = new DBApp( );
			
			Hashtable htblColNameType = new Hashtable( );
			htblColNameType.put("id", "java.lang.Integer");
			htblColNameType.put("name", "java.lang.String");
			htblColNameType.put("gpa", "java.lang.Double");
			dbApp.createTable( strTableName, "id", htblColNameType );
			dbApp.createIndex( strTableName, "id", "gpaIndex" );
			
			dbApp.createTable( "Hello", "id", htblColNameType );
			dbApp.createTable( "Ahmed", "id", htblColNameType );
			
			Hashtable htblColNameValue = new Hashtable( );
			htblColNameValue.put("id", new Integer( 5 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa", new Double( 0.95 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", new Integer( 2 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa", new Double( 0.95 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", new Integer( 4 ));
			htblColNameValue.put("name", new String("Dalia Noor" ) );
			htblColNameValue.put("gpa", new Double( 1.25 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", new Integer( 3 ));
			htblColNameValue.put("name", new String("John Noor" ) );
			htblColNameValue.put("gpa", new Double( 1.5 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", new Integer( 1 ));
			htblColNameValue.put("name", new String("Zaky Noor" ) );
			htblColNameValue.put("gpa", new Double( 0.88 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );
			
			
		Hashtable<String,Object> h1 = new Hashtable();
			h1.put("name", "Dalia Noor");
////			h1.put("name", "BATATES");
//////			System.out.println(Page.n);
////			System.out.println("BEFORE DELETE");
////			System.out.println(database.get(strTableName));
////			
////			
////				System.out.println("INDEX");
////				BTree b = database.get(strTableName).index.get("gpaIndex");
////				b.print();
//				dbApp.updateTable(strTableName, "2", h1);
//				dbApp.deleteFromTable(strTableName, h1);
////			
////			System.out.println("AFTER DELETE");
		System.out.println(database.get(strTableName));
//						
//			
////			System.out.println(database.get(strTableName));
			
			
			SQLTerm[] arrSQLTerms;
			arrSQLTerms = new SQLTerm[2];
			arrSQLTerms[0] = new SQLTerm();
			arrSQLTerms[0]._strTableName =  "Student";
			arrSQLTerms[0]._strColumnName=  "name";
			arrSQLTerms[0]._strOperator  =  "=";
			arrSQLTerms[0]._objValue     =  "Dalia Noor";
			arrSQLTerms[1] = new SQLTerm();
			arrSQLTerms[1]._strTableName =  "Student";
			arrSQLTerms[1]._strColumnName=  "gpa";
			arrSQLTerms[1]._strOperator  =  "<";
			arrSQLTerms[1]._objValue     =  new Double( 0.95);

			String[]strarrOperators = new String[1];
			strarrOperators[0] = "XOR";
			// select * from Student where name = "John Noor" or gpa = 1.5;
			
			Iterator resultSet = dbApp.selectFromTable(arrSQLTerms , strarrOperators);
		
			while(resultSet.hasNext())
			{
				System.out.println(resultSet.next());
			}
		}
		catch(Exception exp){
			exp.printStackTrace( );
		}
	}

}