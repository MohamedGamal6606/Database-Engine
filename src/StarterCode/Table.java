package StarterCode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


public class Table implements Serializable,Iterator{
	String tableName;
	Vector<String> pageFiles = new Vector<String>();
    String Ckey;
    int noOfPages;
    Vector<Comparable> clusteringKeys = new Vector();
    //IndexName,BplusTree
	Hashtable<String, BTree> index = new Hashtable<String, BTree>();
   
	public Table(){
    	
    }
    
    public Table (String tableName,String Ckey){
    	this.tableName = tableName;
    	this.Ckey = Ckey;
    }
	
    
    public String toString()
    {
    	String res = "";
    	
    	for(int i =0 ; i < pageFiles.size() ; i++)
    	{
    		Page p = Page.loadPage(pageFiles.get(i));
    		res += p.toString() + "\n" +"END PAGE"+ "\n";
    	}
    	return res;
    	
    }
    
    public void insertToTable1(Hashtable<String,Object> htblColNameValue) throws Exception
    {
    	if(!checkAttributes(htblColNameValue))
    	{
    		throw new DBAppException("Wrong attributes");
    	}
    	
    	boolean inserted = false;
    	Tuple t = new Tuple(htblColNameValue,Ckey);
    	Object searchKey = null;
    	boolean found = false;
    	Vector<String> pageNames = new Vector();
    	
    	if(!this.index.isEmpty())
		{
    		BufferedReader br = new BufferedReader(new FileReader(DBApp.filePath));
			String line = br.readLine();
			while(line != null)
			{
				String[] sp = line.split(",");
				if(sp[0].equals(tableName))
				{
					// INDEX
					if(sp[5].equals("B+tree"))
					{
						//CKEY
						if(sp[3].equals("True")) {
							if (!clusteringKeys.contains(htblColNameValue
									.get(Ckey))) {
								BTree b = index.get(sp[4]);
								searchKey = getSearchKey(htblColNameValue);
								if (searchKey != null) {
									pageNames = (Vector<String>) b
											.search((Comparable) searchKey);
									if (pageNames != null) {
										found = true;
									}
								}
								break;
							} else {
								throw new DBAppException(
										"Clustering Key is REPEATED!");
							}
						}
						//NOT CKEY but INDEX
						else
						{
							line = br.readLine();
						}
						
					}
					//NOT INDEX
					else
					{
						line = br.readLine();
					}
				}
				//NOT TABLE NAME
				else
				{
					line = br.readLine();
				}
			}
			br.close();
			//IF PAGE IS FOUND USING INDEX
			if(found)
			{
				Page p = Page.loadPage(pageNames.get(0));
				if(!inserted)
				{
				p.insertToPage(t, this);
				if(!pageFiles.contains(p.name))
	    		{
	    		pageFiles.add(p.name);
	    		}
				inserted = true;
				clusteringKeys.add((Comparable) htblColNameValue.get(Ckey));
				Collections.sort(clusteringKeys);
				}
			}	
		}
    	//IF THERE IS NO INDEX ON CKEY
    	if(!inserted)
    	{
    	Page p = binarySearch(htblColNameValue, pageFiles, htblColNameValue.get(Ckey));
    	
    	if(p != null)
    	{
    		p.insertToPage(t, this);
    		if(!pageFiles.contains(p.name))
    		{
    		pageFiles.add(p.name);
    		}
    		clusteringKeys.add((Comparable) htblColNameValue.get(Ckey));
			Collections.sort(clusteringKeys);
    	}
    	else
    	{
    		p = new Page(tableName + noOfPages);
    		noOfPages++;
    		p.insertToPage(t, this);
    		if(!pageFiles.contains(p.name))
    		{
    		pageFiles.add(p.name);
    		}
    		clusteringKeys.add((Comparable) htblColNameValue.get(Ckey));
			Collections.sort(clusteringKeys);
    	}
    	
    	}
    	updateIndices();
    }
    
    
    
    private Object getSearchKey(Hashtable<String, Object> htblColNameValue) {
        // TODO Auto-generated method stub
        
        Comparable comp = (Comparable) htblColNameValue.get(Ckey);
        
        if(clusteringKeys==null|| clusteringKeys.size()==0){
            return null;
        }
        for (int i = clusteringKeys.size()-1; i >= 0; i--) {
            if(comp.compareTo(clusteringKeys.get(i))>0){
                return clusteringKeys.get(i);
            }
            
        }
        
        
        
        return null;
    }

