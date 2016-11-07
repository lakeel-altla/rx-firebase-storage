package com.lakeel.altla.tango;

// NOTE:
//
// Tango#connectTextureId を呼びださなければ (カメラのテクスチャへ接続しなければ) onFrameAvailable は呼び出されない。

public interface OnFrameAvailableListener {

    void onFrameAvailable(int cameraId);
}
