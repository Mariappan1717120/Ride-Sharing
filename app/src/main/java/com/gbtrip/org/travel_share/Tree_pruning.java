package com.gbtrip.org.travel_share;

import java.util.List;
import java.util.Random;


public class Tree_pruning {

	public Tree_nodes pruning(Tree_inst valDataRows, Tree_inst valResultRows,int lValue, int kValue, Tree_nodes root) {

		Tree_nodes prune_tree = new Tree_nodes();
		prune_tree = root;
		Decision_tree_accuaracy prune_acc = new Decision_tree_accuaracy();
		prune_acc.parseInstances(valDataRows, valResultRows, root);
		double bestAcc=prune_acc.getAcc();

		for(int i=1;i<=lValue;i++) {

			Tree_nodes newTree = new Tree_nodes();
			newTree= root;
			Random rand=new Random();
			int m=rand.nextInt(kValue);
			m=(m==0)?1:m;
			int n = countLeafNodes(newTree,0);

			for(int j=1;j<=m;j++) {
				Random rand1=new Random();
				int p=rand1.nextInt(n);
				removeLeaf(newTree,0,p);
			}
			Decision_tree_accuaracy tree_accuracy = new Decision_tree_accuaracy();
			tree_accuracy.parseInstances(valDataRows, valResultRows, newTree);
			double Acc=tree_accuracy.getAcc();
			if(Acc > bestAcc) {
				bestAcc=Acc;
				prune_tree=newTree;
			}
		}
		return prune_tree;
	}
	private int removeLeaf(Tree_nodes newTree, int count, int p) {
		if (newTree.getSubTreeList() == null||newTree.getSubTreeList().isEmpty()){ 
			count++;
			if (count==p)return count;
		}
		if (newTree.getSubTreeList() != null && !newTree.getSubTreeList().isEmpty()) {
			List<Tree_nodes> keyArray = newTree.getSubTreeList();
			for (int y = 0; y < keyArray.size(); y++ ) {
				count = removeLeaf(keyArray.get(y),count,p);
				if (count==p){
					int c= count01new(newTree,0,0);
					newTree.getSubTreeList().remove(y);

					newTree.getSubTreeList().add(y,new Tree_nodes(c,null,null));
				}
			}
		}

		return count;
	}

	private int count01new(Tree_nodes newTree, int count0, int count1) {

		if (newTree.getSubTreeList() == null|| newTree.getSubTreeList().isEmpty())return (count1>=count0)?1:0;
		if (newTree.getSubTreeList() != null && !newTree.getSubTreeList().isEmpty()) {
			List<Tree_nodes> keyArray = newTree.getSubTreeList();
			for (int y = 0; y < keyArray.size(); y++) {
				if(keyArray.get(y).getValue() ==0)count0++;
				else count1++;
				count01new(keyArray.get(y),count0,count1);
			}
		}
		return (count1>=count0)?1:0;

	}

	private int countLeafNodes(Tree_nodes newTree, int count) {
		if (newTree.getSubTreeList() == null|| newTree.getSubTreeList().isEmpty()) count++;
		if (newTree.getSubTreeList() != null && !newTree.getSubTreeList().isEmpty()) {
			List<Tree_nodes> keyArray = newTree.getSubTreeList();
			for (int y = 0; y < keyArray.size(); y++ ) {
				count = countLeafNodes(keyArray.get(y),count);
			}
		}
		return count;
	}
}