	// Insert to Table
    public void insertToTable(Hashtable<String,Object> htblColNameValue) throws Exception{
    	//check if the data types are similar
    	//sort
    	//otherwise throw exceptions so the code doesn't crash
    	if(!checkAttributes(htblColNameValue))
    	{
    		throw new DBAppException("Wrong attributes");
    	}
    	
    	
    	boolean inserted = false;
    	Tuple t = new Tuple(htblColNameValue,Ckey);
    	Object searchKey = null;
    	if(!this.index.isEmpty())
		{
			
			BufferedReader br = new BufferedReader(new FileReader(DBApp.filePath));
			String line = br.readLine();
			while (line != null) 
			{
				String[] sp = line.split(",");
				if(sp[0].equals(this.tableName))
				{
					if(sp[5].equals("B+tree") && sp[3].equals("True"))
					{
							//Fetch B+Tree
							BTree b = index.get(sp[4]);
							
							for(int i = clusteringKeys.size() -1 ; i >= 0 ; i--)
							{
								if(htblColNameValue.get(Ckey)instanceof Double)
								{
								if(Double.compare((double)clusteringKeys.get(i), (double)htblColNameValue.get(Ckey)) < 0)
									searchKey = clusteringKeys.get(i);
								}
								else
								{
									int cmp = ((String) clusteringKeys.get(i)).compareTo((String) htblColNameValue.get(Ckey));
								if(cmp < 0)
									searchKey = clusteringKeys.get(i);
									
								}		
							}
								
								
								
							//Fetch page vector
					        Vector<String> pageNames =  (Vector) b.search(((Comparable)searchKey)) ;
					        
					        //If vector is not empty means duplicates
					        if(pageNames != null)
					        {
					        	//Get page from page vector to insert into using binary search
					        	Page p = Page.loadPage(pageNames.get(0));
					        	
					        	if(!inserted)
					        	{
					        	if(p != null)
					        	{
					        	p.insertToPage(t, this);
					        	inserted = true;
					        	pageFiles.add(p.name);
					        	clusteringKeys.add((Comparable) htblColNameValue.get(Ckey));
					        	Collections.sort(clusteringKeys);
					        	}
					        	else
					        	{
					        		p = new Page(tableName + noOfPages);
					        		noOfPages++;
					        		p.insertToPage(t, this);
					        		inserted = true;
					        		pageFiles.add(p.name);
					        		clusteringKeys.add((Comparable) htblColNameValue.get(Ckey));
						        	Collections.sort(clusteringKeys);
					        	}
					        	}
					        	pageNames = searchDuplicates(t,this);
					        	
					        }
					        
					        //else first time to insert value means make new vector
					        else
					        {
					        	Page p = binarySearch(htblColNameValue, pageFiles,htblColNameValue.get(Ckey));
					        	if(!inserted)
					        	{
					        	if(p != null)
					        	{
					        	p.insertToPage(t, this);
					        	inserted = true;
					        	pageFiles.add(p.name);
					        	clusteringKeys.add((Comparable) htblColNameValue.get(Ckey));
					        	Collections.sort(clusteringKeys);
					        	}
					        	else
					        	{
					        		p = new Page(tableName + noOfPages);
					        		noOfPages++;
					        		p.insertToPage(t, this);
					        		inserted = true;
					        		pageFiles.add(p.name);
					        		clusteringKeys.add((Comparable) htblColNameValue.get(Ckey));
						        	Collections.sort(clusteringKeys);
					        	}
					        	}
					        	Vector<String> v = new Vector();
					        	v = searchDuplicates(t, this);
					        	b.insert((Comparable)htblColNameValue.get(sp[1]), v);
					        }
					}
					
				        
						
						
					}
				line = br.readLine();
			}
			br.close();
		}
    	else
    	{	
    		if(pageFiles.size() == 1)
    		{
    			Page p = Page.loadPage(pageFiles.get(0));
    			if(p.tuples.size() != p.n)
    			{
    			p.insertToPage(t, this);
            	inserted = true;
            	return;
    			}
    		
    		}
    		Page p = binarySearch(htblColNameValue,pageFiles,htblColNameValue.get(Ckey));
    		
    		if(p != null)
        	{
        	p.insertToPage(t, this);
        	inserted = true;
        	
        	}
        	else
        	{
        		p = new Page(tableName + noOfPages);
        		noOfPages++;
        		p.insertToPage(t, this);
        		inserted = true;
        		pageFiles.add(p.name);
        	}
    		
    	}

    	
    }


	

