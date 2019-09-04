import React, { useEffect, useContext, useMemo, useState } from 'react';
import {useGlobalState, useGlobalMutation} from '../utils/container';
import RTCClient from '../rtc-client';

export default function useDevices () {
  const stateCtx = useGlobalState();
  const mutationCtx = useGlobalMutation();
  const client = useMemo(() => {
    const _rtcClient = new RTCClient()
    _rtcClient.createClient({codec: stateCtx.codec, mode: stateCtx.mode});
    return _rtcClient;
  })

  const cameraList = useMemo(() => {
    return stateCtx.devicesList
    .filter((item) => item.kind === 'videoinput')
    .map((item, idx) => ({
      value: item.deviceId,
      label: item.label ? item.label : `Video Input ${idx}`
    }));
  })

  const microphoneList = useMemo(() => {
    return stateCtx.devicesList
    .filter((item) => item.kind === 'audioinput')
    .map((item, idx) => ({
      value: item.deviceId,
      label: item.label ? item.label : `Audio Input ${idx}`
    }));
  })

  useEffect(() => {
    console.log(`use devices ${stateCtx.devicesList.length}`)
    if (cameraList.length > 0 || microphoneList.length > 0) return;
    mutationCtx.startLoading();
    client.getDevices().then((devices) => {
      mutationCtx.setDevicesList(devices);
      mutationCtx.stopLoading();
    });
    return () => {
      client.destroy();
    }
  }, []);

  useEffect(() => {
    if (cameraList[0] && microphoneList[0]) {
      mutationCtx.updateConfig({
        cameraId: cameraList[0].value,
        microphoneId: microphoneList[0].value,
      });
    }
  }, [cameraList[0], microphoneList[0]]);

  return [cameraList, microphoneList];
}