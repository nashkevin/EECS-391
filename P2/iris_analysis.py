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

def plot_linear_bound(dataset_1, dataset_2, filename=None):
    fig = plt.figure()
    ax = fig.add_subplot(111)
    
    ax.scatter(x=dataset_1["Petal Length"], y=dataset_1["Petal Width"],
               color="b", marker="o", label="versicolor")
    ax.scatter(x=dataset_2["Petal Length"], y=dataset_2["Petal Width"],
               color="r", marker="^", label="virginica")
    plt.xlabel("Petal Length (cm)")
    plt.ylabel("Petal Width (cm)")
    plt.legend(loc="upper left")
    # plt.show() # debug
    if filename == None:
        plt.savefig("plot_1a.pdf", bbox_inches="tight")

    # Coords: (3, 2.4), (6.5, 1)
    # Vars: m = -0.4, b = 3.6
    plt.plot([3, 6.5], [2.4, 1], color="k", linestyle="-", linewidth=1)
    # plt.show() # debug
    if filename == None:
        plt.savefig("plot_1b.pdf", bbox_inches="tight")
    else:
        plt.savefig(filename, bbox_inches="tight")

    plt.close(fig)

def plot_circular_bound(dataset_1, dataset_2, filename, x, y, r):
    fig = plt.figure()
    ax = fig.add_subplot(111)
    
    ax.scatter(x=dataset_1["Petal Length"], y=dataset_1["Petal Width"],
               color="b", marker="o", label="versicolor")
    ax.scatter(x=dataset_2["Petal Length"], y=dataset_2["Petal Width"],
               color="r", marker="^", label="virginica")
    plt.xlabel("Petal Length (cm)")
    plt.ylabel("Petal Width (cm)")
    plt.legend(loc="upper left")

    # Coords: (4.75, 1.7)
    circle = plt.Circle((x, y), r, color="k", fill=False)
    ax.add_artist(circle)
    # plt.show() # debug
    plt.savefig(filename, bbox_inches="tight")
    plt.close(fig)

def dist_from_line(x, y, m, b):
    return abs(y - (m * x + b))

""" Returns "virginica" if the point (length, width) is above the line """
def classify_linear(row, slope, intercept):
    if 0 < dist_from_line(row["Petal Length"], row["Petal Width"], slope, intercept):
        return "virginica"
    return "versicolor"

def dist_from_circle(x, y, x_circ, y_circ, r):
    return ((x - x_circ)**2 + (y - y_circ)**2)**0.5 - r

""" Returns "virginica" if the point (length, width) is outside of the circle """
def classify_circular(row, x, y, radius):
    if 0 < dist_from_circle(row["Petal Length"], row["Petal Width"], x, y, r):
        return "virginica"
    return "versicolor"

def sigmoid(x):
    return 1 / (1 + 2.7182818285**(-x))

def main(filename):
    # Read the file into a Pandas dataframe
    iris_data = pd.read_csv(filename)
    iris_data.columns = ["Sepal Length", "Sepal Width", "Petal Length", "Petal Width", "Species"]
    # Remove any species other than those specified by the assignment
    iris_data = iris_data.loc[iris_data["Species"].isin(["versicolor", "virginica"])]
    # Select the versicolor and virginica species individually
    versi_data = iris_data.loc[iris_data["Species"] == "versicolor"]
    virgi_data = iris_data.loc[iris_data["Species"] == "virginica"]

    # Generate plots for parts 1a and 1b
    plot_linear_bound(versi_data, virgi_data)
    
    # Classify data linearly for part 1c
    iris_data["Classification"] = iris_data.apply(
        lambda row: classify_linear(row, -0.4, 3.6), axis=1
    )
    misclassified = pd.DataFrame(columns=iris_data.columns)
    
    for i, row in iris_data.iterrows():
        if not row["Classification"] == row["Species"]:
            misclassified.loc[len(misclassified.index)] = row
    
    print("Linear decision accuracy: {}%".format(100 - len(misclassified.index) /
          len(iris_data.index) * 100))
    
    versi_class = misclassified.loc[misclassified["Species"] == "versicolor"]
    virgi_class = misclassified.loc[misclassified["Species"] == "virginica"]
    plot_linear_bound(versi_class, virgi_class, "plot_1c.pdf")
    
    # Classify data circularly for part 1d
    iris_data.drop("Classification", axis=1, inplace=True)
    iris_data["Classification"] = iris_data.apply(
        lambda row: classify_circular(row, 4.75, 1.7, 0.5), axis=1
    )
    misclassified = pd.DataFrame(columns=iris_data.columns)
    
    for i, row in iris_data.iterrows():
        if not row["Classification"] == row["Species"]:
            misclassified.loc[len(misclassified.index)] = row
    
    print("Circular decision accuracy (1): {}%".format(100 - len(misclassified.index) /
          len(iris_data.index) * 100))
    
    versi_class = misclassified.loc[misclassified["Species"] == "versicolor"]
    virgi_class = misclassified.loc[misclassified["Species"] == "virginica"]
    plot_circular_bound(versi_data, virgi_data, "plot_1d_1.pdf", 4.75, 1.7, 0.5)

    # Second circle
    iris_data.drop("Classification", axis=1, inplace=True)
    iris_data["Classification"] = iris_data.apply(
        lambda row: classify_circular(row, 4.5, 1.2, 0.5), axis=1
    )
    misclassified = pd.DataFrame(columns=iris_data.columns)
    
    for i, row in iris_data.iterrows():
        if not row["Classification"] == row["Species"]:
            misclassified.loc[len(misclassified.index)] = row
    
    print("Circular decision accuracy (2): {}%".format(100 - len(misclassified.index) /
          len(iris_data.index) * 100))
    
    versi_class = misclassified.loc[misclassified["Species"] == "versicolor"]
    virgi_class = misclassified.loc[misclassified["Species"] == "virginica"]
    plot_circular_bound(versi_data, virgi_data, "plot_1d_2.pdf", 4.5, 1.2, 0.5)

    # Third circle
    iris_data.drop("Classification", axis=1, inplace=True)
    iris_data["Classification"] = iris_data.apply(
        lambda row: classify_circular(row, 4.0, 1.2, 1), axis=1
    )
    misclassified = pd.DataFrame(columns=iris_data.columns)
    
    for i, row in iris_data.iterrows():
        if not row["Classification"] == row["Species"]:
            misclassified.loc[len(misclassified.index)] = row
    
    print("Circular decision accuracy (3): {}%".format(100 - len(misclassified.index) /
          len(iris_data.index) * 100))
    
    versi_class = misclassified.loc[misclassified["Species"] == "versicolor"]
    virgi_class = misclassified.loc[misclassified["Species"] == "virginica"]
    plot_circular_bound(versi_data, virgi_data, "plot_1d_3.pdf", 4.0, 1.2, 1)

    # Calculate the mean-squared error 2a
    # MSE = 1/n * sum

    # data vectors, decision boundary, pattern classes
    

main("irisdata.csv")

# Make function callable from CLI
#if __name__ == "__main__":
#    main(sys.argv[1])