	private boolean checkAttributes(Hashtable<String, Object> htblColNameValue) throws IOException {
		// TODO Auto-generated method stub
		
		Enumeration<String> setKey =  htblColNameValue.keys();
		while(setKey.hasMoreElements())
		{
			String key = setKey.nextElement();
		BufferedReader br = new BufferedReader(new FileReader(DBApp.filePath));
		String line = br.readLine();
		boolean found = false;
		
		while (line != null) {
			String[] sp = line.split(",");
			if(sp[0].equals(this.tableName))
			{
				
			if(sp[1].equals(key))	
				
			{	
				
				//compare the type of the value(getClass.getName) to the type of the column(sp[2])
				Object o =(Object)htblColNameValue.get(key);
				String s = o.getClass().getName().toLowerCase();
				if(sp[2].toLowerCase().compareTo(s) == 0)
				{
					
					found = true;
					line = br.readLine();
					break;
				}
				
				
				
			}
			
			line = br.readLine();
			}
			else
			{
				line = br.readLine();
			}
		}
		if(!found)
		{
			br.close();
			return false;
		}
		br.close();
	}
		
		return true;
	}


	//p = tuple
	//pages = vector of pages
	public Page binarySearch(Hashtable<String,Object> p, Vector<String> pages, Object cKeyValue) {
		// TODO Auto-generated method stub
		
		
		
		
		int lower = 0;
		int upper = pages.size()-1;
		int curIndex;
		while(lower <= upper)
		{
			curIndex = (lower+upper)/2;
			Page pag = Page.loadPage( pages.get(curIndex));
		
			
			pag.minMax();
			
			
			
			if(pag.m.get(0) instanceof String)
			{
				if(((String) cKeyValue).compareTo((String) pag.m.get(0))>=0 && ((String) cKeyValue).compareTo((String)pag.m.get(1)) <= 0)
				{
					return pag;
				}
				if(((String) cKeyValue).compareTo((String)pag.m.get(0)) < 0)	
				{
				upper = curIndex - 1;
				}	
				if(((String) cKeyValue).compareTo((String)pag.m.get(1)) > 0)
				{
				lower = curIndex + 1;
				}
				
			}
			if(pag.m.get(0) instanceof Integer)
			{
				if((((int) cKeyValue) >= ((int) pag.m.get(0))  && ((int) cKeyValue) <= ((int) pag.m.get(1)))
						||(((int)cKeyValue) < ((int) pag.m.get(0)) && curIndex == 0)
						||(((int)cKeyValue) > ((int) pag.m.get(1)) && curIndex == pages.size()-1))
				{
					return pag;
				}
				if(((int)cKeyValue) < ((int) pag.m.get(0)))
				{
				upper = curIndex - 1;
				}	
				if(((int)cKeyValue) > ((int) pag.m.get(1)))
				{
				lower = curIndex + 1;
				}
				
			}
			if(pag.m.get(0) instanceof Double)
			{
				if(((double) cKeyValue) >= ((double) pag.m.get(0))  && ((double) cKeyValue) <= ((double) pag.m.get(1)))
				{
					return pag;
				}
				if(((double) cKeyValue) < ((double) pag.m.get(0)))	
				{
				upper = curIndex - 1;
				}	
				if(((double) cKeyValue) > ((double) pag.m.get(1)))
				{
				lower = curIndex + 1;
				}
				
			}
		
		
		}
		
		if(pages.size() == 1)
		{
			Page pag = Page.loadPage( pages.get(0));
			if(pag.tuples.size() != pag.n)
			return pag;
		}
		
		return null;
	}


