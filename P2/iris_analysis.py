#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import print_function

__date__    = "2017-12-10"
__author__  = "Kevin Nash"
__email__   = "kjn33@case.edu"

import sys
import pprint
import pandas as pd
import numpy as numpy
import matplotlib.pyplot as plt


def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)

def main(filename):
    # Read the file into a Pandas dataframe
    iris_data = pd.read_csv(filename)
    iris_data.columns = ["Sepal Length", "Sepal Width", "Petal Length", "Petal Width", "Species"]
    # Select the versicolor and virginica species individually
    versi_data = iris_data.loc[iris_data["Species"] == "versicolor"]
    virgi_data = iris_data.loc[iris_data["Species"] == "virginica"]

    my_plot(versi_data, virgi_data)

def my_plot(set1, set2):
    fig = plt.figure()
    ax = fig.add_subplot(111)
    
    ax.scatter(x=set1["Petal Length"], y=set1["Petal Width"],
               color="b", marker="o", label=set1["Species"].iloc[0])
    ax.scatter(x=set2["Petal Length"], y=set2["Petal Width"],
               color="r", marker="^", label=set2["Species"].iloc[0])
    plt.xlabel("Petal Length (cm)")
    plt.ylabel("Petal Width (cm)")
    plt.legend(loc="upper left")
    # plt.show() # debug
    plt.savefig('plot_1a.pdf', bbox_inches='tight')

    plt.plot([3, 6.5], [2.4, 1], color='k', linestyle='-', linewidth=1)
    # plt.show() # debug
    plt.savefig('plot_1b.pdf', bbox_inches='tight')

    plt.close(fig)





main("irisdata.csv")

# Make function callable from CLI
#if __name__ == "__main__":
#    main(sys.argv[1])

