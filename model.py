from numpy import mean
from numpy import std
from matplotlib import pyplot as plt
from sklearn.model_selection import KFold
from tensorflow.keras.datasets import mnist
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D, Dense, Flatten
from tensorflow.keras.optimizers import SGD
from scipy.ndimage.interpolation import shift
from tensorflow.keras.models import load_model
import numpy as np

(Xtrain, Ytrain), (Xtest, Ytest) = mnist.load_data()

def Quad1():
  (Xtrain, Ytrain), (Xtest, Ytest) = mnist.load_data()
  Xtrain1, Xtest1 = [], []
  for x in Xtrain:
    Xtrain1.append(x[0:14, 0:14])
  for x in Xtest:
    Xtest1.append(x[0:14, 0:14])
  Xtrain1, Xtest1 = np.array(Xtrain1), np.array(Xtest1)
  Xtrain1 = Xtrain1.reshape((Xtrain.shape[0], 14, 14, 1))
  Xtest1 = Xtest1.reshape((Xtest.shape[0], 14, 14, 1))
  Ytest = to_categorical(Ytest)
  Ytrain = to_categorical(Ytrain)
  return Xtrain1, Ytrain, Xtest1, Ytest

def Quad2():
  (Xtrain, Ytrain), (Xtest, Ytest) = mnist.load_data()
  Xtrain1, Xtest1 = [], []
  for x in Xtrain:
    Xtrain1.append(x[0:14, 14:28])
  for x in Xtest:
    Xtest1.append(x[0:14, 14:28])
  Xtrain1, Xtest1 = np.array(Xtrain1), np.array(Xtest1)
  Xtrain1 = Xtrain1.reshape((Xtrain.shape[0], 14, 14, 1))
  Xtest1 = Xtest1.reshape((Xtest.shape[0], 14, 14, 1))
  Ytest = to_categorical(Ytest)
  Ytrain = to_categorical(Ytrain)
  return Xtrain1, Ytrain, Xtest1, Ytest

def Quad3():
  (Xtrain, Ytrain), (Xtest, Ytest) = mnist.load_data()
  Xtrain1, Xtest1 = [], []
  for x in Xtrain:
    Xtrain1.append(x[14:28, 0:14])
  for x in Xtest:
    Xtest1.append(x[14:28, 0:14])
  Xtrain1, Xtest1 = np.array(Xtrain1), np.array(Xtest1)
  Xtrain1 = Xtrain1.reshape((Xtrain.shape[0], 14, 14, 1))
  Xtest1 = Xtest1.reshape((Xtest.shape[0], 14, 14, 1))
  Ytest = to_categorical(Ytest)
  Ytrain = to_categorical(Ytrain)
  return Xtrain1, Ytrain, Xtest1, Ytest

def Quad4():
  (Xtrain, Ytrain), (Xtest, Ytest) = mnist.load_data()
  Xtrain1, Xtest1 = [], []
  for x in Xtrain:
    Xtrain1.append(x[14:28, 14:28])
  for x in Xtest:
    Xtest1.append(x[14:28, 14:28])
  Xtrain1, Xtest1 = np.array(Xtrain1), np.array(Xtest1)
  Xtrain1 = Xtrain1.reshape((Xtrain.shape[0], 14, 14, 1))
  Xtest1 = Xtest1.reshape((Xtest.shape[0], 14, 14, 1))
  Ytest = to_categorical(Ytest)
  Ytrain = to_categorical(Ytrain)
  return Xtrain1, Ytrain, Xtest1, Ytest

xtrain1, ytrain1, xtest1, ytest1= Quad1()
xtrain2, ytrain2, xtest2, ytest2= Quad2()
xtrain3, ytrain3, xtest3, ytest3= Quad3()
xtrain4, ytrain4, xtest4, ytest4= Quad4()

from sklearn.model_selection import train_test_split
import tensorflow as tf

def processData(train, test):
  train = train.astype('float32')
  test = test.astype('float32')
  train = train / 255.0
  test = test / 255.0
  return train, test


def constructModel():
  model = Sequential()
  model.add(Conv2D(32, (3, 3), activation='relu', kernel_initializer='he_uniform', input_shape=(14, 14, 1)))
  model.add(MaxPooling2D((2, 2)))
  model.add(Conv2D(64, (3, 3), activation='relu', kernel_initializer='he_uniform'))
  model.add(Conv2D(64, (3, 3), activation='relu', kernel_initializer='he_uniform'))
  model.add(MaxPooling2D((2, 2)))
  model.add(Flatten())
  model.add(Dense(100, activation='relu', kernel_initializer='he_uniform'))
  model.add(Dense(10, activation='softmax'))
  opt = SGD(learning_rate=0.001, momentum=0.9)
  model.compile(optimizer=opt, loss='categorical_crossentropy', metrics=['accuracy'])
  return model


def checkpoint(saveweight):
    checkpoint_filepath = saveweight
    model_checkpoint_callback = tf.keras.callbacks.ModelCheckpoint(
    filepath=checkpoint_filepath,
    save_weights_only=True,
    monitor='val_acc',
    mode='max',
    save_best_only=True)
    return model_checkpoint_callback

def modelEvaluation(dataX, dataY, saveweight):
    scores, histories = list(), list()
    trainX, testX, trainY, testY = train_test_split(dataX, dataY, test_size=0.2, random_state=42)
    model = constructModel()
    callback_checkpoint = checkpoint(saveweight)
    history = model.fit(trainX, trainY, epochs=10, batch_size=32, validation_data=(testX, testY), verbose=0, callbacks=[callback_checkpoint])
    _, acc = model.evaluate(testX, testY, verbose=0)
    print('> %.3f' % (acc * 100.0))
    scores.append(acc)
    histories.append(history)
    return scores, histories


def getDiagnostics(histories):
  for i in range(len(histories)):
          plt.subplot(2, 1, 1)
          plt.title('Cross Entropy Loss')
          plt.plot(histories[i].history['loss'], color='blue', label='train')
          plt.plot(histories[i].history['val_loss'], color='orange', label='test')
          plt.subplot(2, 1, 2)
          plt.title('Classification Accuracy')
          plt.plot(histories[i].history['acc'], color='blue', label='train')
          plt.plot(histories[i].history['val_acc'], color='orange', label='test')
  plt.show()


def getPerformance(scores):
  print('Accuracy: mean=%.3f std=%.3f, n=%d' % (mean(scores)*100, std(scores)*100, len(scores)))
  plt.boxplot(scores)
  plt.show()


def startTraining(xtrain, xtest, ytrain, saveweight):
    trainX, testX = processData(xtrain, xtest)
    scores, histories = modelEvaluation(xtrain, ytrain, saveweight)
    getDiagnostics(histories)


def startTesting(xtrain, xtest, ytest, bestweight):
  trainX, testX = processData(xtrain, xtest)
  model = constructModel()
  model.load_weights(bestweight)
  _, acc = model.evaluate(xtest, ytest, verbose=1)
  print('> %.3f' % (acc * 100.0))

startTraining(xtrain1, xtest1, ytrain1, 'best1.hdf5')
startTesting(xtrain1, xtest1, ytest1, 'best1.hdf5')

startTraining(xtrain2, xtest2, ytrain2, 'best2.hdf5')
startTesting(xtrain2, xtest2, ytest2, 'best2.hdf5')

startTraining(xtrain3, xtest3, ytrain3, 'best3.hdf5')
startTesting(xtrain3, xtest3, ytest3, 'best3.hdf5')

startTraining(xtrain4, xtest4, ytrain4, 'best4.hdf5')
startTesting(xtrain4, xtest4, ytest4, 'best4.hdf5')