	public void updateTableValues(String strClusteringKeyValue,
			Hashtable<String, Object> htblColNameValue) throws Exception {
		// Lookup using primary key (strClusteringKeyValue)
		
		
		if(!checkAttributes(htblColNameValue))
    	{
    		throw new DBAppException("Wrong attributes");
    	}
		
		BufferedReader br = new BufferedReader(new FileReader(DBApp.filePath));
		String line = br.readLine();
		Page p = new Page(tableName + noOfPages);
		noOfPages++;
		Tuple t1 = new Tuple();
	
		while (line != null) 
		{
			String[] sp = line.split(",");
			if(sp[0].equals(tableName))
			{
			if(sp[3].toLowerCase().equals("true"))
			{
				if(sp[2].toLowerCase().equals("java.lang.integer"))
				{
					int value = Integer.parseInt(strClusteringKeyValue);
					
					if(sp[5].equals("B+tree"))
					{
						BTree b = index.get(sp[4]);
						Vector<String> pages = (Vector<String>) b.search(value);
						p = Page.loadPage(pages.get(0));
					}
					else
					{
						p = binarySearch(htblColNameValue, pageFiles, value);
						
					}
					
					Hashtable<String,Object> h1 = new Hashtable();
					h1.put(sp[1], value);
					 t1 = new Tuple(h1,Ckey);
				}
				if(sp[2].toLowerCase().equals("java.lang.double"))
				{
					double value = Double.parseDouble(strClusteringKeyValue);
					
					if(sp[5].equals("B+tree"))
					{
						BTree b = index.get(sp[4]);
						Vector<String> pages = (Vector<String>) b.search(value);
						p = Page.loadPage(pages.get(0));
					}
					else
					{
						p = binarySearch(htblColNameValue, pageFiles, value);
					}
					
					Hashtable<String,Object> h1 = new Hashtable();
					h1.put(sp[1], value);
					 t1 = new Tuple(h1,Ckey);
				}
				if(sp[2].toLowerCase().equals("java.lang.string"))
				{
					String value = strClusteringKeyValue;
					
					if(sp[5].equals("B+tree"))
					{
						BTree b = index.get(sp[4]);
						Vector<String> pages = (Vector<String>) b.search(value);
						p = Page.loadPage(pages.get(0));
					}
					else
					{
						p = binarySearch(htblColNameValue, pageFiles, value);
					}
					
					Hashtable<String,Object> h1 = new Hashtable();
					h1.put(sp[1], value);
					t1 = new Tuple(h1,Ckey);
				}
				
				if(p == null)
					throw new DBAppException("Clustering Key Values does not EXIST!");
				
				
			}
			
			
			line = br.readLine();
		}
			else
			{
				line = br.readLine();
			}
		}
		int index = Collections.binarySearch(p.tuples, t1);
		if(index >= 0)
		{
		Tuple t = p.tuples.get(index);
		for(String key : htblColNameValue.keySet())	
		{
			t.row.remove(key);
			t.row.put(key,htblColNameValue.get(key));
		}
		p.savePage(p.name);
		}
			
		
		
		br.close();
	}

	public void deleteValuesFromTable1(String strTableName,Hashtable<String, Object> htblColNameValue) throws Exception {
		// TODO Auto-generated method stub
		
		//if we were passed an empty hashtable we delete the all rows
		if(htblColNameValue == null || htblColNameValue.size() == 0){
			pageFiles.clear();
			noOfPages = 0;
			index.clear();
			System.out.println("Deleted Successfully");
			return;
		}
		
		boolean deleted = false;
		
		if(!index.isEmpty())
		{
			BufferedReader br = new BufferedReader(new FileReader(DBApp.filePath));
			String line = br.readLine();
			while(line != null)
			{
				String[] sp = line.split(",");
				if(sp[0].equals(tableName))
				{
					//IF THE COLUMN IS TO BE DELETED
					if(htblColNameValue.containsKey(sp[1]))
					{
						//CHECK IF IT HAS AN INDEX
						if(sp[5].equals("B+tree"))
						{
							BTree b = index.get(sp[4]);
							Vector<String> pageNames = (Vector<String>) b.search((Comparable) htblColNameValue.get(sp[1]));
							if(pageNames != null)
							{
								this.deleteFromTables(pageNames,htblColNameValue);
								deleted = true;
								updateIndices();
							}
							else
							{
								line = br.readLine();
							}
						}
						else
						{
							line = br.readLine();
						}
					}
					else
					{
						line = br.readLine();
					}
				}
				else
				{
					line = br.readLine();
				}	
				
			}
			br.close();
			
		}
		
		if(!deleted)
		{
			Tuple t = new Tuple(htblColNameValue,Ckey);
			for(int i = 0; i < pageFiles.size() ; i++)
			{
				Page p = Page.loadPage(pageFiles.get(i));
				for(int j = 0; j < p.tuples.size();j++ )
				{
					int count = 0;
					for(String key : htblColNameValue.keySet())
					{
					if(p.tuples.get(j).compareTo(t,key) == 0)
						count++;
					}
					if(count == htblColNameValue.size())
						p.tuples.remove(j);
				}
				p.savePage(p.name);
				if(p.tuples.isEmpty())
				{
					pageFiles.remove(i);
				}
				
			}
		}
		updateIndices();
	}
	

