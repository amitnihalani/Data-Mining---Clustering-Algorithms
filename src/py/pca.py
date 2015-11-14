#!/usr/bin/python
import matplotlib.pyplot as plt
import numpy as np
from sklearn import datasets
from sklearn.decomposition import PCA
#from sklearn.discriminant_analysis import LinearDiscriminantAnalysis


from os import listdir
from os.path import isfile, join
import re

mypath = "."
onlyfiles = [f for f in listdir(mypath) if isfile(join(mypath, f))]

print onlyfiles

pattern = "py$"
for file in onlyfiles:
	if re.match(r'(.*)out$', file, re.I):
		infile = open(file,'r')
		lines = []
		for line in infile.readlines():
			lines.append(line)
		arrays = lines[0].split()
		matrix = []
		for i in range(len(arrays)):
		    array = map(float, arrays[i].split(','))
		    matrix.append(array)
		X = np.array(matrix)
		y = np.array(map(int, lines[1].split(',')))
		target_names = np.array(map(int, lines[2].split(',')))
		#X = iris.data
		#y = iris.target
		#target_names = iris.target_names

		pca = PCA(n_components=2)
		X_r = pca.fit(X).transform(X)

		#lda = LinearDiscriminantAnalysis(n_components=2)
		#X_r2 = lda.fit(X, y).transform(X)

		# Percentage of variance explained for each components
		print('explained variance ratio (first two components): %s'
		      % str(pca.explained_variance_ratio_))

		plt.figure()
		for c, i, target_name in zip("rgbyk", target_names, target_names):
		    plt.scatter(X_r[y == i, 0], X_r[y == i, 1], c=c, label=target_name)
		plt.legend()
		plt.title('PCA of ' + lines[3])

		#plt.figure()
		#for c, i, target_name in zip("rgb", [0, 1, 2], target_names):
		#    plt.scatter(X_r2[y == i, 0], X_r2[y == i, 1], c=c, label=target_name)
		#plt.legend()
		#plt.title('LDA of IRIS dataset')

		plt.show()
