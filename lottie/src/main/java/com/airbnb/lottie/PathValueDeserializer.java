package com.airbnb.lottie;

import android.graphics.Path;
import android.graphics.PointF;

import org.json.JSONArray;
import org.json.JSONException;

public class PathValueDeserializer implements AnimatableValueDeserializer<PointF> {

  @Override public PointF valueFromObject(Object object, float scale) throws JSONException {
    return JsonUtils.pointFromJsonArray((JSONArray) object, scale);
  }

  Path createPath(PointF startPoint, PointF endPoint, PointF cp1, PointF cp2) {
    Path path = new Path();
    path.moveTo(startPoint.x, startPoint.y);

    if (cp1 != null && cp1.length() != 0 && cp2 != null && cp2.length() != 0) {
      path.cubicTo(
          startPoint.x + cp1.x, startPoint.y + cp1.y,
          endPoint.x + cp2.x, endPoint.y + cp2.y,
          endPoint.x, endPoint.y);
    } else {
      path.lineTo(endPoint.x, endPoint.y);
    }
    return path;
  }
}