	private void deleteFromTables(Vector<String> pageNames,
			Hashtable<String, Object> htblColNameValue) throws Exception {
		// TODO Auto-generated method stub
		
		Tuple t = new Tuple(htblColNameValue,Ckey);
		for(int i = 0; i < pageNames.size() ; i++)
		{
			Page p = Page.loadPage(pageNames.get(i));
			for(int j = 0; j < p.tuples.size();j++ )
			{
				int count = 0;
				for(String key : htblColNameValue.keySet())
				{
				if(p.tuples.get(j).compareTo(t,key) == 0)
					count++;
				}
				if(count == htblColNameValue.size())
					p.tuples.remove(j);
			}
			p.savePage(p.name);
			if(p.tuples.isEmpty())
			{
				pageFiles.remove(i);
			}
		}
		
	}

	public void deleteValuesFromTable(String strTableName,Hashtable<String, Object> htblColNameValue) throws Exception {
		// TODO Auto-generated method stub
		
		//if we were passed an empty hashtable we delete the all rows
		if(htblColNameValue == null || htblColNameValue.size() == 0){
			pageFiles.clear();
			noOfPages = 0;
			index.clear();
			System.out.println("Deleted Successfully");
			return;
		}
		
		
		BufferedReader br = new BufferedReader(new FileReader(DBApp.filePath));
		String line = br.readLine();
		Page p = new Page();
		boolean deleted = false;
		
		while(line!=null){
			String[] sp = line.split(",");
			if(sp[0].equals(tableName)){
				if(htblColNameValue.containsKey(sp[1])){
					if(sp[5].equals("B+tree")){
						BTree b = index.get(sp[4]);
						Vector<String> pageNames = (Vector) b.search((Comparable) htblColNameValue.get(sp[1]));
						if(pageNames !=null){
								///loop through pageNames and delete occurrences
								for (int i = 0; i < pageNames.size(); i++) {
									p = Page.loadPage(pageNames.get(i));
									Tuple t = new Tuple(htblColNameValue,Ckey);
									for (int j = 0; j < p.tuples.size(); j++) {
										//loop through tuples
										int count = 0;
										Enumeration<String> keys = htblColNameValue.keys();
										
										while(keys.hasMoreElements()){
											// Retrieve the first key
											String firstKey = keys.nextElement();
												if(p.tuples.get(j).compareTo(t,firstKey)==0){
													
													count++;
												}
											
										}
										if(count == htblColNameValue.size() ){
											p.tuples.remove(j);
											 deleted = true;
										}
									}
									
								}

							//Go through the pages and check other attributes
						}
					}
					else
					{
						 line = br.readLine();
					}
				}
				else
				{
					 line = br.readLine();
				}
			}
			else
			{
				line = br.readLine();
			}	
		}
		
		br.close();
		p.savePage(p.name);
		//No index
		if(!deleted)
		{
			Vector<String> pageNames = pageFiles;
		
		
		for (int i = 0; i < pageNames.size(); i++) {
			p = Page.loadPage(pageNames.get(i));
			Tuple t = new Tuple(htblColNameValue,Ckey);
			for (int j = 0; j < p.tuples.size(); j++) {
				//loop through tuples
				int count = 0;
				Enumeration<String> keys = htblColNameValue.keys();
				
				while(keys.hasMoreElements()){
					// Retrieve the first key
					String firstKey = keys.nextElement();
						if(p.tuples.get(j).compareTo(t,firstKey)==0){
							
							count++;
						}
					
				}
				if(count == htblColNameValue.size() ){
					p.tuples.remove(j);
					if(p.tuples.isEmpty())
					{
						int index = pageFiles.indexOf(p.name);
						if(index != 0)
						{
						Page previousPage = Page.loadPage(pageFiles.get(index - 1));
						previousPage.nextPage = p.nextPage;
						previousPage.savePage(previousPage.name);
						}
						pageFiles.remove(p.name);
					}
					else
					{
						p.savePage(p.name);
					}
				}
		
			}
			}
		}
		
		updateIndices();
		
		
		
	}


