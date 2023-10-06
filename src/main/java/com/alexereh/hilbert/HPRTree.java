package com.alexereh.hilbert;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public final class HPRTree<T> implements Iterable<Item<T>> {
	private static final int MBR_SIZE = 4;

	private static final int HILBERT_LEVEL = 12;

	private static final int DEFAULT_NODE_CAPACITY = 16;

	private final List<Item<T>> items = new ArrayList<>();

	private final int nodeCapacity;

	private final MBR totalExtent = new MBR();

	private int[] layerStartIndex;

	private double[] nodeBounds;

	private boolean isBuilt = false;

	/**
	 * Creates a new index with the default node capacity.
	 */
	public HPRTree() {
		this(DEFAULT_NODE_CAPACITY);
	}

	/**
	 * Creates a new index with the given node capacity.
	 *
	 * @param nodeCapacity the node capacity to use
	 */
	public HPRTree(int nodeCapacity) {
		this.nodeCapacity = nodeCapacity;
	}

	private static double[] createBoundsArray(int size) {
		double[] a = new double[4 * size];
		for (int i = 0; i < size; i++) {
			int index = 4 * i;
			a[index] = Double.MAX_VALUE;
			a[index + 1] = Double.MAX_VALUE;
			a[index + 2] = -Double.MAX_VALUE;
			a[index + 3] = -Double.MAX_VALUE;
		}
		return a;
	}

	private static int[] computeLayerIndices(int itemSize, int nodeCapacity) {
		List<Integer> layerIndexList = new ArrayList<>();
		int layerSize = itemSize;
		int index = 0;
		do {
			layerIndexList.add(index);
			layerSize = numNodesToCover(layerSize, nodeCapacity);
			index += MBR_SIZE * layerSize;
		} while (layerSize > 1);
		return toIntArray(layerIndexList);
	}

	private static int numNodesToCover(int nChild, int nodeCapacity) {
		int mult = nChild / nodeCapacity;
		int total = mult * nodeCapacity;
		if (total == nChild) return mult;
		return mult + 1;
	}

	private static int[] toIntArray(List<Integer> list) {
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public int size() {
		return items.size();
	}

	public void insert(MBR itemEnv, T item) {
		if (isBuilt) {
			throw new IllegalStateException("Cannot insert items after tree is built.");
		}
		items.add(new Item<>(itemEnv, item));
		totalExtent.expandToInclude(itemEnv);
	}

	public List<T> query(MBR searchEnv) {
		build();

		if (!totalExtent.intersects(searchEnv))
			return new ArrayList<>();

		ListVisitor<T> visitor = new ListVisitor<>();
		query(searchEnv, visitor);
		return visitor.getItems();
	}

	public void query(MBR searchEnv, ItemVisitor<T> visitor) {
		build();
		if (!totalExtent.intersects(searchEnv))
			return;
		if (layerStartIndex == null) {
			queryItems(0, searchEnv, visitor);
		} else {
			queryTopLayer(searchEnv, visitor);
		}
	}

	private void queryTopLayer(MBR searchEnv, ItemVisitor<T> visitor) {
		int layerIndex = layerStartIndex.length - 2;
		int layerSize = layerSize(layerIndex);
		// query each node in layer
		for (int i = 0; i < layerSize; i += MBR_SIZE) {
			queryNode(layerIndex, i, searchEnv, visitor);
		}
	}

	private void queryNode(int layerIndex, int nodeOffset, MBR searchEnv, ItemVisitor<T> visitor) {
		int layerStart = layerStartIndex[layerIndex];
		int nodeIndex = layerStart + nodeOffset;
		if (!intersects(nodeIndex, searchEnv)) return;
		if (layerIndex == 0) {
			int childNodesOffset = nodeOffset / MBR_SIZE * nodeCapacity;
			queryItems(childNodesOffset, searchEnv, visitor);
		} else {
			int childNodesOffset = nodeOffset * nodeCapacity;
			queryNodeChildren(layerIndex - 1, childNodesOffset, searchEnv, visitor);
		}
	}

	private boolean intersects(int nodeIndex, MBR env) {
		//nodeIntersectsCount++;
		boolean isBeyond = (env.getMaxX() < nodeBounds[nodeIndex])
				|| (env.getMaxY() < nodeBounds[nodeIndex + 1])
				|| (env.getMinX() > nodeBounds[nodeIndex + 2])
				|| (env.getMinY() > nodeBounds[nodeIndex + 3]);
		return !isBeyond;
	}

	private void queryNodeChildren(int layerIndex, int blockOffset,
	                               MBR searchEnv, ItemVisitor<T> visitor) {
		int layerStart = layerStartIndex[layerIndex];
		int layerEnd = layerStartIndex[layerIndex + 1];
		for (int i = 0; i < nodeCapacity; i++) {
			int nodeOffset = blockOffset + MBR_SIZE * i;
			// don't query past layer end
			if (layerStart + nodeOffset >= layerEnd) break;

			queryNode(layerIndex, nodeOffset, searchEnv, visitor);
		}
	}

	private void queryItems(int blockStart, MBR searchEnv, ItemVisitor<T> visitor) {
		for (int i = 0; i < nodeCapacity; i++) {
			int itemIndex = blockStart + i;
			// don't query past end of items
			if (itemIndex >= items.size()) break;

			// visit the item if its MBR intersects search env
			Item<T> item = items.get(itemIndex);
			if (item.getMBR().intersects(searchEnv)) {
				visitor.visitItem(item.getItem());
			}
		}
	}

	private int layerSize(int layerIndex) {
		int layerStart = layerStartIndex[layerIndex];
		int layerEnd = layerStartIndex[layerIndex + 1];
		return layerEnd - layerStart;
	}

	public void build() {
		// skip if already built
		if (isBuilt) return;
		isBuilt = true;
		// don't need to build an empty or very small tree
		if (items.size() <= nodeCapacity) return;

		sortItems();
		//dumpItems(items);

		layerStartIndex = computeLayerIndices(items.size(), nodeCapacity);
		// allocate storage
		int nodeCount = layerStartIndex[layerStartIndex.length - 1] / 4;
		nodeBounds = createBoundsArray(nodeCount);

		// compute tree nodes
		computeLeafNodes(layerStartIndex[1]);
		for (int i = 1; i < layerStartIndex.length - 1; i++) {
			computeLayerNodes(i);
		}
	}

	private void computeLayerNodes(int layerIndex) {
		int layerStart = layerStartIndex[layerIndex];
		int childLayerStart = layerStartIndex[layerIndex - 1];
		int layerSize = layerSize(layerIndex);
		int childLayerEnd = layerStart;
		for (int i = 0; i < layerSize; i += MBR_SIZE) {
			int childStart = childLayerStart + nodeCapacity * i;
			computeNodeBounds(layerStart + i, childStart, childLayerEnd);
		}
	}

	private void computeNodeBounds(int nodeIndex, int blockStart, int nodeMaxIndex) {
		for (int i = 0; i <= nodeCapacity; i++) {
			int index = blockStart + 4 * i;
			if (index >= nodeMaxIndex) break;
			updateNodeBounds(nodeIndex, nodeBounds[index], nodeBounds[index + 1], nodeBounds[index + 2], nodeBounds[index + 3]);
		}
	}

	private void computeLeafNodes(int layerSize) {
		for (int i = 0; i < layerSize; i += MBR_SIZE) {
			computeLeafNodeBounds(i, nodeCapacity * i / 4);
		}
	}

	private void computeLeafNodeBounds(int nodeIndex, int blockStart) {
		for (int i = 0; i <= nodeCapacity; i++) {
			int itemIndex = blockStart + i;
			if (itemIndex >= items.size()) break;
			MBR env = items.get(itemIndex).getMBR();
			updateNodeBounds(nodeIndex, env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY());
		}
	}

	private void updateNodeBounds(int nodeIndex, double minX, double minY, double maxX, double maxY) {
		if (minX < nodeBounds[nodeIndex]) nodeBounds[nodeIndex] = minX;
		if (minY < nodeBounds[nodeIndex + 1]) nodeBounds[nodeIndex + 1] = minY;
		if (maxX > nodeBounds[nodeIndex + 2]) nodeBounds[nodeIndex + 2] = maxX;
		if (maxY > nodeBounds[nodeIndex + 3]) nodeBounds[nodeIndex + 3] = maxY;
	}

	private void sortItems() {
		ItemComparator comp = new ItemComparator(new HilbertEncoder(HILBERT_LEVEL, totalExtent));
		items.sort(comp);
	}

	@NotNull
	@Override
	public Iterator<Item<T>> iterator() {
		var itemsIterator = items.iterator();
		return new Iterator<>() {
			@Override
			public boolean hasNext() {
				return itemsIterator.hasNext();
			}

			@Override
			public Item<T> next() {
				return itemsIterator.next();
			}
		};
	}

	final class ItemComparator implements Comparator<Item<T>> {

		private final HilbertEncoder encoder;

		public ItemComparator(HilbertEncoder encoder) {
			this.encoder = encoder;
		}

		@Override
		public int compare(Item item1, Item item2) {
			int hilbertCode1 = encoder.encode(item1.getMBR());
			int hilbertCode2 = encoder.encode(item2.getMBR());
			return Integer.compare(hilbertCode1, hilbertCode2);
		}
	}

}