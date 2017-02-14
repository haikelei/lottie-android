package com.airbnb.lottie;

import java.util.List;

public abstract class KeyframeAnimation<T> extends BaseKeyframeAnimation<T, T> {
  KeyframeAnimation(long startDelay, long duration, LottieComposition composition,
      List<? extends Keyframe<T>> keyframes) {
    super(keyframes);
  }
}