	private void updateIndices() throws Exception {
		// TODO Auto-generated method stub
		
		
		Hashtable<String, BTree> Temp = new Hashtable<String, BTree>();
		//loop through indices
		//clear indices
		//fill indices we need column name
		//need to check the previous index key
		for (String key: index.keySet()) {
			//get column name
			
			String res = getColumnName(key);
			
			BTree b = new BTree();
			fill(b,res);
			Temp.put(key, b);

		}
		
		index.clear();
		index = Temp;
		
		
	}

	
	public String getColumnName(String indexName) throws Exception{
		String res = "";
		
		BufferedReader br = new BufferedReader(new FileReader(DBApp.filePath));
		String line = br.readLine();

		while(line!=null){
			String[] sp = line.split(",");
			if(sp[0].equals(tableName))
			{
				if(sp[4].equals(indexName))
				{
					res = sp[1];
					break;
				}
				
				
				
				
			}
			line = br.readLine();
			}
		
		br.close();
		
		
		return res;
	}
	
	
	public int getNoOfPages() {
		return noOfPages;
	}


	public void setNoOfPages(int noOfPages) {
		this.noOfPages = noOfPages;
	}
	
	
	//Will search for all the pages where tuple t occurs
	//CAN BE OPTIMISED
	public Vector<String> searchDuplicates(Tuple t, Table table) throws Exception {
			Vector<String> res = new Vector<String>();	
			
			for (int i = 0; i < pageFiles.size(); i++) {
				Page p = Page.loadPage(pageFiles.get(i));
				
				for (int j = 0; j < p.tuples.size(); j++) {
					
					if(p.tuples.get(j).compareTo(t)==0){
						res.add(pageFiles.get(j));
					}
					
					
				}
				p.savePage(pageFiles.get(i));
				
				
			}

				
		return res;
				
				
	}


	public void fill(BTree b, String strColName) throws Exception 
	{
		// TODO Auto-generated method stub
		
		//Loop on all pages
		for(int i = 0 ; i < pageFiles.size() ; i++)
		{
			
			//Load page since page is serialized
			Page p = Page.loadPage(pageFiles.get(i));
			
			//Loop on all tuples
			for(int j = 0 ; j < p.tuples.size();j++)
			{
				//Get attribute that is indexed from Tuple
				Object value = p.tuples.get(j).row.get(strColName);
				Vector<String> pageNames;
				int returnIntegerValue;
				double returnDoubleValue;
				String returnStringValue;
				
				//Check instance of Object
				if(value instanceof Integer)
				{
					 returnIntegerValue = (int) value;
					 pageNames = (Vector) b.search(returnIntegerValue);
					//If vector is null means that this is the fist time to insert value
					 if(pageNames == null)
						{
						 	pageNames = new Vector();
							pageNames.add(p.name);
							b.insert(returnIntegerValue, pageNames);
						}
					 //Else the value is duplicated then insert current page name to the vector
					 else
						{
							pageNames.add(p.name);
						}
					 
				}
				if(value instanceof Double)
				{
					returnDoubleValue = (double) value;
					pageNames = (Vector) b.search((double)value);
					if(pageNames == null)
					{
						pageNames = new Vector();
						pageNames.add(p.name);
						b.insert(returnDoubleValue, pageNames);
					}
					else
					{
						pageNames.add(p.name);
					}
				}
				if(value instanceof String)
				{
					returnStringValue = (String) value;
					pageNames = (Vector) b.search((String)value);
					if(pageNames == null)
					{
						pageNames.add(p.name);
						b.insert(returnStringValue, pageNames);
					}
					else
					{
						pageNames.add(p.name);
					}
				
				}
				
				
			}
			p.savePage(p.name);
    
		}
    
	}

	
	
	

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Object next() {
		// TODO Auto-generated method stub
		return null;
	}



	
}
