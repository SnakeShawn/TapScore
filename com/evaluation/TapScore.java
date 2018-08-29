package com.evaluation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TapScore {
	
	private int TotalCount = 0;
	private int CorrectCount = 0;
	private int WrongCount = 0;
	private int MissCount = 0;
	private int SurplusCount = 0;
	
	public int execute(int[] answerArr, int[] correctArr) {
		return execute(answerArr, correctArr, 200);
	}
	
	public int execute(int[] answerArr, int[] correctArr, int errorRange) {
		if(answerArr == null || correctArr == null ||answerArr.length<1||correctArr.length<1) return -1;
		
		int correctCount = correctArr.length;
		
		this.setTotalCount(correctCount);
		int startTime = correctArr[0]; 
		int endTime = correctArr[correctCount-1];
		
		List<Integer> evalList = new ArrayList<Integer>();
		
		for (int i=0; i<answerArr.length; i++) {
			int answerBeat = answerArr[i];
			if (answerBeat > (startTime-errorRange) && answerBeat < (endTime+errorRange)) {
				evalList.add(answerBeat);
			}
		}
		int evalCount = evalList.size();
		if (evalCount == 0) return 0;
		int[] evalArr = new int[evalCount];
		Iterator<Integer> it = (Iterator<Integer>) evalList.iterator();
		int ei = 0;
		while (it.hasNext()) {
			Integer ii = (Integer) it.next();
			evalArr[ei] = ii;
			ei++;
		}
		int[][] dim = new int[correctCount+1][evalCount+1];
		int[][] Mat = new int[correctCount][evalCount];
		int[][] CumulMat = new int[correctCount][evalCount];
		for (int i=0; i<correctCount+1; i++) {
			for (int j=0; j<evalCount+1; j++) {
				if (i==0 || j==0) {
					dim[i][j] = Integer.MAX_VALUE;
				}else {
					if (Math.abs(correctArr[i-1]-evalArr[j-1]) > errorRange){
						dim[i][j] = 100;
						Mat[i-1][j-1] = 100;
						CumulMat[i-1][j-1] = 100;
						
					} else {
						dim[i][j] = 0;
						Mat[i-1][j-1] = 0;
						CumulMat[i-1][j-1] = 0;
					}
				}
			}
		}
		dim[0][0] = 0;
		
		for (int i=0; i<correctCount; i++) {
			for (int j=0; j<evalCount; j++) {
				// ternary operator
				int a = dim[i][j];
				int b = dim[i+1][j];
				int c = dim[i][j+1];
				CumulMat[i][j] += a < b ? (a < c ? a : c) : (b < c ? b : c);
				dim[i+1][j+1] = CumulMat[i][j];
			}
		}
		int i = correctCount - 1;
		int j = evalCount - 1;
		if (i==0 && j==0 && Math.abs(correctArr[i]-evalArr[j])<errorRange){
			this.CorrectCount++;
		}
		
		while (i>0 || j>0) {
			// get the index of the minimum number
			int tb = this.argmin(dim[i][j],dim[i][j+1],dim[i+1][j]); 
			//System.out.println(dim[i][j]+","+dim[i][j+1]+","+dim[i+1][j]);
			if (tb == 0) {
				if ((Mat[i][j])==0) this.CorrectCount++;
				else this.WrongCount++;
				j--;
				i--;
				if (i==0 && j==0) {
					if (Mat[0][0]==0)this.CorrectCount++;
					else this.WrongCount++;
				}
			}
			else if (tb == 1) {
				this.MissCount++;
				i--;
				if (i==0 && j==0) {
					if (Mat[0][0]==0)this.CorrectCount++;
					else this.MissCount++;
				}
			}
			else {
				this.SurplusCount++;
				j--;
				if (i==0 && j==0) {
					if (Mat[0][0]==0)this.CorrectCount++;
					else this.SurplusCount++;
				}
				
			}
		}
		
		return 0;
	}
	
	public float getScoreRate() {
		float rate = 0.0f;
		float suplusrate = (float)this.SurplusCount/this.TotalCount;
		if (suplusrate < 0.1)
			rate = (float) ((this.CorrectCount - 1.0 * this.SurplusCount)/this.TotalCount);
		else if (suplusrate < 0.2)
			rate = (float) ((this.CorrectCount - 1.5 * this.SurplusCount)/this.TotalCount);
		else if (suplusrate < 0.3)
			rate = (float) ((this.CorrectCount - 2.0 * this.SurplusCount)/this.TotalCount);
		else
			rate = 0.0f;
		
		if (rate < 0.0f) return 0.0f;
		
		return rate;
	}
	
	public float getScore() {
		float rate = getScoreRate();
		
		if (rate>0)  return rate * 100;
		else return 0.0f;
	}
	
	private int argmin(int a, int b, int c) {
		int[] s = {a,b,c};
		int index = 0;
		int num = a;
		for(int i=0; i<3; i++) {
			if (num>s[i]) {
				num = s[i];
				index = i;
			}
		}
		return index;
	}
	
	public int getTotalCount() {
		return TotalCount;
	}

	public void setTotalCount(int totalCount) {
		TotalCount = totalCount;
	}
	
	public int getCorrectCount() {
		return CorrectCount;
	}

	public void setCorrectCount(int correctCount) {
		CorrectCount = correctCount;
	}

	public int getMissCount() {
		return MissCount;
	}

	public void setMissCount(int missCount) {
		MissCount = missCount;
	}

	public int getSurplusCount() {
		return SurplusCount;
	}

	public void setSurplusCount(int surplusCount) {
		SurplusCount = surplusCount;
	}

	public int getWrongCount() {
		return WrongCount;
	}

	public void setWrongCount(int wrongCount) {
		WrongCount = wrongCount;
	}
}
