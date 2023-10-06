package com.alexereh;

import com.alexereh.hilbert.MBR;
import com.alexereh.hilbert.HPRTree;
import com.alexereh.hilbert.Item;

public class Main {
	public static void main(String[] args) {
		HPRTree<Integer> tree = new HPRTree<>();
		tree.insert(new MBR(1.0, 5.0, 1.0, 5.0), 1);
		tree.insert(new MBR(2.0, 9.0, 4.0, 9.0), 2);
		tree.insert(new MBR(7.0, 5.0, 9.0, 5.0), 3);
		for (Item<Integer> integerItem : tree) {
			System.out.println(integerItem);
		}
		tree.build();
		for (Item<Integer> integerItem : tree) {
			System.out.println(integerItem);
		}
		System.out.println("Hello world!");
	}
}