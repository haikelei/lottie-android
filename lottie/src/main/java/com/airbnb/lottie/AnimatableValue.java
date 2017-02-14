package com.airbnb.lottie;

interface AnimatableValue<T> {
  BaseKeyframeAnimation<?, T> createAnimation();
  boolean hasAnimation();
}
