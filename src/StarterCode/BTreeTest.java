package StarterCode;

import java.io.Serializable;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

public class BTreeTest implements Serializable {
	public static void main(String[] args) {
//		IntegerBTree tree = new IntegerBTree();
//
//		tree.insert(10);
//		tree.insert(48);
//		tree.insert(23);
//		tree.insert(33);
//		tree.insert(12);
//
//		tree.insert(50);
//
//		tree.insert(15);
//		tree.insert(18);
//		tree.insert(20);
//		tree.insert(21);
//		tree.insert(31);
//		tree.insert(45);
//		tree.insert(47);
//		tree.insert(52);
//
//		tree.insert(30);
//
//		tree.insert(19);
//		tree.insert(22);
//
//		tree.insert(11);
//		tree.insert(13);
//		tree.insert(16);
//		tree.insert(17);
//
//		tree.insert(1);
//		tree.insert(2);
//		tree.insert(3);
//		tree.insert(4);
//		tree.insert(5);
//		tree.insert(6);
//		tree.insert(7);
//		tree.insert(8);
//		tree.insert(9);
		
		
		BTree b = new BTree();
		Vector<String> v = new Vector();
		v.add("page1");
		v.add("page2");
		Vector<String> v1 = new Vector();
		v1.add("page3");
		v1.add("page4");
		Vector<String> v2 = new Vector();
		v2.add("page5");
		v2.add("page6");
		Vector<String> v4 = new Vector();
		v4.add("page12");
		v4.add("page41");
		Vector<String> v5 = new Vector();
		v5.add("page122");
		v5.add("page211");
		Vector<String> v6 = new Vector();
		v6.add("page3121");
		v6.add("page4123");
		b.insert(13, v);
		b.insert(14, v1);
		b.insert(18, v2);
		b.insert(11, v4);
		b.insert(8, v5);
		b.insert(20, v6);
		Vector x = (Vector) b.searchGreaterThan(null);
		System.out.println(x);
		
//		Hashtable<String,Object> h1 = new Hashtable();
//		h1.put("name", "Ahmed");
//		h1.put("age", 12);
//		
//		Hashtable<String,Object> h2 = new Hashtable();
//		h2.put("name", "Youssef");
//		h2.put("age", 13);
//		
//		Hashtable<String,Object> h3 = new Hashtable();
//		h3.put("name", "Mohamed");
//		h3.put("age", 10);
//		
//		Hashtable<String,Object> h4 = new Hashtable();
//		h4.put("age", 13);
//		Vector<Tuple> t = new Vector();
//		Tuple t1 = new Tuple(h1,"age");
//		Tuple t2 = new Tuple(h2,"age");
//		Tuple t3 = new Tuple(h3,"age");
//		Tuple t4 = new Tuple(h4,"age");
//		
//		t.add(t1);
//		t.add(t2);
//		t.add(t3);
//		Collections.sort(t);
//		int index = Collections.binarySearch(t, t4);
//		System.out.println(t.get(index));
		
		//System.out.println(v2.get(1));
//
//		tree.print();
		//BTree tree = new BTree();
        //b.print();
//		b.delete(1.8);
//		System.out.println(b.search(3.1));
		// DBBTreeIterator iterator = new DBBTreeIterator(tree);
		// iterator.print();
    }
}

class IntegerBTree extends BTree<Integer, Integer> {
	public void insert(int key) {
		this.insert(key, key);
	}

	public void remove(int key) {
		this.delete(key);
	}
}