package com.airbnb.lottie;

import org.json.JSONException;

public interface AnimatableValueDeserializer<T> {

  T valueFromObject(Object object, float scale) throws JSONException;
}
