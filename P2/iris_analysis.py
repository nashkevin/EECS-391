#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import print_function

__date__    = "2017-12-10"
__author__  = "Kevin Nash"
__email__   = "kjn33@case.edu"

import sys
import pprint
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt


def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)

""" Returns the distance from point (x,y) to the line in slope-intercept form """
def dist_from_line(x=0, y=0, m=0, b=0, row=None):
    if row is None:
        return y - (m * x + b)
    return row["Petal Width"] - (m * row["Petal Length"] + b)

""" Returns "virginica" if the point (length, width) is above the line """
def classify_linear(row, slope, intercept):
    if 0 < dist_from_line(row["Petal Length"], row["Petal Width"], slope, intercept):
       return "virginica"
    return "versicolor"
    
def classify_prediction(pred):
    if 0 < pred:
        return "virginica"
    return "versicolor"

""" Returns the distance from point (x,y) to the edge of the circle """
def dist_from_circle(x, y, x_circ, y_circ, r):
    return ((x - x_circ)**2 + (y - y_circ)**2)**0.5 - r

""" Returns "virginica" if the point (length, width) is outside of the circle """
def classify_circular(row, x, y, radius):
    if 0 < dist_from_circle(row["Petal Length"], row["Petal Width"], x, y, radius):
        return "virginica"
    return "versicolor"

def sigmoid(x):
    return 1 / (1 + 10**(-x))

def d_sigmoid(x):
    sx = sigmoid(x)
    return sx * (1 - sx) * 2.3025851

def mean_squared_error(dataset, m, b, classes={"versicolor": 0, "virginica": 1}):
    sum_diffs = 0
    for i, row in dataset.iterrows():
        pred = sigmoid(dist_from_line(m=m, b=b, row=row))
        sum_diffs += (pred - classes[row["Species"]])**2
    return sum_diffs / len(dataset.index)

def sum_gradient(dataset, m, b, classes={"versicolor": 0, "virginica": 1}):
    # next_m = m
    # next_b = b
    # for i, row in dataset.iterrows():
    #     dist = dist_from_line(m=m, b=b, row=row)
    #     pred = sigmoid(dist)
    #     next_m = next_m - EPSILON * (classes[row["Species"]] - pred) * d_sigmoid(dist) * row["Petal Width"]
    #     next_b = next_b - EPSILON * (classes[row["Species"]] - pred) * d_sigmoid(dist) * row["Petal Length"]
    # return {"slope": next_m, "intercept": next_b}

    # grad_b = 0
    # grad_m = 0
    # for i, row in dataset.iterrows():
    #     line = m * row["Petal Length"] + b
    #     grad_b = grad_b + (line - row["Petal Width"])
    #     grad_step = (line - row["Petal Width"]) * row["Petal Length"]
    #     grad_m += grad_step
    # update_m = (grad_m * 2) / len(dataset.index)
    # update_b = (grad_b * 2) / len(dataset.index)
    # epsilon = 0.1 / len(dataset.index)
    # return {"slope": m - update_m * epsilon, "intercept": b - update_b * epsilon}
    epsilon = 0.1
    gradient = 0
    for i, row in dataset.iterrows():
        pred = sigmoid(dist_from_line(m=m, b=b, row=row))
        error = pred - classes[row["Species"]]
        gradient += (2 / len(dataset.index)) * (1 - pred) * error
    
    new_m = m + epsilon * gradient
    new_b = b - epsilon * gradient
    return new_m, new_b

def plot_linear_bound(dataset_1, dataset_2, slope, intercept,
                      show_mse=False, show_grad=False, filename=None):
    fig = plt.figure()
    ax = fig.add_subplot(111)

    # print("intercept = " + str(intercept))
    # print("slope = " + str(slope))
    
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

    x_vals = np.array(ax.get_xlim())
    y_vals = intercept + slope * x_vals
    plt.plot(x_vals, y_vals, color="k", linestyle='-', linewidth=1)
    # plt.show() # debug
    plt.text(0.4, 0.05, "m: {:.4f}  b: {:.4f}".format(slope, intercept), ha='center',
             va='center', fontsize=12, transform=ax.transAxes)
    combined_data = pd.concat([dataset_1, dataset_2])
    if show_mse:
        mse = mean_squared_error(combined_data, slope, intercept)
        plt.text(0.85, 0.05, "MSE: %.4f" % mse, ha='center', va='center',
                 fontsize=12, transform=ax.transAxes)
    if show_grad:
        gradient = sum_gradient(combined_data, slope, intercept)
        x_vals = np.array(ax.get_xlim())
        y_vals = gradient["intercept"] + gradient["slope"] * x_vals
        plt.plot(x_vals, y_vals, color="m", linestyle='-', linewidth=1)
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

    circle = plt.Circle((x, y), r, color="k", fill=False)
    ax.add_artist(circle)
    # plt.show() # debug
    plt.savefig(filename, bbox_inches="tight")
    plt.close(fig)

def gradient_descent(dataset_all, dataset_1, dataset_2, slope, intercept):
    m = slope
    b = intercept
    combined_data = pd.concat([dataset_1, dataset_2])
    for i in range(15):
        fn = "plot_test_" + str(i) + ".pdf"
        m, b = sum_gradient(combined_data, m, b)
        if i % 1 == 0:
            print("m = " + str(m))
            print("b = " + str(b))
            plot_linear_bound(dataset_1, dataset_2, m, b, show_mse=True, show_grad=False, filename=fn)

def main(filename):
    # Read the file into a Pandas dataframe
    iris_data = pd.read_csv(filename)
    # Capitalize the column names and remove underscores
    iris_data.columns = ["Sepal Length", "Sepal Width", "Petal Length", "Petal Width", "Species"]
    # Remove any species other than those specified by the assignment
    iris_data = iris_data.loc[iris_data["Species"].isin(["versicolor", "virginica"])]
    # Select the versicolor and virginica species individually
    versi_data = iris_data.loc[iris_data["Species"] == "versicolor"]
    virgi_data = iris_data.loc[iris_data["Species"] == "virginica"]

    # Generate plots for parts 1a and 1b
    plot_linear_bound(versi_data, virgi_data, -0.4, 3.6)
    
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
    plot_linear_bound(versi_class, virgi_class, -0.4, 3.6, filename="plot_1c.pdf")
    
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

    # Calculate the mean-squared error for part 2a
    plot_linear_bound(versi_data, virgi_data, -0.4, 3.6, show_mse=True, filename="plot_2a.pdf")
    
    # Calculate a large and a small MSE value for part 2b
    # Very high MSE
    plot_linear_bound(versi_data, virgi_data, 1.5, -5.5, show_mse=True, filename="plot_2b_1.pdf")
    # Lower MSE
    plot_linear_bound(versi_data, virgi_data, 0, 1.65, show_mse=True, filename="plot_2b_2.pdf")

    # 2e
    gradient_descent(iris_data, versi_data, virgi_data, 0, 2)
    

main("irisdata.csv")

# Make function callable from CLI
#if __name__ == "__main__":
#    main(sys.argv[1])

