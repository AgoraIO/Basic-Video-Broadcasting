import React, {useEffect} from 'react';
import {useGlobalState, useGlobalMutation} from './container';

export default function useStream (client) {
  const stateCtx = useGlobalState();
  const mutationCtx = useGlobalMutation();

  const [streamList, localStream, currentStream] = [stateCtx.streams, stateCtx.localStream, stateCtx.currentStream];

  // const [streamList, localStream, currentStream] = useMemo(() => {
  //   return [stateCtx.streams, stateCtx.localStream, stateCtx.currentStream];
  // }, [stateCtx]);

  useEffect(() => {
    const addRemoteStream = (evt) => {
      const {stream} = evt;
      client.subscribe(stream, (err) => {
        mutationCtx.toastError(`stream ${evt.stream.getId()} subscribe failed: ${err}`);
      });
    }
    // const canceledScreenSharing = () => {
    //   if (stateCtx.localStream) {
    //     stateCtx.localStream.close();
    //   }
    //   stateCtx.toastInfo('Screen Sharing Stopped');
    // }
    if (client && client._subscribed === false) {
      // client.on("stopScreenSharing", canceledScreenSharing);
      client.on("connection-state-change", mutationCtx.connectionStateChanged);
      client.on("localStream-added", mutationCtx.addLocal);
      client.on("stream-published", mutationCtx.addStream);
      client.on("stream-added", addRemoteStream);
      client.on("stream-removed", mutationCtx.removeStream);
      client.on("stream-subscribed", mutationCtx.subscribeStream);
      client.on("peer-leave", mutationCtx.removeStream);
      client._subscribed = true;
    }
  }, [client, mutationCtx])

  return [localStream, currentStream, streamList];
}