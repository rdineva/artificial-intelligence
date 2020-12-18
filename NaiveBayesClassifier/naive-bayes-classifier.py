import numpy as np
from math import sqrt, pi, exp, log

labels = [
  'handicapped-infants',
  'water-project-cost-sharing',
  'adoption-of-the-budget-resolution',
  'physician-fee-freeze',
  'el-salvador-aid',
  'religious-groups-in-schools',
  'anti-satellite-test-ban',
  'aid-to-nicaraguan-contras',
  'mx-missile',
  'immigration',
  'synfuels-corporation-cutback',
  'education-spending',
  'superfund-right-to-sue',
  'crime',
  'duty-free-exports',
  'export-administration-act-south-africa'
]

N_folds = 10
num_rows = 435

def get_folds():
  file = open('house-votes-84.data')
  file_lines = file.readlines()
  dataset = []

  for line in file_lines:
    attributes = line.strip('\n').split(',')
    dataset.append(attributes)

  limits = np.linspace(0, num_rows+1, N_folds+1, dtype=int)

  folds = []

  for i in range(len(limits) - 1):
    folds += [dataset[limits[i] : limits[i+1]]]

  return folds  

def to_numbers(data): 
  for i in range(len(data)):
    if data[i] == 'y':
      data[i] = 2
    elif data[i] == 'n':
      data[i] = 1
    elif data[i] == '?':
      data[i] = 0

  return data 

def gaussian_probability(x, mean, standard_deviation):
  exponent = exp(-((x-mean)**2 / (2 * standard_deviation**2 )))
  return (1 / (sqrt(2 * pi) * standard_deviation)) * exponent

def train(dataset):
  dataset_by_class = dict({'democrat': [], 'republican': []})

  for i in range(len(dataset)):
    class_value = dataset[i][0]  
    row = dataset[i][1:17]
    dataset_by_class[class_value].append(row)

  summaries = dict({'democrat': [], 'republican': []})
  
  for class_value, rows in dataset_by_class.items():
    for column in zip(*rows):
      summaries[class_value].append([np.mean(column), np.std(column), len(column)])

  return summaries

def predict_probabilities(summaries, row, total_rows):
  probabilities = dict()
  
  for class_value, class_summaries in summaries.items():
    class_probability = summaries[class_value][0][2]/float(total_rows)
    probability = 1
    gaussian_probabilities_product = 1
  
    for i in range(len(class_summaries)):
      mean, stdev, _count = class_summaries[i]
      gaussian_probabilities_product *= gaussian_probability(row[i], mean, stdev)
      probability = log(class_probability) + log(gaussian_probabilities_product)

    probabilities[class_value] = probability
  
  return probabilities

def predict(summaries, dataset, total_rows):
  correct = 0
  incorrect = 0

  for row in dataset:
    real_class = row[0]
    del(row[0])
    
    predicted_probabilities = predict_probabilities(summaries, row, total_rows) 
    prediction = {k:v for k,v in predicted_probabilities.items() if v == max(predicted_probabilities.values())}
    predicted_class = list(prediction.keys())[0]

    if predicted_class == real_class:
      correct+=1
    else:
      incorrect+=1

  print('Accuracy:', accuracy(correct, incorrect))

def accuracy(correct, incorrect):
  return correct/float(correct+incorrect)*100

def normalize_data(folds):
  dataset = sum(folds, [])
  for i in range(len(dataset)):
    dataset[i] = to_numbers(dataset[i])

  return dataset

# def cross_validation_split(dataset, n_folds)

def main():
  folds = get_folds()
  
  training_folds = folds[1:10]
  train_dataset = normalize_data(training_folds)

  test_fold = folds[0]
  test_dataset = normalize_data([test_fold])

  summaries = train(train_dataset)
  print(summaries)

  predict(summaries, test_dataset, len(train_dataset))

if __name__== "__main__":
  main()
