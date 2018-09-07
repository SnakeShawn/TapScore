# -*- coding: utf-8 -*- 
import sys
import csv
import numpy as np
from numpy import array, zeros, inf, argmin

class TapScore:

    def __init__(self):
    	self.__TotalCount = 0
    	self.__CorrectCount = 0
    	self.__WrongCount = 0
    	self.__MissCount = 0
    	self.__SurplusCount = 0
   
    def __clearCounts(self):
    	self.__TotalCount = 0
    	self.__CorrectCount = 0
    	self.__WrongCount = 0
    	self.__MissCount = 0
    	self.__SurplusCount = 0

    def loadData(self, answerArr, correctArr):
        self.AnswerArr = answerArr
        self.CorrectArr = correctArr
        self.__clearCounts()

    def reloadData(self, answerArr, correctArr):
        loadData(answerArr, correctArr)
	
    def execute(self, error = 200):
        answerArr = self.AnswerArr
        correctArr = self.CorrectArr
        if len(answerArr)<1 or len(correctArr)<1 :
            return False
		
        self.__TotalCount = len(correctArr)
        startTime = correctArr[0]
        endTime = correctArr[-1]
        evalArr = []
        
        for i in range(len(answerArr)):
            if (answerArr[i] < (endTime + error)) and (answerArr[i] > (startTime - error)):
                evalArr.append(answerArr[i])
        evalCount = len(evalArr)
        if evalCount == 0 :
            return True
        dim = zeros((self.__TotalCount+1, evalCount+1))
        dim[0, 1:] = inf
        dim[1:, 0] = inf
        dim1 = dim[1:, 1:] # 浅复制
        for i in range(self.__TotalCount):
            for j in range(evalCount):
                if abs(correctArr[i] - evalArr[j]) > error:
                    dim1[i,j] = 100.0
                else:
                    dim1[i,j] = 0.0
                #dim1[i,j] = manhattan_distances(correctArr[i], evalArr[j])
        M = dim1.copy()
        for i in range(self.__TotalCount):
            for j in range(evalCount):
                dim1[i,j] += min(dim[i,j],dim[i,j+1],dim[i+1,j])
        i,j = array(dim.shape) - 2
        p,q = [i],[j]
        while(i>0 or j>0):
            tb = argmin((dim[i,j],dim[i,j+1],dim[i+1,j]))
            #if tb==0 and dim[i,j] == dim[i+1,j+1]:
            if tb==0 :
                if M[i,j]==0:
                    self.__CorrectCount+=1
                else:
                    self.__WrongCount+=1
                i-=1
                j-=1
                if i==0 and j==0:
                    if M[0,0]==0:
                        self.__CorrectCount+=1
                    else:
                        self.__WrongCount +=1
            elif tb==1 :
                self.__MissCount+=1
                i-=1
                if i==0 and j==0:
                    if M[0,0]==0:
                        self.__CorrectCount+=1
                    else:
                        self.__MissCount +=1
            else:
                self.__SurplusCount+=1
                j-=1
                if i==0 and j==0:
                    if M[0,0]==0:
                        self.__CorrectCount+=1
                    else:
                        self.__SurplusCount +=1
                
            p.insert(0,i)
            q.insert(0,j)
      
        return True
    
    def getScoreRate(self):
        rate = 0.0
        suplusRate = self.__SurplusCount/self.__TotalCount
        if suplusRate < 0.1:
            rate = (self.__CorrectCount - self.__SurplusCount)/self.__TotalCount
        elif suplusRate < 0.2:
            rate = (self.__CorrectCount - 1.5*self.__SurplusCount)/self.__TotalCount
        elif suplusRate < 0.3:
            rate = (self.__CorrectCount - 2*self.__SurplusCount)/self.__TotalCount
        else:
            rate = 0
        if rate<0:
            rate = 0.0
        return round(rate, 4)
        
    def getScore(self):
        rate = getScoreRate()
        if rate>0:
            return rate * 100
        else:
            return 0.0

    def getTotalCount(self):
        return self.__TotalCount
        
    def getCorrectCount(self):
        return self.__CorrectCount

    def getWrongCount(self):
        return self.__WrongCount

    def getMissCount(self):
        return self.__MissCount

    def getSurplusCount(self):
        return self.__SurplusCount

