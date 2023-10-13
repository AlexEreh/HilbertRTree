package com.alexereh;

import com.alexereh.hilbert.HPRTree;
import com.alexereh.hilbert.Item;
import com.alexereh.hilbert.MBR;

public class Main {
	public static void main(String[] args) {
		HPRTree<String> tree = new HPRTree<>();
		tree.insert(new MBR(1.0, 5.0, 1.0, 5.0), "1aaa");
		tree.insert(new MBR(2.0, 9.0, 4.0, 9.0), "2aaa");
		tree.insert(new MBR(7.0, 5.0, 9.0, 5.0), "3aaa");
		for (Item<String> integerItem : tree) {
			System.out.println(integerItem);
		}
		tree.build();
		var test = new MBR(1.0, 6.0, 3.0, 7.0);
		System.out.println(tree.query(test));


	}
}