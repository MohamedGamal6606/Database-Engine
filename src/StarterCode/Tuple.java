package StarterCode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

public class Tuple implements Serializable, Comparable<Tuple> {
	Hashtable<String,Object> row = new Hashtable();
	String CKey;
	
	public String getCKey() {
		return CKey;
	}

	public Tuple(Hashtable<String,Object> row,String Ckey){
		this.row=row;
		this.CKey = Ckey;
	}

	public Tuple(){
		
	}
	
	
	public String toString(){
		String res = "";
		for(String key : row.keySet()) {
			res += row.get(key)+ ", ";
    	}
		return res;
	  }

	
	
	public static void main(String[]args)
	{
		Hashtable<String,Object> h1 = new Hashtable();
		h1.put("primary", "b");
		
		Hashtable<String,Object> h2 = new Hashtable();
		h2.put("primary", "a");
		Tuple t = new Tuple(h1,"primary");
		Tuple t2 = new Tuple(h2,"primary");
		
		System.out.print(t.compareTo(t2));
	}

	@Override
	public int compareTo(Tuple t) {
		if(this.row.get(CKey) instanceof String)
		{
		return (""+this.row.get(CKey)).compareTo(""+(t).row.get(CKey));
		}
		if(this.row.get(CKey) instanceof Double)
		{
		if(((double)this.row.get(CKey))>((double)(t).row.get(CKey)))
			return 1;
		else
			return -1;
		}
		return ((int)this.row.get(CKey)) - ((int)(t).row.get(CKey));
		
		
	}
	public int compareTo(Tuple t,String key) {
		if(this.row.get(key) instanceof String)
		{
		return (""+this.row.get(key)).compareTo(""+(t).row.get(key));
		}
		if(this.row.get(key) instanceof Double)
		{
		return Double.compare((Double)this.row.get(key), (Double)t.row.get(key));
		}
		return ((int)this.row.get(key)) - ((int)(t).row.get(key));
		
		
	}

	

	
	
	
	
}

	



