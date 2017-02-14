package com.airbnb.lottie;

import android.graphics.PointF;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Keyframe<T> {
  private static Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();

  static <T> List<Keyframe<T>> parseKeyframes(JSONArray json, LottieComposition composition,
      float scale, AnimatableValueDeserializer<T> valueDeserializer) throws JSONException {
    if (json.length() == 0) {
      return Collections.emptyList();
    }
    List<Keyframe<T>> keyframes = new ArrayList<>();
    for (int i = 0; i < json.length(); i++) {
      keyframes.add(new Keyframe<>(json.getJSONObject(i), composition, scale, valueDeserializer));
    }

    setEndFrames(keyframes);

    return keyframes;
  }

  static <T> void setEndFrames(List<? extends Keyframe<T>> keyframes) {
    if (keyframes.size() > 1) {
      for (int i = 0; i < keyframes.size() - 1; i++) {
        keyframes.get(i).endFrame = keyframes.get(i + 1).startFrame;
      }
      keyframes.remove(keyframes.size() - 1);
    }
  }


  private final LottieComposition composition;
  T startValue;
  T endValue;
  Interpolator timingFunction;
  @SuppressWarnings("WeakerAccess") float startFrame;
  @SuppressWarnings("WeakerAccess") @Nullable Float endFrame = null;

  public Keyframe(LottieComposition composition, float startFrame, float endFrame) {
    this.composition = composition;
    this.startFrame = startFrame;
    this.endFrame = endFrame;
  }

  Keyframe(JSONObject json, LottieComposition composition, float scale,
      AnimatableValueDeserializer<T> valueDeserializer) throws JSONException {
    this.composition = composition;

    parseKeyframe(json, scale, valueDeserializer);
  }

  private void parseKeyframe(JSONObject json, float scale,
      AnimatableValueDeserializer<T> valueDeserializer)
      throws JSONException {
    PointF cp1 = null;
    PointF cp2 = null;
    boolean hold = false;

    if (json.has("t")) {
      startFrame = (float) json.getDouble("t");
      if (json.has("s")) {
        startValue = valueDeserializer.valueFromObject(json.get("s"), scale);
      }
      if (json.has("e")) {
        endValue = valueDeserializer.valueFromObject(json.get("e"), scale);
      }
      if (json.has("o") && json.has("i")) {
        cp1 = JsonUtils.pointFromJsonObject(json.getJSONObject("o"), scale);
        cp2 = JsonUtils.pointFromJsonObject(json.getJSONObject("i"), scale);
      }

      if (json.has("h")) {
        hold = json.getInt("h") == 1;
      }

      if (hold) {
        endValue = startValue;
        // TODO: create a HoldInterpolator so progress changes don't invalidate.
        timingFunction = LINEAR_INTERPOLATOR;
      } else if (cp1 != null) {
        timingFunction = PathInterpolatorCompat.create(
            cp1.x / scale, cp1.y / scale, cp2.x / scale, cp2.y / scale);
      } else {
        timingFunction = LINEAR_INTERPOLATOR;
      }
    } else {
      startValue = valueDeserializer.valueFromObject(json, scale);
      endValue = startValue;
    }
  }

  @FloatRange(from = 0f, to = 1f)
  float getStartProgress() {
    return startFrame / composition.getDurationFrames();
  }

  @FloatRange(from = 0f, to = 1f)
  float getEndProgress() {
    //noinspection Range
    return endFrame == null ? 1f : endFrame / composition.getDurationFrames();
  }

  boolean isStatic() {
    return timingFunction == null;
  }

  boolean containsProgress(@FloatRange(from = 0f, to = 1f) float progress) {
    return progress >= getStartProgress() && progress <= getEndProgress();
  }

  @Override public String toString() {
    return "Keyframe{" + "startValue=" + startValue +
        ", endValue=" + endValue +
        ", startFrame=" + startFrame +
        ", endFrame=" + endFrame +
        ", timingFunction=" + timingFunction +
        '}';
  }
}
