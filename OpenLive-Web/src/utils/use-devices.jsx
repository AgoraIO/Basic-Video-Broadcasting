import React, { useEffect, useMemo } from 'react';
import {useGlobalState, useGlobalMutation} from '../utils/container';
import RTCClient from '../rtc-client';

export default function useDevices () {
  const stateCtx = useGlobalState();
  const mutationCtx = useGlobalMutation();

  const client = useMemo(() => {
    const _rtcClient = new RTCClient()
    return _rtcClient;
  }, []);

  const [cameraList, microphoneList] = useMemo(() => {
    return [
      stateCtx.devicesList
      .filter((item) => item.kind === 'videoinput')
      .map((item, idx) => ({
        value: item.deviceId,
        label: item.label ? item.label : `Video Input ${idx}`
      })),
      stateCtx.devicesList
      .filter((item) => item.kind === 'audioinput')
      .map((item, idx) => ({
        value: item.deviceId,
        label: item.label ? item.label : `Audio Input ${idx}`
      }))
    ];
  }, [stateCtx.devicesList]);

  useEffect(() => {
    if (cameraList.length > 0 || microphoneList.length > 0) return;
    client.getDevices().then((devices) => {
      mutationCtx.setDevicesList(devices);
    });
    return () => {
      client.destroy();
    }
  }, [microphoneList, mutationCtx, cameraList, client]);

  useEffect(() => {
    if (cameraList[0] &&
        microphoneList[0] &&
        (!stateCtx.config.cameraId ||
         !stateCtx.config.microphoneId)) {
      mutationCtx.updateConfig({
        cameraId: cameraList[0].value,
        microphoneId: microphoneList[0].value,
      });
      mutationCtx.stopLoading();
    }
  }, [mutationCtx, stateCtx.devicesList, stateCtx.config, cameraList, microphoneList]);

  return [cameraList, microphoneList];
}