package StarterCode;
import java.util.*;
import java.io.*;
import java.util.Comparator;
import java.util.Collections;
public class Page implements java.io.Serializable {
	static int n ;
	String  name;
	Vector<Tuple> tuples = new Vector<>();
	Page nextPage;
	Vector<Object> m = new Vector();
	
	
	public Page()
	{
		
	}
	
	public Page(String name){
		this.name = name;
	}
	
	public static void setN(int n) {
		Page.n = n;
	}
	//
	
	public boolean isFull() {
        return tuples.size() == n + 1;
    }
	
	public  void insertToPage(Tuple col,Table table) throws Exception {
		// Check if Page is full
		tuples.add(col);
		Collections.sort(tuples);
		if(this.isFull())
		{
			
			Tuple t = tuples.remove(n);
			if(nextPage == null)
			{
				int i = table.getNoOfPages();
				table.setNoOfPages(i+1);
				Page p = new Page(table.tableName + i);
				this.nextPage = p;
				 p.insertToPage(t, table);
				 table.pageFiles.add(p.name);
			}
			else
				nextPage.insertToPage(t, table);	
		}
		
		
		
        savePage(name);
        
       
    }
	
	public void minMax()
	{
		m.add(tuples.get(0).row.get(tuples.get(0).CKey));
		m.add(tuples.get(tuples.size()-1).row.get(tuples.get(0).CKey));
	}
	

	
	public String toString(){
		String res = "";
		res += "Page: " + name + "\n"; 
		for (int i = 0; i < tuples.size(); i++)
        {
            res += "Tuple no. "+ i + ": "+ tuples.get(i).toString() + "\n";
        }
		res += "Page End!!!!!";
		return res;
	  
	  }
	
	
	
	public static Page loadPage(String name){
		  //try and catch
		  try{
			  FileInputStream fileIn = new FileInputStream(name + ".ser");
		      ObjectInputStream in = new ObjectInputStream(fileIn);
		      Page  res = (Page) in.readObject();
		      in.close();
		      fileIn.close();
		      return res;
		  }
		  catch(Exception e){
			  e.printStackTrace();
		  }
		return null;
	      
	  }
		
	  	public  void savePage(String name)throws Exception{
		  FileOutputStream fileOut = new FileOutputStream(name+".ser");
	      ObjectOutputStream out = new ObjectOutputStream(fileOut);
	      out.writeObject(this);
	      out.close();
	      fileOut.close();

	  }
	  	
	    public static void readConfig(String path) throws IOException{
			
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while(line!=null){
				
				String[] sp = line.split(" ");
				
				n = Integer.parseInt(sp[2]);
				
				line = br.readLine();
				
			}
			br.close();
		}
	  	

	  	
//	  	public static void main(String[] args)
//	  	{
//	  	Hashtable<String,Object> h = new Hashtable();
//	  	h.put("id", 1);
//	  	h.put("name", "ahmed");
//	  	
//	  	Hashtable<String,Object> h2 = new Hashtable();
//	  	h2.put("id", 0);
//	  	h2.put("name", "ahmed");
//	  	Tuple t = new Tuple(h,"id");
//	  	Tuple t2 = new Tuple(h2,"id");
////	  	
////	  	Table table = new Table("Student","id");
////	  	
//	  	Vector<Tuple> ta = new Vector<>();
////	  	
////	  	
////	  	
//	  	ta.add(t);
//	  	ta.add(t2);
//	  	 
//	  		
//	  	System.out.println("BEFORE:");
//	  	
//	  	for(int i = 0 ; i < ta.size();i++)
//	  	{
//	  		System.out.println(ta.get(i).toString()+ "\n");
//	  	}
//	  	Collections.sort(ta);
//	  	System.out.println("After:");
//	  	for(int i = 0 ; i < ta.size();i++)
//	  	{
//	  		System.out.println(ta.get(i).toString()+ "\n");
//	  	}
//	  	//Comparator<Tuple> comparator = new TupleComparator();
//	  	
//	  		  	
//	  	
//	  	} 	
}
	  	
/*  	
	
class TupleComparator implements Comparator<Tuple>
{
    public int compare(Tuple o1, Tuple o2)
    {
        return o1.compareTo(o2);
    }
}
*/
