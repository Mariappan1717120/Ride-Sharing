package com.gbtrip.org.travel_share;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Decision_tree {

// The following function calculates the information gain
	public static double computeGain (int colIndex, Tree_inst dataRows, Tree_inst resultRows, int c) {
		
		ArrayList<int[]> dataCount = getDataCounts(colIndex, dataRows, resultRows);
		int[] resultCountArr = getResultCountArray(resultRows);
		double totalResultCount = 0;
		double[] totalDataCount = new double[2];
		double Entropy = 0;
		double gain = computeEntropy(resultCountArr,c);


		for (int i = 0; i < 2; i++) {
			totalResultCount += resultCountArr[i];
			for (int l = 0; l < 2; l++) {
				totalDataCount[i] += dataCount.get(i)[l];
			}
		}
	
		for (int i = 0; i < 2; i++) {
			Entropy += (-1) * (totalDataCount[i] / totalResultCount) * computeEntropy(dataCount.get(i),c);
		}

		gain += Entropy;
		
		return gain;
	}

//The following function calculates the entropy from the given data
	private static double computeEntropy (int[] data, int c) {

		double totalDataCnt = 0;
		int count = 0;
		double Entropy= 0;
		
		for (int i = 0; i < 2; i ++) {
			if (data[i] == 0)
				count++;
			totalDataCnt += data[i];
		}
		
		if (totalDataCnt == 0 || count == 1)
			return 0;

		
		if(c==1){
			for (int i = 0; i < 2; i++) {
				if (data[i] != 0)
					Entropy += (-1) * (data[i]/totalDataCnt) * (Math.log10(data[i]/totalDataCnt) / Math.log10(2)); 
			}
		} 
		else {

			if (data[0] != 0 || data[1] !=0)
				Entropy =  data[0] /totalDataCnt * data[1] / totalDataCnt;
		}
		return Entropy;
	}


	private static int[] getResultCountArray(Tree_inst resultRows) {

		int[] resultDataArray = new int[2];
		for (int i = 0; i < 2; i++) {
			resultDataArray[i] = 0;
			for (int j = 0; j < resultRows.getRowCount(); j++) {
				if ( i == resultRows.getRowColumnVal(j, 0))
					resultDataArray[i]++;
			}
		}
		return resultDataArray;
	}


	private static ArrayList<int[]> getDataCounts (int colIndex, Tree_inst dataRows, Tree_inst resultRows) {

		ArrayList<int[]> dataCountMainArr = new ArrayList<int[]>();
		for (int i = 0; i < 2; i++) {
			int[] dataCountSubArr = new int[2];
			for (int j = 0; j < dataRows.getRowCount(); j++) {
				int resultVal = (int)resultRows.getRowColumnVal(j, 0);
				if ( i == dataRows.getRowColumnVal(j, colIndex) )
					dataCountSubArr[resultVal]++;
			}
			dataCountMainArr.add(dataCountSubArr);
		}
		return dataCountMainArr;
	}


	public void printTree(Tree_nodes root) throws IOException {

		//System.setOut(new PrintStream(new File("output.txt")));
		System.out.println("Printing Tree :: " );
		printTreeRecursive(root,0);
		System.out.println("");
	}

	private void printTreeRecursive(Tree_nodes root,int depth) {

		String myLabel = "";
		if (root.getSubTreeList().isEmpty()) {
			myLabel = "" + root.getValue();
			System.out.print(myLabel);
		}

		if (!root.getSubTreeList().isEmpty()) {
			List<Tree_nodes> keyArray = root.getSubTreeList();
			for (int y = 0; y < keyArray.size(); y++ ) {

				System.out.println();
				for(int x = 0; x < depth ; x++){
					System.out.print(" | ");
				}
				myLabel = root.getColumns().getName() + "-" + root.getColumns().getIndex() + " =  ";
				System.out.print(myLabel);

				System.out.print( y + " : ");

				printTreeRecursive(keyArray.get(y), depth + 1);
			}
		}
	}

	public static Tree_inst[] createSubInstance(int colIndex, int value, Tree_inst dataRows, 
			Tree_inst resultRows){

		List<Integer> indexList = new ArrayList<Integer>();
		Tree_inst dataSubRows = getRowsOfCol(dataRows, colIndex, dataRows.getRowCount() , dataRows.getColCount(), value, indexList);
		Tree_inst resultSubRows = getRowsOfCol(resultRows, 0, resultRows.getRowCount() , resultRows.getColCount(), -1, indexList);
		Tree_inst[] subRowsArray = new Tree_inst[2];
		subRowsArray[0] = dataSubRows;
		subRowsArray[1] = resultSubRows;
		return subRowsArray;

	}

	public static Tree_inst getRowsOfCol( Tree_inst entities, int columnIndex, int rowCnt, int colCnt,
			int value, List<Integer> indexList ){

		List<int []> tempRowDataLst = new ArrayList<int[]>();
		if (value == -1) {
			for (int i = 0; i < entities.getRowCount(); i++) {
				if (indexList.contains(i)) {
					int[] rowSrc = entities.getRow(i);
					int[] rowDest = new int[colCnt];
					for(int j = 0; j < colCnt; j++)
						rowDest[j] = rowSrc[j];
					tempRowDataLst.add(rowDest);
				}
			}
		}
		else {
			for(int j = 0; j < entities.getRowCount(); j++) {
				if (entities.getRowColumnVal(j, columnIndex) == value) {
					int[] rowSrc = entities.getRow(j);
					int[] rowDest = new int[colCnt];
					for(int i = 0; i < colCnt; i++)
						rowDest[i] = rowSrc[i];
					tempRowDataLst.add(rowDest);
					indexList.add(j);
				}
			}
		}
		List<String> tmpColumnNamesLst = new ArrayList<String>();
		for(int i = 0; i < colCnt; i++) {
			tmpColumnNamesLst.add(entities.getColumnName(i));
		}

		Tree_inst subTree_inst = new Tree_inst(tempRowDataLst, tmpColumnNamesLst);

		return subTree_inst;
	}

	public Tree_nodes makeTree (Tree_inst dataRows, Tree_inst resultRows, int i) {

		List<Columns> columnList =  createAllAttributes(dataRows);
		return makeTreeRecursive(dataRows, resultRows, columnList, i);

	}


	private Tree_nodes makeTreeRecursive (Tree_inst dataRows, Tree_inst resultRows, List<Columns> columnList, int i) {
		Tree_nodes root = new Tree_nodes();
		if (equalNodes(resultRows) || columnList.isEmpty()) {
			/*if(columnLst.isEmpty()){
				return null;
			}*/
			int l = -1;
			try{
				l = resultRows.getRowColumnVal(0, 0);
			}catch (Exception e) {
				l = 0;
			}
			root.setValue(l);
		}else {

			if(resultRows.getRowCount() == 0 ){
				return null;
			}
			Columns bestColumn = splitColumn(dataRows, resultRows, columnList,i);
			root.setColumns(bestColumn);
			//System.out.print(bestColumn.getName() +" ");
			for (int value = 0; value < 2 ; value++) {

				Tree_inst[] subEntitiesArray = createSubInstance(bestColumn.getIndex(),
						value, dataRows, resultRows);

				columnList.remove(bestColumn);
				Tree_nodes Tree_nodes = makeTreeRecursive(subEntitiesArray[0], subEntitiesArray[1], columnList,i);
				if(Tree_nodes != null)
					root.addSubTreeList(Tree_nodes);
				columnList.add(bestColumn);
			}
		}
		return root;
	}

	private Columns splitColumn(Tree_inst dataRows, Tree_inst resultRows, List<Columns> columnList, int i) {
		double maxGain = -1;
		Columns returnColumn = null;
		for (Columns column : columnList) {
			int colIndex = column.getIndex();
			double gain = computeGain(colIndex, dataRows, resultRows,i);
			if (gain > maxGain) {
				maxGain = gain;
				returnColumn = column;
			}
		}
		//System.out.println("gain = " +maxGain + "col= "+returnColumn.getName());
		return returnColumn;
	}


	private boolean equalNodes(Tree_inst resultRows){

		if(resultRows != null && resultRows.getRowVal() != null && resultRows.getRowVal().size() > 0 ){
			int temp = resultRows.getRowVal().get(0)[0];
			for(int[] i : resultRows.getRowVal()){
				if( temp != i[0]){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}

	private List<Columns> createAllAttributes (Tree_inst entities) {
		List<Columns> columnLst = new LinkedList<Columns>();
		for (int i = 0; i < entities.getColCount(); i++) {
			Columns column = new Columns(i, entities.getColumnName(i));
			columnLst.add(column);
		}
		return columnLst;
	}
}

class Columns {

	private int index;
	private String name;
	private List<Integer> values;
	
	
	
	public Columns(int index, String name, List<Integer> values) {
		this.index = index;
		this.name = name;
		this.values = values;
	}
	
	public Columns(int index, String name) {
		this.index = index;
		this.name = name;
		values = new  ArrayList<Integer>();
		values.add(0);
		values.add(1);
	}

	@Override
	public boolean equals(Object data){
		if(data instanceof Columns)
			if(((Columns)data).getIndex() == this.index)
				return true;
		
		return false;
	}
	public int getIndex() {return index;}
	public void setIndex(int index) {this.index = index;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public List<Integer> getValues() {return values;}
	public void setValues(List<Integer> values) {this.values = values;} 
	
}

 class Tree_inst {
	private List<int[]> rowValues;
	private List<String> columnNames;

	public Tree_inst() {}
	public Tree_inst(List<int[]> row_val, List<String> column) {
		super();
		this.rowValues = row_val;
		this.columnNames = column;
	}

	public int getColCount() {return columnNames.size();}
	public List<int[]> getRowVal() {return rowValues;}
	public int getRowCount() {return rowValues.size();}
	public void setRowVal(List<int[]> rowVal) {this.rowValues = rowVal;}
	public List<String> getColumnNames() {return columnNames;}
	public String getColumnName(int rowIndex) {return columnNames.get(rowIndex);}
	public void setColumnNames(List<String> columnNames) {this.columnNames = columnNames;}
	public int[] getRow(int rowNum){return rowValues.get(rowNum);}
	public int getRowColumnVal(int rowNum, int column){return rowValues.get(rowNum)[column];}
}


 class Tree_nodes {

		private int value ;
		private List<Tree_nodes> subTreeList = new ArrayList<Tree_nodes>();
		private Columns column  ;
		public Tree_nodes() {}
		public Tree_nodes(int value, List<Tree_nodes> subTreeList,Columns column){
			this.subTreeList  =  subTreeList;
			this.value  = value;
			this.column  = column;
		}
		public int getValue() {return value;	}
		public void setValue(int value) {this.value = value;}
		public List<Tree_nodes> getSubTreeList() {return subTreeList;}
		public void setSubTreeList(List<Tree_nodes> subTreeList) {this.subTreeList = subTreeList;}
		public void addSubTreeList(Tree_nodes subTreeObj) {subTreeList.add(subTreeObj);}
		public Columns getColumns() {return column;}
		public void setColumns(Columns columns) {this.column = columns;}
	}

