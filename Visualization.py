__author__ = 'hharwani'
import numpy as np
from scipy.cluster.hierarchy import dendrogram, linkage
from matplotlib import pyplot as plt
np.set_printoptions(precision=8, suppress=True)
data = np.loadtxt("results.txt",dtype=float,delimiter=",")
print type(data)
# data = np.loadtxt("iyer.txt",dtype=float,delimiter="\t",usecols = (2,3,4,5,6,7,8,9,10,11,12))
# Z = linkage(data, 'single')

plt.figure(figsize=(25, 10))
plt.title('Hierarchical Clustering Dendrogram')
plt.xlabel('sample index')
plt.ylabel('distance')
dendrogram(
    data,
    truncate_mode='lastp',  # show only the last p merged clusters
    p=12,  # show only the last p merged clusters
    show_leaf_counts=False,  # otherwise numbers in brackets are counts
    leaf_rotation=90.,
    leaf_font_size=12.,
    show_contracted=True,  # to get a distribution impression in truncated branches
)

# dendrogram(
#     data,
#     leaf_rotation=90.,  # rotates the x axis labels
#     leaf_font_size=8.,  # font size for the x axis labels
# )

plt.show()