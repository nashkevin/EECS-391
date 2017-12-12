#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import print_function

__date__    = "2017-12-10"
__author__  = "Kevin Nash"
__email__   = "kjn33@case.edu"


import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import random


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

def mean_squared_error(dataset, m, b, classes={"versicolor": 0, "virginica": 1}):
    sum_diffs = 0
    for i, row in dataset.iterrows():
        pred = sigmoid(dist_from_line(m=m, b=b, row=row))
        sum_diffs += (pred - classes[row["Species"]])**2
    return sum_diffs / len(dataset.index)

def sum_gradient(dataset, m, b, classes={"versicolor": 0, "virginica": 1}):
    epsilon = 0.1 / len(dataset.index)
    gradient = 0
    for i, row in dataset.iterrows():
        pred = sigmoid(dist_from_line(m=m, b=b, row=row))
        error = pred - classes[row["Species"]]
        gradient += (2 / len(dataset.index)) * (1 - pred) * error
    if m < 0:
        new_m = m + epsilon * gradient
    else:
        new_m = m - epsilon * gradient
    new_b = b - epsilon * gradient
    return new_m, new_b

def plot_summary(dataset_1, dataset_2):
    # Set random weights
    slope = -0.3184#random.uniform(-0.4, -0.1)
    intercept = 2.2558#random.uniform(1.5, 2.5)
    combined_data = pd.concat([dataset_1, dataset_2])
    # Initial boundary
    plot_linear_bound(dataset_1, dataset_2, slope, intercept, show_mse=True,
                      filename="plot_3c_1.pdf")
    # Initial learning
    plot_learning_curve(combined_data, slope, intercept, "plot_3c_2.pdf", steps=1)
    # Middle boundary
    m = slope
    b = intercept
    for i in range(50):
        m, b = sum_gradient(combined_data, m, b)
    plot_linear_bound(dataset_1, dataset_2, m, b, show_mse=True,
                      filename="plot_3c_3.pdf")
    # Middle learning
    plot_learning_curve(combined_data, slope, intercept, "plot_3c_4.pdf", steps=300)
    # Final boundary
    b_2 = 99
    i = 0
    while 0.0001 < b_2 - b:
        b_2 = b
        m, b = sum_gradient(combined_data, m, b)
        i += 1
    plot_linear_bound(dataset_1, dataset_2, m, b, show_mse=True,
                      filename="plot_3c_5.pdf")
    # Final learning
    plot_learning_curve(combined_data, slope, intercept, "plot_3c_6.pdf", steps=i)


def plot_learning_curve(dataset, slope, intercept, filename, steps=10000):
    mse_vals = []
    m = slope
    b = intercept
    for i in range(steps):
        mse_vals.append(mean_squared_error(dataset, m, b))
        m, b = sum_gradient(dataset, m, b)

    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.scatter(x=list(range(1, len(mse_vals) + 1)), y=mse_vals, color="k", marker="|",linewidths=0.01)
    plt.xlabel("Steps")
    plt.ylabel("MSE at Step")
    plt.savefig(filename, bbox_inches="tight")
    plt.close(fig)

def plot_gradient_descent(dataset_1, dataset_2, slope, intercept, filename, snapshots=1):
    fig = plt.figure()
    ax = fig.add_subplot(111)

    ax.scatter(x=dataset_1["Petal Length"], y=dataset_1["Petal Width"],
               color="b", marker="o", label="versicolor")
    ax.scatter(x=dataset_2["Petal Length"], y=dataset_2["Petal Width"],
               color="r", marker="^", label="virginica")
    plt.xlabel("Petal Length (cm)")
    plt.ylabel("Petal Width (cm)")
    plt.legend(loc="upper left")

    x_vals = np.array(ax.get_xlim())
    y_vals = intercept + slope * x_vals
    plt.plot(x_vals, y_vals, color="k", linestyle='--', linewidth=2)
    # plt.show() # debug
    plt.text(0.4, 0.05, "m: {:.4f}  b: {:.4f}".format(slope, intercept), ha='center',
             va='center', fontsize=12, transform=ax.transAxes)
    combined_data = pd.concat([dataset_1, dataset_2])
    mse_0 = mean_squared_error(combined_data, slope, intercept)
    m = slope
    b = intercept
    steps = 1000
    for i in range(steps):
        m, b = sum_gradient(combined_data, m, b)
        if (i == 1) or (i % (steps / snapshots) == 0):
            # x_vals = np.array(ax.get_xlim())
            y_vals = b + m * x_vals
            plt.plot(x_vals, y_vals, color="m", linestyle='-', linewidth=1)
    mse_f = mean_squared_error(combined_data, m, b)
    plt.text(0.6, 0.95, "MSE_0: %.4f, MSE_f: %.4f" % (mse_0, mse_f), ha='center', va='center',
             fontsize=12, transform=ax.transAxes)
    plt.savefig(filename, bbox_inches="tight")
    plt.close(fig)

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
        m, b = sum_gradient(combined_data, slope, intercept)
        x_vals = np.array(ax.get_xlim())
        y_vals = b + m * x_vals
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
    # plot_linear_bound(versi_data, virgi_data, -0.4, 3.6)
    
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
    # plot_linear_bound(versi_class, virgi_class, -0.4, 3.6, filename="plot_1c.pdf")
    
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
    # plot_circular_bound(versi_data, virgi_data, "plot_1d_1.pdf", 4.75, 1.7, 0.5)

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
    # plot_circular_bound(versi_data, virgi_data, "plot_1d_2.pdf", 4.5, 1.2, 0.5)

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
    # plot_circular_bound(versi_data, virgi_data, "plot_1d_3.pdf", 4.0, 1.2, 1)

    # Calculate the mean-squared error for part 2a
    # plot_linear_bound(versi_data, virgi_data, -0.4, 3.6, show_mse=True, filename="plot_2a.pdf")
    
    # Calculate a large and a small MSE value for part 2b
    # Very high MSE
    # plot_linear_bound(versi_data, virgi_data, 1.5, -5.5, show_mse=True, filename="plot_2b_1.pdf")
    # Lower MSE
    # plot_linear_bound(versi_data, virgi_data, 0, 1.65, show_mse=True, filename="plot_2b_2.pdf")

    # 2e
    # plot_gradient_descent(versi_data, virgi_data, -0.1, 5, snapshots=5, filename="plot_2e_1.pdf")
    # plot_gradient_descent(versi_data, virgi_data, -0.5, 3, snapshots=5, filename="plot_2e_2.pdf")

    # 3b
    # plot_gradient_descent(versi_data, virgi_data, -0.1, 5, snapshots=100, filename="plot_3b_1.pdf")
    # plot_learning_curve(iris_data, -0.1, 5, "plot_3b_2.pdf")

    # 3c
    plot_summary(versi_data, virgi_data)

# Make function callable from CLI
if __name__ == "__main__":
    main("irisdata.csv")

